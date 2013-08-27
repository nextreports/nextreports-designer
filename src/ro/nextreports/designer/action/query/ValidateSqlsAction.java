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
package ro.nextreports.designer.action.query;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

import ro.nextreports.engine.Report;

public class ValidateSqlsAction extends AbstractAction {

    public static String VALID_SQL_PROPERTY = "VALID_SQL";
    private DBObject sqlObject;
    private ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(Globals.getReportPersistenceType());
    private boolean multiple;

    public ValidateSqlsAction(DBObject sqlObject) {
        String text;
        
        multiple = (sqlObject.getType() == DBObject.REPORTS_GROUP) || 
				(sqlObject.getType() == DBObject.QUERIES_GROUP) ||
				(sqlObject.getType() == DBObject.CHARTS_GROUP) ||
				(sqlObject.getType() == DBObject.FOLDER_QUERY) ||
				(sqlObject.getType() == DBObject.FOLDER_REPORT) ||
				(sqlObject.getType() == DBObject.FOLDER_CHART); 
        
        if (multiple) {
            text = I18NSupport.getString("sql.validation");
        } else {
            text = I18NSupport.getString("sql.validation.single");
        }
        putValue(Action.NAME, text);
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("sql_validation"));       
        putValue(Action.SHORT_DESCRIPTION, text);
        putValue(Action.LONG_DESCRIPTION, text);
        this.sqlObject = sqlObject;
    }

    public void actionPerformed(ActionEvent e) {

        Thread executorThread = new Thread(new Runnable() {

            public void run() {            	            	

                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("sql.validation"));
                activator.start();                                                                               
                
                if (!multiple) {
                	try {
                		testSingleValidation(sqlObject, true);
                    } finally {
                        if (activator != null) {
                            activator.stop();
                        }
                    }

                // validate all sqls
                } else {
                    try {
                        DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();                                                
                        DBBrowserNode node;
                        if ((sqlObject.getType() == DBObject.REPORTS_GROUP) || 
                        		(sqlObject.getType() == DBObject.QUERIES_GROUP) ||
                        		(sqlObject.getType() == DBObject.CHARTS_GROUP) ) {
                        	node = tree.searchNode(sqlObject.getName());
                        } else {
                        	node = tree.searchNode(sqlObject.getName(), sqlObject.getAbsolutePath(), sqlObject.getType());
                        }                                                                                           
                        testMultipleValidation(node, tree);
                    } finally {
                        if (activator != null) {
                            activator.stop();
                        }
                    }
                }
            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }
    
    private void testMultipleValidation(DBBrowserNode node, DBBrowserTree tree) {
    	if (node.getChildCount() == 0) {
            tree.startExpandingTree(node, false, null);
        }  
    	for (int i = 0, size = node.getChildCount(); i < size; i++) {
            DBBrowserNode child = (DBBrowserNode) node.getChildAt(i);
            DBObject object = child.getDBObject();
            if ((object.getType() == DBObject.FOLDER_QUERY) ||
				(object.getType() == DBObject.FOLDER_REPORT) ||
				(object.getType() == DBObject.FOLDER_CHART)) {
            	testMultipleValidation(child, tree);
            } else {
            	testSingleValidation(object, false);
            }
        }
    }
    
    private void testSingleValidation(DBObject obj, boolean showMessage) {
    	Report report = null;    	
		if (obj.getType() == DBObject.QUERIES) {
			report = repPersist.loadReport(obj.getAbsolutePath());
		} else if (obj.getType() == DBObject.REPORTS) {
			report = FormLoader.getInstance().load(obj.getAbsolutePath());
		} else if (obj.getType() == DBObject.CHARTS) {
			report = ChartUtil.loadChart(obj.getAbsolutePath()).getReport();
		} 
	    if (report != null) {
	    	if (Globals.getDBViewer().isValidSql(report)) {
	    		obj.putProperty(VALID_SQL_PROPERTY, true);
	    		if (showMessage) {
	    			Show.info(I18NSupport.getString("sql.valid"));
	    		}
	    	} else {
	    		obj.putProperty(VALID_SQL_PROPERTY, false);
	    		if (showMessage) {
	    			Show.warning(I18NSupport.getString("sql.invalid"));
	    		}
	    	}
	    }
    }
}
