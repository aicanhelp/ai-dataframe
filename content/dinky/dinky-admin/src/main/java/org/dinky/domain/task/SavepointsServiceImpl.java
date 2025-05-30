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
import org.dinky.data.model.Savepoints;
import org.dinky.gateway.enums.SavePointStrategy;
import org.dinky.infrastructure.mapper.task.SavepointsMapper;
import org.dinky.common.mybatis.service.impl.SuperServiceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * SavepointsServiceImpl
 *
 * @since 2021/11/21
 */
@Service
public class SavepointsServiceImpl extends SuperServiceImpl<SavepointsMapper, Savepoints> implements SavepointsService {

    @Override
    public List<Savepoints> listSavepointsByTaskId(Integer taskId) {
        return list(new LambdaQueryWrapper<>(Savepoints.class).eq(Savepoints::getTaskId, taskId));
    }

    @Override
    public Savepoints getLatestSavepointByTaskId(Integer taskId) {
        return baseMapper.getLatestSavepointByTaskId(taskId);
    }

    @Override
    public Savepoints getEarliestSavepointByTaskId(Integer taskId) {
        return baseMapper.getEarliestSavepointByTaskId(taskId);
    }

    @Override
    public Savepoints getSavePointWithStrategy(TaskDTO task) {
        SavePointStrategy savePointStrategy = SavePointStrategy.get(task.getSavePointStrategy());
        switch (savePointStrategy) {
            case LATEST:
                return getLatestSavepointByTaskId(task.getId());
            case EARLIEST:
                return getEarliestSavepointByTaskId(task.getId());
            case CUSTOM:
                return new Savepoints() {
                    {
                        setPath(task.getSavePointPath());
                    }
                };
            default:
                return null;
        }
    }
}
