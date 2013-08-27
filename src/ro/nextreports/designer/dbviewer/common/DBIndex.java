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
 * Time: 3:32:36 PM
 */
public class DBIndex implements Serializable {

    private static final long serialVersionUID = -7920327987425667114L;

    private String indexName;
    private String columnName;

    public DBIndex(String indexName, String columnName) {
        this.indexName = indexName;
        this.columnName = columnName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DBIndex)) return false;

        final DBIndex index = (DBIndex) o;

        if (columnName != null ? !columnName.equals(index.columnName) : index.columnName != null) return false;
        if (indexName != null ? !indexName.equals(index.indexName) : index.indexName != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (indexName != null ? indexName.hashCode() : 0);
        result = 29 * result + (columnName != null ? columnName.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Index name=").append(indexName).
           append(" Column name=").append(columnName);
        return sb.toString();
    }
}
