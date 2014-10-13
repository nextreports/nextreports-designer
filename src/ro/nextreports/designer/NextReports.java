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
package ro.nextreports.designer;

import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.action.ExitAction;
import ro.nextreports.designer.action.datasource.AddDataSourceAction;
import ro.nextreports.designer.action.help.HelpMovieAction;
import ro.nextreports.designer.action.help.SurveyAction;
import ro.nextreports.designer.action.query.OpenQueryPerspectiveAction;
import ro.nextreports.designer.action.report.OpenReportPerspectiveAction;
import ro.nextreports.designer.action.report.WizardAction;
import ro.nextreports.designer.action.tools.ImportAction;
import ro.nextreports.designer.action.tools.LanguageAction;
import ro.nextreports.designer.ui.GlobalHotkeyManager;
import ro.nextreports.designer.ui.vista.VistaButton;
import ro.nextreports.designer.ui.vista.VistaDialog;
import ro.nextreports.designer.ui.vista.VistaDialogContent;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.LicenseUtil;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.SplashScreen;

import ro.nextreports.engine.EngineProperties;
import ro.nextreports.engine.exporter.PdfExporter;
import ro.nextreports.engine.querybuilder.sql.dialect.OracleDialect;
import ro.nextreports.engine.util.DateUtil;
import ro.nextreports.engine.util.FontUtil;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

/**
 * @author Decebal Suiu
 */
public class NextReports  {

    private static final Log LOG = LogFactory.getLog(NextReports.class);
   
    private static NextReports singleton;
    private volatile boolean stop = false;

//    private License license;

    private NextReports() {
    }

    public static NextReports getInstance() {
        if (singleton == null) {
            singleton = new NextReports();
        }

        return singleton;
    }

    /*
    public License getLicense() {
        return license;
    }
    */

    void init() {
        long time = System.currentTimeMillis();

        // set look and feel
        setLookAndFeel();

        // init language
        // here is initialized ReporterPreferencesManager!
        new LanguageAction(null).initLanguage();

        // check the license
        /*
        try {
            license = LicenseManager.getInstance().getLicense();
            Globals.setDataSources(Integer.parseInt(license.getFeature("dataSources")));
            // @todo we removed the property from template signature file
            //Globals.setReports(Integer.parseInt(license.getFeature("reports")));
            Globals.setReports(100000);

            if (!LicenseManager.getInstance().isValidLicense(license)) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        new ImportLicenseAction(I18NSupport.getString("invalid.license")).actionPerformed(null);
                        try {
                            license = LicenseManager.getInstance().getLicense();
                            if (!LicenseManager.getInstance().isValidLicense(license)) {
                                JOptionPane.showMessageDialog(Globals.getMainFrame(), I18NSupport.getString("invalid.license"));
                                System.exit(1);
                            }
                            Globals.setDataSources(Integer.parseInt(license.getFeature("dataSources")));
                            //@todo
                            //Globals.setReports(Integer.parseInt(license.getFeature("reports")));
                            Globals.setReports(100000);
                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);                            
                            System.exit(1);
                        }
                    }
                });
            }
        } catch (LicenseNotFoundException e) {
            new ImportLicenseAction(I18NSupport.getString("license.notfound")).actionPerformed(null);
            try {
                license = LicenseManager.getInstance().getLicense();
                if (!LicenseManager.getInstance().isValidLicense(license)) {
                    JOptionPane.showMessageDialog(Globals.getMainFrame(), I18NSupport.getString("invalid.license"));
                    System.exit(1);
                }
                Globals.setDataSources(Integer.parseInt(license.getFeature("dataSources")));
                //@todo
                //Globals.setReports(Integer.parseInt(license.getFeature("reports")));
                Globals.setReports(100000);
            } catch (Exception ex) {
                LOG.error(e.getMessage(), ex);                
                System.exit(1);
            }            
        } catch (LicenseException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }
        */
        Globals.setDataSources(Integer.MAX_VALUE);
        Globals.setReports(Integer.MAX_VALUE);

        time = System.currentTimeMillis() - time;
        LOG.info("Init in " + time + " ms");
    }


    void start() {
        long time = System.currentTimeMillis();

        final SplashScreen splash = new SplashScreen("splash");

        Runnable runnable = new Runnable() {

            public void run() {
                for (int i = 0; i <= 100; i += 5) {
                    if (stop) {
                        break;
                    }
                    splash.updateSplash(i);
                    try {
                        Thread.sleep(250);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(runnable);
        t.start();

        // redirect output
        redirectOutput();

        // set engine properties
        System.setProperty(EngineProperties.RUN_PRIORITY_PROPERTY, String.valueOf(Thread.MIN_PRIORITY));
        System.setProperty(EngineProperties.RECORDS_YIELD_PROPERTY, String.valueOf(5000));
        System.setProperty(EngineProperties.MILLIS_YIELD_PROPERTY, String.valueOf(100));

        // set pdf font and encoding
        Globals.setPdfEncoding();
        Globals.setPdfFont();
        // set pdf direction and arabic
        Globals.setPdfDirection();
        Globals.setPdfArabicOptions();

        // set oracle client path
        Globals.setOracleClientPath();

        // set locale
        Globals.setLocale();
        
        FontUtil.registerFonts(Globals.getFontDirectories());

        // print parameters
        printParameters();

        // create the main frame
        final MainFrame mainFrame = createMainFrame();

        // add global actions
        GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
        InputMap inputMap = hotkeyManager.getInputMap();
        ActionMap actionMap = hotkeyManager.getActionMap();
        Action queryPerspectiveAction = new OpenQueryPerspectiveAction();
        inputMap.put((KeyStroke) queryPerspectiveAction.getValue(Action.ACCELERATOR_KEY), "queryPerspective");
        actionMap.put("queryPerspective", queryPerspectiveAction);
        Action reportPerspectiveAction = new OpenReportPerspectiveAction();
        inputMap.put((KeyStroke) reportPerspectiveAction.getValue(Action.ACCELERATOR_KEY), "reportPerspective");
        actionMap.put("reportPerspective", reportPerspectiveAction);

        disposeSplash(splash);

        mainFrame.setVisible(true);

        time = System.currentTimeMillis() - time;
        LOG.info("Start in " + time + " ms");

        // do not show start dialog if we start from server
        if (Globals.getServerUrl() == null) {        	
        	showStartDialog(true);
        	showSurveyDialog();
        } else {        	
        	new NextReportsServerRequest();
        }
    }

    private void disposeSplash(SplashScreen splash) {
        stop = true;
        splash.updateSplash(100);
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        splash.dispose();
    }

    boolean checkWrite() {
        try {
            String testFile = Globals.USER_DATA_DIR + File.separator +  "test.temp";
            File file = new File(testFile);
            boolean ok = file.createNewFile();
            if (ok) {
               file.delete();
            }
            return ok;
        } catch (IOException ex) {
            return false;
        }
    }

    public static MainFrame createMainFrame() {
        // create the main frame
        final MainFrame mainFrame = new MainFrame("NextReports " + ReleaseInfoAdapter.getVersion() + LicenseUtil.getEdition());

        // load preferences
        final ReporterPreferencesManager preferencesManager = ReporterPreferencesManager.getInstance();
        Rectangle bounds = preferencesManager.loadBoundsForWindow(MainFrame.class);

        if (bounds == null) {
            mainFrame.pack();
        } else {
            mainFrame.setBounds(bounds);
        }

        // centrate the main frame
        mainFrame.setLocationRelativeTo(null);

        // store (on close) and load (on start) the docking manager workspace.
        // store preferences and stop log
        mainFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                try {
                    new ExitAction().actionPerformed(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error(e.getMessage(), e);
                }
            }

        });

        try {
            WorkspaceManager.getInstance().restoreWorkspaces();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }

        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        return mainFrame;
    }

    public static void showStartDialog(boolean test) {

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        final JCheckBox chk = new JCheckBox(I18NSupport.getString("start.panel.show.startup"), true);
        bottomPanel.add(chk);
        bottomPanel.add(Box.createHorizontalGlue());
        String start = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.SHOW_AT_STARTUP + "_" + ReleaseInfo.getVersion());
        if (test && "false".equals(start)) {
            return;
        }

        HelpMovieAction helpAction = new HelpMovieAction();
        VistaButton buttonHelp = new VistaButton(helpAction, I18NSupport.getString("start.panel.help"));
        ImportAction importAction = new ImportAction();
        VistaButton buttonImport = new VistaButton(importAction, I18NSupport.getString("start.panel.import"));
        AddDataSourceAction dataSourceAction = new AddDataSourceAction();
        VistaButton buttonDataSource = new VistaButton(dataSourceAction, I18NSupport.getString("start.panel.datasource"));
        WizardAction wizardAction = new WizardAction(Globals.getMainFrame().getQueryBuilderPanel().getTree());
        VistaButton buttonWizard = new VistaButton(wizardAction, I18NSupport.getString("start.panel.wizard"));

        List<VistaButton> list = new ArrayList<VistaButton>();
        list.add(buttonHelp);
        list.add(buttonImport);
        list.add(buttonDataSource);
        list.add(buttonWizard);

        VistaDialogContent content = new VistaDialogContent(list, I18NSupport.getString("start.panel.title"),
                I18NSupport.getString("start.panel.subtitle"));
        VistaDialog dialog = new VistaDialog(content, bottomPanel, Globals.getMainFrame(), true) {
        	
			private static final long serialVersionUID = 1L;

			@Override
            protected void beforeDispose() {
                String show = chk.isSelected() ? "true" : "false";
                ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.SHOW_AT_STARTUP + "_" + ReleaseInfo.getVersion(), show);
            }
            
        };
        dialog.setTitle(I18NSupport.getString("menu.startup"));
        dialog.selectButton(buttonHelp);

        dialog.setDispose(true);
        dialog.setEscapeOption(true);

        dialog.pack();
        dialog.setResizable(false);
        Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.setVisible(true);
    }
    
    public static void showSurveyDialog() {
    	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    	
        String initDate = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.INIT_DATE + "_" + ReleaseInfo.getVersion());
        if (initDate == null) {
        	ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.INIT_DATE + "_" + ReleaseInfo.getVersion(), df.format(new Date()));
        } else {        	 
        	
        	String surveyTaken = ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.SURVEY_TAKEN + "_" + ReleaseInfo.getVersion());
        	if ("true".equals(surveyTaken)) {
        		return;
        	}
        	
        	Date currentDate = new Date();
        	Date startDate = currentDate;
        	try {
				startDate = df.parse(initDate);
			} catch (ParseException e) {
				// should never happen
				LOG.error("Survey: Parse INIT_DATE failed: " + initDate);				
			}
        	
        	int[] array = DateUtil.getElapsedTime(startDate, currentDate);
        	// ten days passed
        	if ((array != null) && (array[0] >= 10)) {
        		SurveyAction surveyAction = new SurveyAction();
    	        VistaButton buttonSurvey = new VistaButton(surveyAction, I18NSupport.getString("start.panel.survey"));	        
    	
    	        List<VistaButton> list = new ArrayList<VistaButton>();
    	        list.add(buttonSurvey);
    	      
    	        VistaDialogContent content = new VistaDialogContent(list, I18NSupport.getString("start.panel.survey.title"),
    	                I18NSupport.getString("start.panel.survey.subtitle"));
    	        VistaDialog dialog = new VistaDialog(content, Globals.getMainFrame(), true) {
    	        	
    				private static final long serialVersionUID = 1L;
    	
    				@Override
    	            protected void beforeDispose() {	                
    	                ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.SURVEY_TAKEN + "_" + ReleaseInfo.getVersion(), "true");
    	            }
    	            
    	        };	        
    	        dialog.selectButton(buttonSurvey);
    	
    	        dialog.setDispose(true);
    	        dialog.setEscapeOption(true);
    	
    	        dialog.pack();
    	        dialog.setResizable(false);
    	        Show.centrateComponent(Globals.getMainFrame(), dialog);
    	        dialog.setVisible(true);
        	}
        		        
        }
    }

    void shutdown() {
        long time = System.currentTimeMillis();

        Connection con = Globals.getConnection();
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }

        time = System.currentTimeMillis() - time;
        LOG.info("Shutdown in " + time + " ms");
    }

    void printReleaseInfo() {
        LOG.info("#####################################");
        LOG.info("Product: " + ReleaseInfo.getProject());
        LOG.info("Company: " + ReleaseInfo.getCompany());
        LOG.info("Web: " + ReleaseInfo.getHome());
        LOG.info("Engine version: "  + ro.nextreports.engine.ReleaseInfoAdapter.getVersionNumber());
        LOG.info("Version: " + ReleaseInfoAdapter.getVersion());
        LOG.info("Build: " + ReleaseInfo.getBuildNumber() + " (" + ReleaseInfo.getBuildDate() + ")");
        LOG.info("Java Version: " + Globals.getJavaVersion());
        LOG.info("#####################################");
    }

    private void printParameters() {
        LOG.info("-------------------------------------");
        LOG.info("Next Reports Parameters : ");
        LOG.info("nextreports.user.data" + " = " + System.getProperty("nextreports.user.data"));
        LOG.info("nextreports.log" + " = " + System.getProperty("nextreports.log"));
        LOG.info("spy.log" + " = " + System.getProperty("spy.log"));
        LOG.info("db.spy" + " = " + Globals.getConfig().getString("db.spy"));
        LOG.info("console.file" + " = " + Globals.getConfig().getString("console.file"));
        LOG.info("singlesource.autoconnect" + " = " + Globals.singleSourceAutoConnect());
        LOG.info(PdfExporter.PDF_ENCODING_PROPERTY + " = " + System.getProperty(PdfExporter.PDF_ENCODING_PROPERTY));
        LOG.info(PdfExporter.PDF_FONT_PROPERTY + " = " + System.getProperty(PdfExporter.PDF_FONT_PROPERTY));
        LOG.info(PdfExporter.PDF_DIRECTION + " = " + System.getProperty(PdfExporter.PDF_DIRECTION));
        LOG.info(PdfExporter.PDF_ARABIC_OPTIONS + " = " + System.getProperty(PdfExporter.PDF_ARABIC_OPTIONS));
        LOG.info("font.directories" + " = " + Arrays.asList(Globals.getFontDirectories()));
        LOG.info(OracleDialect.ORACLE_CLIENT_PROPERTY + " = " + System.getProperty(OracleDialect.ORACLE_CLIENT_PROPERTY));
        LOG.info("query.timeout" + " = " + Globals.getQueryTimeout());
        LOG.info("max.rows.checked" + " = " + Globals.isMaxChecked());
        LOG.info("accessibility.html" + " = " + Globals.getAccessibilityHtml());
        LOG.info("A4.warning" + " = " + Globals.getA4Warning());
        LOG.info("csv.delimiter" + " = " + Globals.getCsvDelimiter());
        LOG.info("ruler.isVisible" + " = " + Globals.isRulerVisible());
        LOG.info("ruler.unit" + " = " + Globals.getRulerUnit());
        LOG.info("chart.webserver.port" + " = " + Globals.getChartWebServerPort());
        LOG.info("locale" + " = " + Locale.getDefault());
        LOG.info(EngineProperties.RUN_PRIORITY_PROPERTY + " = " + EngineProperties.getRunPriority());
        LOG.info(EngineProperties.RECORDS_YIELD_PROPERTY + " = " + EngineProperties.getRecordsYield());
        LOG.info(EngineProperties.MILLIS_YIELD_PROPERTY + " = " + EngineProperties.getMillisYield());
        LOG.info("-------------------------------------");
    }

    private void setLookAndFeel() {
        try {
            PlasticLookAndFeel.setCurrentTheme(new ExperienceBlue());
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }
    }

    private void redirectOutput() {
        if (Globals.getConfig().getBoolean("console.redirect", false)) {
            try {
                String file =  Globals.getConfig().getString("console.file", Globals.USER_DATA_DIR + File.separator + "/logs/console.log");
                if (file.startsWith("./")) {
                	file = file.substring(1);
                }
                file =  Globals.USER_DATA_DIR  + file;
                LOG.info("Redirect output to '" + file + "'");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file, true));
                PrintStream ps = new PrintStream(os);
                System.setOut(ps);
                System.setErr(ps);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error(e.getMessage(), e);
            }
        }
    }
        	
}
