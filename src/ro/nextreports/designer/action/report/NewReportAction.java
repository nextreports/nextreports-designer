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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import ro.nextreports.designer.Cache;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.GroupIndexGenerator;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportLayoutFactory;
import ro.nextreports.designer.ReportLayoutPanel;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.querybuilder.SQLViewPanel;
import ro.nextreports.designer.template.report.TemplateManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.util.DisplayData;
import ro.nextreports.engine.exporter.util.IndicatorData;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.template.ReportTemplate;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.util.QueryUtil;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 3, 2006
 * Time: 12:09:25 PM
 */
public class NewReportAction extends AbstractAction {

	private boolean keepTemplate = false;
    private List<String> columnNames;
    private boolean askForSave = false;
    private int reportType = ResultExporter.DEFAULT_TYPE;
    
    private FormattingConditions alarmConditions;
    private List<String> alarmMessages;
    private IndicatorData indicatorData;
    private DisplayData displayData;

    public NewReportAction() {
		this(false, true);
        askForSave = true;
    }

    public NewReportAction(boolean fullName) {
		this(false, fullName);
        askForSave = true;
    }

    public NewReportAction(List<String> columnNames, int reportType) {
		this(false, true);
        this.columnNames = columnNames;
        this.reportType = reportType;
    }

    public NewReportAction(boolean keepTemplate, boolean fullName) {
		this.keepTemplate = keepTemplate;
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("new.report"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("new.report.small"));
        }
        Icon icon = ImageUtil.getImageIcon("report_new");
		putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("report.new.mnemonic",  new Integer('R')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("report.new.accelerator", "control R")));        
		putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("new.report.desc"));
		putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("new.report.desc"));
	}
    
    public void setAlarm(FormattingConditions alarmConditions, List<String> alarmMessages) {
    	this.alarmConditions = alarmConditions;
    	this.alarmMessages = alarmMessages;
    }        

	public void setIndicatorData(IndicatorData indicatorData) {
		this.indicatorData = indicatorData;
	}
	
	public void setDisplayData(DisplayData displayData) {
		this.displayData = displayData;
	}

	public void actionPerformed(ActionEvent e) {
		
		if (NextReportsUtil.isInnerEdit()) {
    		return;
    	}

        if (MessageUtil.showReconnect()) {
            return;
        }

        if (!LicenseUtil.allowToAddAnotherReport()) {
			return;
		}

        if (askForSave) {
            if (Globals.isReportLoaded()) {
                if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("new.report"))) {
                    return;
                }
            }
        }

        ReportTemplate template = null;
		if (keepTemplate) {
			try {
				template = TemplateManager.getTemplate(LayoutHelper.getReportLayout());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
            //LayoutHelper.getReportLayout().setReportTitle(ReportLayout.DEFAULT_REPORT_TITLE);
        }

        Globals.setInitialQuery("");
        Globals.setChartLoaded(false);        
        Globals.setReportLoaded(false);
		Globals.getReportUndoManager().discardAllEdits();

		QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
		builderPanel.refreshSql();
		String sql = builderPanel.getUserSql();
		if (!canBePerformed(sql)) {
            Show.info(I18NSupport.getString("new.report.warning"));
            return;
		}

		List<String> columnNames = this.columnNames;
		try {
			QueryUtil qu = new QueryUtil(Globals.getConnection(), Globals.getDialect());
			// get parameters definition from system
			Map<String, QueryParameter> params = new HashMap<String, QueryParameter>();
			ParameterManager paramManager = ParameterManager.getInstance();
			List<String> paramNames = paramManager.getParameterNames();
			for (String paramName : paramNames) {
				QueryParameter param = paramManager.getParameter(paramName);
				if (param == null) {
					throw new Exception(I18NSupport.getString("parameter.undefined"));
				}
				params.put(paramName, param);
			}
            if  (columnNames == null) {
            	String md5Key = Cache.getColumnsKey(sql);                
                List<NameType> result = Cache.getColumns(md5Key);
                if (result != null) {
                	columnNames = ReportUtil.getColumnNames(result);                	
                } else {
                	 List<NameType> columns = qu.getColumns(sql, params);
                	 Cache.setColumns(md5Key, columns);
                	 columnNames = ReportUtil.getColumnNames(columns);    
                }
            }
        } catch (Exception ex) {
			Show.error(ex);
			return;
		}

        String duplicateColumn = StringUtil.getFirstDuplicateValue(columnNames);        
        if (duplicateColumn != null) {
            Show.info(I18NSupport.getString("new.report.ambigous.columns", duplicateColumn));
            return;
        }

        Globals.getMainMenuBar().enableLayoutPerspective(true);
		Globals.getMainToolBar().enableLayoutPerspective(true);
		String title = null;
		if (keepTemplate) {
			title = Globals.getCurrentReportName();
		}
        if (title == null) {
            title = LayoutHelper.DEFAULT_REPORT_TITLE;
        }

        GroupIndexGenerator.resetCurrentIndex();

        ReportLayoutPanel layout = Globals.getReportDesignerPanel().getLayoutPanel();
        if (layout != null) {
            layout.getReportGrid().getSelectionModel().clearSelection();
        }

        //builderPanel.showReport(columnNames, title, !keepTemplate);
        ReportLayout reportLayout;
        if (reportType == ResultExporter.TABLE_TYPE) {
        	reportLayout = ReportLayoutFactory.createTable(columnNames);        
        } else if (reportType == ResultExporter.ALARM_TYPE) {
            reportLayout = ReportLayoutFactory.createAlarm(columnNames.get(0), alarmConditions, alarmMessages);
        } else if (reportType == ResultExporter.INDICATOR_TYPE) {
            reportLayout = ReportLayoutFactory.createIndicator(columnNames.get(0), indicatorData);
        } else if (reportType == ResultExporter.DISPLAY_TYPE) {
        	String secondColumn = null;
        	if (columnNames.size() > 1) {
        		secondColumn = columnNames.get(1);
        	}
            reportLayout = ReportLayoutFactory.createDisplay(columnNames.get(0), secondColumn, displayData);                  
        } else {
        	reportLayout = ReportLayoutFactory.create(columnNames, title);
        }
        builderPanel.loadReport(reportLayout);
        
        Globals.getMainMenuBar().newReportActionUpdate();
        Globals.getMainToolBar().newReportActionUpdate();
        if (!keepTemplate) {
			Globals.setCurrentReportName(null);
            Globals.setCurrentReportAbsolutePath(null);            
        }		
		Globals.setOriginalSql(sql);
		Globals.setReportLoaded(true);

		if (template != null) {
			TemplateManager.applyGeneralTemplate(LayoutHelper.getReportLayout(), template);
		}


    }

	public boolean canBePerformed(String sql) {
		if (sql.trim().equals("") || sql.equals(SQLViewPanel.DEFAULT_QUERY)) {
			return false;
		} else {
			return true;
		}
	}


}
