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
package ro.nextreports.designer.datasource;

import java.util.List;
import java.io.Serializable;

import ro.nextreports.engine.util.xstream.XStreamable;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 17, 2008
 * Time: 3:32:11 PM
 */
public class PersistedSchema implements XStreamable, Serializable {

    private static final long serialVersionUID = -5980456305821446434L;

    private String dbName;
    private List<String> schemas;
    
    public PersistedSchema(){
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersistedSchema that = (PersistedSchema) o;

        if (dbName != null ? !dbName.equals(that.dbName) : that.dbName != null) return false;
        if (schemas != null ? !schemas.equals(that.schemas) : that.schemas != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (dbName != null ? dbName.hashCode() : 0);
        result = 31 * result + (schemas != null ? schemas.hashCode() : 0);
        return result;
    }


    public String toString() {
        return "PersistedSchema{" +
                "dbName='" + dbName + '\'' +
                ", schemas=" + schemas +
                '}';
    }
}
