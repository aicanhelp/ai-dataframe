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

package org.dinky.domain.task;

import org.dinky.data.dto.TaskDTO;
import org.dinky.data.model.TaskVersion;
import org.dinky.common.mybatis.service.ISuperService;

import java.util.List;

public interface TaskVersionService extends ISuperService<TaskVersion> {

    /**
     * @description 通过作业Id查询版本数据
     * @param taskId
     * @return java.util.List<org.dinky.data.model.TaskVersion>
     */
    List<TaskVersion> getTaskVersionByTaskId(Integer taskId);

    /**
     * Create a snapshot of a task version.
     *
     * @param task A {@link TaskDTO} object representing the task to create a snapshot for.
     * @return
     */
    Integer createTaskVersionSnapshot(TaskDTO task);
}
