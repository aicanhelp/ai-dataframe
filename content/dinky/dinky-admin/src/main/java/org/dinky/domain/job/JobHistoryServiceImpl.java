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

package org.dinky.domain.job;

import org.dinky.data.dto.JobDataDto;
import org.dinky.data.model.job.JobHistory;
import org.dinky.infrastructure.mapper.job.JobHistoryMapper;
import org.dinky.common.mybatis.service.impl.SuperServiceImpl;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * JobHistoryServiceImpl
 *
 * @since 2022/3/2 20:00
 */
@Service
@Slf4j
public class JobHistoryServiceImpl extends SuperServiceImpl<JobHistoryMapper, JobHistory> implements JobHistoryService {

    @Override
    public JobHistory getByIdWithoutTenant(Integer id) {
        return baseMapper.getByIdWithoutTenant(id);
    }

    @Override
    public JobHistory getJobHistory(Integer id) {
        return baseMapper.getByIdWithoutTenant(id);
    }

    @Override
    public JobDataDto getJobHistoryDto(Integer id) {
        return JobDataDto.fromJobHistory(getJobHistory(id));
    }
}
