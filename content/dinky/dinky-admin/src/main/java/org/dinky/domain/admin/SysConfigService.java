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

package org.dinky.domain.admin;

import org.dinky.data.model.Configuration;
import org.dinky.data.model.SysConfig;
import org.dinky.data.result.Result;
import org.dinky.common.mybatis.service.ISuperService;

import java.util.List;
import java.util.Map;

/**
 * SysConfig
 *
 * @since 2021/11/18
 */
public interface SysConfigService extends ISuperService<SysConfig> {

    /**
     * Get all configurations.
     *
     * @return A map of string keys to lists of {@link Configuration} objects.
     */
    Map<String, List<Configuration<?>>> getAll();

    /**
     * Get one configuration by key.
     *
     * @return A map of string keys to lists of {@link Configuration} objects.
     */
    Configuration<Object> getOneConfigByKey(String key);

    /**
     * Initialize system configurations.
     */
    void initSysConfig();

    /**
     * Initialize expression variables.
     */
    void initExpressionVariables();

    /**
     * Update system configurations by key-value pairs.
     *
     * @param key The key of the configuration to update.
     * @param value The new value of the configuration.
     */
    void updateSysConfigByKv(String key, String value);

    /**
     * Get needed configurations.
     *
     * @return A map of string keys to lists of {@link Configuration} objects.
     */
    Result<Map<String, Object>> getNeededCfg();

    /**
     * Set initial configurations.
     *
     * @param params The parameters for initializing configurations.
     */
    Result<Void> setInitConfig(Map<String, Object> params);
}
