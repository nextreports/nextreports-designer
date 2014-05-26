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
package ro.nextreports.designer.wizrep;

import ro.nextreports.engine.exporter.ResultExporter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;

/**
 * User: mihai.panaitescu
 * Date: 12-Jan-2010
 * Time: 13:34:03
 */
public class EntityWizardPanel extends WizardPanel {

    private JRadioButton reportButton;
    private JRadioButton chartButton;
    private JComboBox reportTypeCombo;
    private Dimension dim = new Dimension(100, 20);

    public EntityWizardPanel() {
        super();
        banner.setTitle(getTitle());
        banner.setSubtitle(getSubtitle());
        init();        
    }

    public String getTitle() {
        return I18NSupport.getString("wizard.panel.step",1, 5)  + I18NSupport.getString("wizard.panel.entity.title");
    }

    public String getSubtitle() {
        return I18NSupport.getString("wizard.panel.entity.subtitle");
    }

    /**
     * Called when the panel is set.
     */
    public void onDisplay() {
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return true;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(java.util.List<String> messages) {    	
        context.setAttribute(WizardConstants.ENTITY, getEntity());
        context.setAttribute(WizardConstants.REPORT_TYPE, getReportType());
        return true;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return new DataSourceWizardPanel();
    }

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return false;
    }

    /**
     * Called to validate the panel before finishing the wizard. Should return
     * false if canFinish returns false.
     *
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(java.util.List<String> messages) {
        return false;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }


    private void init() {
        setLayout(new BorderLayout());

        reportButton = new JRadioButton(I18NSupport.getString("wizard.panel.entity.report"));
        chartButton = new JRadioButton(I18NSupport.getString("wizard.panel.entity.chart"));
        ButtonGroup bg = new ButtonGroup();
        bg.add(reportButton);
        bg.add(chartButton);
        reportButton.setSelected(true);
        
        reportTypeCombo = new JComboBox();
        reportTypeCombo.setPreferredSize(dim);
        reportTypeCombo.addItem(WizardConstants.REPORT_TYPE_DEFAULT);
        reportTypeCombo.addItem(WizardConstants.REPORT_TYPE_TABLE);
        reportTypeCombo.addItem(WizardConstants.REPORT_TYPE_ALARM);
        reportTypeCombo.addItem(WizardConstants.REPORT_TYPE_INDICATOR);
        reportTypeCombo.addItem(WizardConstants.REPORT_TYPE_DISPLAY);
        reportTypeCombo.setRenderer(new ReportTypeRenderer());
        
        reportButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				reportTypeCombo.setEnabled(reportButton.isSelected());				
			}        	
        });
        chartButton.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				reportTypeCombo.setEnabled(!chartButton.isSelected());				
			}        	
        });                

        JPanel panel = new JPanel(new GridBagLayout());
        panel.add(reportButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(reportTypeCombo, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        panel.add(new JLabel(""), new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(chartButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(new JLabel(""), new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
        add(panel, BorderLayout.CENTER);
    }

    private String getEntity() {
        if (reportButton.isSelected()) {
            return WizardConstants.ENTITY_REPORT;
        } else {
            return WizardConstants.ENTITY_CHART;
        }
    }
    
    private int getReportType() {
        if (reportButton.isSelected()) {
            String s =  (String)reportTypeCombo.getSelectedItem();
            if (WizardConstants.REPORT_TYPE_TABLE.equals(s)) {
            	return ResultExporter.TABLE_TYPE;
            } else if (WizardConstants.REPORT_TYPE_ALARM.equals(s)) {
            	return ResultExporter.ALARM_TYPE;
            } else if (WizardConstants.REPORT_TYPE_INDICATOR.equals(s)) {
            	return ResultExporter.INDICATOR_TYPE;
            } else if (WizardConstants.REPORT_TYPE_DISPLAY.equals(s)) {
            	return ResultExporter.DISPLAY_TYPE;	
            } else {
            	return ResultExporter.DEFAULT_TYPE;
            }
        } else {
        	// does not matter
            return ResultExporter.DEFAULT_TYPE;
        }
    }
    
    class ReportTypeRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (value != null) {
                String type = (String) value;
                if (type != null) {
                    value = I18NSupport.getString("property.report." + type);
                } 
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

}

