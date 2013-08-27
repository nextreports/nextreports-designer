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
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 4, 2006
 * Time: 3:14:56 PM
 */
public class DBTable implements Serializable, Comparable {

    private static final long serialVersionUID = 9028924720968445334L;

    private String schema;
    private String name;
    private String type;
    private List indexes;


    public DBTable(String schema, String name, String type) {
        this.schema = schema;
        this.name = name;
        this.type = type;
    }

    public DBTable(String schema, String name, String type, List indexes) {
        this.schema = schema;
        this.name = name;
        this.type = type;
        this.indexes = indexes;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List getIndexes() {
        return indexes;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBTable dbTable = (DBTable) o;

        if (indexes != null ? !indexes.equals(dbTable.indexes) : dbTable.indexes != null) return false;
        if (name != null ? !name.equals(dbTable.name) : dbTable.name != null) return false;
        if (schema != null ? !schema.equals(dbTable.schema) : dbTable.schema != null) return false;
        if (type != null ? !type.equals(dbTable.type) : dbTable.type != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (indexes != null ? indexes.hashCode() : 0);
        return result;
    }


    public String toString() {
        return "DBTable{" +
                "schema='" + schema + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", indexes=" + indexes +
                '}';
    }

    public int compareTo(Object o) throws ClassCastException  {
        if (!(o instanceof DBTable)) {
            throw new ClassCastException("Object is not a DBTable!");
        }
        String name = ((DBTable) o).getName();
        return this.name.compareTo(name);

    }
}
