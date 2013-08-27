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
package ro.nextreports.designer.ui.list;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Decebal Suiu
 */
public class CheckListBox extends JList {

    public static Dimension tableDim = new Dimension(200, 150);

    public CheckListBox() {
        super(new DefaultListModel());
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setDoubleBuffered(true);
        this.setCellRenderer(new CheckListBoxCellRenderer());
        this.addMouseListener(new MouseAdapter() {

        	@Override
            public void mouseClicked(MouseEvent event) {
                if (!SwingUtilities.isLeftMouseButton(event)) {
                	return;
                }
                
                int index = locationToIndex(event.getPoint());
                if (index == -1) {
                	return;
                }

                CheckListItem item = (CheckListItem) getModel().getElementAt(index);
                if (item == null) {
                	return;
                }

                final int checkBoxWidth = UIManager.getIcon("CheckBox.icon").getIconWidth() + 2;

                if (event.getPoint().x < getLocation().x + checkBoxWidth) {
                	onCheck(item);
                }
            }
            
        });
    }

    public CheckListItem addItem(String label, Object object) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = new CheckListItem(label, object);
        model.addElement(item);
        return item;
    }

    @Override
    public void removeAll() {
        DefaultListModel model = (DefaultListModel) this.getModel();
        model.removeAllElements();
    }

    public boolean isSelected(int index) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = (CheckListItem) (model.getElementAt(index));
        return item.isSelected();
    }

    public void setSelected(int index, boolean b) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = (CheckListItem) (model.getElementAt(index));
        item.setSelected(b);
        repaint();
    }

    public void setSelected(String label, boolean b) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        for (int i = 0; i < model.size(); i++) {
            CheckListItem item = (CheckListItem) (model.getElementAt(i));
            if (item.getText().equals(label)) {
                setSelected(i, b);
                return;
            }
        }
    }

    public List getSelected() {
        return getSelected(false);
    }

    @SuppressWarnings("unchecked")
    public List getSelected(boolean onlyEnabled) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        List selectedObjects = new ArrayList();
        for (int i = 0; i < model.size(); i++) {
            CheckListItem item = (CheckListItem) model.getElementAt(i);
            if (item.isSelected()) {
                if (!onlyEnabled) {
                    selectedObjects.add(item.getText());
                } else if (item.isEnabled()) {
                    selectedObjects.add(item.getText());
                }
            }
        }

        return selectedObjects;
    }

    public List getSelectedObjects() {
        return getSelectedObjects(false);
    }

    @SuppressWarnings("unchecked")
    public List getSelectedObjects(boolean onlyEnabled) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        List selectedObjects = new ArrayList();
        for (int i = 0; i < model.size(); i++) {
            CheckListItem item = (CheckListItem) model.getElementAt(i);
            if (item.isSelected()) {
                if (!onlyEnabled) {
                    selectedObjects.add(item.getObject());
                } else if (item.isEnabled()) {
                    selectedObjects.add(item.getObject());
                }
            }
        }

        return selectedObjects;
    }

    public void setSelected(boolean b) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        int size = model.size();
        for (int i = 0; i < size; i++) {
            CheckListItem item = (CheckListItem) model.getElementAt(i);
            item.setSelected(b);
        }
        repaint();
    }

    public void selectRow(int index) {
        this.setSelectedIndex(index);
    }

    public boolean isEnabled(int index) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = (CheckListItem) model.getElementAt(index);
        return item.isEnabled();
    }

    public void setEnabled(int index, boolean b) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = (CheckListItem) model.getElementAt(index);
        item.setEnabled(b);
        repaint();
    }

    public void setEnabledAll(boolean b) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        int size = model.size();
        for (int i = 0; i < size; i++) {
            CheckListItem item = (CheckListItem) model.getElementAt(i);
            item.setEnabled(b);
        }
        repaint();
    }

    public Object getObject(int index) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = (CheckListItem) model.getElementAt(index);
        return item.getObject();
    }

    public void setObject(Object object, int index) {
        DefaultListModel model = (DefaultListModel) this.getModel();
        CheckListItem item = (CheckListItem) model.getElementAt(index);
        item.setObject(object);
    }

    public int sizeCheck() {
        DefaultListModel model = (DefaultListModel) this.getModel();
        int size = model.size();
        if (size == 0) {
            return 0;
        }

        int sizeCheck = 0;
        for (int i = 0; i < size; i++) {
            CheckListItem item = (CheckListItem) model.getElementAt(i);
            if (item.isSelected()) {
                sizeCheck++;
            }
        }

        return sizeCheck;
    }

    public int sizeList() {
        DefaultListModel model = (DefaultListModel) this.getModel();
        return model.getSize();
    }


    protected void onCheck(CheckListItem item) {
        item.setSelected(!item.isSelected());
        DefaultListModel model = (DefaultListModel) this.getModel();
        int index = model.indexOf(item);
        Rectangle r = getCellBounds(index, index);
        CheckListBox.this.repaint(r);
    }
    
}
