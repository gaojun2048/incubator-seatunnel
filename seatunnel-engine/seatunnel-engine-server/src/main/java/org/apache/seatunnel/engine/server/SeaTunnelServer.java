/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.engine.server;

import org.apache.seatunnel.common.utils.RetryUtils;
import org.apache.seatunnel.engine.common.Constant;
import org.apache.seatunnel.engine.common.config.SeaTunnelConfig;
import org.apache.seatunnel.engine.common.exception.SeaTunnelEngineException;
import org.apache.seatunnel.engine.server.execution.ExecutionState;
import org.apache.seatunnel.engine.server.execution.TaskGroupLocation;
import org.apache.seatunnel.engine.server.service.slot.DefaultSlotService;
import org.apache.seatunnel.engine.server.service.slot.SlotService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hazelcast.instance.impl.Node;
import com.hazelcast.internal.services.ManagedService;
import com.hazelcast.internal.services.MembershipAwareService;
import com.hazelcast.internal.services.MembershipServiceEvent;
import com.hazelcast.jet.impl.LiveOperationRegistry;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.IMap;
import com.hazelcast.spi.impl.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.LiveOperations;
import com.hazelcast.spi.impl.operationservice.LiveOperationsTracker;
import lombok.NonNull;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SeaTunnelServer implements ManagedService, MembershipAwareService, LiveOperationsTracker {
    private static final ILogger LOGGER = Logger.getLogger(SeaTunnelServer.class);
    public static final String SERVICE_NAME = "st:impl:seaTunnelServer";

    private NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private final LiveOperationRegistry liveOperationRegistry;

    private volatile SlotService slotService;
    private TaskExecutionService taskExecutionService;
    private CoordinatorService coordinatorService;
    private ScheduledExecutorService monitorService;

    private final ExecutorService executorService;

    private final SeaTunnelConfig seaTunnelConfig;

    private boolean isRunning = true;

    public SeaTunnelServer(@NonNull Node node, @NonNull SeaTunnelConfig seaTunnelConfig) {
        this.logger = node.getLogger(getClass());
        this.liveOperationRegistry = new LiveOperationRegistry();
        this.seaTunnelConfig = seaTunnelConfig;
        this.executorService =
            Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                .setNameFormat("seatunnel-server-executor-%d").build());
        logger.info("SeaTunnel server start...");
    }

    /**
     * Lazy load for Slot Service
     */
    public SlotService getSlotService() {
        if (slotService == null) {
            synchronized (this) {
                if (slotService == null) {
                    SlotService service = new DefaultSlotService(nodeEngine, taskExecutionService, true, 2);
                    service.init();
                    slotService = service;
                }
            }
        }
        return slotService;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void init(NodeEngine engine, Properties hzProperties) {
        this.nodeEngine = (NodeEngineImpl) engine;
        // TODO Determine whether to execute there method on the master node according to the deploy type
        taskExecutionService = new TaskExecutionService(
            nodeEngine, nodeEngine.getProperties()
        );
        taskExecutionService.start();
        getSlotService();
        coordinatorService = new CoordinatorService(nodeEngine, executorService, this);
        monitorService = Executors.newSingleThreadScheduledExecutor();
        monitorService.scheduleAtFixedRate(() -> printExecutionInfo(), 0, 60, TimeUnit.SECONDS);
    }

    @Override
    public void reset() {

    }

    @Override
    public void shutdown(boolean terminate) {
        isRunning = false;
        if (monitorService != null) {
            monitorService.shutdown();
        }
        if (slotService != null) {
            slotService.close();
        }
        if (coordinatorService != null) {
            coordinatorService.shutdown();
        }
        executorService.shutdown();
        taskExecutionService.shutdown();
    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {

    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {
        try {
            if (isMasterNode()) {
                this.getCoordinatorService().memberRemoved(event);
            }
        } catch (SeaTunnelEngineException e) {
            logger.severe("Error when handle member removed event", e);
        }
    }

    @Override
    public void populate(LiveOperations liveOperations) {

    }

    /**
     * Used for debugging on call
     */
    public String printMessage(String message) {
        this.logger.info(nodeEngine.getThisAddress() + ":" + message);
        return message;
    }

    public LiveOperationRegistry getLiveOperationRegistry() {
        return liveOperationRegistry;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public CoordinatorService getCoordinatorService() {
        int retryCount = 0;
        if (isMasterNode()) {
            // TODO the retry count and sleep time need configurable
            while (!coordinatorService.isCoordinatorActive() && retryCount < 20 && isRunning) {
                try {
                    logger.warning("This is master node, waiting the coordinator service init finished");
                    Thread.sleep(1000);
                    retryCount++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (coordinatorService.isCoordinatorActive()) {
                return coordinatorService;
            }

            throw new SeaTunnelEngineException("Can not get coordinator service from an active master node.");
        } else {
            throw new SeaTunnelEngineException("Please don't get coordinator service from an inactive master node");
        }
    }

    public TaskExecutionService getTaskExecutionService() {
        return taskExecutionService;
    }

    /**
     * return whether task is end
     *
     * @param taskGroupLocation taskGroupLocation
     * @return
     */
    public boolean taskIsEnded(@NonNull TaskGroupLocation taskGroupLocation) {
        IMap<Object, Object> runningJobState =
            nodeEngine.getHazelcastInstance().getMap(Constant.IMAP_RUNNING_JOB_STATE);
        if (runningJobState == null) {
            return false;
        }

        Object taskState = runningJobState.get(taskGroupLocation);
        return taskState == null ? false : ((ExecutionState) taskState).isEndState();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public boolean isMasterNode() {
        // must retry until the cluster have master node
        try {
            return RetryUtils.retryWithException(() -> {
                return nodeEngine.getMasterAddress().equals(nodeEngine.getThisAddress());
            }, new RetryUtils.RetryMaterial(20, true,
                exception -> exception instanceof NullPointerException && isRunning, 1000));
        } catch (Exception e) {
            throw new SeaTunnelEngineException("cluster have no master node", e);
        }
    }

    private void printExecutionInfo() {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
        int activeCount = threadPoolExecutor.getActiveCount();
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        int poolSize = threadPoolExecutor.getPoolSize();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        long taskCount = threadPoolExecutor.getTaskCount();
        StringBuffer sbf = new StringBuffer();
        sbf.append("activeCount=")
            .append(activeCount)
            .append("\n")
            .append("corePoolSize=")
            .append(corePoolSize)
            .append("\n")
            .append("maximumPoolSize=")
            .append(maximumPoolSize)
            .append("\n")
            .append("poolSize=")
            .append(poolSize)
            .append("\n")
            .append("completedTaskCount=")
            .append(completedTaskCount)
            .append("\n")
            .append("taskCount=")
            .append(taskCount);
        logger.info(sbf.toString());
    }
}
