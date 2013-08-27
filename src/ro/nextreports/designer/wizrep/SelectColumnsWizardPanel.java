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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ListSelectionPanel;

import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.util.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 10, 2008
 * Time: 11:56:18 AM
 */
public class SelectColumnsWizardPanel extends WizardPanel {

    private ListSelectionPanel panel;
    private List<String> allColumns = new ArrayList<String>();

    public SelectColumnsWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.panel.step",4,5) + I18NSupport.getString("wizard.panel.selcolumns.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.selcolumns.subtitle"));
        init();
    }

    /**
     * Called when the panel is set.
     */
    public void onDisplay() {
        String sql = ((Query) context.getAttribute(WizardConstants.QUERY)).getText();
        try {
            allColumns = ReportLayoutUtil.getAllColumnNamesForSql(null, sql);
        } catch (Exception e) {
            e.printStackTrace();  
        }
        List<String> source = getSourceColumns();
        allColumns.removeAll(source);
        panel.setLists(source, allColumns);
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
        List<String> columns = getReportColumns();        
        if (columns.size() == 0) {
            messages.add(I18NSupport.getString("select.columns.none"));
            return false;
        }
        // must test on all columns!
        String duplicateColumn = StringUtil.getFirstDuplicateValue(allColumns);
        if (duplicateColumn != null) {
            messages.add(I18NSupport.getString("new.ambigous.columns.wizard", duplicateColumn));
            return false;
        }
        context.setAttribute(WizardConstants.REPORT_COLUMNS, columns);
        return true;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return new SelectTemplateWizardPanel();
    }

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public boolean canFinish() {
        return true;
    }

    /**
     * Called to validate the panel before finishing the wizard. Should return
     * false if canFinish returns false.
     *
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public boolean validateFinish(List<String> messages) {
        boolean result = validateNext(messages);
        if (result) {
            WizardUtil.openReport(context, null);
        }
        return result;
    }

    /**
     * Handle finishing the wizard.
     */
    public void onFinish() {
    }

    private void init() {
        setLayout(new BorderLayout());
        //JPanel qPanel = new JPanel(new GridBagLayout());

        panel = new ListSelectionPanel(new ArrayList<String>(),new ArrayList<String>(),
                I18NSupport.getString("select.columns.query"),
                I18NSupport.getString("select.columns.report"),
                false, true);

        add(panel, BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    public List<String> getReportColumns() {
        return panel.getDestinationElements();
    }

    @SuppressWarnings("unchecked")
    public List<String> getSourceColumns() {
        return panel.getSourceElements();
    }


}

