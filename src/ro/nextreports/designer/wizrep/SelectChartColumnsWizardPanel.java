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

import ro.nextreports.engine.queryexec.Query;
import ro.nextreports.engine.util.StringUtil;
import ro.nextreports.engine.util.NameType;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.JXCollapsiblePane;

import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.ui.wizard.WizardPanel;
import ro.nextreports.designer.util.I18NSupport;

/**
 * User: mihai.panaitescu
 * Date: 12-Jan-2010
 * Time: 13:54:15
 */
public class SelectChartColumnsWizardPanel extends WizardPanel {

    private final int no = 5;
    private JComboBox xComboBox = new JComboBox();
    private JComboBox yComboBox = new JComboBox();
    private JComboBox[] more = new JComboBox[no];
    private List<NameType> allColumns = new ArrayList<NameType>();
    private Dimension dim = new Dimension(200, 20);

    public SelectChartColumnsWizardPanel() {
        super();
        banner.setTitle(I18NSupport.getString("wizard.panel.step",4,5) + I18NSupport.getString("wizard.panel.selcolumns.title"));
        banner.setSubtitle(I18NSupport.getString("wizard.panel.selcolumns.chart.subtitle"));
        init();
    }

    /**
     * Called when the panel is set.
     */
    public void onDisplay() {
        String sql = ((Query) context.getAttribute(WizardConstants.QUERY)).getText();
        try {
            allColumns = ReportLayoutUtil.getAllColumnsForSql(null, sql, (DataSource)context.getAttribute(WizardConstants.DATA_SOURCE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        xComboBox.setPreferredSize(dim);
        yComboBox.setPreferredSize(dim);
        xComboBox.removeAllItems();
        yComboBox.removeAllItems();
        xComboBox.addItem(I18NSupport.getString("chart.column.select"));
        yComboBox.addItem(I18NSupport.getString("chart.column.select"));
        for (int i=0; i<no; i++) {
            more[i].setPreferredSize(dim);
            more[i].removeAllItems();
            more[i].addItem(I18NSupport.getString("chart.column.select"));
        }
        for (NameType column : allColumns){
            xComboBox.addItem(column.getName());
            yComboBox.addItem(column.getName());
            for (int i=0; i<no; i++) {
                more[i].addItem(column.getName());
            }
        }
    }

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public boolean hasNext() {
        return false;
    }

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public boolean validateNext(List<String> messages) {         
        if (xComboBox.getSelectedIndex() == 0) {
            messages.add(I18NSupport.getString("wizard.panel.selcolumns.chart.x"));
            return false;
        }
        if (yComboBox.getSelectedIndex() == 0) {
            messages.add(I18NSupport.getString("wizard.panel.selcolumns.chart.y"));
            return false;
        }

        if (emptyYColumns()) {
            messages.add(I18NSupport.getString("wizard.panel.selcolumns.chart.y.order"));
            return false;
        }

        // must test on all columns!
        String duplicateColumn = StringUtil.getFirstDuplicateValue(ReportLayoutUtil.getColumnNames(allColumns));
        if (duplicateColumn != null) {
            messages.add(I18NSupport.getString("new.ambigous.columns.wizard", duplicateColumn));
            return false;
        }
        context.setAttribute(WizardConstants.CHART_X_COLUMN, getXColumn());
        context.setAttribute(WizardConstants.CHART_Y_COLUMNS, getYColumns());
        context.setAttribute(WizardConstants.REPORT_COLUMNS, allColumns);
        return true;
    }

    private boolean emptyYColumns() {
        boolean empty = false;
        for (int i=0; i<no; i++) {
            if (more[i].getSelectedIndex() == 0) {
                empty = true;
            } else {
                if (empty) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the next panel to go to.
     */
    public WizardPanel getNextPanel() {
        return null;
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
            WizardUtil.openChart(context, null);
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
        for (int i=0; i<no; i++) {
            more[i] = new JComboBox();
        }
        JPanel panel = new JPanel(new GridBagLayout());

        panel.add(new JLabel(I18NSupport.getString("chart.xcolumn")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        panel.add(xComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        panel.add(new JLabel(I18NSupport.getString("chart.ycolumn")), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        panel.add(yComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));


        JXCollapsiblePane cp = new JXCollapsiblePane();
        cp.setCollapsed(true);

        // get the built-in toggle action
        Action toggleAction = cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION);
        // use the collapse/expand icons from the JTree UI
        toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, UIManager.getIcon("Tree.expandedIcon"));
        toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, UIManager.getIcon("Tree.collapsedIcon"));
        JButton toggle = new JButton(toggleAction);
        toggle.setText("");
        toggle.setBorder(BorderFactory.createEmptyBorder());

        cp.setLayout(new GridBagLayout());
        for (int i=0; i<no; i++) {
            cp.add(more[i], new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 35, 5, 5), 0, 0));
        }
        cp.add(new JXTitledSeparator(""), new GridBagConstraints(0, no, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(10, 0, 5, 140), 0, 0));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.add(toggle);
        headerPanel.add(Box.createHorizontalStrut(5));
        headerPanel.add(new JXTitledSeparator(I18NSupport.getString("chart.ycolumn.more")));

        panel.add(headerPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 140), 0, 0));
        panel.add(cp, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 0), 0, 0));

        panel.add(new JLabel(""), new GridBagConstraints(3, 9, 1, 3, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(5, 5, 5, 5), 0, 0));

        add(panel, BorderLayout.CENTER);
    }

    public String getXColumn() {
        return (String)xComboBox.getSelectedItem();
    }

    public List<String> getYColumns() {
        List<String> list = new ArrayList<String>();
        list.add((String)yComboBox.getSelectedItem());
        for (int i=0; i<no; i++) {
            if (more[i].getSelectedIndex() > 0) {
                list.add((String)more[i].getSelectedItem());
            } else {
                break;
            }
        }
        return list;
    }
        
}

