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

package org.dinky.endpoint.rest.udf;

import org.dinky.data.annotations.Log;
import org.dinky.data.constant.PermissionConstants;
import org.dinky.data.enums.BusinessType;
import org.dinky.data.enums.Status;
import org.dinky.data.model.udf.UDFTemplate;
import org.dinky.data.result.ProTableResult;
import org.dinky.data.result.Result;
import org.dinky.domain.udf.UDFTemplateService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** UDFTemplateController */
@Slf4j
@Api(tags = "UDF Controller")
@RestController
@RequestMapping("/api/udf/template")
@SaCheckLogin
@RequiredArgsConstructor
public class UDFTemplateController {

    private final UDFTemplateService udfTemplateService;

    /**
     * build udf tree
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperation("Build UDF Tree")
    public Result<List<Object>> listUdfTemplates() {
        List<UDFTemplate> list = udfTemplateService.list();
        Map<String, Dict> one = new HashMap<>(3);
        Map<String, Dict> two = new HashMap<>(3);
        Map<String, Dict> three = new HashMap<>(3);
        Map<String, Object> result = new HashMap<>(3);
        list.forEach(t -> {
            one.putIfAbsent(
                    t.getCodeType(), Dict.create().set("label", t.getCodeType()).set("value", t.getCodeType()));
            two.putIfAbsent(
                    t.getCodeType() + t.getFunctionType(),
                    Dict.create().set("label", t.getFunctionType()).set("value", t.getFunctionType()));
            three.putIfAbsent(
                    t.getCodeType() + t.getFunctionType() + t.getId(),
                    Dict.create().set("label", t.getName()).set("value", t.getId()));
        });
        Set<String> twoKeys = two.keySet();
        Set<String> threeKeys = three.keySet();
        one.forEach((k1, v1) -> {
            result.put(k1, v1);
            ArrayList<Dict> c1 = new ArrayList<>();
            v1.put("children", c1);
            twoKeys.stream()
                    .filter(x -> x.contains(k1))
                    .map(x -> StrUtil.strip(x, k1))
                    .forEach(k2 -> {
                        Dict v2 = two.get(k1 + k2);
                        c1.add(v2);
                        ArrayList<Dict> c2 = new ArrayList<>();
                        v2.put("children", c2);
                        threeKeys.stream()
                                .filter(x -> x.contains(k1 + k2))
                                .map(x -> StrUtil.strip(x, k1 + k2))
                                .forEach(k3 -> {
                                    c2.add(three.get(k1 + k2 + k3));
                                });
                    });
        });
        return Result.succeed(CollUtil.newArrayList(result.values()));
    }

    /**
     * get udf template list
     *
     * @param params {@link JsonNode}
     * @return {@link ProTableResult} <{@link UDFTemplate}>
     */
    @PostMapping("/list")
    @ApiOperation("Get UDF Template List")
    @ApiImplicitParam(
            name = "params",
            value = "ProTable Params",
            dataType = "JsonNode",
            paramType = "body",
            required = true)
    public ProTableResult<UDFTemplate> listUdfTemplates(@RequestBody JsonNode params) {
        return udfTemplateService.selectForProTable(params);
    }

    /**
     * save or update udf template
     *
     * @param udfTemplate {@link UDFTemplate}
     * @return {@link Result} <{@link String}>
     */
    @PutMapping
    @ApiOperation("Insert or Update UDF Template")
    @Log(title = "Insert or Update UDF Template", businessType = BusinessType.INSERT_OR_UPDATE)
    @ApiImplicitParam(
            name = "udfTemplate",
            value = "UDF Template",
            dataType = "UDFTemplate",
            paramType = "body",
            required = true)
    @SaCheckPermission(
            value = {
                PermissionConstants.REGISTRATION_UDF_TEMPLATE_ADD,
                PermissionConstants.REGISTRATION_UDF_TEMPLATE_EDIT
            },
            mode = SaMode.OR)
    public Result<String> saveOrUpdateUDFTemplate(@RequestBody UDFTemplate udfTemplate) {
        return udfTemplateService.saveOrUpdate(udfTemplate)
                ? Result.succeed(Status.SAVE_SUCCESS)
                : Result.failed(Status.SAVE_FAILED);
    }

    /**
     * delete udf template by id
     *
     * @param id {@link Integer}
     * @return {@link Result} <{@link Void}>
     */
    @DeleteMapping("/delete")
    @Log(title = "Delete UDF Template By Id", businessType = BusinessType.DELETE)
    @ApiOperation("Delete UDF Template By Id")
    @ApiImplicitParam(
            name = "id",
            value = "UDF Template Id",
            dataType = "Integer",
            paramType = "query",
            required = true)
    @SaCheckPermission(PermissionConstants.REGISTRATION_UDF_TEMPLATE_DELETE)
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteUDFTemplate(@RequestParam Integer id) {
        if (udfTemplateService.removeById(id)) {
            return Result.succeed(Status.DELETE_SUCCESS);
        } else {
            return Result.failed(Status.DELETE_FAILED);
        }
    }

    @PutMapping("/enable")
    @ApiOperation("Modify UDF Template Status")
    @Log(title = "Modify UDF Template Status", businessType = BusinessType.UPDATE)
    @ApiImplicitParam(
            name = "id",
            value = "UDF Template Id",
            dataType = "Integer",
            paramType = "query",
            required = true)
    @SaCheckPermission(PermissionConstants.REGISTRATION_UDF_TEMPLATE_EDIT)
    public Result<Void> modifyUDFTemplateStatus(@RequestParam Integer id) {
        if (udfTemplateService.modifyUDFTemplateStatus(id)) {
            return Result.succeed(Status.MODIFY_SUCCESS);
        } else {
            return Result.failed(Status.MODIFY_FAILED);
        }
    }
}
