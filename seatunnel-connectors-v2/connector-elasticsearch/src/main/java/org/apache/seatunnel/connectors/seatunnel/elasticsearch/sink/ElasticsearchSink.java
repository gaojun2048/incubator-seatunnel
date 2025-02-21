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

package org.apache.seatunnel.connectors.seatunnel.elasticsearch.sink;

import static org.apache.seatunnel.connectors.seatunnel.elasticsearch.config.SinkConfig.HOSTS;
import static org.apache.seatunnel.connectors.seatunnel.elasticsearch.config.SinkConfig.INDEX;

import org.apache.seatunnel.api.common.PrepareFailException;
import org.apache.seatunnel.api.sink.SeaTunnelSink;
import org.apache.seatunnel.api.sink.SinkWriter;
import org.apache.seatunnel.api.table.type.SeaTunnelDataType;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.common.config.CheckConfigUtil;
import org.apache.seatunnel.common.config.CheckResult;
import org.apache.seatunnel.common.constants.PluginType;
import org.apache.seatunnel.connectors.seatunnel.elasticsearch.config.SinkConfig;
import org.apache.seatunnel.connectors.seatunnel.elasticsearch.state.ElasticsearchAggregatedCommitInfo;
import org.apache.seatunnel.connectors.seatunnel.elasticsearch.state.ElasticsearchCommitInfo;
import org.apache.seatunnel.connectors.seatunnel.elasticsearch.state.ElasticsearchSinkState;

import com.google.auto.service.AutoService;

import java.util.Collections;

@AutoService(SeaTunnelSink.class)
public class ElasticsearchSink implements SeaTunnelSink<SeaTunnelRow, ElasticsearchSinkState, ElasticsearchCommitInfo, ElasticsearchAggregatedCommitInfo> {


    private org.apache.seatunnel.shade.com.typesafe.config.Config pluginConfig;
    private SeaTunnelRowType seaTunnelRowType;

    @Override
    public String getPluginName() {
        return "Elasticsearch";
    }

    @Override
    public void prepare(org.apache.seatunnel.shade.com.typesafe.config.Config pluginConfig) throws
        PrepareFailException {
        CheckResult result = CheckConfigUtil.checkAllExists(pluginConfig, HOSTS, INDEX);
        if (!result.isSuccess()) {
            throw new PrepareFailException(getPluginName(), PluginType.SINK, result.getMsg());
        }
        this.pluginConfig = pluginConfig;
        SinkConfig.setValue(pluginConfig);
    }

    @Override
    public void setTypeInfo(SeaTunnelRowType seaTunnelRowType) {
        this.seaTunnelRowType = seaTunnelRowType;
    }

    @Override
    public SeaTunnelDataType<SeaTunnelRow> getConsumedType() {
        return this.seaTunnelRowType;
    }

    @Override
    public SinkWriter<SeaTunnelRow, ElasticsearchCommitInfo, ElasticsearchSinkState> createWriter(SinkWriter.Context context) {
        return new ElasticsearchSinkWriter(context, seaTunnelRowType, pluginConfig, Collections.emptyList());
    }
}
