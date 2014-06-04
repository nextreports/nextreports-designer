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
package ro.nextreports.designer.action.chart;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.util.NextChartUtil;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.i18n.I18nUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.action.query.OpenQueryPerspectiveAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.i18n.action.I18nManager;
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

/**
 * User: mihai.panaitescu
 * Date: 17-Dec-2009
 * Time: 13:59:57
 */
public class OpenChartAction extends AbstractAction {

    private String chartName;
    private String chartPath;
    private static BrowserDialog dialog;
    private Map<String, List<CheckListItem>> itemMap;

    private boolean error = false;
    private String errorMessage = "";
    private boolean resetServerChart = true;
    private boolean testInner = true;

    private static final Log LOG = LogFactory.getLog(OpenChartAction.class);

    public OpenChartAction() {
        this(true);
    }

    public OpenChartAction(boolean fullName) {
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("open.chart"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("open.chart.small"));
        }
        Icon icon = ImageUtil.getImageIcon("chart_open");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("chart.open.mnemonic", new Integer('A')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("chart.open.accelerator", "control A")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("open.chart"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("open.chart"));
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public void setChartPath(String chartPath) {
        this.chartPath = chartPath;
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

        if (chartName == null) {
            BrowserPanel browser = new BrowserPanel(BrowserPanel.CHART_BROWSER);
            dialog = new BrowserDialog(browser);
            dialog.pack();
            dialog.setResizable(false);
            Show.centrateComponent(Globals.getMainFrame(), dialog);
            dialog.setVisible(true);

            // On double click in OpenChart dialog the dialog is closed and set to null
            // see comment #2
            if (dialog != null) {
                if (dialog.okPressed()) {
                    chartName = browser.getSelectedName();
                    chartPath = browser.getSelectedFilePath();
                } else {
                    return;
                }
            }
        }

        Globals.getReportUndoManager().discardAllEdits();

        if (chartName == null) {
            return;
        }

        // comment #2  : for double click in OpenChart dialog
        if ((dialog != null) && dialog.isVisible()) {
            dialog.dispose();
            dialog = null;
        }

        final QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();

        if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("open.chart"))) {
            return;
        }

        if (chartPath != null) {
            byte status = NextChartUtil.isValidChartVersion(chartPath);
            if (NextChartUtil.CHART_INVALID_NEWER == status) {
                Show.error(I18NSupport.getString("chart.version.invalid.newer", ReleaseInfoAdapter.getVersionNumber()));
                return;
            }
        }
        
        if (resetServerChart) {        	
        	Globals.resetServerFile();
        }

        Globals.setReportLoaded(false);        
        Globals.setChartLoaded(false);

        Thread executorThread = new Thread(new Runnable() {

            public void run() {
                UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("load.chart"));
                activator.start();

                try {
                    Globals.getMainMenuBar().enableLayoutPerspective(true);
                    Globals.getMainToolBar().enableLayoutPerspective(true);

                    final Chart chart = ChartUtil.loadChart(chartPath);
                    if (chart == null) {
                        if (activator != null) {
                            activator.stop();
                            activator = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                Show.error(I18NSupport.getString("could.not.load.chart"));
                            }
                        });
                        return;
                    }
                    
                    I18nManager.getInstance().setKeys(chart.getI18nkeys());
                    I18nManager.getInstance().setLanguages(chart.getLanguages());
                    I18nManager.getInstance().setCurrentLanguage(I18nUtil.getDefaultLanguage(chart));    

                    final Report report = chart.getReport();
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

                    if (Globals.getConnection() != null) {
                        List<NameType> columns = new ArrayList<NameType>();
                        try {
                            columns = ReportLayoutUtil.getAllColumnsForReport(chart.getReport());
                        } catch (Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            error = true;
                            errorMessage = I18NSupport.getString("could.not.load.chart.error.columns") + "\r\n" + ex.getMessage();
                        }
                        Globals.getChartDesignerPanel().getPropertiesPanel().setColumns(columns);
                        Globals.getChartLayoutPanel().setColumns(columns);
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
                                Globals.setInitialQuery(report.getSql());
                            } else if (report.getQuery() != null) {
                                builderPanel.drawDesigner(report, itemMap);
                                itemMap.clear();
                                Globals.setInitialQuery(report.getQuery().toString());
                            }
                            builderPanel.setParameters(report.getParameters());
                            builderPanel.selectTreeNode(chartName, DBObject.CHARTS);

                            Globals.setCurrentQueryName(null);
                            Globals.setCurrentQueryAbsolutePath(null);
                            Globals.setCurrentChartName(chartName);
                            Globals.setCurrentChartAbsolutePath(chartPath);

                            builderPanel.loadChart(chart);
                            Globals.getMainMenuBar().newChartActionUpdate();
                            Globals.getMainToolBar().newChartActionUpdate();

                            Globals.setOriginalSql(Globals.getMainFrame().getQueryBuilderPanel().getUserSql());
                            Globals.setChartLoaded(true);

                            if (error) {
                                new OpenQueryPerspectiveAction().actionPerformed(null);
                                Show.error(errorMessage);
                            }

                        }
                    });
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    Show.error(ex);
                } finally {
                    chartName = null;
                    if (activator != null) {
                        activator.stop();
                    }
                }

            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();
    }

	public void setResetServerChart(boolean resetServerChart) {
		this.resetServerChart = resetServerChart;
	}        

}
