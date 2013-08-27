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

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.query.OpenQueryAction;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.TreeUtil;

import java.util.List;
import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 18-Dec-2009
 * Time: 11:39:01
 */
public class NewChartFromQueryAction extends AbstractAction {

    private boolean save;
    private List<String> selectedColumns;
    private Chart createdChart;
    private NewChartAction chartAction;
    private OpenQueryAction queryAction = new OpenQueryAction() {
        public void afterCreation() {
            //  must expand (load nodes) to be able to save chart
            TreeUtil.expandConnectedDataSource();
            chartAction.actionPerformed(null);
            if (save) {
                new SaveChartAction().actionPerformed(null);
            }
        }
    };


    public NewChartFromQueryAction() {
        this(false);
    }

    public NewChartFromQueryAction(boolean save) {
        this(null, true);
        this.save = save;
    }

    public NewChartFromQueryAction(List<String> selectedColumns) {
        this(selectedColumns, true);
    }

    public NewChartFromQueryAction(List<String> selectedColumns, boolean fullName) {    	    	    	
        this.selectedColumns = selectedColumns;
        this.chartAction = new NewChartAction(selectedColumns);
        queryAction.setSelection(false);
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("new.chart.from.query"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("new.chart.from.query.small"));
        }
        Icon icon = ImageUtil.getImageIcon("chart_query_new");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("new.chart.from.query.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("new.chart.from.query.desc"));
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public void setQueryName(String queryName) {
        queryAction.setQueryName(queryName);
    }

    public void setQueryPath(String queryPath) {
        queryAction.setQueryPath(queryPath);
    }

    public void setCreatedChart(Chart createdChart, List<NameType> columns) {
        this.createdChart = createdChart;
        chartAction.setChart(createdChart);
        Globals.getChartDesignerPanel().getPropertiesPanel().setColumns(columns);
        Globals.getChartLayoutPanel().setColumns(columns);
        Globals.getChartLayoutPanel().refresh();
        queryAction.setCreatedReport(createdChart.getReport());
    }

    public void actionPerformed(ActionEvent e) {   
    	if (NextReportsUtil.isInnerEdit()) {
    		return;
    	}
        queryAction.actionPerformed(e);
    }
}
