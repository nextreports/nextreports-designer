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
package ro.nextreports.designer.action.report.layout.export;

import ro.nextreports.engine.exporter.ExporterBean;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.exporter.event.ExporterEvent;
import ro.nextreports.engine.exporter.event.ExporterEventListener;
import ro.nextreports.engine.exporter.exception.NoDataFoundException;
import ro.nextreports.engine.i18n.I18nLanguage;
import ro.nextreports.engine.i18n.I18nUtil;
import ro.nextreports.engine.util.QueryUtil;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.util.ReportUtil;

import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.EngineProperties;
import ro.nextreports.engine.ReportLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.i18n.action.I18nManager;
import ro.nextreports.designer.querybuilder.ExportPropertiesDialog;
import ro.nextreports.designer.querybuilder.ExportPropertiesPanel;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.UIActivator;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * @author mihai.panaitescu
 */
public abstract class ExportAction extends AbstractAction {
	
	private static final Log LOG = LogFactory.getLog(ExportAction.class);

    protected Report report;
    protected String exportType;

    public static final String REPORTS_DIR = Globals.USER_DATA_DIR + "/reports";
    public boolean layoutSelection = false;

    private Thread executorThread;

    private List<QueryParameter> oldParameters;

    // threshold after which we do something for the notified event
    protected int eventThreshold = 1;
    protected int eventCounter = 0;

    // if we have more than RECORDS, we notify after RECORDS_INCREMENT,
    // otherwise at every record
    private int RECORDS = 10000;
    private int RECORDS_INCREMENT = 50;

    private boolean stop = false;

    public ExportAction(Report report) {
        this(report, false);
    }

    public ExportAction(Report report, boolean layoutSelection) {
        super();
        this.oldParameters = null;
        this.report = report;        
        if (report != null) {
            // a report is opened, we run a report from tree,
            // after running we must set the parameters of the opened report!
            oldParameters = ParameterManager.getInstance().getParameters();            
        }
        this.layoutSelection = layoutSelection;        
    }

    public void actionPerformed(ActionEvent event) {
        executorThread = new Thread(new Runnable() {

            public void run() {

                if (MessageUtil.showReconnect()) {
                    return;
                }

                DataSource runDS = Globals.getReportLayoutPanel().getRunDataSource();

                if (report != null) {
                    ParameterManager.getInstance().setParameters(report.getParameters());
                }

                UIActivator activator = null;
                UIActivator activatorParam = null;
                UIActivator exporterActivator = null;
                QueryResult qr = null;

                activatorParam = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("generate.report.prepare"));
                activatorParam.start();

                try {
                    FileUtil.copyImagesToClasspath(report);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                try {
                    FileUtil.copyTemplateToClasspath(report);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Connection con = null;
                try {
                    Set<String> columns = BandUtil.getNotFoundColumns(report, runDS);
                    if (columns.size() > 0) {
                        StringBuilder message = new StringBuilder();
                        message.append(I18NSupport.getString("band.column.notfound"));
                        message.append("\r\n");
                        for (String col : columns) {
                            message.append(col).append("\r\n");
                        }
                        if (activatorParam != null) {
                            activatorParam.stop();
                        }
                        Show.error(message.toString());
                        return;
                    }
                    
                    Set<String> groupColumns = BandUtil.getNotFoundColumnsUsedByGroups(report, runDS);
                    if (groupColumns.size() > 0) {
                        StringBuilder message = new StringBuilder();
                        message.append(I18NSupport.getString("band.column.group.notfound"));
                        message.append("\r\n");
                        for (String col : groupColumns) {
                            message.append(col).append("\r\n");
                        }
                        if (activatorParam != null) {
                            activatorParam.stop();
                        }
                        Show.error(message.toString());
                        return;
                    }

                    //NextReportsUtil.reloadReportIfNecessary();

                    if (activatorParam != null) {
                        activatorParam.stop();
                        activatorParam = null;
                    }

                    // name when run from explorer tree (without open)
                    String notLoadedName = null;
                    if (report != null)  {
                       notLoadedName = getNameWithoutExtension(report.getName());
                    }

                    ExportPropertiesPanel propsPanel = new ExportPropertiesPanel(notLoadedName, exportType, layoutSelection);
                    ExportPropertiesDialog dialog = new ExportPropertiesDialog(propsPanel);

                    // we are interested only in layout property (generated report name is automatically set)
                    // otherwise we do not show the dialog
                    if (layoutSelection) {
                        Show.centrateComponent(Globals.getMainFrame(), dialog);
                        dialog.setVisible(true);
                    }

                    String name = dialog.getReportName();
                    if (name == null) {
                        return;
                    }

                    if (layoutSelection && !dialog.okPressed() && (report == null)) {
                        return;
                    }                   

                    name = name.trim();
                    if (name.length() == 0) {
                        name = "Report";
                    }

                    ParametersBean pBean = Globals.getMainFrame().getQueryBuilderPanel().selectParameters(report, runDS);
                    if (pBean == null) {
                        return;
                    }                    

                    if (QueryUtil.restrictQueryExecution(pBean.getQuery().getText())) {
                        Show.info(I18NSupport.getString("export.action.execute"));
                        return;
                    }

                    con = Globals.createTempConnection(runDS);   
                    boolean isProcedure = QueryUtil.isProcedureCall(pBean.getQuery().getText());
                    if (isProcedure) {
                        if (!QueryUtil.isValidProcedureCall(pBean.getQuery().getText(), DialectUtil.getDialect(con))) {
                            Show.info(I18NSupport.getString("export.action.execute.procedure"));
                            return;
                        }
                    }

                    activator = new UIActivator(Globals.getMainFrame(),
                            I18NSupport.getString("generate.report"));
                    activator.start(new ExportStopAction());

                    qr = Globals.getMainFrame().getQueryBuilderPanel().runQuery(con, pBean, false);
                    if (activator != null) {
                        activator.stop();
                        activator = null;
                    }
                    if (qr == null) {
                        return;
                    }

                    final int records = qr.getRowCount();
                    if (records > RECORDS) {
                        eventThreshold = RECORDS_INCREMENT;
                    }
                    exporterActivator = new UIActivator(Globals.getMainFrame(),
                            I18NSupport.getString("generate.report.export"), records);
                    exporterActivator.start(new ExportStopAction());

                    //
                    boolean ok = startExporter(name, qr, pBean, exporterActivator, isProcedure);
                    if (!ok) {
                        Show.dispose();  // close a possible previous dialog message
                        Show.info(I18NSupport.getString("report.cancelled"));
                    }
                    //

                } catch (NoDataFoundException e) {
                    Show.info(e.getMessage());
                } catch (InterruptedException e) {
                    Show.dispose();  // close a possible previous dialog message
                    Show.info(I18NSupport.getString("report.cancelled"));
                } catch (Exception e) {
                    Show.error(e);
                } finally {
                    stop = false;
                    if  (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (qr != null) {
                        qr.close();
                    }
                    Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    Globals.getMainFrame().setCursor(normalCursor);
                    if (activatorParam != null) {
                        activatorParam.stop();
                    }
                    if (activator != null) {
                        activator.stop();
                    }
                    if (exporterActivator != null) {
                        exporterActivator.stop();
                    }
                    if (oldParameters != null) {
                        ParameterManager.getInstance().setParameters(oldParameters);
                    }
                }
            }

        }, "NEXT : " + getClass().getSimpleName());
        executorThread.setPriority(EngineProperties.getRunPriority());
        executorThread.start();
    }

    private boolean startExporter(String reportName, QueryResult qr, ParametersBean pBean, 
    		final UIActivator activator, boolean isProcedure) throws Exception {
    	
    	System.gc();
		String fileName = REPORTS_DIR + File.separator + reportName + "." + getFileExtension();
		OutputStream fos = new FileOutputStream(fileName);
        ReportLayout layout = LayoutHelper.getReportLayout();
        if (report != null) {
            layout = report.getLayout();
        }       
        
        Connection con =  Globals.createTempConnection(Globals.getReportLayoutPanel().getRunDataSource());        
        ReportLayout convertedLayout = ReportUtil.getDynamicReportLayout(con, layout, pBean);                        
        
        ExporterBean eb = new ExporterBean(con, Globals.getQueryTimeout(), qr, fos, convertedLayout, pBean, getReportName(), false, isProcedure);
        I18nLanguage language = I18nUtil.getDefaultLanguage(layout);
        if (language != null) {
        	eb.setLanguage(language.getName());
        }
        ResultExporter exporter = getResultExporter(eb);
        exporter.setDocumentTitle(getReportName());
        exporter.addExporterEventListener(new ExporterEventListener() {
            public void notify(final ExporterEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        eventCounter++;
                        if ((eventCounter % eventThreshold == 0) ||
                            (eventCounter == event.getExporterObject().getRecordCount()) ){
                            activator.updateProgress(event.getExporterObject().getRecord(),
                                    event.getExporterObject().getRecord() + "/" +
                                    event.getExporterObject().getRecordCount());
                        }
                    }
                });
            }
        });
        boolean ok = true;
        try {
			ok = exporter.export();
		} catch (NoDataFoundException e) {
			fos.close();
			//
			// Delete bad file?
			if (fos instanceof FileOutputStream) {
				(new File(fileName)).delete();
			}
			throw new NoDataFoundException(I18NSupport.getString("run.nodata"));
		} 
		con.close();
		fos.close();
		System.gc();
        afterExport(fileName, getReportName());
        FileUtil.openFile(fileName, ExportAction.class);
        return ok;
    }
    
    protected abstract String getFileExtension();
    protected abstract ResultExporter getResultExporter(ExporterBean bean);
    protected boolean hasMacro() {
    	return false;
    }
    
    protected void afterExport(String filePath, String reportName)  {    	
    }

    private class ExportStopAction extends AbstractAction {

        public ExportStopAction() {
            super();
            putValue(Action.NAME, I18NSupport.getString("stop.export.action.name"));
            putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("stop_execution"));
            putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("stop.export.action.desc"));
            putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("stop.export.action.desc"));
            putValue(Action.MNEMONIC_KEY, new Integer('S'));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if  (stop) {
                Show.disposableInfo(I18NSupport.getString("stop.wait.second"));
                return;
            } else {
                Show.disposableInfo(I18NSupport.getString("stop.wait"));
            }
            if (executorThread != null) {
                stop = true;
                executorThread.interrupt();
            }
        }
    }
   
    protected String getReportName() {
        String name = Globals.getCurrentReportName();
        if (name == null) {
            name = "";
        }
        return name;
    }

    private String getNameWithoutExtension(String name)  {
        int index = name.lastIndexOf(".");
        if (index == -1)  {
            return name;
        }
        return name.substring(0, index);
    }        
    
}
