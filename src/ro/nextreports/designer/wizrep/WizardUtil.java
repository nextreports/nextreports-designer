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
package ro.nextreports.designer.wizrep;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.util.DisplayData;
import ro.nextreports.engine.exporter.util.IndicatorData;
import ro.nextreports.engine.template.ReportTemplate;
import ro.nextreports.engine.queryexec.Query;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.action.chart.NewChartFromQueryAction;
import ro.nextreports.designer.action.report.NewReportFromQueryAction;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DataSourceManager;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.exception.NotFoundException;
import ro.nextreports.designer.i18n.action.I18nManager;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.ui.wizard.WizardContext;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 10, 2008
 * Time: 11:45:50 AM
 */
public class WizardUtil {

    private static Log LOG = LogFactory.getLog(WizardUtil.class);

    public static void disconnect() {
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        DataSource ds = manager.getConnectedDataSource();
        if (ds != null) {
            try {
//                System.out.println(">>> DISCONNECT");
                manager.disconnect(ds.getName());
            } catch (NotFoundException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public static boolean connect(String name) {
        DataSourceManager manager = DefaultDataSourceManager.getInstance();
        try {
            DataSource ds = manager.getConnectedDataSource();
            if (ds != null) {
                if (name.equals(ds.getName())) {
                    return true;
                } else  {
//                    System.out.println(">>> DISCONNECT");
                    manager.disconnect(ds.getName());
                }
            }
//            System.out.println(">>> CONNECT");
            manager.connect(name);
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public static void resetParametersAndLanguages()  {
//        System.out.println(">>> clear parameters");
        ParameterManager.getInstance().clearParameters();
        I18nManager.getInstance().clear();
    }

    @SuppressWarnings("unchecked")
    public static void openReport(WizardContext context, File selectedTemplate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

        Report report = (Report)context.getAttribute(WizardConstants.LOAD_REPORT);
        if (report == null) {
            report = new Report();
            report.setVersion(ReleaseInfoAdapter.getVersionNumber());
            report.setName("Temp_" + sdf.format(new Date()));            
            report.setSql(((Query) context.getAttribute(WizardConstants.QUERY)).getText());
            report.setParameters(ParameterManager.getInstance().getParameters());
        }
        ReportTemplate template = TemplateManager.loadTemplate(selectedTemplate);
        int reportType = (Integer)context.getAttribute(WizardConstants.REPORT_TYPE);
        NewReportFromQueryAction action = new NewReportFromQueryAction(template,
                (List<String>)context.getAttribute(WizardConstants.REPORT_COLUMNS), reportType);        
        if (ResultExporter.ALARM_TYPE == reportType) {        	
        	FormattingConditions alarmConditions = (FormattingConditions)context.getAttribute(WizardConstants.ALARM_CONDITIONS);		 
   		 	List<String> alarmMessages = (List<String>)context.getAttribute(WizardConstants.ALARM_MESSAGES);
   		 	Boolean shadow = (Boolean)context.getAttribute(WizardConstants.ALARM_SHADOW);
        	action.setAlarm(alarmConditions, alarmMessages, shadow);        	 
        } else if (ResultExporter.INDICATOR_TYPE == reportType) {
        	IndicatorData data = (IndicatorData)context.getAttribute(WizardConstants.INDICATOR_DATA);
        	action.setIndicator(data);
        } else if (ResultExporter.DISPLAY_TYPE == reportType) {
        	DisplayData data = (DisplayData)context.getAttribute(WizardConstants.DISPLAY_DATA);
        	action.setDisplay(data);
        }
        action.setSave(true);
        action.setQueryName(report.getName());
        action.setCreatedReport(report);
        action.actionPerformed(null);

    }

    @SuppressWarnings("unchecked")
    public static void openChart(WizardContext context, File selectedTemplate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");

        Report report = (Report)context.getAttribute(WizardConstants.LOAD_REPORT);
        if (report == null) {
            report = new Report();
            report.setVersion(ReleaseInfoAdapter.getVersionNumber());
            report.setName("Temp_" + sdf.format(new Date()));
            report.setSql(((Query) context.getAttribute(WizardConstants.QUERY)).getText());
            report.setParameters(ParameterManager.getInstance().getParameters());
        }

        Chart chart = new Chart();
        chart.setReport(report);
        chart.setXColumn((String)context.getAttribute(WizardConstants.CHART_X_COLUMN));
        List<String> yColumns  = (List<String>)context.getAttribute(WizardConstants.CHART_Y_COLUMNS);
        chart.setYColumns(yColumns);

        NewChartFromQueryAction action = new NewChartFromQueryAction();
        action.setSave(true);
        action.setQueryName(report.getName());
        action.setCreatedChart(chart, (List<NameType>)context.getAttribute(WizardConstants.REPORT_COLUMNS));
        action.actionPerformed(null);

    }

}
