/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ro.nextreports.designer.dbviewer.common;

import java.io.Serializable;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 24-Apr-2009
// Time: 15:48:29

//
public class DBProcedureColumn implements Serializable {

    private String schema;
    private String procedure;
    private String name;
    private String returnType;
    private String dataType;
    private int length;
    private int precision;
    private int scale;

    public DBProcedureColumn(String schema, String procedure, String name, String returnType, String dataType, int length, int precision, int scale) {
        this.schema = schema;
        this.procedure = procedure;
        this.name = name;
        this.returnType = returnType;
        this.dataType = dataType;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
    }

    public String getSchema() {
        return schema;
    }

    public String getProcedure() {
        return procedure;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getDataType() {
        return dataType;
    }

    public int getLength() {
        return length;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBProcedureColumn that = (DBProcedureColumn) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (procedure != null ? !procedure.equals(that.procedure) : that.procedure != null) return false;
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (procedure != null ? procedure.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
