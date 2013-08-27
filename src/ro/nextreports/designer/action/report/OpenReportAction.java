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
package ro.nextreports.designer.action.report;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportLayoutPanel;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.querybuilder.BrowserDialog;
import ro.nextreports.designer.querybuilder.BrowserPanel;
import ro.nextreports.designer.querybuilder.DBObject;
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
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 3, 2006
 * Time: 1:59:10 PM
 */
public class OpenReportAction extends AbstractAction {

    private String reportName;
    private String reportPath;
    private static BrowserDialog dialog;
    private Map<String, List<CheckListItem>> itemMap;
    private boolean resetServerReport = true;    
    private boolean testInner = true;

    public OpenReportAction() {
        this(true);
    }

    public OpenReportAction(boolean fullName) {
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("open.report"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("open.report.small"));
        }
        Icon icon = ImageUtil.getImageIcon("report_open");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("report.open.mnemonic", new Integer('P')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("report.open.accelerator", "control P")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("open.report"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("open.report"));
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }
        
    public void setTestInner(boolean testInner) {
		this.testInner = testInner;
	}

	public void actionPerformed(ActionEvent e) {
    	
    	if (testInner) {
    		if (NextReportsUtil.isInnerEdit()) {
    			return;
    		}
    	}

        if (MessageUtil.showReconnect()) {
            return;
        }

        if (reportName == null) {
            BrowserPanel browser = new BrowserPanel(BrowserPanel.REPORT_BROWSER);
            dialog = new BrowserDialog(browser);
            dialog.pack();
            dialog.setResizable(false);
            Show.centrateComponent(Globals.getMainFrame(), dialog);
            dialog.setVisible(true);

            // On double click in OpenReport dialog the dialog is closed and set to null
            // see comment #2
            if (dialog != null) {
                if (dialog.okPressed()) {
                    reportName = browser.getSelectedName();
                    reportPath = browser.getSelectedFilePath();
                } else {
                    return;
                }
            }
        }

        Globals.getReportUndoManager().discardAllEdits();

        if (reportName == null) {
            return;
        }

        // comment #2  : for double click in OpenReport dialog
        if ((dialog != null) && dialog.isVisible()) {
            dialog.dispose();
            dialog = null;
        }

        final QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();

        if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("open.report"))) {
            return;
        }

        if (reportPath != null) {
            byte status = ReportUtil.isValidReportVersion(reportPath);
            if (ReportUtil.REPORT_INVALID_OLDER == status) {
                Show.error(I18NSupport.getString("report.version.invalid.older"));
                return;
            } else if (ReportUtil.REPORT_INVALID_NEWER == status) {
                Show.error(I18NSupport.getString("report.version.invalid.newer", ReleaseInfoAdapter.getVersionNumber()));
                return;
            }
        }
        
        if (resetServerReport) {
        	Globals.resetServerFile();
        }

        Globals.setChartLoaded(false);        
        Globals.setReportLoaded(false);
        Globals.setInitialQuery("");

        Thread executorThread = new Thread(new Runnable() {

            public void run() {
                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("load.report"));
                activator.start();

                try {

                    Globals.getMainMenuBar().enableLayoutPerspective(true);
                    Globals.getMainToolBar().enableLayoutPerspective(true);

                    final Report report = FormLoader.getInstance().load(reportPath);
                    if (report == null) {
                        if (activator != null) {
                            activator.stop();
                            activator = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Show.error(I18NSupport.getString("could.not.load.report"));
                            }
                        });
                        return;
                    }

                    // set current group index
                    ReportLayoutUtil.setCurrentGroupIndex(report.getLayout());

                    try {
                        itemMap = TableUtil.getItemMap(report);
                    } catch (Exception ex) {
                        // designer cannot be created (tables not found)
                        if (activator != null) {
                            activator.stop();
                            activator = null;
                        }
                        int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                                "<html>" + I18NSupport.getString("designer.load.error") + "<br>" +
                                I18NSupport.getString("designer.load.error.table") + "<br>" +
                                I18NSupport.getString("designer.load.error.edit.mode") + "</html>", "",
                            JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                        if (option != JOptionPane.YES_OPTION) {
                            return;
                        } else {
                            report.setSql(report.getQuery().toString());
                        }
                    }

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
                                itemMap.clear();
                            }
                            builderPanel.setParameters(report.getParameters());
                            builderPanel.selectTreeNode(reportName, DBObject.REPORTS);

                            String qName = FormSaver.getInstance().getReportFileName(report.getName());
                            Globals.setCurrentQueryName(null);
                            Globals.setCurrentQueryAbsolutePath(null);
                            Globals.setCurrentReportName(reportName);
                            Globals.setCurrentReportAbsolutePath(reportPath);
                            ReportLayoutPanel layout = Globals.getReportDesignerPanel().getLayoutPanel();
                            if (layout != null) {
                                layout.getReportGrid().getSelectionModel().clearSelection();
                            }

                            builderPanel.loadReport(report.getLayout());
                            Globals.getReportLayoutPanel().updateUseSize();

                            Globals.getMainMenuBar().newReportActionUpdate();
                            Globals.getMainToolBar().newReportActionUpdate();

                            Globals.setOriginalSql(Globals.getMainFrame().getQueryBuilderPanel().getUserSql());
                            Globals.setReportLoaded(true);
                            
                            afterOpen();

                        }
                    });
                } catch (Exception e1) {
                    Show.error(e1);
                } finally {
                    reportName = null;
                    if (activator != null) {
                        activator.stop();
                    }
                }

            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }

	public void setResetServerReport(boolean resetServerReport) {
		this.resetServerReport = resetServerReport;
	}
    
	public void afterOpen() {		
	}

}
