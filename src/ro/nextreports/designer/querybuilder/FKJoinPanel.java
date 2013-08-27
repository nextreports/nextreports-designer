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

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.dbviewer.common.NextSqlException;
import ro.nextreports.designer.util.ImageUtil;

import java.util.List;
import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 29, 2006
 * Time: 10:28:08 AM
 */
public class FKJoinPanel extends JPanel {

    private JList list;
    private Dimension scrDim = new Dimension(300, 200);
    private Thread t;
    private FKJoinDialog dialog;

    public FKJoinPanel(final String schemaName, final String pkTableName, final String pkColumnName) {

        list = new JList();
        list.setCellRenderer(new JoinListCellRenderer());

        setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(scrDim);
        add(scroll, BorderLayout.CENTER);

        Runnable r = new Runnable() {
            public void run() {
                DBColumn c = new DBColumn(schemaName, pkTableName, pkColumnName, null, false, false, null, 0, 0, 0);
                try {
                    Cursor hourGlassCursor = new Cursor(Cursor.WAIT_CURSOR);
                    dialog.setCursor(hourGlassCursor);
//                    System.out.println("----- start ");
//                    long start = System.currentTimeMillis();
                    List<DBColumn> columns = Globals.getDBViewer().getForeignKeyColumns(c);
                    final DefaultListModel listModel = new DefaultListModel();
                    for (DBColumn column : columns) {
                        //String element = column.getTable() + "." + column.getName();
                        listModel.addElement(column);
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            list.setModel(listModel);
                        }
                    });
//                    System.out.println(" --> " + columns);
//                    long end = System.currentTimeMillis();
//                    System.out.println("----- end in " + (end - start) / 1000L + " sec");
                } catch (NextSqlException e1) {
                    e1.printStackTrace();
                } finally {
                    Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
                    dialog.setCursor(normalCursor);
                }
            }
        };
        t = new Thread(r, "NEXT : " + getClass().getSimpleName());
    }

    public void fetch() {
        t.start();
    }

    public void setDialog(FKJoinDialog dialog) {
        this.dialog = dialog;
    }

    public DBColumn getSelectedColumn() {
        return (DBColumn)list.getSelectedValue();
    }

    public ArrayList<DBColumn> getSelectedColumns() {
        ArrayList<DBColumn> columns = new ArrayList<DBColumn>();
        Object[] objects = list.getSelectedValues();
        for (Object obj : objects) {
            columns.add((DBColumn)obj);
        }
        return columns;
    }

    class JoinListCellRenderer extends JLabel
            implements ListCellRenderer {

        public JoinListCellRenderer() {
            // Don't paint behind the component
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, // value to display
                                             int index,    // cell index
                                             boolean iss,  // is selected
                                             boolean chf)  // cell has focus?
        {
            DBColumn col = (DBColumn)value;
            setText(col.getTable() + "." + col.getName());
            setBorder(BorderFactory.createEmptyBorder(1,5,1,5));
            setIcon(ImageUtil.getImageIcon("fkeycolumn"));
            if (iss) {
                Color c = (Color)UIManager.getDefaults().get("List.selectionBackground");
                setBackground(c);
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
}
