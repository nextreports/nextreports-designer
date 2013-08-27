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

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import ro.nextreports.designer.util.ColorUtil;
import ro.nextreports.designer.util.UpDownTableKeyAdapter;


/**
 * This class implements a Row header for a given table.
 *
 * @author Decebal Suiu
 */
public class TableRowHeader extends JList {

    private JTable table;
    private int width;

    public TableRowHeader(JTable table) {
        this(table, -1);
    }

    public TableRowHeader(final JTable table, int width) {
        super(new TableRowHeaderModel(table));

        this.table = table;
        this.width = width;

        setFixedCellHeight(table.getRowHeight());
        setFixedCellWidth(preferredHeaderWidth());

        setCellRenderer(new RowHeaderRenderer(table));
        setFocusable(false);
        setAutoscrolls(false);

        // add list mouse listener
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int[] indices = getSelectedIndices();
                int firstIndex = indices[0];
                int lastIndex = indices[indices.length - 1];
                table.setRowSelectionInterval(firstIndex, lastIndex);
            }
        });

        // add table mouse listener
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                setSelectedIndices(table.getSelectedRows());
            }
        });
        table.addKeyListener(new UpDownTableKeyAdapter(table) {
            public void action(int row) {
                setSelectedIndices(new int[]{row});
            }
        });
        setBackground(ColorUtil.PANEL_BACKROUND_COLOR);
    }

    public JTable getTable() {
        return table;
    }

    /**
     * Returns the bounds of the specified range of items in JList coordinates.
     * Returns null if index isn't valid.
     *
     * @param index0
     *            the index of the first JList cell in the range
     * @param index1
     *            the index of the last JList cell in the range
     * @return the bounds of the indexed cells in pixels
     */
    public Rectangle getCellBounds(int index0, int index1) {
        Rectangle rect0 = table.getCellRect(index0, 0, true);
        Rectangle rect1 = table.getCellRect(index1, 0, true);
        int y, height;
        if (rect0.y < rect1.y) {
            y = rect0.y;
            height = rect1.y + rect1.height - y;
        } else {
            y = rect1.y;
            height = rect0.y + rect0.height - y;
        }
        return new Rectangle(0, y, getFixedCellWidth(), height);
    }

    // assume that row header width should be big enough to display row number
    // Integer.MAX_VALUE completely
    private int preferredHeaderWidth() {
        JLabel longestRowLabel = new JLabel("65356#");
        if (width == 1) {
            longestRowLabel = new JLabel("#");
        } else if (width == 2) {
            longestRowLabel = new JLabel("##");
        } else if (width == 3) {
            longestRowLabel = new JLabel("###");
        } else if (width == 4) {
            longestRowLabel = new JLabel("####");
        } else if (width == 5) {
            longestRowLabel = new JLabel("#####");
        }

        JTableHeader header = table.getTableHeader();
        longestRowLabel.setBorder(header.getBorder());
//        UIManager.getBorder("TableHeader.cellBorder"));
        longestRowLabel.setHorizontalAlignment(JLabel.CENTER);
        longestRowLabel.setFont(header.getFont());
        return longestRowLabel.getPreferredSize().width;
    }

    public void updateUI() {
        super.updateUI();
        if (table != null) {
            setCellRenderer(new RowHeaderRenderer(table));
            setBackground(ColorUtil.PANEL_BACKROUND_COLOR);
        }
        setOpaque(true);
        revalidate();
        repaint();
    }

}

