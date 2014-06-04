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
package ro.nextreports.designer.util;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.ParameterUtil;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.util.StringUtil;
import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.i18n.I18nString;
import ro.nextreports.engine.chart.Chart;

import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.FormSaver;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.chart.SaveChartAction;
import ro.nextreports.designer.action.query.SaveQueryAction;
import ro.nextreports.designer.action.report.NewReportAction;
import ro.nextreports.designer.action.report.SaveReportAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.querybuilder.RuntimeParametersDialog;
import ro.nextreports.designer.querybuilder.RuntimeParametersPanel;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jul 31, 2006
 * Time: 11:17:37 AM
 */
public class NextReportsUtil {
	
	private static final Log LOG = LogFactory.getLog(NextReportsUtil.class);

    /**
     * Helper method which is used when a query from a report is modified
     * @return true if the query has to be modified, and in this case
     * a new report will be regenerated
     */
    public static boolean doQueryOperation() {
        if (Globals.isReportLoaded()) {
            Object[] options = {I18NSupport.getString("report.util.yes"), I18NSupport.getString("report.util.no")};
            int option = JOptionPane.showOptionDialog(Globals.getMainFrame(),
                    I18NSupport.getString("report.regenerated"), I18NSupport.getString("report.util.confirm"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[1]);

            return (option == JOptionPane.YES_OPTION);
        } else {
            return true;
        }
    }

    /**
     * Helper method for report recreation
     * @return true if report will be recreated
     */
    public static boolean reportRecreation() {
            if (Globals.isReportLoaded()) {
                Object[] options = {I18NSupport.getString("report.util.yes"), I18NSupport.getString("report.util.no")};
                int option = JOptionPane.showOptionDialog(Globals.getMainFrame(),
                        I18NSupport.getString("report.util.query.modified"), I18NSupport.getString("report.util.confirm"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[1]);

                return (option == JOptionPane.YES_OPTION);
            } else {
                return true;
            }
        }


    /**
     * Helper method to recreate a report if the query inside it was modified
     */
    private static void recreateReport() {
        if (Globals.isReportLoaded()) {
            new NewReportAction(true).actionPerformed(null);
        }
    }
    
    public static boolean saveYesNoCancel(String titleMessage) {
    	return saveYesNoCancel(titleMessage, JOptionPane.YES_NO_CANCEL_OPTION);
    }
    
    // return false if save operation is cancelled (closed)
    // return true if user selects YES or NO (saves or not the current report)
    public static boolean saveYesNoCancel(String titleMessage, int options ) {
        final QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        if (!builderPanel.isCleaned()) {
            String message;
            if (Globals.isChartLoaded()) {
                if (!chartModification()) {
                    return true;
                }
                message = I18NSupport.getString("existing.chart.save");
            } else if (Globals.isReportLoaded()) {
                if (!reportModification()) {
                    return true;
                }
                message = I18NSupport.getString("existing.report.save");
            } else {
                if  (!queryModification()) {
                    return true;
                }
                message = I18NSupport.getString("existing.query.save");
            }

            int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                    message, titleMessage, options);
            if ((option == JOptionPane.CANCEL_OPTION) || (option == JOptionPane.CLOSED_OPTION)) {
                return false;
            } else if (option == JOptionPane.YES_OPTION) {
                if (Globals.isChartLoaded()) {
                    new SaveChartAction().actionPerformed(null);
                } else if (Globals.isReportLoaded()) {
                    new SaveReportAction().actionPerformed(null);
                } else {
                    new SaveQueryAction().actionPerformed(null);
                }
            }
        }
        return true;
    }

    public static boolean saveYesNoCancel() {
        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        if (!builderPanel.isCleaned()) {
            String message;
            if (Globals.isChartLoaded()) {
                if (!chartModification()) {
                    return true;
                }
                message = I18NSupport.getString("existing.chart.save");
            } else if (Globals.isReportLoaded()) {
                if (!reportModification()) {
                    return true;
                }
                message = I18NSupport.getString("existing.report.save");
            } else {
                if (!queryModification()) {
                    return true;
                }
                message = I18NSupport.getString("existing.query.save");
            }

            int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                    message, I18NSupport.getString("exit"), JOptionPane.YES_NO_CANCEL_OPTION);
            if ((option == JOptionPane.CANCEL_OPTION) || (option == JOptionPane.CLOSED_OPTION)) {
                return false;
            } else if (option == JOptionPane.YES_OPTION) {
                if (Globals.isChartLoaded()) {
                    SaveChartAction action = new SaveChartAction();
                    action.actionPerformed(null);
                    if(action.isCancel()) {
                        return false;
                    }
                } else if (Globals.isReportLoaded()) {
                    SaveReportAction action = new SaveReportAction();
                    action.actionPerformed(null);
                    if(action.isCancel()) {
                        return false;
                    }
                } else {
                    SaveQueryAction action = new SaveQueryAction();
                    action.actionPerformed(null);
                    if(action.isCancel()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
	public static boolean saveReportForInserting(String message) {
		QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
		if (!builderPanel.isCleaned()) {			 
			if (Globals.isReportLoaded()) {
				if (!reportModification()) {
					return true;
				}				 
				int option = JOptionPane.showConfirmDialog(
						Globals.getMainFrame(), message,
						I18NSupport.getString("exit"),
						JOptionPane.YES_NO_OPTION);
				if ((option == JOptionPane.NO_OPTION)|| (option == JOptionPane.CLOSED_OPTION)) {
					return false;
				} else if (option == JOptionPane.YES_OPTION) {
					SaveReportAction action = new SaveReportAction();
					action.actionPerformed(null);
					if (action.isCancel()) {
						return false;
					}
				}
			}
		}
		return true;
	}

    private static boolean reportModification() {
        String loadedFilePath = Globals.getCurrentReportAbsolutePath();
        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        if (loadedFilePath != null) {            
            Report reportLoaded = FormLoader.getInstance().load(loadedFilePath);
            if (reportLoaded == null) {
                return true;
            }
            reportLoaded.setName(FormSaver.getInstance().getReportFileName(reportLoaded.getName()));
            Report reportToSave = builderPanel.createReport(Globals.getCurrentReportName());
            reportToSave.setLayout(LayoutHelper.getReportLayout());
            if ((reportLoaded != null) && reportLoaded.equals(reportToSave)) {
                return false;
            } 
        }
        return true;
    }

    private static boolean chartModification() {
        String loadedFilePath = Globals.getCurrentChartAbsolutePath();
        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();
        if (loadedFilePath != null) {
            Chart chartLoaded = ChartUtil.loadChart(loadedFilePath);
            if (chartLoaded == null) {
                return true;
            }
            //chartLoaded.setName(ChartUtil.getChartFileName(chartLoaded.getName()));
            Chart chartToSave = Globals.getChartDesignerPanel().getChart();                     
            chartToSave.setReport(builderPanel.createReport(chartToSave.getReport().getName()));
            if ((chartLoaded != null) && chartLoaded.equals(chartToSave)) {
                return false;
            }
        }
        return true;
    }

    private static boolean queryModification() {
        QueryBuilderPanel builderPanel = Globals.getMainFrame().getQueryBuilderPanel();        
        String queryPath = Globals.getCurrentQueryAbsolutePath();
        if (queryPath != null) {
            ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                    Globals.getReportPersistenceType());
            Report reportLoaded = repPersist.loadReport(queryPath);
            Report reportToSave = builderPanel.createReport(Globals.getCurrentQueryName());
            if ((reportLoaded != null) && reportLoaded.equals(reportToSave)) {
                return false;
            } 
        }
        return true;
    }

    public static String getSql(Report report) {
        String sql;
        if (report.getSql() != null) {
            sql = report.getSql();
        } else {
            sql = report.getQuery().toString();
        }
        return sql;
    }

    public static ParametersBean selectParameters(Report report, DataSource runDS) {
        String sql = getSql(report);
        return selectParameters(sql, runDS);
    }

    public static ParametersBean selectParameters(String sql, DataSource runDS) {
        if (sql == null) {
            return null;
        }
        Query query = new Query(sql);
        //String[] paramNames = query.getParameterNames();
        Map<String, QueryParameter> params = new LinkedHashMap<String, QueryParameter>();
        Map<String, Object> paramValues = new HashMap<String, Object>();

        // used in query or just hidden (may not be used anywhere)
        params = ParameterManager.getInstance().getUsedParametersMap(query);        
        if (params.size() > 0) {

            final UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("run.load.parameters"));
            activator.start();

            if (ParameterUtil.allParametersAreHidden(params)) {
                // no parameter to show at runtime
                if (activator != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            activator.stop();
                        }
                    });
                }
            } else {
            	boolean error = false;
            	RuntimeParametersPanel runtimePanel = null;
            	try {
                    runtimePanel = new RuntimeParametersPanel(params, runDS);            		
            	} catch (Throwable e) {            		
            		LOG.error(e.getMessage(), e);
            		error = true;
				}

                if (error || ((runtimePanel != null) && runtimePanel.isError())) {
                    if (activator != null) {
                        SwingUtilities.invokeLater(new Runnable() {
                        	
                            public void run() {
                                activator.stop();
                            }
                            
                        });
                    }
                    
                    return null;
                }
                final RuntimeParametersDialog runtimeDialog = new RuntimeParametersDialog(runtimePanel);
                final ReporterPreferencesManager lpm = ReporterPreferencesManager.getInstance();
                Rectangle bounds = lpm.loadBoundsForWindow(RuntimeParametersDialog.class);
                runtimeDialog.addWindowListener(new WindowAdapter() {
                    public void windowClosed(WindowEvent event) {
                        lpm.storeBoundsForWindow(RuntimeParametersDialog.class, runtimeDialog.getBounds());
                    }
                });
                if (bounds != null) {
                    runtimeDialog.setBounds(bounds);
                } else {
                    runtimeDialog.pack();
                    Show.centrateComponent(Globals.getMainFrame(), runtimeDialog);
                }

                if (activator != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            activator.stop();
                        }
                    });
                }

                runtimeDialog.setVisible(true);
                if (runtimeDialog.okPressed()) {
                    paramValues = runtimeDialog.getParametersValues();
                } else {
                    return null;
                }
            }
        }
        return new ParametersBean(query, params, paramValues);
    }
    
	/**
	 * Test (by name) if sub-report parameters types are incompatible with
	 * report parameter types (where we want the sub-report to be inserted)
	 * 
	 * @return list of incompatible parameters
	 */
	public static List<String> incompatibleParametersType(Report report) {
		ParameterManager paramManager = ParameterManager.getInstance();
		List<QueryParameter> reportParams = paramManager.getParameters();
		List<QueryParameter> chartParams = report.getParameters();
		List<String> result = new ArrayList<String>();
		for (QueryParameter cqp : chartParams) {
			for (QueryParameter rqp : reportParams) {
				if (rqp.getName().equals(cqp.getName())) {
					if (!rqp.getValueClassName().equals(cqp.getValueClassName())) {
						result.add(rqp.getName());
					} else if (!rqp.getSelection().equals(cqp.getSelection())) {
						result.add(rqp.getName());
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Helper method to notify user if he is editing an inner report or chart
	 * 	
	 * @return true if an inner report or chart is loaded
	 */
	public static boolean isInnerEdit() {
		boolean isInner = Globals.isInner();
		if (isInner) {
			JOptionPane.showMessageDialog(Globals.getMainFrame(), I18NSupport.getString("inner.edit"));
		}
		return isInner;
	}
	
	/**
	 * Get i18n keys for current loaded report
	 * 
	 * @return a set of i18n keys
	 */
	public static List<String> getReportKeys() {
		Set<String> keys = new TreeSet<String>();		
		if (Globals.isReportLoaded()) {
			keys.addAll(ReportUtil.getKeys(LayoutHelper.getReportLayout()));
		}	
		
		List<QueryParameter> params = ParameterManager.getInstance().getParameters();
		for (QueryParameter p : params) {
			if (p.getRuntimeName().contains(I18nString.MARKUP)) {
				keys.add(StringUtil.getKey(p.getRuntimeName()));
			}
		}		
		List<String> result = new ArrayList(keys);
		Collections.sort(result, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {				
				return Collator.getInstance().compare(o1, o2);
			}
		});
		return result;				
	}
}
