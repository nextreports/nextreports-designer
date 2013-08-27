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

import ro.nextreports.engine.util.xstream.XStreamable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 29, 2009
 * Time: 10:16:58 AM
 */
public class PersistedSchemas implements XStreamable, Serializable {

    private static final long serialVersionUID = 4050944073438262503L;

    private ArrayList<PersistedSchema> list;
    private String version;
    
    public PersistedSchemas(ArrayList<PersistedSchema> list, String version) {
        this.list = list;
        this.version = version;
    }

    public ArrayList<PersistedSchema> getList() {
        return list;
    }

    public void setList(ArrayList<PersistedSchema> list) {
        this.list = list;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersistedSchemas that = (PersistedSchemas) o;

        if (list != null ? !list.equals(that.list) : that.list != null) return false;

        return true;
    }

    public int hashCode() {
        return (list != null ? list.hashCode() : 0);
    }
}
