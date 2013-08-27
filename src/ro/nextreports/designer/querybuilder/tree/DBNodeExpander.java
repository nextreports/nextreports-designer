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
package ro.nextreports.designer.querybuilder.tree;

import java.text.Collator;
import java.util.*;
import java.io.File;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.DefaultSchemaManager;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;
import ro.nextreports.designer.dbviewer.common.DBInfo;
import ro.nextreports.designer.dbviewer.common.DBProcedure;
import ro.nextreports.designer.dbviewer.common.DBTable;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.LicenseUtil;


/**
 * @author Decebal Suiu
 */
public class DBNodeExpander implements NodeExpander {

    public static String CONNECTIONS = I18NSupport.getString("node.connections");
    public static String TABLES = I18NSupport.getString("node.tables");
    public static String VIEWS = I18NSupport.getString("table.views");
    public static String QUERIES = I18NSupport.getString("node.queries");
    public static String REPORTS = I18NSupport.getString("node.reports");
    public static String PROCEDURES = I18NSupport.getString("node.procedures");
    public static String CHARTS = I18NSupport.getString("node.charts");

    public DBNodeExpander() {
		super();
	}

	public List createChildren(DBBrowserNode parentNode) throws Exception {
	    boolean onlyDefaultSchema = false;
	    byte parentNodeType = parentNode.getDBObject().getType();
	    List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();

        DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();

        if (parentNodeType == DBObject.DATASOURCE) {            
            childNodes.addAll(createConnectionNodes());
        } else if (parentNodeType == DBObject.DATABASE) {
            if ((ds != null) &&  parentNode.getUserObject().equals(ds.getName()))    {
                if (onlyDefaultSchema) {
                    childNodes.add(createSchemaNode());
                } else {
                    childNodes.addAll(createSchemaNodes((String)parentNode.getUserObject(), onlyDefaultSchema));
                }
                childNodes.add(createQueriesNode(parentNode.getDBObject().getName()));
                childNodes.add(createReportsNode(parentNode.getDBObject().getName()));
                childNodes.add(createChartsNode(parentNode.getDBObject().getName()));
            }
        } else if (parentNodeType == DBObject.SCHEMA) {
            childNodes.add(createTablesNode(parentNode.getDBObject().getName()));
            childNodes.add(createViewsNode(parentNode.getDBObject().getName()));
            childNodes.add(createProceduresNode(parentNode.getDBObject().getName()));
        } else if (parentNodeType == DBObject.TABLES_GROUP) {
            childNodes.addAll(createTableNodes(parentNode.getDBObject().getSchemaName()));
        } else if (parentNodeType == DBObject.VIEWS_GROUP) {
            childNodes.addAll(createViewNodes(parentNode.getDBObject().getSchemaName()));
        } else if (parentNodeType == DBObject.PROCEDURES_GROUP) {
            childNodes.addAll(createProcedureNodes(parentNode.getDBObject().getSchemaName()));
        } else if (parentNodeType == DBObject.QUERIES_GROUP) {
            if (ds != null) {
                childNodes.addAll(createQueryNodes());
            }
        } else if (parentNodeType == DBObject.REPORTS_GROUP) {
            if (ds != null) {
                childNodes.addAll(createReportNodes());
            }
        } else if (parentNodeType == DBObject.CHARTS_GROUP) {
            if (ds != null) {
                childNodes.addAll(createChartNodes());
            }
        } else if (parentNodeType == DBObject.FOLDER_QUERY) {
            if (ds != null) {
                childNodes.addAll(createQueryNodesForFolder(parentNode.getDBObject().getAbsolutePath()));
            }
        } else if (parentNodeType == DBObject.FOLDER_REPORT) {
            if (ds != null) {
                childNodes.addAll(createReportNodesForFolder(parentNode.getDBObject().getAbsolutePath()));
            }
        } else if (parentNodeType == DBObject.FOLDER_CHART) {
            if (ds != null) {
                childNodes.addAll(createChartNodesForFolder(parentNode.getDBObject().getAbsolutePath()));
            }
        }
	    return childNodes;
	}

    public static String getNodeExpanderName(byte nodeType) {
        switch (nodeType) {
            case DBObject.DATASOURCE :
                return CONNECTIONS;
            case DBObject.TABLE :
                return TABLES;
            case DBObject.VIEW :
                return VIEWS;
            case DBObject.QUERIES :
                return QUERIES;
            case DBObject.REPORTS :
                return REPORTS;
            case DBObject.CHARTS :
                return CHARTS;
            case DBObject.PROCEDURES :
                return PROCEDURES;
            default :
                return CONNECTIONS;
        }
    }

    private DBBrowserNode createTablesNode(String schemaName) {
        return new DBBrowserNode(new DBObject(TABLES, schemaName, DBObject.TABLES_GROUP));
    }

    private DBBrowserNode createViewsNode(String schemaName) {
	    return new DBBrowserNode(new DBObject(VIEWS, schemaName, DBObject.VIEWS_GROUP));
	}

    private DBBrowserNode createQueriesNode(String schemaName) {
        return new DBBrowserNode(new DBObject(QUERIES, schemaName, DBObject.QUERIES_GROUP));
    }

    private DBBrowserNode createReportsNode(String schemaName) {
        return new DBBrowserNode(new DBObject(REPORTS, schemaName, DBObject.REPORTS_GROUP));
    }

    private DBBrowserNode createChartsNode(String schemaName) {
        return new DBBrowserNode(new DBObject(CHARTS, schemaName, DBObject.CHARTS_GROUP));
    }

    private DBBrowserNode createProceduresNode(String schemaName) {
	    return new DBBrowserNode(new DBObject(PROCEDURES, schemaName, DBObject.PROCEDURES_GROUP));
	}

    private List<DBBrowserNode> createSchemaNodes(String dbName, boolean onlyDefaultSchema) throws Exception {
        List<String> list = Globals.getDBViewer().getSchemas();
        SortedSet<String> schemas = new TreeSet<String>(list);
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        String schemaName = Globals.getDBViewer().getUserSchema();
        DefaultSchemaManager  schemaManager = DefaultSchemaManager.getInstance();
        for (String schema : schemas) {
            if (schemaManager.isVisible(dbName, schema)) {
                String info = "";
                if (schema.equals(schemaName)) {
                    info = " (" + I18NSupport.getString("schema.default") + ")";
                }
                DBObject object = new DBObject(schema, schema, DBObject.SCHEMA);
                object.setInfo(info);
                childNodes.add(new DBBrowserNode(object));
            }
        }
        if (childNodes.size() == 0) {
            // we may have no schema , but user exists allways
            boolean found = false;
            for (String schema : schemas) {
                if (schema.equals(schemaName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
               schemaName = DefaultDBViewer.NO_SCHEMA_NAME;
           }
           DBObject object = new DBObject(schemaName, schemaName, DBObject.SCHEMA);
           object.setInfo(" (" + I18NSupport.getString("schema.default") + ")");
           childNodes.add(new DBBrowserNode(object));
        }
        return childNodes;
	}

    private List<DBBrowserNode> createConnectionNodes() {
        List<DBBrowserNode> list = new ArrayList<DBBrowserNode>();
        //String schemaName = Globals.getDBViewer().getUserSchema();
        List<DataSource> sources = DefaultDataSourceManager.getInstance().getDataSources();
        Collections.sort(sources, new Comparator<DataSource>() {
			public int compare(DataSource o1, DataSource o2) {
				return Collator.getInstance().compare(o1.getName(), o2.getName());
			}        	
        });
        if (sources != null) {
            if (LicenseUtil.maxDataSourcesReached()) {
                return list;
            }
            for (DataSource source :sources) {
                DBBrowserNode node =  new DBBrowserNode(new DBObject(source.getName(), null, DBObject.DATABASE));
                list.add(node);
            }
        }
        return list;
    }

    private DBBrowserNode createSchemaNode() throws Exception {
        String schemaName = Globals.getDBViewer().getUserSchema();
        return new DBBrowserNode(new DBObject(schemaName, schemaName, DBObject.SCHEMA));
    }

	private List<DBBrowserNode> createTableNodes(String schemaName) throws Exception {
        List<DBTable> list = Globals.getDBViewer().getDBInfo(schemaName, DBInfo.TABLES).getTables();
        Collections.sort(list);
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        for (DBTable table : list) {
            childNodes.add(new DBBrowserNode(new DBObject(table.getName(), schemaName, DBObject.TABLE)));
        }
        return childNodes;
	}

	private List<DBBrowserNode> createViewNodes(String schemaName) throws Exception {
        List<DBTable> list = Globals.getDBViewer().getDBInfo(schemaName, DBInfo.VIEWS).getTables();
        Collections.sort(list);
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        for (DBTable table : list) {
            childNodes.add(new DBBrowserNode(new DBObject(table.getName(), schemaName, DBObject.VIEW)));
        }
        return childNodes;
	}

    private List<DBBrowserNode> createProcedureNodes(String schemaName) throws Exception {
        List<DBProcedure> list = Globals.getDBViewer().getDBInfo(schemaName, DBInfo.PROCEDURES).getProcedures();
        Collections.sort(list);
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        for (DBProcedure proc : list) {
            DBObject obj = new DBObject(proc.getName(), schemaName, DBObject.PROCEDURES);
            obj.setCatalog(proc.getCatalog());
            childNodes.add(new DBBrowserNode(obj, true));            
        }
        return childNodes;
    }


    private List<DBBrowserNode> createQueryNodes() throws Exception {
        return createQueryNodesForFolder(FileReportPersistence.getQueriesRelativePath());
    }

    private List<DBBrowserNode> createQueryNodesForFolder(String folderPath) throws Exception {
        ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                Globals.getReportPersistenceType());
        String schemaName = Globals.getDBViewer().getUserSchema();
        File root = new File(folderPath);
        List<File> list = repPersist.getReportFiles(root.getAbsolutePath());
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        for (File file : list) {            
            if (file.isDirectory()) {
                DBObject obj = new DBObject(file.getName(), schemaName, DBObject.FOLDER_QUERY);
                obj.setAbsolutePath(file.getAbsolutePath());
                childNodes.add(new DBBrowserNode(obj));
            } else {
                int index  = file.getName().indexOf(FileReportPersistence.REPORT_EXTENSION_SEPARATOR + FileReportPersistence.REPORT_EXTENSION);
                if (index != -1) {
                    DBObject obj = new DBObject(file.getName().substring(0, index), schemaName, DBObject.QUERIES);
                    obj.setAbsolutePath(file.getAbsolutePath());
                    childNodes.add(new DBBrowserNode(obj));
                }
            }
        }
        return childNodes;
    }

    private List<DBBrowserNode> createReportNodes() throws Exception {
        return createReportNodesForFolder(FileReportPersistence.getReportsRelativePath());
    }

    private List<DBBrowserNode> createReportNodesForFolder(String folderPath) throws Exception {

        String schemaName = Globals.getDBViewer().getUserSchema();
        File root = new File(folderPath);
        List<File> list = FormLoader.getInstance().getFiles(root.getAbsolutePath(), FormSaver.REPORT_FULL_EXTENSION);
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        if (LicenseUtil.maxReportsReached()) {
            return childNodes;
        }
        for (File file : list) {
            if (file.isDirectory() && !FileReportPersistence.SUBREPORT_TEMP_DIR.equals(file.getName())) {
                DBObject obj = new DBObject(file.getName(), schemaName, DBObject.FOLDER_REPORT);
                obj.setAbsolutePath(file.getAbsolutePath());
                childNodes.add(new DBBrowserNode(obj));
            } else {
                int index  = file.getName().indexOf(FormSaver.REPORT_FULL_EXTENSION);
                if (index != -1) {
                    DBObject obj = new DBObject(file.getName().substring(0, index), schemaName, DBObject.REPORTS);
                    obj.setAbsolutePath(file.getAbsolutePath());
                    if (childNodes.size() < Globals.getReports()) {
                        childNodes.add(new DBBrowserNode(obj));
                    }
                }
            }
        }
        return childNodes;
    }

    private List<DBBrowserNode> createChartNodes() throws Exception {
        return createChartNodesForFolder(FileReportPersistence.getChartsRelativePath());
    }

    private List<DBBrowserNode> createChartNodesForFolder(String folderPath) throws Exception {

        String schemaName = Globals.getDBViewer().getUserSchema();
        File root = new File(folderPath);
        List<File> list = FormLoader.getInstance().getFiles(root.getAbsolutePath(), ChartUtil.CHART_FULL_EXTENSION);
        List<DBBrowserNode> childNodes = new ArrayList<DBBrowserNode>();
        if (LicenseUtil.maxReportsReached()) {
            return childNodes;
        }
        for (File file : list) {
            if (file.isDirectory() && !FileReportPersistence.SUBREPORT_TEMP_DIR.equals(file.getName())) {
                DBObject obj = new DBObject(file.getName(), schemaName, DBObject.FOLDER_CHART);
                obj.setAbsolutePath(file.getAbsolutePath());
                childNodes.add(new DBBrowserNode(obj));
            } else {
                int index  = file.getName().indexOf(ChartUtil.CHART_FULL_EXTENSION);
                if (index != -1) {
                    DBObject obj = new DBObject(file.getName().substring(0, index), schemaName, DBObject.CHARTS);
                    obj.setAbsolutePath(file.getAbsolutePath());
                    if (childNodes.size() < Globals.getReports()) {
                        childNodes.add(new DBBrowserNode(obj));
                    }
                }
            }
        }
        return childNodes;
    }

    // for internationalization
    public static void fetchNodeNames() {
        CONNECTIONS = I18NSupport.getString("node.connections");
        TABLES = I18NSupport.getString("node.tables");
        VIEWS = I18NSupport.getString("table.views");
        QUERIES = I18NSupport.getString("node.queries");
        REPORTS = I18NSupport.getString("node.reports");
        PROCEDURES = I18NSupport.getString("node.procedures");
        CHARTS = I18NSupport.getString("node.charts");
    }


}

