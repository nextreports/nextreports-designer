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

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.WorkspaceManager;

import java.awt.*;

/**
 * User: mihai.panaitescu
 * Date: 16-Dec-2009
 * Time: 12:12:13
 */
public class ChartDesignerPanel extends JPanel {

    private ChartPropertyPanel propertiesPanel;
    private ChartLayoutPanel layoutPanel;

    public ChartDesignerPanel() {
        super();
        initComponents();
    }

    public void initWorkspace() {
        this.putClientProperty(WorkspaceManager.CHART_CONTENT, layoutPanel);
        propertiesPanel.setPreferredSize(new Dimension(300, 200));
        this.putClientProperty(WorkspaceManager.CHART_PROPERTIES, propertiesPanel);
    }

	public ChartLayoutPanel getLayoutPanel() {
        return layoutPanel;
    }

    public ChartPropertyPanel getPropertiesPanel() {
        return propertiesPanel;
    }

	public void clear() {        
        Globals.getChartDesignerPanel().initComponents();
        Globals.getChartDesignerPanel().initWorkspace();
        Globals.refreshChartLayoutPanel();
    }

    public void refresh() {
        propertiesPanel.refresh();
        //layoutPanel.refresh();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        propertiesPanel = new ChartPropertyPanel();

        layoutPanel = new ChartLayoutPanel();
        layoutPanel.setEnabled(true);
    }

    public void recreatePropertiesPanel() {
        propertiesPanel = new ChartPropertyPanel();
    }

    public void selectProperties(int category) {
        propertiesPanel.selectProperties(category);
    }

    public void hideProperties() {
        propertiesPanel.selectProperties(0);
    }

    public Chart getChart() {
        return propertiesPanel.getChart();
    }

    public void setChart(Chart chart) {
        propertiesPanel.setChart(chart);
        try {
            layoutPanel.setChart(chart);            
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }

}
