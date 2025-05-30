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

package org.dinky.data.dto;

import org.dinky.common.mybatis.annotation.Save;

import java.util.Map;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AlertInstanceDTO
 *
 * @since 2023/10/20 16:36
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "AlertInstanceDTO", description = "API Alert Instance Data Transfer Object")
public class AlertInstanceDTO {

    @NotNull(
            message = "Name cannot be null",
            groups = {Save.class})
    @ApiModelProperty(value = "Name", required = true, dataType = "String", example = "Name")
    private String name;

    @ApiModelProperty(value = "Tenant ID", required = true, dataType = "Integer", example = "1")
    private Integer tenantId;

    @ApiModelProperty(
            value = "Alert Instance Type",
            required = true,
            dataType = "String",
            example = "DingTalk",
            extensions = {
                @Extension(
                        name = "alertType-enum",
                        properties = {@ExtensionProperty(name = "values", value = "DingTalk,WeChat,Email,FeiShu,Sms")})
            })
    private String type;

    @ApiModelProperty(
            value = "Alert Instance Params",
            required = true,
            dataType = "String",
            example = "{\"webhook\":\"https://oapi.dingtalk.com/robot/send?access_token=xxxxxx\"}")
    private Map<String, Object> params;
}
