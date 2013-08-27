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
package ro.nextreports.designer.querybuilder;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Decebal Suiu
 */
public class DBObject implements Serializable {

    public static final byte OTHER = 0;
    public static final byte DATASOURCE = 1;
    public static final byte DATABASE = 2;
    public static final byte SCHEMA = 3;
    public static final byte TABLES_GROUP = 4;
    public static final byte TABLE = 5;
    public static final byte VIEWS_GROUP = 6;
    public static final byte VIEW = 7;
    public static final byte COLUMN = 8;
    public static final byte QUERIES_GROUP = 9;
    public static final byte QUERIES = 10;
    public static final byte REPORTS_GROUP = 11;
    public static final byte REPORTS = 12;
    public static final byte FOLDER_QUERY = 13;
    public static final byte FOLDER_REPORT = 14;
    public static final byte PROCEDURES_GROUP = 15;
    public static final byte PROCEDURES = 16;
    public static final byte CHARTS_GROUP = 17;
    public static final byte CHARTS = 18;
    public static final byte FOLDER_CHART = 19;

    private String name;
    private String schemaName;
    private String catalog;
    private byte type;
    private Map<String, Serializable> properties = new HashMap<String, Serializable>();
    // info field which will be appended to the name in the tree view  (ex: (default) for a schema)
    private String info;
    // for reports and queries
    private String absolutePath;

    public DBObject(String name, String schemaName, byte type) {
        this.name = name;
        this.schemaName = schemaName;
        this.type = type;
        this.info = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public byte getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getParentPath() {
        if (absolutePath == null) {
            return null;
        }
        int index = absolutePath.lastIndexOf(File.separator);
        if (index == -1) {
            return absolutePath;
        } else {
            return absolutePath.substring(0, index);
        }
    }

    public boolean isFolder() {
        return  ( (getType() == FOLDER_QUERY) || (getType() == FOLDER_REPORT) || (getType() == FOLDER_CHART));
    }

    public void putProperty(String name, Serializable value) {
        properties.put(name, value);
    }

    public Serializable getProperty(String name) {
        return properties.get(name);
    }
}
