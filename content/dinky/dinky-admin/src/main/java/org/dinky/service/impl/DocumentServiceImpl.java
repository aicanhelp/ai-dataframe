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

package org.dinky.service.impl;

import org.dinky.assertion.Asserts;
import org.dinky.data.model.Document;
import org.dinky.infrastructure.mapper.DocumentMapper;
import org.dinky.common.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.DocumentService;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/** DocumentServiceImpl */
@Service
public class DocumentServiceImpl extends SuperServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    public List<Document> getFillAllByVersion(String version) {
        if (Asserts.isNotNullString(version)) {
            return baseMapper.selectList(
                    new QueryWrapper<Document>().eq("version", version).eq("enabled", 1));
        } else {
            return baseMapper.selectList(new QueryWrapper<Document>().eq("enabled", 1));
        }
    }

    @Override
    public Boolean modifyDocumentStatus(Integer id) {
        Document document = baseMapper.selectById(id);
        document.setEnabled(!document.getEnabled());
        return updateById(document);
    }
}
