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

package org.dinky.infrastructure.mapper.job;

import org.dinky.data.model.home.JobInstanceCount;
import org.dinky.data.model.home.JobModelOverview;
import org.dinky.data.model.job.JobInstance;
import org.dinky.common.mybatis.mapper.SuperMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;

/**
 * JobInstanceMapper
 *
 * @since 2022/2/2 13:02
 */
@Mapper
public interface JobInstanceMapper extends SuperMapper<JobInstance> {

    @InterceptorIgnore(tenantLine = "true")
    JobInstance getByIdWithoutTenant(Integer id);

    List<JobInstanceCount> countStatus();

    JobModelOverview getJobStreamingOrBatchModelOverview();

    @InterceptorIgnore(tenantLine = "true")
    List<JobInstance> listJobInstanceActive();

    JobInstance getJobInstanceByTaskId(Integer id);

    @InterceptorIgnore(tenantLine = "true")
    Integer getTenantByJobInstanceId(@Param("id") Integer id);
}
