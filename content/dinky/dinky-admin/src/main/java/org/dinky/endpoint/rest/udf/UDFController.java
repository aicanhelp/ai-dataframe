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

import org.dinky.data.dto.CommonDTO;
import org.dinky.data.model.Resources;
import org.dinky.data.model.udf.UDFManage;
import org.dinky.data.result.Result;
import org.dinky.data.vo.CascaderVO;
import org.dinky.data.vo.UDFManageVO;
import org.dinky.function.data.model.UDF;
import org.dinky.domain.task.TaskService;
import org.dinky.domain.udf.UDFService;
import org.dinky.infrastructure.utils.UDFUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Flink udf controller
 */
@Slf4j
@Api(tags = "UDF Controller")
@RestController
@RequestMapping("/api/udf")
@SaCheckLogin
@RequiredArgsConstructor
public class UDFController {
    private final UDFService udfService;
    private final TaskService taskService;

    /**
     * update udf name by id
     *
     * @return Result
     */
    @GetMapping("/list")
    public Result<List<UDFManageVO>> list() {
        return Result.succeed(udfService.selectAll());
    }

    /**
     * update udf
     *
     * @param udfManage udfManage
     * @return Result
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody UDFManage udfManage) {
        udfService.update(udfManage);
        return Result.succeed();
    }

    /**
     * get udf resources list
     *
     * @return Result
     */
    @GetMapping("/udfResourcesList")
    public Result<List<Resources>> udfResourcesList() {
        return Result.succeed(udfService.udfResourcesList());
    }

    /**
     * add or update by resource id
     *
     * @param dto dto
     * @return Result
     */
    @PostMapping("/addOrUpdateByResourceId")
    public Result<Void> addOrUpdateByResourceId(@RequestBody CommonDTO<List<Integer>> dto) {
        udfService.addOrUpdateByResourceId(dto.getData());
        return Result.succeed();
    }

    /**
     * get all udf and convert its to cascader
     * @return {@link Result} of {@link List} of {@link CascaderVO}
     */
    @GetMapping("/getAllUdfs")
    @ApiOperation("Get All UDFs")
    public Result<List<CascaderVO>> getAllUdfsToCascader() {
        // get all UDFs of dynamic UDFs(user defined UDFs in the task)
        List<UDF> userDefinedReleaseUdfs =
                taskService.getReleaseUDF().stream().map(UDFUtils::taskToUDF).collect(Collectors.toList());
        return Result.succeed(udfService.getAllUdfsToCascader(userDefinedReleaseUdfs));
    }
}
