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

package org.dinky.domain.alert;

import org.dinky.data.model.alert.AlertGroup;
import org.dinky.common.mybatis.service.ISuperService;

import java.util.List;

/** AlertGroupService */
public interface AlertGroupService extends ISuperService<AlertGroup> {

    /**
     * list all enabled alert group
     *
     * @return {@link List<AlertGroup>}
     */
    List<AlertGroup> listEnabledAllAlertGroups();

    /**
     * get alert group info by id
     *
     * @param id {@link Integer}
     * @return {@link AlertGroup}
     */
    AlertGroup getAlertGroupInfo(Integer id);

    /**
     * alert group enable or disable by id
     *
     * @param id
     * @return {@link Boolean}
     */
    Boolean modifyAlertGroupStatus(Integer id);

    /**
     * delete alert group by id and cascade delete alert history
     *
     * @param id {@link Integer}
     * @return {@link Boolean}
     */
    Boolean deleteGroupById(Integer id);

    List<AlertGroup> selectListByKeyWord(String keyword);

    /**
     * check alert group has relationship with other table
     * @param id {@link Integer} alert group id
     * @return {@link Boolean} true: has relationship, false: no relationship
     */
    boolean hasRelationShip(Integer id);
}
