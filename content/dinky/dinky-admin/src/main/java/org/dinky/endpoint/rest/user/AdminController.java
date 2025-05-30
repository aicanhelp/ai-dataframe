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

package org.dinky.endpoint.rest.user;

import org.dinky.DinkyVersion;
import org.dinky.data.dto.LoginDTO;
import org.dinky.data.dto.UserDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.data.result.Result;
import org.dinky.domain.user.UserService;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AdminController
 *
 * @since 2021/5/28 15:52
 */
@Slf4j
@Api(tags = "Admin Controller")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * user login
     *
     * @param loginDTO basic information for user login
     * @return {@link Result}{@link UserDTO} obtain the user's UserDTO
     */
    @PostMapping("/login")
    @ApiImplicitParam(name = "loginDTO", value = "LoginDTO", required = true, dataTypeClass = LoginDTO.class)
    @ApiOperation(value = "Login", notes = "Login")
    @SaIgnore
    public Result<UserDTO> login(@RequestBody LoginDTO loginDTO) {
        return userService.loginUser(loginDTO);
    }

    /**
     * user logout
     *
     * @return {@link Result}{@link Void} user loginout status information
     */
    @DeleteMapping("/outLogin")
    @ApiOperation(value = "LogOut", notes = "LogOut")
    @SaIgnore
    public Result<Void> outLogin() {
        userService.outLogin();
        return Result.succeed(Status.SIGN_OUT_SUCCESS.getMessage());
    }

    /**
     * get current user info
     *
     * @return {@link Result}{@link UserDTO} obtain the current user's UserDTO
     */
    @GetMapping("/current")
    @ApiOperation(value = "Current User Info", notes = "Current User Info")
    @SaCheckLogin
    public Result<UserDTO> getCurrentUserInfo(@CookieValue(name = "tenantId") Integer tenantId) {
        if (tenantId == null) {
            throw NotLoginException.newInstance(
                    "LOCAL", NotLoginException.NOT_TOKEN, NotLoginException.NOT_TOKEN_MESSAGE, null);
        }
        return userService.queryCurrentUserInfo(tenantId);
    }

    /**
     * choose tenant by tenantId
     *
     * @param tenantId
     * @return {@link Result}{@link Tenant} the specified tenant
     */
    @PostMapping("/chooseTenant")
    @ApiImplicitParam(name = "tenantId", value = "tenantId", required = true, dataTypeClass = Integer.class)
    @ApiOperation(value = "Choose Tenant To Login", notes = "Choose Tenant To Login")
    @SaCheckLogin
    public Result<Tenant> switchingTenant(@RequestParam("tenantId") Integer tenantId) {
        return userService.chooseTenant(tenantId);
    }

    /**
     * get tenant info
     *
     * @return {@link Result}{@link SaTokenInfo}
     */
    @GetMapping("/tokenInfo")
    @ApiOperation(value = "Query Current User Token Info", notes = "Query Current User Token Info")
    @SaCheckLogin
    public Result<SaTokenInfo> getTokenInfo() {
        return Result.succeed(StpUtil.getTokenInfo());
    }

    @GetMapping("/version")
    @ApiOperation(value = "Query Service Version", notes = "Query Dinky Service Version Number")
    @ApiImplicitParam(name = "isExternalCall", value = "isExternalCall", dataTypeClass = Boolean.class)
    @SaCheckLogin
    public Result<Object> getVersionInfo(
            @RequestParam(required = false, defaultValue = "true") boolean isExternalCall) {
        if (isExternalCall) {
            return Result.succeed((Object) DinkyVersion.getShortVersion());
        }
        return Result.succeed((Object) DinkyVersion.getVersion());
    }
}
