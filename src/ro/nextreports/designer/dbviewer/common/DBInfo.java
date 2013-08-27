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
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 4, 2006
 * Time: 3:17:47 PM
 */
public class DBInfo implements Serializable {

    private static final long serialVersionUID = 1362345191714837204L;

    public static final int INFO = 1;
    public static final int TABLES = 2;
    public static final int VIEWS = 4;
    public static final int INDEXES = 8;
    public static final int SUPPORTED_KEYWORDS = 16;
    public static final int PROCEDURES = 32;

    private String info = "";
    private List<DBTable> tables = new ArrayList<DBTable>();
    private List<DBProcedure> procedures = new ArrayList<DBProcedure>();
    private List keywords = new ArrayList();

    public DBInfo(String info, List<DBTable> tables, List<DBProcedure> procedures, List keywords) {
        this.info = info;
        this.tables = tables;
        this.procedures = procedures;
        this.keywords = keywords;
    }

    public String getInfo() {
        return info;
    }

    public List<DBTable> getTables() {
        return tables;
    }

    public List<DBProcedure> getProcedures() {
        return procedures;
    }

    public List getKeywords() {
        return keywords;
    }

    public String toString() {
        return "DBInfo{" +
                "info='" + info + '\'' +
                ", tables=" + tables +
                ", procedures=" + procedures +
                ", keywords=" + keywords +
                '}';
    }
}
