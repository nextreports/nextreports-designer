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
// Time: 14:44:05

//
public class DBProcedure implements Serializable, Comparable {

    private String schema;
    private String catalog;
    private String name;
    private int resultType;

    public DBProcedure(String schema, String catalog, String name, int resultType) {
        this.schema = schema;
        this.catalog = catalog;
        this.name = name;
        this.resultType = resultType;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBProcedure that = (DBProcedure) o;

        if (resultType != that.resultType) return false;
        if (catalog != null ? !catalog.equals(that.catalog) : that.catalog != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (schema != null ? !schema.equals(that.schema) : that.schema != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (catalog != null ? catalog.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + resultType;
        return result;
    }


    public String toString() {
        return "DBProcedure{" +
                "schema='" + schema + '\'' +
                ", catalog='" + catalog + '\'' +
                ", name='" + name + '\'' +
                ", resultType=" + resultType +
                '}';
    }

    public int compareTo(Object o) throws ClassCastException  {
        if (!(o instanceof DBProcedure)) {
            throw new ClassCastException("Object is not a DBProcedure!");
        }
        String catalog = ((DBProcedure) o).getCatalog();
        if (this.catalog != null) {
            if (catalog == null) {
                return 1;
            } else {
                if (!this.catalog.equals(catalog)) {
                    return this.catalog.compareTo(catalog);
                }
            }
        } else if (catalog != null) {
            return -1;
        }

        String name = ((DBProcedure) o).getName();
        return this.name.compareTo(name);

    }
}
