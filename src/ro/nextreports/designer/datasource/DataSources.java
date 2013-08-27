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
 * Time: 9:50:58 AM
 */
public class DataSources implements XStreamable, Serializable {

    private static final long serialVersionUID = -7953430453765464736L;

    private ArrayList<DataSource> list;
    private String version;
    
    public DataSources(ArrayList<DataSource> dataSources, String version) {
        this.list = dataSources;
        this.version = version;
    }

    public ArrayList<DataSource> getList() {
        return list;
    }

    public String getVersion() {
        return version;
    }

    public void setList(ArrayList<DataSource> list) {
        this.list = list;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSources that = (DataSources) o;

        if (list != null ? !list.equals(that.list) : that.list != null) return false;

        return true;
    }

    public int hashCode() {
        return (list != null ? list.hashCode() : 0);
    }
}
