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

import ro.nextreports.engine.exporter.util.function.FunctionFactory;
import ro.nextreports.engine.exporter.util.function.AbstractGFunction;
import ro.nextreports.engine.exporter.util.function.GFunction;

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
 * Date: Nov 25, 2008
 * Time: 10:43:26 AM
 */
public class GroupPanel extends JPanel {

    private JComboBox columnComboBox;
    private JComboBox functionComboBox;
    private JComboBox functionColumnComboBox;
    private JCheckBox headerCheckBox;
    private JCheckBox headerOnEveryPageCheckBox;
    private JCheckBox newPageAfterCheckBox;

    public GroupPanel() {

        List<String> columns = new ArrayList<String>();
        try {
            columns = ReportLayoutUtil.getAllColumnNamesForReport(null);
        } catch (Exception e) {
            Show.error(e);
        }
        List<GFunction> functions = FunctionFactory.getFunctions();

        JLabel columnLabel = new JLabel(I18NSupport.getString("function.column.group"));
        DefaultComboBoxModel columnGroupComboModel = new DefaultComboBoxModel(columns.toArray());
        columnComboBox = new JComboBox(columnGroupComboModel);        

        JLabel functionLabel = new JLabel(I18NSupport.getString("function.title"));
        DefaultComboBoxModel functionComboModel = new DefaultComboBoxModel(functions.toArray());
        functionComboBox = new JComboBox(functionComboModel);
        functionComboBox.setRenderer(new FunctionRenderer());
        functionComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                 updateSelection();
            }
        });

        JLabel functionColumnLabel = new JLabel(I18NSupport.getString("function.column"));
        DefaultComboBoxModel functionColumnGroupComboModel = new DefaultComboBoxModel(columns.toArray());
        functionColumnComboBox = new JComboBox(functionColumnGroupComboModel);

        headerCheckBox = new JCheckBox(I18NSupport.getString("group.header.row"), true);
        headerOnEveryPageCheckBox = new JCheckBox(I18NSupport.getString("group.header.row.everyPage"));

        headerCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                 if (headerCheckBox.isSelected()) {
                     headerOnEveryPageCheckBox.setEnabled(true);
                 } else {
                     headerOnEveryPageCheckBox.setSelected(false);
                     headerOnEveryPageCheckBox.setEnabled(false);
                 }
            }
        });

        newPageAfterCheckBox = new JCheckBox(I18NSupport.getString("group.new.page"));

        updateSelection();

        setLayout(new GridBagLayout());

        add(columnLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
        add(columnComboBox, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 0), 0, 0));
        add(functionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
        add(functionComboBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 0), 0, 0));
        add(functionColumnLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
        add(functionColumnComboBox, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 0), 0, 0));
        add(headerCheckBox, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
//        add(headerOnEveryPageCheckBox, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
//                GridBagConstraints.WEST, GridBagConstraints.NONE,
//                new Insets(0, 0, 0, 0), 0, 0));
        add(newPageAfterCheckBox, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 0), 0, 0));
        
    }

    public GFunction getFunction() {
        return (GFunction) functionComboBox.getSelectedItem();
    }

    public String getGroupColumn() {
        return (String) columnComboBox.getSelectedItem();
    }

    public String getFunctionColumn() {
        return (String) functionColumnComboBox.getSelectedItem();
    }

    public boolean hasHeader() {
        return headerCheckBox.isSelected();
    }

    public boolean onEveryPage() {
        return headerOnEveryPageCheckBox.isSelected();
    }

    public boolean hasFooter() {
        return !AbstractGFunction.NOOP.equals(getFunction().getName());
    }

    public boolean isNewPageAfter() {
        return newPageAfterCheckBox.isSelected();
    }

    private void updateSelection() {
        if (hasFooter()) {
            headerCheckBox.setEnabled(true);
            functionColumnComboBox.setEnabled(true);
        } else {
            headerCheckBox.setSelected(true);
            headerCheckBox.setEnabled(false);
            functionColumnComboBox.setEnabled(false);
        }
        if (hasHeader()) {
            headerOnEveryPageCheckBox.setEnabled(true);
        } else {
            headerOnEveryPageCheckBox.setSelected(false);
            headerOnEveryPageCheckBox.setEnabled(false);
        }
    }

}
