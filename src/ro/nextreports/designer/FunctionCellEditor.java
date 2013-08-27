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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.*;

import org.jdesktop.swingx.JXPanel;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.exporter.util.function.FunctionFactory;

/**
 * @author Decebal Suiu
 */
public class FunctionCellEditor extends DefaultGridCellEditor {

    private FunctionPanel panel;
    private BaseDialog dialog;
    private FunctionBandElement bandElement;

    public FunctionCellEditor() {
        super(new JTextField()); // not really relevant - sets a text field as the editing default.
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        boolean isEditable = super.isCellEditable(event);
        if (isEditable) {
            editorComponent = new JLabel("...", JLabel.HORIZONTAL);
            delegate = new FunctionDelegate();
        }

        return isEditable;
    }

    /*
    private void applyStyleToEditor() {
        editorComponent.setFont(bandElement.getFont());
        editorComponent.setBackground(bandElement.getBackground());
        editorComponent.setForeground(bandElement.getForeground());
        Border outer = new CustomLineBorder(bandElement.getBorder());
        Border inner = null;
        if (bandElement.getPadding() != null) {
            Padding padding = bandElement.getPadding();
            inner = new EmptyBorder(padding.getTop(), padding.getLeft(), padding.getBottom(), padding
                    .getRight());
        } else {
            inner = new EmptyBorder(0, 0, 0, 0);
        }
        CompoundBorder border = new CompoundBorder(outer, inner);
        editorComponent.setBorder(border);
    }
    */

    class FunctionDelegate extends EditorDelegate {

        FunctionDelegate() {
            panel = new FunctionPanel();
            dialog = new BaseDialog(panel, I18NSupport.getString("function.title"), true);
            dialog.pack();
            dialog.setLocationRelativeTo(Globals.getMainFrame());
        }

        public void setValue(Object value) {
            bandElement = (FunctionBandElement) value;
            panel.setFunctionName(bandElement.getFunction());
            panel.setColumn(bandElement.getColumn(), bandElement.isExpression());
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dialog.setVisible(true);
                    if (dialog.okPressed()) {
                        stopCellEditing();
                    } else {
                        cancelCellEditing();
                        //delete $F{(?,?)} (when close function panel)
                        if (bandElement.getColumn().equals("?")) {
                            new ClearCellAction().actionPerformed(null);
                        }

                    }
                }

            });
        }

        public Object getCellEditorValue() {
            ReportLayout oldLayout = getOldLayout();
            bandElement.setFunction(panel.getFunctionName());
            bandElement.setColumn(panel.getColumn());
            bandElement.setExpression(panel.isExpression());
            registerUndoRedo(oldLayout, I18NSupport.getString("edit.function"), I18NSupport.getString("edit.function.insert"));            
            return bandElement;
        }

    }

    class FunctionPanel extends JXPanel {

        private JComboBox nameComboBox;
        private JRadioButton columnRadioButton;
        private JRadioButton expressionRadioButton;
        private JComboBox expressionComboBox;
        private JComboBox columnComboBox;

        public FunctionPanel() {

            columnRadioButton = new JRadioButton();
            expressionRadioButton = new JRadioButton();
            ButtonGroup group = new ButtonGroup();
            group.add(columnRadioButton);
            group.add(expressionRadioButton);
            columnRadioButton.setSelected(true);
            columnRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     if (columnRadioButton.isSelected()) {
                         enableButtons(false);
                     }
                }
            });
            expressionRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     if (expressionRadioButton.isSelected()) {
                         enableButtons(true);
                     }
                }
            });

            List<String> columns = new ArrayList<String>();
            try {
                columns = ReportLayoutUtil.getAllColumnNamesForReport(null);
            } catch (Exception e) {
                Show.error(e);
            }
            List<String> functions = FunctionFactory.getFunctionNames();

            JLabel functionLabel = new JLabel(I18NSupport.getString("function.name"));
            DefaultComboBoxModel functionComboModel = new DefaultComboBoxModel(functions.toArray());
            nameComboBox = new JComboBox(functionComboModel);
            nameComboBox.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");

            JLabel functionColumnLabel = new JLabel(I18NSupport.getString("designer.column"));
            DefaultComboBoxModel functionColumnGroupComboModel = new DefaultComboBoxModel(columns.toArray());
            columnComboBox = new JComboBox(functionColumnGroupComboModel);

            JLabel expColumnLabel = new JLabel(I18NSupport.getString("function.expression"));
            DefaultComboBoxModel expColumnGroupComboModel = new DefaultComboBoxModel(ReportUtil.getExpressionsNames(LayoutHelper.getReportLayout()).toArray());
            expressionComboBox = new JComboBox(expColumnGroupComboModel);

            setLayout(new GridBagLayout());

            add(functionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
            add(nameComboBox, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 5, 0), 0, 0));
            if (expColumnGroupComboModel.getSize() > 0) {
                add(columnRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));
            }
            add(functionColumnLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
            add(columnComboBox, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 5, 0), 0, 0));
            if (expColumnGroupComboModel.getSize() > 0) {
                add(expressionRadioButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));
                add(expColumnLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 0), 0, 0));
                add(expressionComboBox, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 5, 5, 0), 0, 0));
            }
        }

        public String getFunctionName() {
            return (String) nameComboBox.getSelectedItem();
        }

        public void setFunctionName(String name) {
            nameComboBox.setSelectedItem(bandElement.getFunction());
        }

        public String getColumn() {
            if (columnRadioButton.isSelected()) {
                return (String) columnComboBox.getSelectedItem();
            } else {
                return (String) expressionComboBox.getSelectedItem();
            }
        }

        public void setColumn(String column, boolean isExpression) {
            if (isExpression) {
                expressionComboBox.setSelectedItem(bandElement.getColumn());
                expressionRadioButton.setSelected(true);
            } else {
                columnComboBox.setSelectedItem(bandElement.getColumn());
                columnRadioButton.setSelected(true);
            }
            enableButtons(isExpression);
        }

        public boolean isExpression() {
            return expressionRadioButton.isSelected();
        }

        private void enableButtons(boolean isExpression) {
           columnComboBox.setEnabled(!isExpression);
           expressionComboBox.setEnabled(isExpression);
        }

    }

}
