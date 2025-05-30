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

package org.dinky.domain.alert.impl;

import org.dinky.data.model.alert.AlertHistory;
import org.dinky.domain.alert.AlertGroupService;
import org.dinky.domain.alert.AlertHistoryService;
import org.dinky.infrastructure.mapper.alert.AlertHistoryMapper;
import org.dinky.common.mybatis.service.impl.SuperServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;

/** AlertHistoryServiceImpl */
@Service
@RequiredArgsConstructor
public class AlertHistoryServiceImpl extends SuperServiceImpl<AlertHistoryMapper, AlertHistory>
        implements AlertHistoryService {

    private final AlertGroupService alertGroupService;

    /**
     * delete alert history by alert group id
     *
     * @param alertGroupId {@link Integer}
     * @return {@link Boolean}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteByAlertGroupId(Integer alertGroupId) {
        List<AlertHistory> alertHistoryList = getBaseMapper()
                .selectList(new LambdaQueryWrapper<AlertHistory>().eq(AlertHistory::getAlertGroupId, alertGroupId));
        return baseMapper.deleteBatchIds(alertHistoryList) > 0;
    }

    /**
     * @param jobInstanceId
     * @return
     */
    @Override
    public List<AlertHistory> queryAlertHistoryRecordByJobInstanceId(Integer jobInstanceId) {

        return baseMapper
                .selectList(new LambdaQueryWrapper<AlertHistory>().eq(AlertHistory::getJobInstanceId, jobInstanceId))
                .stream()
                .peek(alertHistory ->
                        alertHistory.setAlertGroup(alertGroupService.getById(alertHistory.getAlertGroupId())))
                .collect(Collectors.toList());
    }
}
