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

package org.dinky.endpoint.rest.cluster;

import org.dinky.data.annotations.Log;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.dto.ClusterConfigurationDTO;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.ClusterConfiguration;
import org.dinky.data.result.Result;
import org.dinky.gateway.result.TestResult;
import org.dinky.domain.cluster.ClusterConfigurationService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ClusterConfigController
 *
 * @since 2021/11/6 21:16
 */
@Slf4j
@RestController
@Api(tags = "Cluster Config Controller")
@RequestMapping("/api/clusterConfiguration")
@RequiredArgsConstructor
@SaCheckLogin
public class ClusterConfigurationController {

    private final ClusterConfigurationService clusterConfigurationService;

    /**
     * @param clusterConfiguration
     * @return
     */
    @PutMapping("/saveOrUpdate")
    @Log(title = "Insert Or Update Cluster Config", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiOperation("Insert Or Update Cluster Config")
    @ApiImplicitParam(
            name = "clusterConfiguration",
            value = "Cluster Configuration",
            dataType = "ClusterConfiguration",
            paramType = "body",
            required = true,
            dataTypeClass = ClusterConfiguration.class)
    @SaCheckPermission(
            value = {
                PermissionConstants.REGISTRATION_CLUSTER_CONFIG_ADD,
                PermissionConstants.REGISTRATION_CLUSTER_CONFIG_EDIT
            },
            mode = SaMode.OR)
    public Result<Void> saveOrUpdateClusterConfig(@RequestBody ClusterConfigurationDTO clusterConfiguration) {
        TestResult testResult = clusterConfigurationService.testGateway(clusterConfiguration);
        clusterConfiguration.setIsAvailable(testResult.isAvailable());
        if (clusterConfigurationService.saveOrUpdate(clusterConfiguration.toBean())) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    /**
     * query cluster config list
     * @param keyword
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("Cluster Config List")
    public Result<List<ClusterConfigurationDTO>> listClusterConfigList(@RequestParam String keyword) {
        return Result.succeed(clusterConfigurationService.selectListByKeyWord(keyword));
    }

    /**
     * query cluster config list of all
     *
     * @return
     */
    @GetMapping("/listAll")
    @ApiOperation("Cluster Config List All")
    @ApiImplicitParam(
            name = "para",
            value = "Cluster Configuration",
            dataType = "JsonNode",
            paramType = "body",
            required = true)
    public Result<List<ClusterConfiguration>> listAllClusterConfig() {
        return Result.succeed(clusterConfigurationService.listAllClusterConfig());
    }

    /**
     * delete by id
     *
     * @param id {@link Integer}
     * @return {@link Result}<{@link Void}>
     */
    @DeleteMapping("/delete")
    @Log(title = "Cluster Config Delete by id", businessType = BusinessType.DELETE)
    @ApiOperation("Cluster Config Delete by id")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(value = PermissionConstants.REGISTRATION_CLUSTER_CONFIG_DELETE)
    public Result<Void> deleteById(@RequestParam("id") Integer id) {
        if (clusterConfigurationService.deleteClusterConfigurationById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    /**
     * enable by id
     *
     * @param id {@link Integer}
     * @return {@link Result}<{@link Void}>
     */
    @PutMapping("/enable")
    @Log(title = "Modify Cluster Config Status", businessType = BusinessType.UPDATE)
    @ApiOperation("Modify Cluster Config Status")
    @ApiImplicitParam(name = "id", value = "id", dataType = "Integer", paramType = "query", required = true)
    @SaCheckPermission(value = PermissionConstants.REGISTRATION_CLUSTER_CONFIG_EDIT)
    public Result<Void> modifyClusterConfigStatus(@RequestParam("id") Integer id) {
        if (clusterConfigurationService.modifyClusterConfigStatus(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }

    /**
     * test connection
     *
     * @param clusterConfiguration {@link ClusterConfiguration}
     * @return {@link Result}<{@link Void}>
     */
    @PostMapping("/testConnect")
    @Log(title = "Test Connection", businessType = BusinessType.TEST)
    @ApiOperation("Test Connection")
    @ApiImplicitParam(
            name = "clusterConfiguration",
            value = "Cluster Configuration",
            dataType = "ClusterConfiguration",
            paramType = "body",
            required = true,
            dataTypeClass = ClusterConfiguration.class)
    @SaCheckPermission(value = PermissionConstants.REGISTRATION_CLUSTER_CONFIG_HEARTBEATS)
    public Result<Void> testConnect(@RequestBody ClusterConfigurationDTO clusterConfiguration) {
        TestResult testResult = clusterConfigurationService.testGateway(clusterConfiguration);
        if (testResult.isAvailable()) {
            return Result.succeed(Status.TEST_CONNECTION_SUCCESS);
        } else {
            return Result.failed(testResult.getError());
        }
    }
}
