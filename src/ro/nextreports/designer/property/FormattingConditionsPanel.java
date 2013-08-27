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
package ro.nextreports.designer.property;

import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.condition.BandElementCondition;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.HeaderStyle;

import javax.swing.*;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 12:08:43
 */
public class FormattingConditionsPanel extends JPanel {

    private JXTable table;
    private BandElementConditionTableModel model;
    private String type;
    // for wizard alarm definition (title != null)
    private String title;

    public FormattingConditionsPanel(String type) {
        this.type = type;
        initUI();
    }
    
    public FormattingConditionsPanel(String type, String title) {
        this.type = type;
        this.title=title;
        initUI();
    }

    public FormattingConditions getFinalRenderConditions() {
        FormattingConditions rc = new FormattingConditions();
        rc.set(model.getElements());
        return rc;
    }

    public void setRenderConditions(FormattingConditions conditions) {
        if (conditions != null) {
            model.addElements(conditions.getConditions());
        }
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        if (title == null) {
        	toolBar.setBorderPainted(false);
        }

        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("add");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("condition.add");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                add();
            }

        });

        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("edit");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("condition.edit");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                modify();
            }

        });

        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("delete");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("condition.delete");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                delete();
            }

        });
        
        if (title != null) {
        	toolBar.add(new JLabel(title));
        }


        add(toolBar, BorderLayout.NORTH);

        createTable();

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    modify();
                }
            }

        });
        table.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER) {
                    modify();
                    // don't let anyone else handle the event
                    event.consume();
                } else if (keyCode == KeyEvent.VK_INSERT) {
                    add();
                    // don't let anyone else handle the event
                    event.consume();
                } else if (keyCode == KeyEvent.VK_DELETE) {
                    delete();
                    // don't let anyone else handle the event
                    event.consume();
                }
            }

        });
        add(new JScrollPane(table), BorderLayout.CENTER);
        if (title != null) {
        	table.setPreferredScrollableViewportSize(new Dimension(300, 200));
        } else {
        	table.setPreferredScrollableViewportSize(new Dimension(400, 200));
        }
        if (title == null) {
        	setPrefferedColumnsSize();
        }
    }

    private void setPrefferedColumnsSize() {
        TableColumn col = table.getColumnModel().getColumn(0);
        int width = 110;
        col.setMinWidth(width);
        col.setMaxWidth(width);
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(1);
        width = 90;
        col.setMinWidth(width);
        col.setMaxWidth(width);
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(2);
        width = 200;
        col.setMinWidth(width);
        col.setMaxWidth(width);
        col.setPreferredWidth(width);

    }

    private void createTable() {
        model = new BandElementConditionTableModel();
        table = new JXTable(model);
        table.setSortable(false);
    }

    public void addAll(java.util.List<BandElementCondition> conditions) {
        for (BandElementCondition condition : conditions) {
            model.addElement(condition);
        }
    }


    public void addCondition(BandElementCondition condition) {
        model.addElement(condition);
    }

    private void add() {
    	boolean lockBackground = (title != null);
        BandElementConditionEditPanel panel = new BandElementConditionEditPanel(null, type, lockBackground);
        BandElementConditionEditDialog dlg = new BandElementConditionEditDialog(panel, I18NSupport.getString("condition.add"), true);
        dlg.pack();
        Show.centrateComponent(SwingUtilities.getWindowAncestor(FormattingConditionsPanel.this), dlg);
        dlg.setVisible(true);

        if (dlg.okPressed() && (dlg.getCondition() != null)) {
            model.addElement(dlg.getCondition());
        }
    }

    private void modify() {
    	boolean lockBackground = (title != null);
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length != 1) {
            Show.info(SwingUtilities.getWindowAncestor(FormattingConditionsPanel.this), I18NSupport.getString("condition.select"));
            return;
        }

        BandElementCondition oldCond = model.getObjectForRow(selectedRows[0]);

        BandElementConditionEditPanel panel = new BandElementConditionEditPanel(oldCond, type, lockBackground);
        BandElementConditionEditDialog dlg = new BandElementConditionEditDialog(panel, I18NSupport.getString("condition.edit"), true);
        dlg.pack();
        Show.centrateComponent(SwingUtilities.getWindowAncestor(FormattingConditionsPanel.this), dlg);
        dlg.setVisible(true);

        if (!dlg.okPressed()) {
            return;
        }

        BandElementCondition newCond = dlg.getCondition();

        updateObject(selectedRows[0], newCond);
    }

    public void updateObject(int row, BandElementCondition object) {
        row = table.convertRowIndexToModel(row);
        model.updateObject(row, object);
    }

    private void delete() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            Show.info(SwingUtilities.getWindowAncestor(FormattingConditionsPanel.this), I18NSupport.getString("condition.select"));
            return;
        }

        if (JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(FormattingConditionsPanel.this), I18NSupport.getString("condition.askDelete")) != JOptionPane.OK_OPTION) {
            return;
        }

        for (int i = selectedRows.length - 1; i >= 0; i--) {
            model.deleteElement(selectedRows[i]);
        }
    }
    
    public void setType(String type) {
    	this.type = type;
    }


}
