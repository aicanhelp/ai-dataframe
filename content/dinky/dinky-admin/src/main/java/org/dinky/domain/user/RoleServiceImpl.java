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

package org.dinky.domain.user;

import org.dinky.assertion.Asserts;
import org.dinky.data.dto.RoleDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.model.rbac.Role;
import org.dinky.data.model.rbac.RowPermissions;
import org.dinky.data.model.rbac.Tenant;
import org.dinky.data.model.rbac.User;
import org.dinky.data.model.rbac.UserRole;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.infrastructure.mapper.user.RoleMapper;
import org.dinky.common.mybatis.service.impl.SuperServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;

/** role service impl */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserRoleService userRoleService;
    private final TenantService tenantService;
    private final RowPermissionsService roleSelectPermissionsService;

    @Lazy
    @Resource
    private RoleService roleService;

    @Lazy
    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> addedOrUpdateRole(RoleDTO roleDTO) {
        Role role = roleDTO.toBean();

        if (Asserts.isNull(role.getId())) {
            Role roleCode =
                    roleService.getOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, role.getRoleCode()));
            if (Asserts.isNotNull(roleCode)) {
                return Result.failed(Status.ROLE_ALREADY_EXISTS);
            }
        }
        Boolean roleSaveOrUpdate = saveOrUpdate(role);
        if (roleSaveOrUpdate) {
            return Result.succeed(Status.SAVE_SUCCESS);
        } else {
            return Result.failed(Status.SAVE_FAILED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteRoleById(Integer id) {
        Role role = getById(id);
        Long selectUserRoleCnt = userRoleService
                .getBaseMapper()
                .selectCount(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        if (selectUserRoleCnt > 0) {
            return Result.failed(Status.ROLE_BINDING_USER);
        }
        Long selectedRowPermissionsCount = roleSelectPermissionsService
                .getBaseMapper()
                .selectCount(new LambdaQueryWrapper<RowPermissions>().eq(RowPermissions::getRoleId, id));
        if (selectedRowPermissionsCount > 0) {
            return Result.failed(Status.ROLE_BINDING_ROW_PERMISSION);
        }

        Boolean removeById = roleService.removeById(role);
        if (removeById) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    @Override
    public ProTableResult<Role> selectForProTable(JsonNode params, boolean isDelete) {
        ProTableResult<Role> roleProTableResult = super.selectForProTable(params, isDelete);
        roleProTableResult.getData().forEach(role -> {
            List<Integer> idsList = new ArrayList<>();
            Tenant tenant = tenantService.getBaseMapper().selectById(role.getTenantId());
            role.setTenant(tenant);
            String result = idsList.stream().map(Object::toString).collect(Collectors.joining(","));
        });

        return roleProTableResult;
    }

    @Override
    public List<Role> getRoleByUserId(Integer userId) {
        return userRoleService.getRoleByUserId(userId);
    }

    /**
     * Query role permission by user ID.
     *
     * @param userId user ID
     * @return permission list
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Integer userId) {
        List<Role> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (Role perm : perms) {
            if (perm != null) {
                permsSet.addAll(Arrays.asList(perm.getRoleCode().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * Query role list by user ID.
     *
     * @param userId user ID
     * @return role IDs
     */
    @Override
    public List<Integer> selectRoleListByUserId(Integer userId) {
        return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * @param roleId
     * @return
     */
    @Override
    public List<User> getUserListByRoleId(Integer roleId) {
        List<UserRole> userRoleList =
                userRoleService.list(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, roleId));

        List<User> userList = new ArrayList<>();

        if (Asserts.isNotNull(userRoleList)) {
            userRoleList.forEach(userRole -> {
                User user = userService.getById(userRole.getUserId());
                if (Objects.nonNull(user)) {
                    userList.add(user);
                }
            });
        }
        return userList;
    }
}
