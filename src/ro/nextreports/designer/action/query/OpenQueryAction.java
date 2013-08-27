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
import java.util.Map;
import java.util.List;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.QueryBrowserDialog;
import ro.nextreports.designer.querybuilder.QueryBrowserPanel;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.ui.list.CheckListItem;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.TableUtil;
import ro.nextreports.designer.util.UIActivator;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.util.ReportUtil;

/**
 * @author Decebal Suiu
 * @author Mihai Dinca-Panaitescu
 */
public class OpenQueryAction extends AbstractAction {

    private String queryName;
    private String queryPath;
    private boolean executed;
    private static QueryBrowserDialog dialog = null;
    private Report createdReport;    
    private boolean selection = true;

    public OpenQueryAction() {
        this(null, true);
    }

    public OpenQueryAction(boolean fullName) {
       this(null, fullName); 
    }

    public OpenQueryAction(Report createdReport, boolean fullName) {
        this.createdReport = createdReport;
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("open.query"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("open.query.small"));
        }
        Icon icon = ImageUtil.getImageIcon("query_open");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("query.open.mnemonic",  new Integer('O')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("query.open.accelerator", "control O")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("open.query.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("open.query.desc"));
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public void setQueryPath(String queryPath) {
        this.queryPath = queryPath;
    }

    public void setCreatedReport(Report createdReport) {
        this.createdReport = createdReport;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    public void actionPerformed(ActionEvent e) {
    	
    	if (NextReportsUtil.isInnerEdit()) {
    		return;
    	}

        if (MessageUtil.showReconnect()) {
            return;
        }
                
        if (queryName == null) {
            QueryBrowserPanel browser = new QueryBrowserPanel();
            dialog = new QueryBrowserDialog(browser);
            dialog.pack();
            dialog.setResizable(false);
            Show.centrateComponent(Globals.getMainFrame(), dialog);
            dialog.setVisible(true);

            // On double click in OpenQuery dialog the dialog is closed and set to null
            // see comment #2
            if (dialog != null) {
                if (dialog.okPressed()) {
                    queryName = browser.getSelectedName();
                    queryPath = browser.getSelectedFilePath();
                } else {
                    executed = false;
                    return;
                }
            }
        }

        if (queryName == null) {
            return;
        }

        // comment #2  : for double click in OpenQuery dialog
        if ((dialog != null)  && dialog.isVisible()) {
            dialog.dispose();
            dialog = null;
        }

        final QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();

        if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("open.query.desc"))) {
            executed = false;
            return;
        }

        // queryPath is null when we use Wizard
        if (queryPath != null) {
            byte status = ReportUtil.isValidReportVersion(queryPath);
            if (ReportUtil.REPORT_INVALID_OLDER == status) {
                Show.error(I18NSupport.getString("query.version.invalid.older"));
                return;
            } else if (ReportUtil.REPORT_INVALID_NEWER == status) {
                Show.error(I18NSupport.getString("query.version.invalid.newer", ReleaseInfoAdapter.getVersionNumber()));
                return;
            }
        }

        Thread executorThread = new Thread(new Runnable() {

            public void run() {
                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("load.query"));
                activator.start();

                builderPanel.emptyReportAndChart();
                Globals.setCurrentReportName(null);
                Globals.setCurrentReportAbsolutePath(null);
                Globals.setCurrentChartName(null);
                Globals.setCurrentChartAbsolutePath(null);
                Globals.setReportLoaded(false);
                Globals.setChartLoaded(false);
                Globals.setInitialQuery("");
                //builderPanel.selectDesignerTab();

                ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                        Globals.getReportPersistenceType());
                try {
                    final Report report;
                    if (createdReport != null) {
                        report = createdReport;
                    } else {
                        report = repPersist.loadReport(queryPath);
                    }

                    final Map<String, List<CheckListItem>> itemMap = TableUtil.getItemMap(report);

                    if (activator != null) {
                        activator.stop();
                        activator = null;
                    }

                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {

                            builderPanel.clear(true);

                            if (report.getSql() != null) {
                                builderPanel.selectSQLViewTab();
                                builderPanel.setUserSql(report.getSql());
                            } else if (report.getQuery() != null) {
                                builderPanel.drawDesigner(report, itemMap);
                                builderPanel.selectDesignerTab();
                                itemMap.clear();                                
                            }
                            builderPanel.setParameters(report.getParameters());
                            //builderPanel.selectTreeNode(report.getName(), DBObject.QUERIES);
                            if (selection) {
                                builderPanel.selectTreeNode(report.getName(), queryPath, DBObject.QUERIES);
                            }

                            Globals.setCurrentQueryName(queryName);
                            //System.out.println("---- queryPath="+queryPath);
                            Globals.setCurrentReportAbsolutePath(null);
                            Globals.setCurrentQueryAbsolutePath(queryPath);
                            Globals.getMainMenuBar().newQueryActionUpdate();
                            Globals.getMainToolBar().newQueryActionUpdate();                            
                            Globals.setOriginalSql(builderPanel.getUserSql());                            
                            executed = true;
                            
                            afterCreation();

                        }
                    });

                } catch (Exception e1) {
                    Show.error(e1);
                } finally {
                    queryName = null;
                    if (activator != null) {
                        activator.stop();
                    }
                }
            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }

    public boolean wasExecuted() {
        return executed;
    }

    // invoked on EDT
    public void afterCreation() {
    }

}
