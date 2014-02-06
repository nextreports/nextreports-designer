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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.util.StringUtil;

public class ValidateSqlsAction extends AbstractAction {

    public static String VALID_SQL_PROPERTY = "VALID_SQL";
    private DBObject sqlObject;
    private ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(Globals.getReportPersistenceType());
    private boolean multiple;
    
    private static final Log LOG = LogFactory.getLog(ValidateSqlsAction.class);

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
                        final DBBrowserTree tree = Globals.getMainFrame().getQueryBuilderPanel().getTree();                                                
                        final DBBrowserNode node;
                        if ((sqlObject.getType() == DBObject.REPORTS_GROUP) || 
                        		(sqlObject.getType() == DBObject.QUERIES_GROUP) ||
                        		(sqlObject.getType() == DBObject.CHARTS_GROUP) ) {
                        	node = tree.searchNode(sqlObject.getName());
                        } else {
                        	node = tree.searchNode(sqlObject.getName(), sqlObject.getAbsolutePath(), sqlObject.getType());
                        }                                 
                        StringBuilder result = new StringBuilder();
                        testMultipleValidation(node, tree, result);
                        String message = result.toString();
                        if (!message.isEmpty()) {            	    			    			                        	
                        	Show.warningScroll(I18NSupport.getString("sql.invalid"), message, 10, 30, createActions(node, tree));            	    		
            	    	} else {            	    		
            	    		Show.info(I18NSupport.getString("sql.valid"));            	    		
            	    	}
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
    
    private void testMultipleValidation(DBBrowserNode node, DBBrowserTree tree, StringBuilder result) {
    	if (node.getChildCount() == 0) {
            tree.startExpandingTree(node, false, null);
        }  
    	for (int i = 0, size = node.getChildCount(); i < size; i++) {
            DBBrowserNode child = (DBBrowserNode) node.getChildAt(i);
            DBObject object = child.getDBObject();
            if ((object.getType() == DBObject.FOLDER_QUERY) ||
				(object.getType() == DBObject.FOLDER_REPORT) ||
				(object.getType() == DBObject.FOLDER_CHART)) {
            	testMultipleValidation(child, tree, result);
            } else {
            	String res = testSingleValidation(object, false);
            	result.append(res);
            }
        }
    }
    
    private String testSingleValidation(DBObject obj, boolean showMessage) {
    	Report report = null;    	
		if (obj.getType() == DBObject.QUERIES) {
			report = repPersist.loadReport(obj.getAbsolutePath());
		} else if (obj.getType() == DBObject.REPORTS) {
			report = FormLoader.getInstance().load(obj.getAbsolutePath());
		} else if (obj.getType() == DBObject.CHARTS) {
			report = ChartUtil.loadChart(obj.getAbsolutePath()).getReport();
		} 
		boolean error = false;
	    if (report != null) {
	    	String message = Globals.getDBViewer().isValidSql(report);	    		    	
	    	StringBuilder sb = new StringBuilder();
	    	if (message == null) {	    		
	    		// valid
	    		List<Report> subreports = ReportUtil.getSubreports(report);	    		
	    		for (Report subreport : subreports) {
	    			message = Globals.getDBViewer().isValidSql(subreport);	    			
	    			if (message != null) {
	    				error = true;
	    				sb.append("Subreport '").append(report.getName()).append("/").append(subreport.getName()).append("':\n").append(message).append("\n\n");
	    				break;
	    			}
	    		}	    		
	    	} else {
	    		sb.append("Report '").append(report.getName()).append("':\n").append(message).append("\n\n");
	    		error = true;	    		
	    	}
	    	if (error) {
	    		obj.putProperty(VALID_SQL_PROPERTY, false);
	    		if (showMessage) {	    			
	    			Show.warningScroll(I18NSupport.getString("sql.invalid"), sb.toString(), 10, 30, createActions(obj));
	    		}
	    	} else {
	    		obj.putProperty(VALID_SQL_PROPERTY, true);
	    		if (showMessage) {
	    			Show.info(I18NSupport.getString("sql.valid"));
	    		}
	    	}
	    	return sb.toString();
	    }
	    return "";
    }
    
    private List<Action> createActions (final DBBrowserNode node, final DBBrowserTree tree) {
    	Action replace = new AbstractAction(I18NSupport.getString("validate.replace")) {
			@Override
			public void actionPerformed(ActionEvent e) {

				final FindPanel findPanel = new FindPanel();
				BaseDialog dialog = new FindDialog(findPanel);
		        dialog.pack();
		        Show.centrateComponent(Globals.getMainFrame(), dialog);
		        dialog.setVisible(true);
				if (dialog.okPressed()) {
					 Thread executorThread = new Thread(new Runnable() {

				            public void run() {            	            	

				                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("validate.replace"));
				                activator.start();                                                                               
				                try {
				                	setEnabled(false);
									String oldText = findPanel.getOldText();
									String newText = findPanel.getNewText();
									boolean isCaseSensitive = findPanel.isCaseSensitive();
									LOG.info("Validate replace action '" + node.getDBObject().getName() + "' " + oldText + " -> " + newText);									
									replaceDir(node, tree, oldText, newText, isCaseSensitive);
				                } finally {
				                	setEnabled(true);
			                        if (activator != null) {
			                            activator.stop();
			                        }
			                        Show.info(I18NSupport.getString("validate.replace.finish"));
			                    }
				            }
				        }, "NEXT : " + getClass().getSimpleName());
				        executorThread.start();	
				}
				
			}								
    		
    	};
    	List<Action> actions = new ArrayList<Action>();
    	actions.add(replace);
    	return actions;
    }
    
    private List<Action> createActions (final DBObject object) {
    	Action replace = new AbstractAction(I18NSupport.getString("validate.replace")) {
			@Override
			public void actionPerformed(ActionEvent e) {

				FindPanel findPanel = new FindPanel();
				BaseDialog dialog = new FindDialog(findPanel);
		        dialog.pack();
		        Show.centrateComponent(Globals.getMainFrame(), dialog);
		        dialog.setVisible(true);
				if (dialog.okPressed()) {
					String oldText = findPanel.getOldText();
					String newText = findPanel.getNewText();
					boolean isCaseSensitive = findPanel.isCaseSensitive();
					LOG.info("Validate replace action '" + object.getName() + "' " + oldText + " -> " + newText);					
					replaceFile(object, oldText, newText, isCaseSensitive);
				}
				
			}								
    		
    	};
    	List<Action> actions = new ArrayList<Action>();
    	actions.add(replace);
    	return actions;
    }
    
    private void replaceFile(DBObject obj, String oldText, String newText, boolean isCaseSensitive) {
    	if ((obj.getType() == DBObject.QUERIES) ||
    		(obj.getType() == DBObject.REPORTS)	||
    		(obj.getType() == DBObject.CHARTS)) {
			String filePath = obj.getAbsolutePath();	
			Boolean validQ = (Boolean) obj.getProperty(ValidateSqlsAction.VALID_SQL_PROPERTY);
        	if ((validQ != null) && !validQ.booleanValue()) {			
        		LOG.info("  --> replace file '" + filePath + "' " + oldText + " -> " + newText);
				StringUtil.replaceInFile(new File(filePath), oldText, newText, isCaseSensitive);
			}
		} 
    }
    
    private void replaceDir(DBBrowserNode node,  DBBrowserTree tree, String oldText, String newText, boolean isCaseSensitive) {
    	if (node.getChildCount() == 0) {
            tree.startExpandingTree(node, false, null);
        }  
    	for (int i = 0, size = node.getChildCount(); i < size; i++) {
            DBBrowserNode child = (DBBrowserNode) node.getChildAt(i);
            DBObject object = child.getDBObject();
            if ((object.getType() == DBObject.FOLDER_QUERY) ||
				(object.getType() == DBObject.FOLDER_REPORT) ||
				(object.getType() == DBObject.FOLDER_CHART)) {                	
            	LOG.info("--> replace dir '" + object.getAbsolutePath() + "' " + oldText + " -> " + newText);
            	replaceDir(child, tree, oldText, newText, isCaseSensitive);            	
            } else {
            	replaceFile(object, oldText, newText, isCaseSensitive);            	
            }
        }
    }
}
