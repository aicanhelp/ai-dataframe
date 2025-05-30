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

package org.dinky.infrastructure.utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Safes Tool
 *
 * @since 2024/4/29 14:21
 */
public class Safes {

    public static <K, V> Map<K, V> of(Map<K, V> map) {
        return Optional.ofNullable(map).orElse(Maps.newHashMap());
    }

    public static <T> List<T> of(List<T> list) {
        return Optional.ofNullable(list).orElse(Lists.newArrayList());
    }
}
