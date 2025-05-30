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

package org.dinky.common.configure;

import org.dinky.common.context.TenantContextHolder;
import org.dinky.common.interceptor.PostgreSQLPrepareInterceptor;
import org.dinky.common.interceptor.PostgreSQLQueryInterceptor;
import org.dinky.common.mybatis.handler.DateMetaObjectHandler;
import org.dinky.common.mybatis.MybatisPlusFillProperties;

import java.util.Set;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.google.common.collect.ImmutableSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

/** mybatisPlus config class */
@Configuration
@MapperScan("org.dinky.mapper")
@EnableConfigurationProperties(MybatisPlusFillProperties.class)
@Slf4j
@RequiredArgsConstructor
public class MybatisPlusConfig {

    private final MybatisPlusFillProperties autoFillProperties;

    private static final Set<String> IGNORE_TABLE_NAMES = ImmutableSet.of(
            "dinky_namespace",
            "dinky_alert_group",
            "dinky_alert_history",
            "dinky_alert_instance",
            "dinky_catalogue",
            "dinky_cluster",
            "dinky_cluster_configuration",
            "dinky_database",
            "dinky_fragment",
            "dinky_history",
            "dinky_jar",
            "dinky_job_history",
            "dinky_job_instance",
            "dinky_role",
            "dinky_savepoints",
            "dinky_task",
            "dinky_task_statement",
            "dinky_git_project",
            "dinky_task_version");

    @Bean
    @Profile("postgresql")
    public PostgreSQLQueryInterceptor postgreSQLQueryInterceptor() {
        return new PostgreSQLQueryInterceptor();
    }

    /**
     * Add the plugin to the MyBatis plugin interceptor chain.
     *
     * @return {@linkplain PostgreSQLPrepareInterceptor}
     */
    @Bean
    @Profile("postgresql")
    public PostgreSQLPrepareInterceptor postgreSQLPrepareInterceptor() {
        return new PostgreSQLPrepareInterceptor();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("mybatis plus interceptor execute");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                Integer tenantId = (Integer) TenantContextHolder.get();
                if (tenantId == null) {
                    return new NullValue();
                }
                return new LongValue(tenantId);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                if (TenantContextHolder.isIgnoreTenant()) {
                    return true;
                }
                return !IGNORE_TABLE_NAMES.contains(tableName);
            }
        }));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "dinky.mybatis-plus.fill",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public MetaObjectHandler metaObjectHandler() {
        return new DateMetaObjectHandler(autoFillProperties);
    }
}
