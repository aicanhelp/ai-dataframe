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

package org.dinky.common.sse.git;

import org.dinky.data.enums.Status;
import org.dinky.data.model.GitProject;
import org.dinky.common.sse.StepSse;
import org.dinky.infrastructure.utils.MavenUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
public class HeadStepSse extends StepSse {
    public HeadStepSse(
            int sleep,
            List<SseEmitter> emitterList,
            Dict params,
            AtomicInteger msgId,
            AtomicInteger stepAtomic,
            ExecutorService cachedThreadPool) {
        super("checkEnv", sleep, emitterList, params, msgId, stepAtomic, cachedThreadPool);
    }

    @Override
    public void exec() {
        checkEnv();
    }

    private void checkEnv() {
        GitProject gitProject = (GitProject) params.get("gitProject");
        switch (gitProject.getCodeType()) {
            case 1:
                checkJava();
                break;
            case 2:
                checkPython();
        }
    }

    private void checkJava() {
        String mavenHome = MavenUtil.getMavenHome();

        if (StrUtil.isBlank(mavenHome)) {
            addFileMsgLog(Status.GIT_MAVEN_HOME_NOT_SET.getMessage());
            setFinish(false);
        }
        String mavenVersionMsg = MavenUtil.getMavenVersion();
        mavenVersionMsg += "Your Maven Home is: " + mavenHome;
        addFileMsgLog(mavenVersionMsg);
    }

    private void checkPython() {}
}
