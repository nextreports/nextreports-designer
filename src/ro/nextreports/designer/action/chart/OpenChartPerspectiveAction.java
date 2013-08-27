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

import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.util.ReportUtil;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.WorkspaceManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * User: mihai.panaitescu
 * Date: 16-Dec-2009
 * Time: 13:14:15
 */
public class OpenChartPerspectiveAction extends AbstractAction {

    public OpenChartPerspectiveAction() {
        putValue(Action.NAME, I18NSupport.getString("chart.perspective"));
        Icon icon = ImageUtil.getImageIcon("chart_perspective");
        putValue(Action.SMALL_ICON, icon);                
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("chart.perspective.desc"));
    }

    public void actionPerformed(ActionEvent ev) {

        // see global action from NextReports class
        if (!Globals.isChartLoaded()) {
            // ignore
            return;
        }

        // query can be modified
        // we must synchronize chart with query  (reload columns)
        // also reset x and y if column name was changed
        final Chart chart = Globals.getChartDesignerPanel().getChart();
        chart.setReport(Globals.getMainFrame().getQueryBuilderPanel().createReport(chart.getName()));
        List<NameType> columns = Globals.getChartDesignerPanel().getPropertiesPanel().getColumns();
        final String sql = ReportUtil.getSql(chart.getReport());
        if ((columns.size() == 0) || !sql.equals(Globals.getInitialQuery())) {
            if (Globals.getConnection() != null) {

                Thread executorThread = new Thread(new Runnable() {

                    public void run() {
                        UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("preview.chart.reload"));
                        activator.start();

                        try {
                            final List<NameType> columns = ReportLayoutUtil.getAllColumnsForReport(chart.getReport());
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    Globals.getChartDesignerPanel().getPropertiesPanel().setColumns(columns);
                                    Globals.getChartLayoutPanel().setColumns(columns);
                                    refreshAxes(chart, columns);
                                    Globals.setInitialQuery(sql);
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Show.error(e);
                        } finally {
                            if (activator != null) {
                                activator.stop();
                            }
                        }
                    }
                }, "NEXT : " + getClass().getSimpleName());
                executorThread.start();
            }
        } else {
            refreshAxes(chart, columns);
        }

        WorkspaceManager.getInstance().setCurrentWorkspace(WorkspaceManager.CHART_WORKSPACE);
    }

    private void refreshAxes(Chart chart, List<NameType> columns) {        
        if (chart.getXColumn() != null) {
            if (!contains(columns, chart.getXColumn())) {
                chart.setXColumn(null);
                Globals.getChartLayoutPanel().refreshX();
            }
        }
        if ((chart.getYColumns() != null) && (chart.getYColumns().size() > 0)) {
            if (!contains(columns, chart.getYColumns().get(0))) {
                chart.setYColumns(new ArrayList<String>());
                Globals.getChartLayoutPanel().refreshY();
            }
        }
    }

    private boolean contains(List<NameType> columns, String column) {
        for (NameType nt : columns) {
            if (nt.getName().equals(column)) {
                return true;
            }
        }
        return false;
    }

}
