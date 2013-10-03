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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXStatusBar;
import org.noos.xing.mydoggy.plaf.ui.MyDoggyKeySpace;

import ro.nextreports.designer.action.chart.OpenChartAction;
import ro.nextreports.designer.action.report.OpenReportAction;
import ro.nextreports.designer.action.report.layout.export.ExportAction;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.persistence.FileReportPersistence;
import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBBrowserTree;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.QueryBuilderPanel;
import ro.nextreports.designer.querybuilder.tree.DBNodeExpander;
import ro.nextreports.designer.ui.MemoryStatus;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class MainFrame extends JXFrame {

	private JXPanel workspacePanel;
    private QueryBuilderPanel qbPanel;
    private JXStatusBar statusBar;

    private String baseTitle;

    public MainFrame(String title) {
        super(title);
        baseTitle = title;
        setIconImage(ImageUtil.getImage("next-reports"));
        initComponents();
    }

    private void initComponents() {
        Globals.setMainFrame(this);

        // better text visualization for disabled components
    	Options.setPopupDropShadowEnabled(true); // add drop shadow to popup menu
        UIDefaults uiDefaults = UIManager.getDefaults();
        uiDefaults.put("ComboBox.disabledForeground", Color.DARK_GRAY);
        uiDefaults.put("TextField.inactiveForeground", Color.DARK_GRAY);
        uiDefaults.put("TextArea.inactiveBackground", Color.WHITE);
        uiDefaults.put("FormattedTextField.inactiveForeground",Color.DARK_GRAY);
        uiDefaults.put("PasswordField.inactiveForeground",Color.DARK_GRAY);
        uiDefaults.put("CheckBox.disabledText", Color.DARK_GRAY);

        // internationalization
        UIManager.put("OptionPane.yesButtonText", I18NSupport.getString("optionpanel.yes"));
        UIManager.put("OptionPane.cancelButtonText", I18NSupport.getString("optionpanel.cancel"));
        UIManager.put("OptionPane.noButtonText", I18NSupport.getString("optionpanel.no"));
        UIManager.put("OptionPane.okButtonText", I18NSupport.getString("optionpanel.ok"));
        UIManager.put("OptionPane.messageDialogTitle", I18NSupport.getString("optionpanel.message"));
        UIManager.put("ColorChooser.okText", I18NSupport.getString("colorchooser.ok"));
        UIManager.put("ColorChooser.cancelText", I18NSupport.getString("colorchooser.cancel"));
        UIManager.put("ColorChooser.resetText", I18NSupport.getString("colorchooser.reset"));
        UIManager.put("FileChooser.saveInLabelText", I18NSupport.getString("FileChooser.saveInLabelText"));
        UIManager.put("FileChooser.fileNameLabelText", I18NSupport.getString("FileChooser.fileNameLabelText"));
        UIManager.put("FileChooser.folderNameLabelText", I18NSupport.getString("FileChooser.folderNameLabelText"));
        UIManager.put("FileChooser.filesOfTypeLabelText", I18NSupport.getString("FileChooser.filesOfTypeLabelText"));
        UIManager.put("FileChooser.saveButtonText", I18NSupport.getString("FileChooser.saveButtonText"));
        UIManager.put("FileChooser.cancelButtonText", I18NSupport.getString("FileChooser.cancelButtonText"));
        UIManager.put("FileChooser.saveButtonToolTipText", I18NSupport.getString("FileChooser.saveButtonToolTipText"));
        UIManager.put("FileChooser.cancelButtonToolTipText", I18NSupport.getString("FileChooser.cancelButtonToolTipText"));
        UIManager.put("FileChooser.upFolderToolTipText", I18NSupport.getString("FileChooser.upFolderToolTipText"));
        UIManager.put("FileChooser.homeFolderToolTipText", I18NSupport.getString("FileChooser.homeFolderToolTipText"));
        UIManager.put("FileChooser.newFolderToolTipText", I18NSupport.getString("FileChooser.newFolderToolTipText"));
        UIManager.put("FileChooser.listViewButtonToolTipText", I18NSupport.getString("FileChooser.listViewButtonToolTipText"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", I18NSupport.getString("FileChooser.detailsViewButtonToolTipText"));

        // docking
        UIManager.put(MyDoggyKeySpace.DRAG_ENABLED, false); 
        
        // inside connections dir are kept the queries/reports for every data source
        DefaultDataSourceManager.getInstance().load();
        File  connections = new File(FileReportPersistence.CONNECTIONS_DIR);
        if (!connections.exists()) {
            connections.mkdir();
        }
        // inside reports dir are kept the generated reports
        File reports = new File(ExportAction.REPORTS_DIR);
        if (!reports.exists()) {
            reports.mkdir();
        }

        // create workspace panel
        workspacePanel = new JXPanel(new CardLayout());
        
        // create query builder panel before menu(!!! for docking)
        qbPanel = new QueryBuilderPanel();
        qbPanel.initWorkspace();

        setLayout(new BorderLayout());
//        add(new MainToolBar(), BorderLayout.NORTH);
        setToolBar(new MainToolBar());

        statusBar = new JXStatusBar();
        //statusBar.add(new JXLabel(""), JXStatusBar.Constraint.ResizeBehavior.FILL);
        statusBar.add(new JXLabel(""), new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL, new Insets(0,5,2,2)));        
        statusBar.add(new MemoryStatus());
        statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));                
        setStatusBar(statusBar);

        WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        workspacePanel.add((Component) workspaceManager.getQueryWorkspace().getToolWindowManager(), WorkspaceManager.QUERY_WORKSPACE);
        workspacePanel.add((Component) workspaceManager.getReportWorkspace().getToolWindowManager(), WorkspaceManager.REPORT_WORKSPACE);
        workspacePanel.add((Component) workspaceManager.getChartWorkspace().getToolWindowManager(), WorkspaceManager.CHART_WORKSPACE);
        add(workspacePanel, BorderLayout.CENTER);
        
        DataSource ds = DefaultDataSourceManager.getInstance().getConnectedDataSource();
        if (ds != null) {
            setStatusBarMessage("<html>" + I18NSupport.getString("datasource.active") + 
            		" <b>" + ds.getName() + "</b></html>");
        }

        setJMenuBar(new MainMenuBar());

        Globals.getMainMenuBar().actionUpdate(ds != null);
        Globals.getMainToolBar().actionUpdate(ds != null);
        
        String systemReport = Globals.getSystemReport();
        String systemChart = Globals.getSystemChart();
        String systemPath = Globals.getSystemPath();
        if (systemReport !=  null) {
        	openSystemReport(systemReport, systemPath);
        } else if (systemChart !=  null) {
        	openSystemChart(systemChart, systemPath);
        }
        
    }
    
     public void openSystemReport(String reportName) {    	 
    	openSystemReport(qbPanel.getTree(), reportName);        
     }
     
     public void openSystemReport(String reportName, String relativePath) {    	 
     	openSystemReport(qbPanel.getTree(), reportName, relativePath);        
      }
     
     public void openSystemReport(DBBrowserTree tree, String reportName) {    	 
     	openSystemReport(tree, reportName, null);         
     }
     
     public void openSystemReport(DBBrowserTree tree, String reportName, String relativePath) {    	     	 
    	 
      	if (DefaultDataSourceManager.getInstance().getConnectedDataSource() ==  null) {
      		return;
      	}
      	     	
      	DBBrowserNode node = tree.searchNode(DBNodeExpander.REPORTS);    	
        tree.startExpandingTree(node, true, null);
         
        DBBrowserNode selectedNode = null;
        if (relativePath == null) {
        	// report inside Reports node
        	selectedNode = tree.searchNode(reportName, DBObject.REPORTS);    
        } else {
        	// report inside some folder
        	String absolutePath =  FileReportPersistence.getReportsAbsolutePath() + File.separator + relativePath+ File.separator + reportName + FormSaver.REPORT_FULL_EXTENSION;        	
        	selectedNode = tree.searchNode(reportName, absolutePath, DBObject.REPORTS);    
        }	      	
      	
      	if (selectedNode == null) {
      		return;
      	}    	    	
      	
		OpenReportAction openAction = new OpenReportAction();
		openAction.setResetServerReport(false);
		openAction.setReportName(selectedNode.getDBObject().getName());
		openAction.setReportPath(selectedNode.getDBObject().getAbsolutePath());
		openAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
	}



     public void openSystemChart(String chartName) {    	 
     	openSystemChart(qbPanel.getTree(), chartName);        
     }
     
     public void openSystemChart(String chartName, String relativePath) {    	 
      	openSystemChart(qbPanel.getTree(), chartName, relativePath);        
      }
     
     public void openSystemChart(DBBrowserTree tree, String chartName) {    	 
      	openSystemChart(tree, chartName, null);         
      }
 
     public void openSystemChart(DBBrowserTree tree, String chartName, String relativePath) {
    	 
      	if (DefaultDataSourceManager.getInstance().getConnectedDataSource() ==  null) {
      		return;
      	}
      	      	
      	DBBrowserNode node = tree.searchNode(DBNodeExpander.CHARTS);    	
        tree.startExpandingTree(node, true, null);
      	
        DBBrowserNode selectedNode = null;
        if (relativePath == null) {
        	// chart inside Charts node
        	selectedNode = tree.searchNode(chartName, DBObject.CHARTS);
      	} else {	
        	// chart inside some folder
        	String absolutePath =  FileReportPersistence.getChartsAbsolutePath() + File.separator + relativePath + File.separator + chartName + ChartUtil.CHART_FULL_EXTENSION;
        	selectedNode = tree.searchNode(chartName, absolutePath, DBObject.CHARTS);    
      	}	
      	if (selectedNode == null) {
      		return;
      	}    	    	
      	
      	OpenChartAction openAction = new OpenChartAction();    
      	openAction.setResetServerChart(false);
        openAction.setChartName(selectedNode.getDBObject().getName());
        openAction.setChartPath(selectedNode.getDBObject().getAbsolutePath());       
        openAction.actionPerformed(new ActionEvent(this,  ActionEvent.ACTION_PERFORMED , ""));            
      } 

    public QueryBuilderPanel getQueryBuilderPanel() {
        return qbPanel;
    }

    public void setStatusBarMessage(String message) {
        if (statusBar != null) {
            ((JXLabel) statusBar.getComponent(0)).setText(message);
        }
    }
    
    void changeWorkspace(String workspaceName) {
    	((CardLayout) workspacePanel.getLayout()).show(workspacePanel, workspaceName);
    }

    public void updateTitle(String update) {
        if (update == null) {
            setTitle(baseTitle);
        } else {
            setTitle(baseTitle + " - " + update);
        }
    }
    
}
