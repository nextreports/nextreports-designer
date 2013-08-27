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
import java.awt.Dimension;

import javax.swing.JPanel;

import ro.nextreports.designer.util.TreeUtil;


/**
 * @author Decebal Suiu
 */
public class ReportDesignerPanel extends JPanel {

    private SelectionController selectionController;

    private StructurePanel structurePanel;
    private PropertyPanel propertiesPanel;
    private ReportLayoutPanel layoutPanel;

    public ReportDesignerPanel() {
        super();
        initComponents();
    }

    public void initWorkspace() {
        this.putClientProperty(WorkspaceManager.REPORT_CONTENT, layoutPanel);
//        structurePanel.setMinimumSize(new Dimension(300, 200));
        structurePanel.setPreferredSize(new Dimension(300, 200));
//        structurePanel.setMaximumSize(new Dimension(300, 200));
        this.putClientProperty(WorkspaceManager.REPORT_STRUCTURE, structurePanel);
        
        propertiesPanel.setPreferredSize(new Dimension(300, 200));
        this.putClientProperty(WorkspaceManager.REPORT_PROPERTIES, propertiesPanel);
    }

    public StructurePanel getStructurePanel() {
		return structurePanel;
	}

	public ReportLayoutPanel getLayoutPanel() {
        return layoutPanel;
    }

    public PropertyPanel getPropertiesPanel() {
        return propertiesPanel;
    }

    public SelectionController getTreeSelectionController() {
        return selectionController;
    }

	public void clear() {
//        System.out.println("##################################");
//        System.out.println("##################################");
//        System.out.println("ReportDesignerPanel.clear()");
//        init();
//        ReportDesignerPanel rdp = new ReportDesignerPanel();
//        rdp.initDocking();
        LayoutHelper.getReportLayout().clear();
        Globals.getReportDesignerPanel().initComponents();
        Globals.getReportDesignerPanel().initWorkspace();
//        Globals.setReportDesignerPanel(rdp);
        Globals.refreshReportLayoutPanel();
    }

    public void refresh() {        
        structurePanel.refresh();
        propertiesPanel.refresh();
        layoutPanel.refresh();
        TreeUtil.expandAll(structurePanel.getStructureTree());
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        layoutPanel = new ReportLayoutPanel();
        layoutPanel.setEnabled(true);
        
        ReportGrid reportGrid = layoutPanel.getReportGrid();
        
        selectionController = new SelectionController();
        reportGrid.getSelectionModel().addSelectionModelListener(selectionController);
        
        structurePanel = new StructurePanel();
        structurePanel.getStructureTree().addTreeSelectionListener(selectionController);
        reportGrid.getModel().addGridModelListener(structurePanel);

        propertiesPanel = new PropertyPanel();
        reportGrid.getSelectionModel().addSelectionModelListener(propertiesPanel);
    }

    public void recreatePropertiesPanel() {
        propertiesPanel = new PropertyPanel();
    }

}
