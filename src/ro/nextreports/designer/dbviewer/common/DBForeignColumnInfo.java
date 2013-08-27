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

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 26, 2006
 * Time: 2:24:17 PM
 */
public class DBForeignColumnInfo implements Serializable {

    private static final long serialVersionUID = 4334378045857150199L;

    private String fkSchema;
    private String fkTable;
    private String fkColumn;
    private String pkSchema;
    private String pkTable;
    private String pkColumn;
    
    public DBForeignColumnInfo(String fkSchema, String fkTable, String fkColumn,
                               String pkSchema, String pkTable, String pkColumn) {
        this.fkSchema = fkSchema;
        this.fkTable = fkTable;
        this.fkColumn = fkColumn;
        this.pkSchema = pkSchema;
        this.pkTable = pkTable;
        this.pkColumn = pkColumn;
    }

    public String getFkTable() {
        return fkTable;
    }

    public String getFkColumn() {
        return fkColumn;
    }

    public String getPkTable() {
        return pkTable;
    }

    public String getPkColumn() {
        return pkColumn;
    }

    public String getFkSchema() {
        return fkSchema;
    }

    public String getPkSchema() {
        return pkSchema;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBForeignColumnInfo that = (DBForeignColumnInfo) o;

        if (fkColumn != null ? !fkColumn.equals(that.fkColumn) : that.fkColumn != null) return false;
        if (fkSchema != null ? !fkSchema.equals(that.fkSchema) : that.fkSchema != null) return false;
        if (fkTable != null ? !fkTable.equals(that.fkTable) : that.fkTable != null) return false;
        if (pkColumn != null ? !pkColumn.equals(that.pkColumn) : that.pkColumn != null) return false;
        if (pkSchema != null ? !pkSchema.equals(that.pkSchema) : that.pkSchema != null) return false;
        if (pkTable != null ? !pkTable.equals(that.pkTable) : that.pkTable != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (fkSchema != null ? fkSchema.hashCode() : 0);
        result = 31 * result + (fkTable != null ? fkTable.hashCode() : 0);
        result = 31 * result + (fkColumn != null ? fkColumn.hashCode() : 0);
        result = 31 * result + (pkSchema != null ? pkSchema.hashCode() : 0);
        result = 31 * result + (pkTable != null ? pkTable.hashCode() : 0);
        result = 31 * result + (pkColumn != null ? pkColumn.hashCode() : 0);
        return result;
    }


    public String toString() {
        return "DBForeignColumnInfo{" +
                "fkSchema='" + fkSchema + '\'' +
                ", fkTable='" + fkTable + '\'' +
                ", fkColumn='" + fkColumn + '\'' +
                ", pkSchema='" + pkSchema + '\'' +
                ", pkTable='" + pkTable + '\'' +
                ", pkColumn='" + pkColumn + '\'' +
                '}';
    }
}
