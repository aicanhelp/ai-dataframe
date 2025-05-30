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

package org.dinky.common.interceptor;

import org.dinky.assertion.Asserts;
import org.dinky.data.constant.BaseConstant;
import org.dinky.utils.I18n;

import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.util.WebUtils;

public class LocaleChangeInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie cookie = WebUtils.getCookie(request, BaseConstant.LOCALE_LANGUAGE_COOKIE);
        if (Asserts.isNotNull(cookie)) {
            // Proceed in cookie
            LocaleContextHolder.setLocale(parseLocaleValue(cookie.getValue()));
            I18n.setLocale(LocaleContextHolder.getLocale());
        }
        // Proceed in header
        String newLocale = request.getHeader(BaseConstant.LOCALE_LANGUAGE_COOKIE);
        if (Asserts.isNotNull(newLocale)) {
            LocaleContextHolder.setLocale(parseLocaleValue(newLocale));
            I18n.setLocale(LocaleContextHolder.getLocale());
        }
        return true;
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }
}
