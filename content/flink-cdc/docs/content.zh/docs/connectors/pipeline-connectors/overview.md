---
title: "概览"
weight: 1
type: docs
aliases:
  - /connectors/pipeline-connectors/overview
---
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

# Pipeline Connectors

Flink CDC 提供了可用于 YAML 作业的 Pipeline Source 和 Sink 连接器来与外部系统交互。您可以直接使用这些连接器，只需将 JAR 文件添加到您的 Flink CDC 环境中，并在您的 YAML Pipeline 定义中指定所需的连接器。

## Supported Connectors

| 连接器                                                                      | 类型     | 支持的外部系统                                                                                                                                                                                                                                                                                                                                                                                                | 
|--------------------------------------------------------------------------|--------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Apache Doris]({{< ref "docs/connectors/pipeline-connectors/doris" >}})  | Sink   | <li> [Apache Doris](https://doris.apache.org/): 1.2.x, 2.x.x                                                                                                                                                                                                                                                                                                                                           | 
| [Elasticsearch]({{< ref "docs/connectors/pipeline-connectors/elasticsearch" >}}) | Sink           | <li> [Elasticsearch](https://www.elastic.co/elasticsearch): 6.x, 7.x, 8.x                                                                                                                                                                                                                                                                                                                              |
| [Kafka]({{< ref "docs/connectors/pipeline-connectors/kafka" >}})         | Sink   | <li> [Kafka](https://kafka.apache.org/)                                                                                                                                                                                                                                                                                                                                                                | 
| [MySQL]({{< ref "docs/connectors/pipeline-connectors/mysql" >}})         | Source | <li> [MySQL](https://dev.mysql.com/doc): 5.6, 5.7, 8.0.x <li> [RDS MySQL](https://www.aliyun.com/product/rds/mysql): 5.6, 5.7, 8.0.x <li> [PolarDB MySQL](https://www.aliyun.com/product/polardb): 5.6, 5.7, 8.0.x <li> [Aurora MySQL](https://aws.amazon.com/cn/rds/aurora): 5.6, 5.7, 8.0.x <li> [MariaDB](https://mariadb.org): 10.x <li> [PolarDB X](https://github.com/ApsaraDB/galaxysql): 2.0.1 | 
| [Paimon]({{< ref "docs/connectors/pipeline-connectors/paimon" >}})       | Sink   | <li> [Paimon](https://paimon.apache.org/): 0.6, 0.7, 0.8, 0.9                                                                                                                                                                                                                                                                                                                                                         |
| [StarRocks]({{< ref "docs/connectors/pipeline-connectors/starrocks" >}}) | Sink   | <li> [StarRocks](https://www.starrocks.io/): 2.x, 3.x                                                                                                                                                                                                                                                                                                                                                  |
| [OceanBase]({{< ref "docs/connectors/pipeline-connectors/oceanbase" >}}) | Sink   | <li> [OceanBase](https://www.oceanbase.com/): 3.x, 4.x                                                                                                                                                                                                                                                                                                                                                 |

## Supported Flink Versions
下表展示了 Flink<sup>®</sup> CDC 管道连接器和 Flink<sup>®</sup> 之间的版本映射

|    Flink<sup>®</sup> CDC Version    |                                                        Flink<sup>®</sup> Version                                                         | Pipeline Source |               Pipeline Sink                | 备注                                |
|:-----------------------------------:|:----------------------------------------------------------------------------------------------------------------------------------------:|:---------------:|:------------------------------------------:|:----------------------------------|
| <font color="DarkCyan">3.0.x</font> |                        <font color="MediumVioletRed">1.17.\*</font>, <font color="MediumVioletRed">1.18.\*</font>                        |      Mysql      |              StarRocks,Doris               |                                   |
| <font color="DarkCyan">3.1.x</font> | <font color="MediumVioletRed">1.17.\*</font>, <font color="MediumVioletRed">1.18.\*</font>, <font color="MediumVioletRed">1.19.\*</font> |      Mysql      |        StarRocks,Doris,Paimon,Kafka        | 仅 flink-cdc 3.1.1 支持 flink 1.19.* |
| <font color="DarkCyan">3.2.x</font> | <font color="MediumVioletRed">1.17.\*</font>, <font color="MediumVioletRed">1.18.\*</font>, <font color="MediumVioletRed">1.19.\*</font> |      Mysql      | StarRocks,Doris,Paimon,Kafka,ElasticSearch |                                   |
| <font color="DarkCyan">3.3.x</font> |                        <font color="MediumVioletRed">1.19.\*</font>, <font color="MediumVioletRed">1.20.\*</font>                        |      Mysql      | StarRocks,Doris,Paimon,Kafka,ElasticSearch,OceanBase,MaxCompute |                                   |


## Develop Your Own Connector

如果现有的连接器无法满足您的需求，您可以自行开发自己的连接器，以将您的外部系统集成到 Flink CDC 数据管道中。查阅 [Flink CDC APIs]({{< ref "docs/developer-guide/understand-flink-cdc-api" >}}) 了解如何开发您自己的连接器。

{{< top >}}
