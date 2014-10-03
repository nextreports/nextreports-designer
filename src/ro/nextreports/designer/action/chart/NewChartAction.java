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
import ro.nextreports.engine.template.ChartTemplate;
import ro.nextreports.engine.util.QueryUtil;
import ro.nextreports.engine.util.StringUtil;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.queryexec.QueryParameter;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.property.ExtendedColorChooser;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.querybuilder.SQLViewPanel;
import ro.nextreports.designer.template.chart.TemplateManager;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * User: mihai.panaitescu
 * Date: 15-Dec-2009
 * Time: 13:53:00
 */
public class NewChartAction extends AbstractAction {
	
	private static String DEFAULT_CHART_TEMPLATE = "Relaxing.nctempl"; 

    private boolean done = false;
    private List<String> columnNames;
    private boolean askForSave = false;
    private Chart chart;
    private boolean setChart = false;

    public NewChartAction() {
        this(true);
    }

    public NewChartAction(boolean fullName) {
        askForSave = true;
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("new.chart"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("new.chart.small"));
        }
        Icon icon = ImageUtil.getImageIcon("chart_new");
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, ShortcutsUtil.getMnemonic("chart.new.mnemonic",  new Integer('H')));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("chart.new.accelerator", "control H")));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("new.chart.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("new.chart.desc"));
    }

    public NewChartAction(List<String> columnNames) {
        this();
        this.columnNames = columnNames;
    }

    public void actionPerformed(ActionEvent e) {
    	
    	if (NextReportsUtil.isInnerEdit()) {
    		return;
    	}

        if (MessageUtil.showReconnect()) {
            return;
        }

        if (askForSave) {
            if (Globals.isChartLoaded()) {
                if (!NextReportsUtil.saveYesNoCancel(I18NSupport.getString("new.chart"))) {
                    return;
                }
            }
        }

        Globals.setReportLoaded(false);
        Globals.setChartLoaded(false);        

        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
		builderPanel.refreshSql();
		String sql = builderPanel.getUserSql();
		if (!canBePerformed(sql)) {
            Show.info(I18NSupport.getString("new.chart.warning"));
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
                columnNames = qu.getColumnNames(sql, params);
            }
        } catch (Exception ex) {
			Show.error(ex);
			return;
		}

        String duplicateColumn = StringUtil.getFirstDuplicateValue(columnNames);
        if (duplicateColumn != null) {
            Show.info(I18NSupport.getString("new.chart.ambigous.columns", duplicateColumn));
            return;
        }

        Globals.getMainMenuBar().enableLayoutPerspective(true);
		Globals.getMainToolBar().enableLayoutPerspective(true);
        String title = Globals.getCurrentChartName();
        if (title == null) {
            title = LayoutHelper.DEFAULT_CHART_TITLE;
        }

        if (!setChart) {
            chart = new Chart();
            chart.getTitle().setTitle(title);
            if (chart.getReport() == null) {
                chart.setReport(builderPanel.createReport(chart.getName()));
            }
        }
        try {
            if (Globals.getConnection() != null) {
                List<NameType> columns = ReportLayoutUtil.getAllColumnsForReport(chart.getReport());
                Globals.getChartDesignerPanel().getPropertiesPanel().setColumns(columns);
                Globals.getChartLayoutPanel().setColumns(columns);
                Globals.getChartLayoutPanel().refresh();
            }
        } catch (Exception ex) {
            Show.error(ex);
            return;
        }
                
        File defTemplate = new File(Globals.USER_DATA_DIR + "/templates/" + DEFAULT_CHART_TEMPLATE);        
        if (defTemplate.exists()) {        	
        	ChartTemplate template = TemplateManager.loadTemplate(defTemplate);
            TemplateManager.applyGeneralTemplate(chart, template);
            ExtendedColorChooser.loadColorsFromChartTemplate(template);
        }        
        builderPanel.loadChart(chart);

        Globals.getMainMenuBar().newChartActionUpdate();
        Globals.getMainToolBar().newChartActionUpdate();
        Globals.setCurrentChartName(null);
        Globals.setCurrentChartAbsolutePath(null);   

        Globals.setOriginalSql(sql);
		Globals.setChartLoaded(true);
    }

    public boolean canBePerformed(String sql) {
		if (sql.trim().equals("") || sql.equals(SQLViewPanel.DEFAULT_QUERY)) {
			return false;
		} else {
			return true;
		}
	}

    public void setChart(Chart chart) {
        this.chart = chart;
        setChart = true;
    }


}
