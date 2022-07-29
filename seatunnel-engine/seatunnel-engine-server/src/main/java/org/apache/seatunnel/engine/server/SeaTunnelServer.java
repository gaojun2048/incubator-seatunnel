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

import org.apache.seatunnel.engine.core.job.JobImmutableInformation;

import com.hazelcast.instance.impl.Node;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.internal.services.ManagedService;
import com.hazelcast.internal.services.MembershipAwareService;
import com.hazelcast.internal.services.MembershipServiceEvent;
import com.hazelcast.jet.impl.LiveOperationRegistry;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.LiveOperations;
import com.hazelcast.spi.impl.operationservice.LiveOperationsTracker;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class SeaTunnelServer implements ManagedService, MembershipAwareService, LiveOperationsTracker {
    public static final String SERVICE_NAME = "st:impl:seaTunnelServer";

    private NodeEngineImpl nodeEngine;
    private final ILogger logger;
    private final LiveOperationRegistry liveOperationRegistry;

    public SeaTunnelServer(Node node) {
        this.logger = node.getLogger(getClass());
        this.liveOperationRegistry = new LiveOperationRegistry();
        logger.info("SeaTunnel server start...");
    }

    @Override
    public void init(NodeEngine engine, Properties hzProperties) {
        this.nodeEngine = (NodeEngineImpl) engine;
    }

    @Override
    public void reset() {

    }

    @Override
    public void shutdown(boolean terminate) {

    }

    @Override
    public void memberAdded(MembershipServiceEvent event) {

    }

    @Override
    public void memberRemoved(MembershipServiceEvent event) {

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

    /**
     * call by client to submit job
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public CompletableFuture<Void> submitJob(Data jobImmutableInformation) {
        // TODO Here we need new a JobMaster and run it.
        JobImmutableInformation jobInformation = nodeEngine.getSerializationService().toObject(jobImmutableInformation);
        logger.info("Job [" + jobInformation.getJobId() + "] submit");
        logger.info("Job [" + jobInformation.getJobId() + "] jar urls " + jobInformation.getPluginJarsUrls());
        CompletableFuture<Void> voidCompletableFuture = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                logger.info("I am sleep 2000 ms");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                voidCompletableFuture.complete(null);
            }
        }).start();
        return voidCompletableFuture;
    }
}
