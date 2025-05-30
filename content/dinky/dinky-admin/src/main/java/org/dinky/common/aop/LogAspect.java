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

package org.dinky.common.aop;

import org.dinky.common.context.UserInfoContextHolder;
import org.dinky.data.annotations.Log;
import org.dinky.data.enums.BusinessStatus;
import org.dinky.data.model.OperateLog;
import org.dinky.data.model.rbac.User;
import org.dinky.data.result.Result;
import org.dinky.domain.log.OperateLogServiceImpl;
import org.dinky.infrastructure.utils.IpUtils;
import org.dinky.utils.JsonUtils;
import org.dinky.infrastructure.utils.ServletUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/** 操作日志记录处理 */
@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("@annotation(org.dinky.data.annotations.Log)")
    public void logPointCut() {}

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleCommonLogic(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e 异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleCommonLogic(joinPoint, e, null);
    }

    protected void handleCommonLogic(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获得注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            // 获取当前的用户
            User user = UserInfoContextHolder.get(StpUtil.getLoginIdAsInt()).getUser();

            // *========数据库日志=========*//
            OperateLog operLog = new OperateLog();
            Result<Void> result = JsonUtils.toBean(jsonResult, new TypeReference<Result<Void>>() {});
            if (result == null) {
                result = Result.failed();
            }
            operLog.setStatus(result.isSuccess() ? BusinessStatus.SUCCESS.ordinal() : BusinessStatus.FAIL.ordinal());

            // 请求的地址
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            operLog.setOperateIp(ip);
            // 返回参数
            operLog.setJsonResult(JsonUtils.toJsonString(jsonResult));

            operLog.setOperateUrl(ServletUtils.getRequest().getRequestURI());
            if (user != null) {
                operLog.setOperateName(user.getUsername());
                operLog.setOperateUserId(user.getId());
            }

            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName + "()");
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            getControllerMethodDescription(joinPoint, controllerLog, operLog);

            operLog.setOperateTime(LocalDateTime.now());

            // 保存数据库
            SpringUtil.getBean(OperateLogServiceImpl.class).saveLog(operLog);

        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("pre doAfterThrowing Exception:", exp);
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log 日志
     * @param operLog 操作日志
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, OperateLog operLog) {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setModuleName(log.title());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     */
    private void setRequestValue(JoinPoint joinPoint, OperateLog operLog) {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod)
                || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperateParam(StringUtils.substring(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap =
                    (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operLog.setOperateParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
        }
    }

    /** 是否存在注解，如果存在就获取 */
    private Log getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }

    /** 参数拼装 */
    private String argsArrayToString(Object[] paramsArray) {
        return Arrays.stream(paramsArray)
                .filter(o -> !isFilterObject(o))
                .map(JsonUtils::toJsonString)
                .collect(Collectors.joining(" "));
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    public boolean isFilterObject(final Object o) {
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }
}
