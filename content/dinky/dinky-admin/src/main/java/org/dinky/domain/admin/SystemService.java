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

package org.dinky.domain.admin;

import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.model.ext.FileNode;

import java.util.List;
import java.util.Map;

/**
 * SystemService
 *
 * @since 2022/10/15 19:16
 */
public interface SystemService {

    /**
     * List log root dir.
     *
     * @return {@link List<FileNode>}
     */
    List<TreeNodeDTO> listLogDir();

    /**
     * readFile
     *
     * @param path
     * @return {@link String}
     */
    String readFile(String path);

    Map<String, List<String>> queryAllClassLoaderJarFiles();
}
