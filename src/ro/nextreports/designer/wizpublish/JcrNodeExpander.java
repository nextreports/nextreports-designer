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
package ro.nextreports.designer.wizpublish;

import ro.nextreports.server.api.client.WebServiceClient;
import ro.nextreports.server.api.client.EntityMetaData;

import java.util.List;
import java.util.ArrayList;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Sep-2009
// Time: 17:27:10

import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.tree.NodeExpander;
import ro.nextreports.designer.util.I18NSupport;

//
public class JcrNodeExpander implements NodeExpander {

    private WebServiceClient client;

    public static String REPORTS = I18NSupport.getString("node.reports");
    public static String CHARTS = I18NSupport.getString("node.charts");
    public static String SOURCES  = I18NSupport.getString("node.connections");

    public static String REPORTS_ROOT = "/reports";
    public static String CHARTS_ROOT = "/charts";
    public static String DATABASES_ROOT = "/dataSources";

    public JcrNodeExpander(WebServiceClient client) {
        super();
        this.client = client;
    }

    public List createChildren(DBBrowserNode parentNode) throws Exception {
        byte parentNodeType = parentNode.getDBObject().getType();
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        if (parentNodeType == DBObject.REPORTS_GROUP) {
            childNodes.addAll(createReportNodesForFolder(REPORTS_ROOT));
        } else if (parentNodeType == DBObject.CHARTS_GROUP) {
            childNodes.addAll(createReportNodesForFolder(CHARTS_ROOT));
        } else if (parentNodeType == DBObject.DATABASE) {
            childNodes.addAll(createReportNodesForFolder(DATABASES_ROOT));
        } else if (parentNodeType == DBObject.FOLDER_REPORT) {
            childNodes.addAll(createReportNodesForFolder(parentNode.getDBObject().getAbsolutePath()));
        }
        return childNodes;
    }

    private List<DBBrowserNode> createReportNodesForFolder(String folderPath) throws Exception {
        
        List<EntityMetaData> entities = client.getEntities(folderPath);
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();

        for (EntityMetaData entity : entities) {
            String name = entity.getPath().substring(entity.getPath().lastIndexOf("/") + 1);
            byte type = EntityMetaData.OTHER;
            if (entity.getType() == EntityMetaData.FOLDER) {
                type = DBObject.FOLDER_REPORT;
            } else if (entity.getType() == EntityMetaData.DATA_SOURCE) {
                type = DBObject.DATASOURCE;
            } else if (entity.getType() == EntityMetaData.NEXT_REPORT) {
                type = DBObject.REPORTS;
            } else if (entity.getType() == EntityMetaData.CHART) {
                type = DBObject.CHARTS;
            }
            if (type != EntityMetaData.OTHER) {
                DBObject obj = new DBObject(name, null, type);
                obj.setAbsolutePath(entity.getPath());
                childNodes.add(new DBBrowserNode(obj));
            }
        }
        return childNodes;
    }

}
