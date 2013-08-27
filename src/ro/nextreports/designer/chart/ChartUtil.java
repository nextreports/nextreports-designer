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
package ro.nextreports.designer.chart;

import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartType;
import ro.nextreports.engine.XStreamFactory;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.exporter.util.function.FunctionFactory;
import ro.nextreports.engine.util.ParameterUtil;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.Query;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.querybuilder.SaveEntityDialog;
import ro.nextreports.designer.querybuilder.SaveEntityPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * User: mihai.panaitescu
 * Date: 17-Dec-2009
 * Time: 10:42:54
 */
public class ChartUtil {

    public static final String CHART_EXTENSION_SEPARATOR = ".";
    public static final String CHART_EXTENSION = "chart";
    public static final String CHART_FULL_EXTENSION = CHART_EXTENSION_SEPARATOR + CHART_EXTENSION;
    private static Log LOG = LogFactory.getLog(ChartUtil.class);

    public static boolean chartUndefined() {
        return chartUndefined(null);
    }

    public static boolean allParametersHidden(Chart chart) {
        Map<String, QueryParameter> params = ParameterManager.getInstance().getUsedParametersMap(
                new Query(ReportUtil.getSql(chart.getReport())));
                //new Query(Globals.getMainFrame().getQueryBuilderPanel().getUserSql()));
        if (ParameterUtil.allParametersAreHidden(params)){
            return true;
        } else {
            Show.info(I18NSupport.getString("parameter.hidden.restriction"));
            return false;
        }
    }

    public static boolean allParametersHaveDefaults(Chart chart) {
        Map<String, QueryParameter> params = ParameterManager.getInstance().getUsedParametersMap(
                new Query(ReportUtil.getSql(chart.getReport())));
                //new Query(Globals.getMainFrame().getQueryBuilderPanel().getUserSql()));
        if (ParameterUtil.allParametersHaveDefaults(params)){
            return true;
        } else {
            Show.info(I18NSupport.getString("parameter.default.restriction"));
            return false;
        }
    }

    public static boolean chartUndefined(Chart chart) {
        if (chart == null) {
            chart = Globals.getChartDesignerPanel().getChart();
        }
        if (chart.getType() == null) {
            Show.info(I18NSupport.getString("chart.undefined.type"));
            return true;
        }
        if  (chart.getXColumn() == null) {
            Show.info(I18NSupport.getString("chart.undefined.xcolumn"));
            return true;
        }
        if ((chart.getYColumns() == null) || (chart.getYColumns().size() == 0)) {
        	if (chart.getYColumnQuery() == null) {
        		Show.info(I18NSupport.getString("chart.undefined.ycolumn"));
        		return true;
        	}
        }

		if (chart.getYColumns() != null) {
			if ((chart.getType().getType() == ChartType.PIE) && (getNotNullCount(chart.getYColumns()) > 1)) {
				Show.info(I18NSupport.getString("chart.undefined.ycolumn.toomany"));
				return true;
			}

			if ((chart.getYColumns().size() > 1) && (!FunctionFactory.isCountFunction(chart.getYFunction()))) {
				for (String column : chart.getYColumns()) {
					if (Globals.getChartLayoutPanel().getMarked(column)) {
						Show.info(I18NSupport.getString("chart.undefined.ycolumn.type.error", column));
						return true;
					}
				}
			}
		}

        if (!allParametersHaveDefaults(chart)) {
            return true;
        }

//        try {
//            Report report = chart.getReport();
//            String sql;
//            if (report != null) {
//                if (report.getSql() != null) {
//                    sql = report.getSql();
//                } else {
//                    sql = report.getQuery().toString();
//                }
//                List<NameType> columns = Globals.getChartDesignerPanel().getPropertiesPanel().getColumns();
//                NameType found = null;
//                for (NameType nt : columns) {
//                    if  (nt.getName().equals(chart.getYColumn())) {
//                        found = nt;
//                        break;
//                    }
//                }
//                //String columnType = ReportLayoutUtil.getColumnTypeForReportColumn(sql, chart.getYColumn());
//                String columnType = found.getType();
//                //System.out.println("*** yColumnType=" + columnType);
//                if (!Number.class.isAssignableFrom(Class.forName(columnType))) {
//                    Show.info(I18NSupport.getString("chart.undefined.ycolumn.type"));
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return false;
    }

    public static Chart loadChart(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            return loadChart(fis);
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();  
                }
            }
        }
    }

    public static Chart loadChart(InputStream is) {
        XStream xstream = XStreamFactory.createXStream();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(is, "UTF-8");
            return (Chart) xstream.fromXML(reader);
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    public static String saveChart(String title, boolean as) {
        return saveChart(title, as, null);
    }

    public static String saveChart(String title, boolean as, Chart chart) {
       if (!as && (Globals.getCurrentChartAbsolutePath() != null)) {
            File file = new File(Globals.getCurrentChartAbsolutePath());
            save(file, chart);
            return getChartFileName(file);
        } else {
            return askSave(title, chart);
        }
    }

    public static String getChartFileName(String name) {
        return name.substring(0, name.indexOf(CHART_FULL_EXTENSION));
    }

    public static String getChartFileName(File file) {
        return getChartFileName(file.getName());
    }

    public static boolean save(File file, Chart chart) {
        try {
            saveXStream(file, chart);
            return true;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }


    private static void saveXStream(File file, Chart chart) throws Exception {
        XStream xstream = XStreamFactory.createXStream();
        FileOutputStream fos = new FileOutputStream(file);
        if (chart == null) {
            chart = Globals.getChartDesignerPanel().getChart();
            chart.setVersion(ReleaseInfoAdapter.getVersionNumber());
            chart.setName(getChartFileName(file));
            Report query = ro.nextreports.designer.Globals.getMainFrame().getQueryBuilderPanel().createReport(file.getName());
            chart.setReport(query);
        }
        xstream.toXML(chart, fos);
        fos.close();
    }

    private static String askSave(String title, Chart report) {
        SaveEntityPanel savePanel = new SaveEntityPanel(I18NSupport.getString("save.chart"), DBObject.CHARTS_GROUP);
        SaveEntityDialog dialog = new SaveEntityDialog(title, savePanel, I18NSupport.getString("chart"), true);
        dialog.pack();
        Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.requestFocus();
        dialog.setVisible(true);

        String name = null;
        if (dialog.okPressed()) {
            Globals.setCurrentChartAbsolutePath(null);
            name = savePanel.getName();
        } else {
            return null;
        }
        if (name == null) {
            return null;
        }

        File parent = new File(FileReportPersistence.getConnectedDataSourceRelativePath());
        if (!parent.exists()) {
            parent.mkdirs();
            new File(FileReportPersistence.getQueriesRelativePath()).mkdirs();
            new File(FileReportPersistence.getReportsRelativePath()).mkdirs();
            new File(FileReportPersistence.getChartsRelativePath()).mkdirs();
        }
        
        name = savePanel.getFolderPath() + File.separator + name + ChartUtil.CHART_FULL_EXTENSION;        
        File selectedFile = new File(name);
        
		if (!name.endsWith(ChartUtil.CHART_FULL_EXTENSION)) {
			selectedFile = new File(selectedFile.getAbsolutePath() + ChartUtil.CHART_FULL_EXTENSION);
		}
		Globals.setCurrentChartAbsolutePath(selectedFile.getAbsolutePath());
		save(selectedFile, report);
		if (dialog.isOverwrite()) {
			return null;
		}
		return getChartFileName(selectedFile);        
        
    }

    public static boolean renameChart(String oldName, String newName, String parentPath) {        		
		File file = new File(parentPath+ File.separator + oldName + CHART_FULL_EXTENSION);
        File newFile = new File(parentPath + File.separator + newName + CHART_FULL_EXTENSION);
        boolean result = file.renameTo(newFile);
        if (result) {
            if (file.getAbsolutePath().equals(Globals.getCurrentChartAbsolutePath())) {
                Globals.setCurrentChartAbsolutePath(newFile.getAbsolutePath());
            }
            // change name in xml
            Chart chart = ChartUtil.loadChart(newFile.getAbsolutePath());
            chart.setName(newName + CHART_FULL_EXTENSION);
            try {
				saveXStream(newFile, chart);
			} catch (Exception e) {				
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
			}
        }
        return result;
    }

    private static int getNotNullCount(List<?> list) {
        if (list == null) {
            return 0;
        }
        int count = 0;
        for (Object obj : list) {
            if (obj != null) {
                count++;
            }
        }
        return count;
    }
    
    /** Test (by name) if chart parameters types are incompatible with report parameter types 
        (where we want the chart to be inserted)
        @return list of incompatible parameters        
     */   
    public static List<String> incompatibleParametersType(Chart chart) {
    	ParameterManager paramManager = ParameterManager.getInstance();
		List<QueryParameter> reportParams = paramManager.getParameters();
		List<QueryParameter> chartParams = chart.getReport().getParameters();
		List<String> result = new ArrayList<String>();
		for (QueryParameter cqp : chartParams) {
			for  (QueryParameter rqp : reportParams) {
				if (rqp.getName().equals(cqp.getName())) {
					if (!rqp.getValueClassName().equals(cqp.getValueClassName())){
						result.add(rqp.getName());
					} else if (!rqp.getSelection().equals(cqp.getSelection())) {
						result.add(rqp.getName());
					}
				}
			}
		}
		return result;
    }
    
}
