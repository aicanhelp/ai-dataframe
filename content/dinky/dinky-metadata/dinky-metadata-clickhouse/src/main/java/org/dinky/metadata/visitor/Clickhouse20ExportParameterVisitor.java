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

package org.dinky.metadata.visitor;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.visitor.ExportParameterVisitor;
import com.alibaba.druid.sql.visitor.ExportParameterVisitorUtils;

public class Clickhouse20ExportParameterVisitor extends Clickhouse20OutputVisitor implements ExportParameterVisitor {

    /** true= if require parameterized sql output */
    private final boolean requireParameterizedOutput;

    public Clickhouse20ExportParameterVisitor(
            final List<Object> parameters, final StringBuilder appender, final boolean wantParameterizedOutput) {
        super(appender, true);
        this.parameters = parameters;
        this.requireParameterizedOutput = wantParameterizedOutput;
    }

    public Clickhouse20ExportParameterVisitor() {
        this(new ArrayList<Object>());
    }

    public Clickhouse20ExportParameterVisitor(final List<Object> parameters) {
        this(parameters, new StringBuilder(), false);
    }

    public Clickhouse20ExportParameterVisitor(final StringBuilder appender) {
        this(new ArrayList<Object>(), appender, true);
    }

    public List<Object> getParameters() {
        return parameters;
    }

    @Override
    public boolean visit(SQLSelectItem x) {
        if (requireParameterizedOutput) {
            return super.visit(x);
        }
        return false;
    }

    @Override
    public boolean visit(SQLOrderBy x) {
        if (requireParameterizedOutput) {
            return super.visit(x);
        }
        return false;
    }

    @Override
    public boolean visit(SQLSelectGroupByClause x) {
        if (requireParameterizedOutput) {
            return super.visit(x);
        }
        return false;
    }

    @Override
    public boolean visit(SQLMethodInvokeExpr x) {
        if (requireParameterizedOutput) {
            return super.visit(x);
        }
        ExportParameterVisitorUtils.exportParamterAndAccept(this.parameters, x.getArguments());

        return true;
    }

    @Override
    public boolean visit(SQLInListExpr x) {
        if (requireParameterizedOutput) {
            return super.visit(x);
        }
        ExportParameterVisitorUtils.exportParamterAndAccept(this.parameters, x.getTargetList());

        return true;
    }

    @Override
    public boolean visit(SQLBetweenExpr x) {
        if (requireParameterizedOutput) {
            return super.visit(x);
        }
        ExportParameterVisitorUtils.exportParameter(this.parameters, x);
        return true;
    }

    public boolean visit(SQLBinaryOpExpr x) {
        if (requireParameterizedOutput) {
            return super.visit(x);
        }
        ExportParameterVisitorUtils.exportParameter(this.parameters, x);
        return true;
    }
}
