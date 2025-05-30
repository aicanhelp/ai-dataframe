/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.endpoint.ws.handler;

import org.dinky.common.context.ConsoleContextHolder;
import org.dinky.data.model.ProcessEntity;
import org.dinky.endpoint.ws.GlobalWebSocketTopic;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProcessConsole extends ManualMessageEventHandler {

    @Override
    public Map<String, Object> firstSubscribe(Set<String> allParams) {
        Map<String, Object> result = new HashMap<>();
        allParams.forEach(processName -> {
            ProcessEntity process = ConsoleContextHolder.getInstances().getProcess(processName);
            if (process != null) {
                result.put(processName, process);
            }
        });
        return result;
    }

    @Override
    public GlobalWebSocketTopic getTopic() {
        return GlobalWebSocketTopic.PROCESS_CONSOLE;
    }
}
