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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.dbviewer.common.DBViewer;
import ro.nextreports.designer.dbviewer.common.MalformedTableNameException;
import ro.nextreports.designer.dbviewer.common.NextSqlException;
import ro.nextreports.designer.querybuilder.table.DBColumnTableModel;
import ro.nextreports.designer.util.CopyTableMouseAdapter;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;


/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 19, 2006
 * Time: 10:15:49 AM
 */
public class DBColumnViewerPanel extends JPanel {

    private Dimension viewerDim = new Dimension(400, 200);
    private DBColumnTableModel model;
    private JXTable table;
    private boolean error = false;

    public DBColumnViewerPanel(DBObject tableObject) {

        String tableName = tableObject.getName();
        model = new DBColumnTableModel();
        final DBViewer viewer = Globals.getDBViewer();
        try {
            List<DBColumn> columns = null;
            try {
                columns = viewer.getColumns(tableObject.getSchemaName(), tableName);
            } catch (MalformedTableNameException e1) {
                Show.error(e1);
                error = true;
                return;
            }
            Collections.sort(columns);
            model.addElements(columns);

            table = new JXTable(model);
            table.getColumnModel().getColumn(0).setCellRenderer(new DBKeyRenderer());
            table.getTableHeader().setReorderingAllowed(false);
            //table.setBackground(ColorUtil.PANEL_BACKROUND_COLOR);
            table.setGridColor(Color.LIGHT_GRAY);
            table.addMouseListener(new CopyTableMouseAdapter(table) {
            	protected Object getFormattedValue(Object value) {    	
                	if (value instanceof DBColumn) {
                		// first column is primary key hint : no need to copy it
                		return "";
                	} else {
                		return super.getFormattedValue(value);
                	}
                }
            });
            //the only way to completely prevent a selection in a JTable component
            //table.setFocusable(false);
            //table.setCellSelectionEnabled(false);

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
        int width = 20;
        col.setMinWidth(width);
        col.setMaxWidth(width);
        col.setPreferredWidth(width);

        col = table.getColumnModel().getColumn(1);
        width = 160;
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

    class DBKeyRenderer extends JLabel  implements TableCellRenderer {

        public Component getTableCellRendererComponent(
                JTable table, Object element,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            DBColumn value = (DBColumn) element;
            ImageIcon icon = null;
            if (value != null) {
                if (value.isPrimaryKey()) {
                    icon = ImageUtil.getImageIcon("keycolumn");
                } else if (value.isForeignKey()) {
                    icon = ImageUtil.getImageIcon("fkeycolumn");
                } else {
                    icon = ImageUtil.getImageIcon("column");
                }
            }

            setIcon(icon);
            return this;

        }
    }

    public boolean isError() {
        return error;
    }

}
