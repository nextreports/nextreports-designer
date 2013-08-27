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
package ro.nextreports.designer.querybuilder;


import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.*;

import org.jdesktop.swingx.JXTable;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 24-Apr-2009
// Time: 16:11:45

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.dbviewer.common.DBProcedureColumn;
import ro.nextreports.designer.dbviewer.common.DBViewer;
import ro.nextreports.designer.dbviewer.common.MalformedTableNameException;
import ro.nextreports.designer.dbviewer.common.NextSqlException;
import ro.nextreports.designer.querybuilder.table.DBColumnTableModel;
import ro.nextreports.designer.querybuilder.table.DBProcedureColumnTableModel;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

//
public class DBProcedureColumnViewerPanel extends JPanel {

    private Dimension viewerDim = new Dimension(400, 200);
    private DBProcedureColumnTableModel model;
    private JXTable table;
    private boolean error = false;

    public DBProcedureColumnViewerPanel(DBObject procObject) {

        String procName = procObject.getName();
        model = new DBProcedureColumnTableModel();
        final DBViewer viewer = Globals.getDBViewer();
        try {
            java.util.List<DBProcedureColumn> columns = null;
            columns = viewer.getProcedureColumns(procObject.getSchemaName(), procObject.getCatalog(), procName);
            model.addElements(columns);

            table = new JXTable(model);
            table.getTableHeader().setReorderingAllowed(false);
            //table.setBackground(ColorUtil.PANEL_BACKROUND_COLOR);
            table.setGridColor(Color.LIGHT_GRAY);
            //the only way to completely prevent a selection in a JTable component
            table.setFocusable(false);
            table.setCellSelectionEnabled(false);

            setLayout(new BorderLayout());
            add(new JScrollPane(table));
            table.setPreferredScrollableViewportSize(viewerDim);
            JTableHeader header = table.getTableHeader();
            final Font boldFont = header.getFont().deriveFont(Font.BOLD);
            final TableCellRenderer headerRenderer = header.getDefaultRenderer();
            header.setDefaultRenderer(new TableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component comp = headerRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    comp.setFont(boldFont);
                    return comp;
                }
            });

            setPrefferedColumnsSize();

        } catch (NextSqlException e1) {
            error = true;
            e1.printStackTrace();
            Show.error(e1);
        }
    }

    private void setPrefferedColumnsSize() {
        TableColumn col = table.getColumnModel().getColumn(0);
        int width = 200;
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(1);
        width = 120;
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(2);
        width = 120;
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(3);
        width = 80;
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(4);
        width = 80;
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(5);
        width = 80;
        col.setPreferredWidth(width);
    }


    public boolean isError() {
        return error;
    }

}
