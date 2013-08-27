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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * @author Decebal Suiu
 */
public class CheckListBoxCellRenderer extends JPanel
		implements ListCellRenderer {

    static Border activeBorder;
    static Border emptyBorder;

    private JCheckBox checkBox = new JCheckBox();
    private JLabel label = new JLabel();

    public CheckListBoxCellRenderer() {
        setOpaque(true);
        setLayout(new BorderLayout());
        this.add(checkBox, BorderLayout.WEST);
        this.add(label, BorderLayout.CENTER);
    }

    /**
     * Safe getter for active border.
     */
    Border getActiveBorder() {
        if (activeBorder == null) {
            activeBorder = UIManager.getBorder("List.focusCellHighlightBorder");
        }

        return activeBorder;
    }

    /**
     * Safe getter for empty border.
     */
    Border getEmptyBorder() {
        if (emptyBorder == null) {
            emptyBorder = new EmptyBorder(1, 1, 1, 1);
        }

        return emptyBorder;
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        CheckListItem item = (CheckListItem) value;

        if (isSelected) {
            checkBox.setBackground(UIManager.getColor("List.selectionBackground"));
            label.setBackground(UIManager.getColor("List.selectionBackground"));
            this.setBackground(UIManager.getColor("List.selectionBackground"));
            label.setForeground(list.getSelectionForeground());
        } else {
            checkBox.setBackground(UIManager.getColor("List.background"));
            label.setBackground(UIManager.getColor("List.background"));
            this.setBackground(UIManager.getColor("List.background"));
            label.setForeground(list.getForeground());
        }

        if (cellHasFocus) {
            setBorder(getActiveBorder());
        } else {
            setBorder(getEmptyBorder());
        }

        checkBox.setSelected(item.isSelected());

//		setEnabled(list.isEnabled());

        checkBox.setEnabled(item.isEnabled());
        label.setEnabled(item.isEnabled());

        label.setText(item.getText());
        label.setIcon(item.getIcon());
        label.setFont(list.getFont());

        return this;
    }

}
