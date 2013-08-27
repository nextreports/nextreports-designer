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

import java.awt.Component;
import java.io.File;
import java.io.IOException;

import org.noos.xing.mydoggy.DockedTypeDescriptor;
import org.noos.xing.mydoggy.ToolWindow;
import org.noos.xing.mydoggy.ToolWindowAction;
import org.noos.xing.mydoggy.ToolWindowAnchor;
import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.ToolWindowType;

import ro.nextreports.designer.ui.tail.LogPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.util.StringUtil;

/**
 * @author Decebal Suiu
 */
public class WorkspaceManager {

    public static final String QUERY_WORKSPACE_FILE = Globals.USER_DATA_DIR + "/" + "query-workspace.xml";
    public static final String REPORT_WORKSPACE_FILE = Globals.USER_DATA_DIR + "/" + "report-workspace.xml";
    public static final String CHART_WORKSPACE_FILE = Globals.USER_DATA_DIR + "/" + "chart-workspace.xml";

    public static final String QUERY_WORKSPACE = "query";
    public static final String REPORT_WORKSPACE = "report";
    public static final String CHART_WORKSPACE = "chart";

    public static final String QUERY_CONTENT = "qbMain";
    public static String QUERY_EXPLORER = I18NSupport.getString("query.explorer");
    public static String QUERY_PARAMETERS = I18NSupport.getString("query.parameters");
    public static final String QUERY_SQL_LOG = "Sql Log";

    public static final String REPORT_CONTENT = "rdMain";
    public static String REPORT_STRUCTURE = I18NSupport.getString("report.structure");
    public static String REPORT_PROPERTIES = I18NSupport.getString("report.properties");

    public static final String CHART_CONTENT = "cdMain";
    public static String CHART_PROPERTIES = I18NSupport.getString("chart.properties");

    private static WorkspaceManager instance;

    private Workspace queryWorkspace;
    private Workspace reportWorkspace;
    private Workspace chartWorkspace;
    private String currentWorkspace;

    public static WorkspaceManager getInstance() {
        if (instance == null) {            
            instance = new WorkspaceManager();
        }

        return instance;
    }

    public void clear() {
        instance = null;
        String oldExplorer = QUERY_EXPLORER;
        String oldParameters = QUERY_PARAMETERS;
        String oldStructure = REPORT_STRUCTURE;
        String oldProperties = REPORT_PROPERTIES;
        String oldChartProperties = CHART_PROPERTIES;
        QUERY_EXPLORER = I18NSupport.getString("query.explorer");
        QUERY_PARAMETERS = I18NSupport.getString("query.parameters");
        REPORT_STRUCTURE = I18NSupport.getString("report.structure");
        REPORT_PROPERTIES = I18NSupport.getString("report.properties");
        CHART_PROPERTIES = I18NSupport.getString("chart.properties");
        StringUtil.replaceInFile(new File(QUERY_WORKSPACE_FILE), oldExplorer, QUERY_EXPLORER);
        StringUtil.replaceInFile(new File(QUERY_WORKSPACE_FILE), oldParameters, QUERY_PARAMETERS);
        StringUtil.replaceInFile(new File(REPORT_WORKSPACE_FILE), oldStructure, REPORT_STRUCTURE);
        StringUtil.replaceInFile(new File(REPORT_WORKSPACE_FILE), oldProperties, REPORT_PROPERTIES);
        StringUtil.replaceInFile(new File(CHART_WORKSPACE_FILE), oldChartProperties, CHART_PROPERTIES);
    }

    private WorkspaceManager() {
        createQueryWorkspace();
        createReportWorkspace();
        createChartWorkspace();
    }

    public Workspace getQueryWorkspace() {
		return queryWorkspace;
	}

	public Workspace getReportWorkspace() {
		return reportWorkspace;
	}

    public Workspace getChartWorkspace() {
        return chartWorkspace;
    }

    public void setCurrentWorkspace(String workspaceName) {        
        if (QUERY_WORKSPACE.equals(workspaceName)) {
			currentWorkspace = QUERY_WORKSPACE;
			Globals.getMainFrame().changeWorkspace(workspaceName);
		} else if (REPORT_WORKSPACE.equals(workspaceName)) {
			currentWorkspace = REPORT_WORKSPACE;
			Globals.getMainFrame().changeWorkspace(workspaceName);
		} else if (CHART_WORKSPACE.equals(workspaceName)) {
			currentWorkspace = CHART_WORKSPACE;
			Globals.getMainFrame().changeWorkspace(workspaceName);
		}
	}

	public String getCurrentWorkspace(String workspaceName) {
		return currentWorkspace;
	}
	
	public void storeWorkspaces() throws IOException {
		queryWorkspace.store(QUERY_WORKSPACE_FILE);
		reportWorkspace.store(REPORT_WORKSPACE_FILE);
        chartWorkspace.store(CHART_WORKSPACE_FILE);
    }

    public void restoreWorkspaces() throws IOException {
    	queryWorkspace.restore(QUERY_WORKSPACE_FILE);
    	reportWorkspace.restore(REPORT_WORKSPACE_FILE);
        chartWorkspace.restore(CHART_WORKSPACE_FILE);
    }

	private void createQueryWorkspace() {
    	queryWorkspace = new Workspace(QUERY_WORKSPACE);
    	ToolWindowManager toolWindowManager = queryWorkspace.getToolWindowManager();

		ToolWindow toolWindow = toolWindowManager.registerToolWindow(QUERY_EXPLORER, // Id
                QUERY_EXPLORER, // Title
                ImageUtil.getImageIcon("database"), // Icon
                (Component) Globals.getMainFrame().getQueryBuilderPanel().getClientProperty(QUERY_EXPLORER), // Component
                ToolWindowAnchor.LEFT); // Anchor

        toolWindow.setType(ToolWindowType.DOCKED);

        DockedTypeDescriptor dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(300);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID).setVisible(false);

        toolWindow = toolWindowManager.registerToolWindow(QUERY_PARAMETERS,                      // Id
                QUERY_PARAMETERS,                 // Title
                ImageUtil.getImageIcon("parameters"), // Icon
                (Component) Globals.getMainFrame().getQueryBuilderPanel().getClientProperty(QUERY_PARAMETERS),    // Component
                ToolWindowAnchor.LEFT);     // Anchor

        toolWindow.setType(ToolWindowType.DOCKED);

        dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(300);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID).setVisible(false);

        toolWindow = toolWindowManager.registerToolWindow(QUERY_SQL_LOG, // Id
                QUERY_SQL_LOG,                 // Title
                ImageUtil.getImageIcon("log"), // Icon
                new LogPanel(),    // Component
                ToolWindowAnchor.BOTTOM);     // Anchor

        toolWindow.setType(ToolWindowType.DOCKED);

        dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(300);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID).setVisible(false);

		toolWindowManager.getContentManager().addContent(QUERY_CONTENT,  // Id
        		"Query",                 // Title
        		ImageUtil.getImageIcon("query_perspective"), // Icon
        		(Component) Globals.getMainFrame().getQueryBuilderPanel().getClientProperty(QUERY_CONTENT));    // Component

		for (ToolWindow tmp : toolWindowManager.getToolWindows()) {
			tmp.setAvailable(true);
		}
		toolWindowManager.getToolWindow(1).setActive(true);
		toolWindowManager.getToolWindow(2).aggregate();
	}

	private void createReportWorkspace() {
    	reportWorkspace = new Workspace(REPORT_WORKSPACE);
    	ToolWindowManager toolWindowManager = reportWorkspace.getToolWindowManager();

		ToolWindow toolWindow = toolWindowManager.registerToolWindow(REPORT_STRUCTURE, // Id
                REPORT_STRUCTURE,                 // Title
                ImageUtil.getImageIcon("componenttree"), // Icon
                (Component) Globals.getReportDesignerPanel().getClientProperty(REPORT_STRUCTURE),    // Component
                ToolWindowAnchor.LEFT);            // Anchor

        toolWindow.setType(ToolWindowType.DOCKED);
        toolWindow.aggregate();

        DockedTypeDescriptor dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(300);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID).setVisible(false);
    	
        toolWindow = toolWindowManager.registerToolWindow(REPORT_PROPERTIES,                      // Id
                REPORT_PROPERTIES,                 // Title
                ImageUtil.getImageIcon("properties"), // Icon
                (Component) Globals.getReportDesignerPanel().getClientProperty(REPORT_PROPERTIES),    // Component
                ToolWindowAnchor.LEFT);     // Anchor

        toolWindow.setType(ToolWindowType.DOCKED);
        toolWindow.aggregate();

        dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(300);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID).setVisible(false);
    	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID).setVisible(false);

		toolWindowManager.getContentManager().addContent(REPORT_CONTENT,  // Id
				"Report",                 // Title
				ImageUtil.getImageIcon("report_perspective"), // Icon
				(Component) Globals.getReportDesignerPanel().getClientProperty(REPORT_CONTENT));    // Component

		for (ToolWindow tmp : toolWindowManager.getToolWindows()) {
			tmp.setAvailable(true);
		}
		toolWindowManager.getToolWindow(1).setActive(true);
		toolWindowManager.getToolWindow(2).aggregate();
	}

    private void createChartWorkspace() {
        chartWorkspace = new Workspace(CHART_WORKSPACE);
        ToolWindowManager toolWindowManager = chartWorkspace.getToolWindowManager();

        ToolWindow toolWindow = toolWindowManager.registerToolWindow(CHART_PROPERTIES,  // Id
                CHART_PROPERTIES,                 // Title
                ImageUtil.getImageIcon("properties"), // Icon
                (Component) Globals.getChartDesignerPanel().getClientProperty(CHART_PROPERTIES),    // Component
                ToolWindowAnchor.LEFT);     // Anchor

        toolWindow.setType(ToolWindowType.DOCKED);
        toolWindow.aggregate();

        DockedTypeDescriptor dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        dockedTypeDescriptor.setIdVisibleOnTitleBar(false);
        dockedTypeDescriptor.setDockLength(300);
        dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID).setVisible(false);
        dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID).setVisible(false);
        dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID).setVisible(false);
        dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID).setVisible(false);

        toolWindowManager.getContentManager().addContent(CHART_CONTENT,  // Id
                "Chart",                 // Title
                ImageUtil.getImageIcon("chart_perspective"), // Icon
                (Component) Globals.getChartDesignerPanel().getClientProperty(CHART_CONTENT));    // Component

        for (ToolWindow tmp : toolWindowManager.getToolWindows()) {
            tmp.setAvailable(true);
        }
        toolWindowManager.getToolWindow(1).setActive(true);
    }


}
