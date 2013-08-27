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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.queryexec.Query;

public class SelectOneColumnWizardPanel extends WizardPanel {

	private JComboBox combo;
    private List<String> allColumns = new ArrayList<String>();

    public SelectOneColumnWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.panel.step",4,5) + I18NSupport.getString("wizard.panel.selonecolumn.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.selonecolumn.subtitle"));
        init();
    }

    /**
     * Called when the panel is set.
     */
    @SuppressWarnings("unchecked")
	public void onDisplay() {
        String sql = ((Query) context.getAttribute(WizardConstants.QUERY)).getText();
        try {
            allColumns = ReportLayoutUtil.getAllColumnNamesForSql(null, sql);
        } catch (Exception e) {
            e.printStackTrace();  
        }        
        for (String s : allColumns) {
        	combo.addItem(s);
        }
        
        List<String> all = (List<String>)context.getAttribute(WizardConstants.REPORT_COLUMNS);
        if (all != null) {
        	combo.setSelectedItem( all.get(0) );
        }
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
    public boolean validateNext(List<String> messages) {     
    	List<String> columns = new ArrayList<String>();
    	String column = getReportColumn();
    	columns.add(column);
    	if (column.contains(" ")) {
    		messages.add(I18NSupport.getString("wizard.panel.selonecolumn.error"));
    		return false;
    	}
    	context.setAttribute(WizardConstants.REPORT_COLUMNS, columns);
        return true;
    }

    /**
     * Get the next panel to go to.
     */
	public WizardPanel getNextPanel() {
		String entity = (String) context.getAttribute(WizardConstants.ENTITY);
		Integer reportType = (Integer) context.getAttribute(WizardConstants.REPORT_TYPE);
		if (WizardConstants.ENTITY_REPORT.equals(entity)) {
			if (reportType.equals(ResultExporter.ALARM_TYPE)) {
				return new SelectAlarmSettingsWizardPanel();
			} else if (reportType.equals(ResultExporter.INDICATOR_TYPE)) {
				return new SelectIndicatorSettingsWizardPanel();
			}
		}
		return new SelectTemplateWizardPanel();
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
    public boolean validateFinish(List<String> messages) {
       return false;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }

    private void init() {
        setLayout(new GridBagLayout());        

        combo = new JComboBox();        
        add(combo, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(""), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(""), new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));
    }
    
    public String getReportColumn() {
        return (String)combo.getSelectedItem();
    }   

}
