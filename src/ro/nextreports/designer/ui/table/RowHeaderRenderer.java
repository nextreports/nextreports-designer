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
package ro.nextreports.designer.ui.table;

import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

/**
 * Renderer for the row header for a given table.
 *
 * @author Decebal Suiu
 */

public class RowHeaderRenderer extends JLabel implements ListCellRenderer {

    private JTable table;
    private Border selectedBorder;
    private Border normalBorder;
    private Font selectedFont;
    private Font normalFont;

    RowHeaderRenderer(JTable table) {
        this.table = table;
        // this needs to be updated if the LaF changes
        normalBorder = UIManager.getBorder("TableHeader.cellBorder");
        selectedBorder = BorderFactory.createRaisedBevelBorder();
        final JTableHeader header = this.table.getTableHeader();
        normalFont = header.getFont();
        selectedFont = normalFont.deriveFont(normalFont.getStyle() | Font.BOLD);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setOpaque(true);
        setHorizontalAlignment(CENTER);
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        if (list.getSelectionModel().isSelectedIndex(index)) {
            setFont(selectedFont);
            setBorder(selectedBorder);
        } else {
            setFont(normalFont);
            setBorder(normalBorder);
        }

        int row = index + 1;
        setText(String.valueOf(row));
        return this;
    }

    class ForwardMouseListener implements MouseListener {

        private int row;

        public ForwardMouseListener(int row) {
            this.row = row;
        }

        public void mouseClicked(MouseEvent ev) {
//            System.out.println("ForwardMouseListener.mouseClicked()");
            forwardMouseEventToTable(row, ev);
        }

        public void mouseEntered(MouseEvent ev) {
            forwardMouseEventToTable(row, ev);
        }

        public void mouseExited(MouseEvent ev) {
            forwardMouseEventToTable(row, ev);
        }

        public void mousePressed(MouseEvent ev) {
            forwardMouseEventToTable(row, ev);
        }

        public void mouseReleased(MouseEvent ev) {
            forwardMouseEventToTable(row, ev);
        }

        /**
         * Forwards a mouse event to the table.
         */
        private void forwardMouseEventToTable(int row, MouseEvent ev) {
            Rectangle r = table.getCellRect(row, 0, false);
            ev = new MouseEvent(table, ev.getID(), ev.getWhen(),
                            ev.getModifiers(), r.x, r.y,
                            ev.getClickCount(), ev.isPopupTrigger());
            table.dispatchEvent(ev);
        }

    }
}

