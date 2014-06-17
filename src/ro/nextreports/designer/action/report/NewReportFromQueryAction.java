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

import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.util.DisplayData;
import ro.nextreports.engine.exporter.util.IndicatorData;
import ro.nextreports.engine.template.ReportTemplate;
import ro.nextreports.engine.Report;

import javax.swing.*;

import ro.nextreports.designer.GroupIndexGenerator;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.query.OpenQueryAction;
import ro.nextreports.designer.action.report.SaveReportAction;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.TreeUtil;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 4, 2006
 * Time: 5:21:02 PM
 */
public class NewReportFromQueryAction extends AbstractAction {

    private boolean save;
    private ReportTemplate reportTemplate;
    private List<String> selectedColumns;
    private Report createdReport;
    private NewReportAction reportAction;
    private OpenQueryAction queryAction = new OpenQueryAction() {
        public void afterCreation() {            
            //  must expand (load nodes) to be able to save report
            TreeUtil.expandConnectedDataSource();
            reportAction.actionPerformed(null);
            if (selectedColumns != null) {
//                LayoutHelper.getReportLayout().setColumnNames(selectedColumns);
//                LayoutHelper.getReportLayout().fireColumnNamesChanged();
            }
            if (reportTemplate != null) {
                TemplateManager.applyGeneralTemplate(LayoutHelper.getReportLayout(), reportTemplate);
            }

            if (save) {
                new SaveReportAction().actionPerformed(null);
            }

        }
    };      

    public NewReportFromQueryAction() {
        this(false);
    }

    public NewReportFromQueryAction(boolean save) {
        this(null, null, true, ResultExporter.DEFAULT_TYPE);
        this.save = save;
    }
    
    public NewReportFromQueryAction(ReportTemplate reportTemplate, List<String> selectedColumns) {
        this(reportTemplate, selectedColumns, true, ResultExporter.DEFAULT_TYPE);
    }

    public NewReportFromQueryAction(ReportTemplate reportTemplate, List<String> selectedColumns, int reportType) {
        this(reportTemplate, selectedColumns, true, reportType);
    }

    public NewReportFromQueryAction(ReportTemplate reportTemplate, List<String> selectedColumns, boolean fullName, int reportType) {    	    	    	
        this.reportTemplate = reportTemplate;
        this.selectedColumns = selectedColumns;
        this.reportAction = new NewReportAction(selectedColumns, reportType);
        queryAction.setSelection(false);
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("new.report.from.query"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("new.report.from.query.small"));
        }
        Icon icon = ImageUtil.getImageIcon("report_query_new");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("new.report.from.query.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("new.report.from.query.desc"));
    }
    
    public void setAlarm(FormattingConditions alarmConditions, List<String> alarmMessages, boolean alarmShadow) {
    	reportAction.setAlarm(alarmConditions, alarmMessages, alarmShadow);
    }
    
    public void setIndicator(IndicatorData indicatorData) {
    	reportAction.setIndicatorData(indicatorData);
    }
    
    public void setDisplay(DisplayData displayData) {
    	reportAction.setDisplayData(displayData);
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

    public void setCreatedReport(Report createdReport) {
        this.createdReport = createdReport;
        queryAction.setCreatedReport(createdReport);
    }

    public void actionPerformed(ActionEvent e) {
    	if (NextReportsUtil.isInnerEdit()) {
    		return;
    	}
        GroupIndexGenerator.resetCurrentIndex();
        queryAction.actionPerformed(e);
    }
}
