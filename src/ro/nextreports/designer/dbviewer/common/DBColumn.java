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
 * Date: Apr 4, 2006
 * Time: 3:42:57 PM
 */
public class DBColumn implements Serializable, Comparable {

    private static final long serialVersionUID = 5456492747880137425L;

    private String schema;
    private String table;
    private String name;
    private String type;
    private boolean isPrimaryKey;
    private boolean isForeignKey;
    private DBForeignColumnInfo fkInfo;
    private int length;
    private int precision;
    private int scale;

    public DBColumn(String schema, String table, String name, String type, boolean isPrimaryKey, boolean isForeignKey,
                    DBForeignColumnInfo fkInfo, int length, int precision, int scale) {
        this.schema = schema;
        this.table = table;
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
        this.fkInfo = fkInfo;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
    }

    public String getSchema() {
        return schema;
    }

    public String getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    public DBForeignColumnInfo getFkInfo() {
        return fkInfo;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBColumn dbColumn = (DBColumn) o;

        if (name != null ? !name.equals(dbColumn.name) : dbColumn.name != null) return false;
        if (schema != null ? !schema.equals(dbColumn.schema) : dbColumn.schema != null) return false;
        if (table != null ? !table.equals(dbColumn.table) : dbColumn.table != null) return false;
        if (type != null ? !type.equals(dbColumn.type) : dbColumn.type != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "DBColumn{" +
                "schema='" + schema + '\'' +
                ", table='" + table + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isPrimaryKey=" + isPrimaryKey +
                ", isForeignKey=" + isForeignKey +
                ", fkInfo=" + fkInfo +
                ", length=" + length +
                ", precision=" + precision +
                ", scale=" + scale +
                '}';
    }

    public int compareTo(Object o) throws ClassCastException  {
        if (!(o instanceof DBColumn)) {
            throw new ClassCastException("Object is not a DBColumn!");
        }
        String name = ((DBColumn) o).getName();
        return this.name.compareTo(name);

    }
}
