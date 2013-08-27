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

import ro.nextreports.engine.ReportGroup;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 28, 2008
 * Time: 11:42:51 AM
 */
public class SelectionGroupPanel extends JPanel {

    private boolean columnSelection;
    private JComboBox groupComboBox;
    private JComboBox columnComboBox;
    private JCheckBox headerOnEveryPageCheckBox;
    private JCheckBox newPageAfterCheckBox;

    public SelectionGroupPanel(boolean columnSelection) {

        this.columnSelection = columnSelection;

        List<ReportGroup> groups = LayoutHelper.getReportLayout().getGroups();
        List<String> columns = new ArrayList<String>();
        try {
            columns = ReportLayoutUtil.getAllColumnNamesForReport(null);
        } catch (Exception e) {
            Show.error(e);
        }

        JLabel groupLabel = new JLabel(I18NSupport.getString("selection.group.name"));
        DefaultComboBoxModel groupComboModel = new DefaultComboBoxModel(groups.toArray());
        groupComboBox = new JComboBox(groupComboModel);
        groupComboBox.setRenderer(new GroupRenderer());
        groupComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                 updateColumn();
            }
        });

        JLabel columnLabel = new JLabel(I18NSupport.getString("function.column.group"));
        DefaultComboBoxModel columnGroupComboModel = new DefaultComboBoxModel(columns.toArray());
        columnComboBox = new JComboBox(columnGroupComboModel);

        headerOnEveryPageCheckBox = new JCheckBox(I18NSupport.getString("group.header.row.everyPage"));
        newPageAfterCheckBox = new JCheckBox(I18NSupport.getString("group.new.page"));

        updateColumn();

        setLayout(new GridBagLayout());

        add(groupLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
        add(groupComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 0), 0, 0));
        if (columnSelection) {
            add(columnLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
            add(columnComboBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 5, 0), 0, 0));
//            add(headerOnEveryPageCheckBox, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
//                GridBagConstraints.WEST, GridBagConstraints.NONE,
//                new Insets(0, 0, 0, 0), 0, 0));
        }
        add(newPageAfterCheckBox, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
    }

    private void updateColumn() {
        ReportGroup group = getGroup();
        columnComboBox.setSelectedItem(group.getColumn());
        headerOnEveryPageCheckBox.setSelected(group.isHeaderOnEveryPage());
        newPageAfterCheckBox.setSelected(group.isNewPageAfter());
    }

    public ReportGroup getGroup() {
        return (ReportGroup) groupComboBox.getSelectedItem();
    }

    public String getGroupColumn() {
        return (String) columnComboBox.getSelectedItem();
    }

    public boolean onEveryPage() {
        return headerOnEveryPageCheckBox.isSelected();
    }

    public boolean isNewPageAfter() {
        return newPageAfterCheckBox.isSelected();
    }
}
