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

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import ro.nextreports.designer.chart.ChartDesignerPanel;
import ro.nextreports.designer.chart.ChartLayoutPanel;
import ro.nextreports.designer.config.Config;
import ro.nextreports.designer.config.ConfigFactory;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.exception.ConnectionException;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;
import ro.nextreports.designer.dbviewer.common.DBViewer;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.ui.eventbus.BusListener;
import ro.nextreports.designer.ui.eventbus.DefaultEventBus;
import ro.nextreports.designer.ui.eventbus.EventBus;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.engine.exporter.PdfExporter;
import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.querybuilder.sql.dialect.OracleDialect;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.server.api.client.ChartMetaData;
import ro.nextreports.server.api.client.DataSourceMetaData;
import ro.nextreports.server.api.client.ReportMetaData;
import ro.nextreports.server.api.client.WebServiceClient;
import craftsman.spy.SpyDriver;

/**
 * @author Decebal Suiu
 */
public class Globals {		
	
	private static MainFrame mainFrame;
	private static Connection connection;
	private static DefaultEventBus eventBus;
	private static Config config;
    private static Dialect dialect;
	private static byte reportPersistenceType = ReportPersistenceFactory.INVALID_PERSISTENCE_TYPE;
	private static String currentQueryName;
    private static String currentQueryAbsolutePath;
    private static String currentReportName;
    private static String currentReportAbsolutePath;
    private static String currentChartName;
    private static String currentChartAbsolutePath;
    private static String treeReportAbsolutePath;
    private static MainMenuBar mainMenuBar;
	private static MainToolBar mainToolBar;

	private static ReportDesignerPanel reportDesignerPanel;
	private static boolean reportLoaded;
	private static String originalSql;

    private static ChartDesignerPanel chartDesignerPanel;
    private static boolean chartLoaded;

    private static Integer dataSources;
	private static Integer reports;
	private static ReportUndoManager reportUndoManager;
	
	// stack of report / subreports full path
	private static Stack<String> innerStack = new Stack<String>();

    public static String UNIT_CM = "cm";
    public static String UNIT_IN = "in";
    
    private static String SERVER_URL;
    private static String SERVER_USER;
    private static String SERVER_PATH;
    private static ReportMetaData SERVER_REPORT_META_DATA;
    private static ChartMetaData SERVER_CHART_META_DATA;
    private static DataSourceMetaData SERVER_DS_META_DATA;
    private static WebServiceClient webService;
       
    public static String USER_DATA_DIR = System.getProperty("nextreports.user.data");

    // for now used only for chart to see the query was modified
    // it is updated with new query if modified !
    public static String initialQuery = "";
    
    private static String tableNamePattern = null;
    private static String viewNamePattern = null;
    private static String procedureNamePattern = null;

    public static void setMainFrame(MainFrame mainFrame) {
		Globals.mainFrame = mainFrame;
	}

	public static MainFrame getMainFrame() {
		return mainFrame;
	}

	public static EventBus getEventBus() {
		if (eventBus == null) {
			eventBus = new DefaultEventBus();
			eventBus.setErrorListener(new MyBusListener());
		}

		return eventBus;
	}

    public static void resetConfig() {
        config = null;
    }

    public static Config getConfig() {
		if (config == null) {
			config = ConfigFactory.createFileConfig(USER_DATA_DIR + "/config/next-reports.properties");
		}

		return config;
	}    

    public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection con) {
		closeConnection();
		connection = con;
        Globals.getMainMenuBar().actionUpdate(con !=  null);
        Globals.getMainToolBar().actionUpdate(con !=  null);
        Globals.setTableNamePattern(null);
        Globals.setViewNamePattern(null);
        Globals.setProcedureNamePattern(null);
    }

	public static Connection createConnection(DataSource dataSource)
			throws ConnectionException {
		closeConnection();
		connection = createTempConnection(dataSource);
        if (Globals.getMainMenuBar() != null) {
            Globals.getMainMenuBar().actionUpdate(connection !=  null);
        }
        if (Globals.getMainToolBar() !=  null) {
            Globals.getMainToolBar().actionUpdate(connection !=  null);
        }
        
        return connection;
	}
	
	private static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {			
				e.printStackTrace();
			}
		}
	}

    public static Connection createTempConnection(final DataSource dataSource)
			throws ConnectionException {
		// get config
		Config config = getConfig();

		// spy jdbc
		boolean spy = config.getBoolean("db.spy", false);

		// load the JDBC driver
		String driver = dataSource.getDriver();
		try {
			if (spy) {
				System.setProperty("spy.driver", driver);
				Enumeration<Driver> drivers = DriverManager.getDrivers();
				while (drivers.hasMoreElements()) {
					DriverManager.deregisterDriver(drivers.nextElement());
				}
//                Class.forName("craftsman.spy.SpyDriver");
				DriverManager.registerDriver(new SpyDriver());
			} else {
				Class.forName(driver);
			}
		} catch (Exception e) {
			throw new ConnectionException("Driver '" + driver + "' not found.", e);
		}

		// create a connection to the database (if there are network problems it may take a lot
		// of time until you will get an exception, so we use a timeout) 
		//
		// we did not use DriverManager.setLoginTimeout(CONNECTION_TIME_OUT);
		// because some driver implementations  do not take this into account
		//
		// we use a FututeTask and try to get the connection in at most CONNECTION_TIME_OUT seconds
		final String url = dataSource.getUrl();
		final String username = dataSource.getUser();
		final String password = dataSource.getPassword();
        Connection connection;                
        FutureTask<Connection> createConnectionTask = null;
        try {
        	createConnectionTask = new FutureTask<Connection>(new Callable<Connection>() {
        		
        		public Connection call() throws Exception {
        			if (dataSource.getDriver().equals(CSVDialect.DRIVER_CLASS)) {
        				return DriverManager.getConnection(url,dataSource.getUsedProperties());
        			} else {
        				return DriverManager.getConnection(url, username, password);
        			}
        		}
        		
        	});
        	new Thread(createConnectionTask).start();
        	connection = createConnectionTask.get(getConnectionTimeout(), TimeUnit.SECONDS);        	
		} catch (Exception e) {
			throw new ConnectionException("Could not establish connection.", e);
		}		
		return connection;
	}

    public static byte getReportPersistenceType() {
		if (reportPersistenceType == ReportPersistenceFactory.INVALID_PERSISTENCE_TYPE) {
            // Database not implemented
//			Config config = getConfig();
//			String s = config.getString("report.persistence");
//			if (s.equalsIgnoreCase("FILE")) {
//				reportPersistenceType = ReportPersistenceFactory.FILE_PERSISTENCE_TYPE;
//			} else if (s.equalsIgnoreCase("DATABASE")) {
//				reportPersistenceType = ReportPersistenceFactory.DATABASE_PERSISTENCE_TYPE;
//			}
            reportPersistenceType = ReportPersistenceFactory.FILE_PERSISTENCE_TYPE;
        }
		return reportPersistenceType;
	}
    
    public static int getConnectionTimeout() {
		Config config = getConfig();
		String s = config.getString("connection.timeout");
		return Integer.parseInt(s);
	}

	public static int getQueryTimeout() {
		Config config = getConfig();
		String s = config.getString("query.timeout");
		return Integer.parseInt(s);
	}

    public static String[] getFontDirectories() {        
        Config config = getConfig();
		String s = config.getString("font.directories");
        if (s == null) {
            return new String[0];
        }
        
        return s.split(",");
    }

    public static boolean getAccessibilityHtml() {
        Config config = getConfig();
        String s = config.getString("accessibility.html");
        if (s == null) {
            return false;
        }
        return Boolean.parseBoolean(s);        
    }

    public static boolean getA4Warning() {
        Config config = getConfig();
        String s = config.getString("A4.warning");
        if (s == null) {
            return false;
        }
        return Boolean.parseBoolean(s);
    }

    public static char getCsvDelimiter() {
        Config config = getConfig();
        String s = config.getString("csv.delimiter");
        if (!isValidCsvDelimiter(s)) {
            return ',';
        }
        return s.toCharArray()[0];
    }

    public static boolean isValidCsvDelimiter(String s) {
        if ((s == null) || (s.length() > 1) || (s.trim().equals("")) ||
            (s.charAt(0) == '\'')  || (s.charAt(0) == '\n') || (s.charAt(0) == '\r')) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean getParametersIgnore() {
		Config config = getConfig();
		String s = config.getString("parameters.ignore");
        if (s == null) {
            return false;
        }
        
        return Boolean.parseBoolean(s);
	}	

	public static void setPdfEncoding() {
		setProperty(PdfExporter.PDF_ENCODING_PROPERTY);
	}

	public static void setPdfFont() {
		setProperty(PdfExporter.PDF_FONT_PROPERTY);
	}
	
	public static void setPdfDirection() {
		setProperty(PdfExporter.PDF_DIRECTION);
	}
	
	public static void setPdfArabicOptions() {
		setProperty(PdfExporter.PDF_ARABIC_OPTIONS);
	}

    public static void setOracleClientPath() {
        setProperty(OracleDialect.ORACLE_CLIENT_PROPERTY);
    }
    
    private static void setProperty(String propertyName) {
        Config config = getConfig();
        String s = config.getString(propertyName, null);
        if ("".equals(s)) {
            s = null;
        }
        if (s != null) {
            System.setProperty(propertyName, s);
        } else {
            System.clearProperty(propertyName);
        }
    }

    public static String getOracleClientPath() {
        return System.getProperty(OracleDialect.ORACLE_CLIENT_PROPERTY);
    }
    
    public static boolean isMaxChecked() {
		Config config = getConfig();
		String s = config.getString("max.rows.checked");
        if (s == null) {
            return true;
        }
        return Boolean.parseBoolean(s);
	}

    public static boolean isRulerVisible() {
		Config config = getConfig();
		String s = config.getString("ruler.isVisible");
        if (s == null) {
            return false;
        }
        return Boolean.parseBoolean(s);
	}        

    public static String getRulerUnit() {
        Config config = getConfig();
		String s = config.getString("ruler.unit");
        if (UNIT_CM.equals(s) || UNIT_IN.equals(s)) {
            return s;
        } else {
            return UNIT_CM;
        }
    }

    public static boolean singleSourceAutoConnect() {
		Config config = getConfig();
		String s = config.getString("singlesource.autoconnect");
		return s.equals("true");
	}
    
    public static int getChartWebServerPort() {
		Config config = getConfig();
		return config.getInt("chart.webserver.port");    	
    }

	public static String getDatabaseName() throws Exception {
		return getDatabaseName(getConnection());
	}

	public static String getDatabaseVersion() throws Exception {
		return getDatabaseVersion(getConnection());
	}

    public static String getDatabaseName(Connection conn) throws Exception {
        DatabaseMetaData dbmd = conn.getMetaData();
        return dbmd.getDatabaseProductName();
    }

    public static String getDatabaseVersion(Connection conn) throws Exception {
        DatabaseMetaData dbmd = conn.getMetaData();
        return dbmd.getDatabaseProductVersion();
    }


    public static Dialect getDialect() throws Exception {
		if (dialect == null) {
			dialect = DialectUtil.getDialect(getConnection());
		}
		
		return dialect;
	}

    public static void clearDialect() {
        dialect = null;
    }

    public static String getJavaVersion() {
		return System.getProperty("java.version");
	}

	static class MyBusListener implements BusListener {

		public void eventPublished(EventObject ev) {
//			System.out.println("eventPublished: " + ev);
		}

		public void eventError(EventObject ev, Exception e) {
//			System.out.println("eventError: " + ev);
			e.printStackTrace();
		}

	}

	public static DBViewer getDBViewer() {
		return new DefaultDBViewer();
	}

	public static String getCurrentQueryName() {
		return currentQueryName;
	}

	public static void setCurrentQueryName(String currentQueryName) {
		Globals.currentQueryName = currentQueryName;
	}

    public static String getCurrentQueryAbsolutePath() {
		return currentQueryAbsolutePath;
	}

	public static void setCurrentQueryAbsolutePath(String currentQueryAbsolutePath) {
		Globals.currentQueryAbsolutePath = currentQueryAbsolutePath;
        if (currentQueryAbsolutePath == null) {
            getMainFrame().updateTitle(null);
        } else {
            getMainFrame().updateTitle(currentQueryAbsolutePath.substring(
                currentQueryAbsolutePath.indexOf(File.separator + FileReportPersistence.QUERIES_FOLDER)));
        }
    }

    public static String getCurrentReportAbsolutePath() {
        return currentReportAbsolutePath;
    }

    public static void setCurrentReportAbsolutePath(String currentReportAbsolutePath) {
        Globals.currentReportAbsolutePath = currentReportAbsolutePath;
        if (currentReportAbsolutePath == null) {
            getMainFrame().updateTitle(null);
        } else {
            getMainFrame().updateTitle(currentReportAbsolutePath.substring(
                currentReportAbsolutePath.indexOf(File.separator + FileReportPersistence.REPORTS_FOLDER)));
        }
    }

    public static String getCurrentReportName() {
		return currentReportName;
	}

	public static void setCurrentReportName(String currentReportName) {
		Globals.currentReportName = currentReportName;
	}

    public static String getCurrentChartName() {
		return currentChartName;
	}

	public static void setCurrentChartName(String currentChartName) {
		Globals.currentChartName = currentChartName;
	}

    public static String getCurrentChartAbsolutePath() {
        return currentChartAbsolutePath;
    }

    public static void setCurrentChartAbsolutePath(String currentChartAbsolutePath) {
        Globals.currentChartAbsolutePath = currentChartAbsolutePath;
        if (currentChartAbsolutePath == null) {
            getMainFrame().updateTitle(null);
        } else {
            getMainFrame().updateTitle(currentChartAbsolutePath.substring(
                currentChartAbsolutePath.indexOf(File.separator + FileReportPersistence.CHARTS_FOLDER)));
        }
    }

    public static String getTreeReportAbsolutePath() {
        return treeReportAbsolutePath;
    }

    public static void setTreeReportAbsolutePath(String treeReportAbsolutePath) {
        Globals.treeReportAbsolutePath = treeReportAbsolutePath;
    }

    public static MainMenuBar getMainMenuBar() {
		return mainMenuBar;
	}

	public static void setMainMenuBar(MainMenuBar mainMenuBar) {
		Globals.mainMenuBar = mainMenuBar;
	}

	public static MainToolBar getMainToolBar() {
		return mainToolBar;
	}

	public static void setMainToolBar(MainToolBar mainToolBar) {
		Globals.mainToolBar = mainToolBar;
	}

	public static ReportDesignerPanel getReportDesignerPanel() {
        if (reportDesignerPanel == null) {
            reportDesignerPanel = new ReportDesignerPanel();
		    reportDesignerPanel.initWorkspace();
        }
        
        return reportDesignerPanel;
	}

	public static void setReportDesignerPanel(ReportDesignerPanel reportDesignerPanel) {
		Globals.reportDesignerPanel = reportDesignerPanel;
	}

	public static ReportLayoutPanel getReportLayoutPanel() {
		return getReportDesignerPanel().getLayoutPanel();
	}
	
	public static ReportGrid getReportGrid() {
		return getReportLayoutPanel().getReportGrid();
	}

	public static void refreshReportLayoutPanel() {
		ReportLayoutPanel layoutPanel = getReportLayoutPanel();
		layoutPanel.validate();
		layoutPanel.repaint();
	}

	public static boolean isReportLoaded() {
		return reportLoaded;
	}

	public static void setReportLoaded(boolean reportLoaded) {
		Globals.reportLoaded = reportLoaded;
	}

    public static boolean isChartLoaded() {
		return chartLoaded;
	}

	public static void setChartLoaded(boolean chartLoaded) {
		Globals.chartLoaded = chartLoaded;
	}

    public static ChartDesignerPanel getChartDesignerPanel() {
        if (chartDesignerPanel == null) {
            chartDesignerPanel = new ChartDesignerPanel();
		    chartDesignerPanel.initWorkspace();
        }

        return chartDesignerPanel;
	}

	public static void setChartDesignerPanel(ChartDesignerPanel chartDesignerPanel) {
		Globals.chartDesignerPanel = chartDesignerPanel;
	}

    public static ChartLayoutPanel getChartLayoutPanel() {
        return getChartDesignerPanel().getLayoutPanel();
    }

    public static void refreshChartLayoutPanel() {
        ChartLayoutPanel layoutPanel = getChartLayoutPanel();
        layoutPanel.validate();
        layoutPanel.repaint();
    }
    
    public static String getOriginalSql() {
		return originalSql;
	}

	public static void setOriginalSql(String originalSql) {
		Globals.originalSql = originalSql;
	}

	public static Integer getDataSources() {
		return dataSources;
	}

	public static void setDataSources(Integer dataSources) {
		Globals.dataSources = dataSources;
	}

	public static Integer getReports() {
		return reports;
	}

	public static void setReports(Integer reports) {
		Globals.reports = reports;
	}
	
	public static ReportUndoManager getReportUndoManager() {
		if (reportUndoManager == null) {
			reportUndoManager = new ReportUndoManager();
		}
		
		return reportUndoManager;
	}

    public static String getInitialQuery() {
        return initialQuery;
    }

    public static void setInitialQuery(String initialQuery) {
        Globals.initialQuery = initialQuery;
    }
    
    public static void setLocale() {        
        Locale.setDefault(getConfigLocale());        
    }
    
    public static Locale getConfigLocale() {
    	Config config = getConfig();
        String s = config.getString("locale", null);
        if ((s != null) && (s.trim().length() > 0)) {
        	String[] tokens = s.split(",");
        	if (tokens.length != 2) {
        		System.out.println("Invalid locale property format");
        		return Locale.getDefault();
        	}
        	
        	String language = tokens[0].trim();
        	String country = tokens[1].trim();
        	return new Locale(language, country);        	
        }
        return Locale.getDefault();
    }       
    
    public static String getSystemDataSource() {
    	return System.getProperty("next.datasource");
    }
    
    public static String getSystemReport() {
    	return System.getProperty("next.report");
    }
    
    public static String getSystemPath() {    	
    	return FileUtil.convertPathToSystemSeparators(System.getProperty("next.path"));    	
    }
    
    public static String getSystemChart() {
    	return System.getProperty("next.chart");
    }

	public static String getServerUrl() {
		return SERVER_URL;
	}

	public static void setServerUrl(String serverUrl) {
		SERVER_URL = serverUrl;		
	}

	public static String getServerUser() {
		return SERVER_USER;
	}

	public static void setServerUser(String serverUser) {
		SERVER_USER = serverUser;		
	}

	public static String getServerPath() {
		return SERVER_PATH;
	}

	public static void setServerPath(String serverPath) {
		SERVER_PATH = serverPath;		
	}
	
	public static ReportMetaData getServerReportMetaData() {
		return SERVER_REPORT_META_DATA;
	}

	public static void setServerReportMetaData(ReportMetaData serverReportMetaData) {
		SERVER_REPORT_META_DATA = serverReportMetaData;
	}
	
	public static ChartMetaData getServerChartMetaData() {
		return SERVER_CHART_META_DATA;
	}

	public static void setServerChartMetaData(ChartMetaData serverChartMetaData) {
		SERVER_CHART_META_DATA = serverChartMetaData;
	}
	
	public static DataSourceMetaData getServerDSMetaData() {
		return SERVER_DS_META_DATA;
	}

	public static void setServerDSMetaData(DataSourceMetaData serverDSMetaData) {
		SERVER_DS_META_DATA = serverDSMetaData;
	}	
	
	public static WebServiceClient getWebService() {
		return webService;
	}

	public static void setWebService(WebServiceClient webService) {
		Globals.webService = webService;
	}

	public static void resetServerFile() {		
		setServerReportMetaData(null);
		setServerChartMetaData(null);
		setServerDSMetaData(null);
		setServerPath(null);
		setServerUrl(null);
		setServerUser(null);
		setWebService(null);
	}
	
	public static void pushPath(String path) {
		innerStack.push(path);
	}
	
	public static String popPath() {
		return innerStack.pop();
	}
	
	public static String peekPath() {
		return innerStack.peek();
	}
	
	public static String peekPrePath() {
		String s = innerStack.pop();
		String prePath = innerStack.peek();
		innerStack.push(s);
		return prePath;
	}
	
	// first in stack is the master then all the subreports
	public static boolean isInner() {
		return innerStack.size() > 1;
	}

	public static String getTableNamePattern() {
		return tableNamePattern;
	}

	public static void setTableNamePattern(String tableNamePattern) {
		Globals.tableNamePattern = tableNamePattern;
	}

	public static String getViewNamePattern() {
		return viewNamePattern;
	}

	public static void setViewNamePattern(String viewNamePattern) {
		Globals.viewNamePattern = viewNamePattern;
	}

	public static String getProcedureNamePattern() {
		return procedureNamePattern;
	}

	public static void setProcedureNamePattern(String procedureNamePattern) {
		Globals.procedureNamePattern = procedureNamePattern;
	}				
        
}
