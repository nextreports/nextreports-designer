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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.query.DeselectListAction;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.datasource.DefaultSchemaManager;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.dbviewer.common.DBInfo;
import ro.nextreports.designer.dbviewer.common.DBTable;
import ro.nextreports.designer.dbviewer.common.DBViewer;
import ro.nextreports.designer.dbviewer.common.MalformedTableNameException;
import ro.nextreports.designer.dbviewer.common.NextSqlException;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 4, 2006
 * Time: 2:34:20 PM
 */
public class SelectionColumnPanel extends JPanel {

    private JComboBox schemaCombo;
    private JXList tableList;
    private JXList columnList;
    private JXList shownColumnList;
    private JScrollPane scrTable;
    private JScrollPane scrColumn;
    private JScrollPane scrShownColumn;
    private DefaultListModel tableModel = new DefaultListModel();
    private DefaultListModel columnModel = new DefaultListModel();
    private DefaultListModel shownColumnModel = new DefaultListModel();

    private Dimension scrDim = new Dimension(200, 200);
    private Dimension comboDim = new Dimension(200, 20);
    private String schema;
    private boolean show  = true;
    private boolean singleSelection = true;

    private static final Log LOG = LogFactory.getLog(SelectionColumnPanel.class);

    public SelectionColumnPanel(String schema) {
        this.schema = schema;
        buildUI();
    }

    public SelectionColumnPanel(String schema, boolean show, boolean singleSelection) {
        this.schema = schema;
        this.show = show;
        this.singleSelection = singleSelection;
        buildUI();
    }

    private void buildUI() {

        setLayout(new GridBagLayout());

        final DBViewer viewer = Globals.getDBViewer();

        schemaCombo = new JComboBox();
        schemaCombo.setPreferredSize(comboDim);

        schemaCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String schema = (String) e.getItem();
                shownColumnModel.clear();
                columnModel.clear();
                tableModel.clear();
                try {
                    DBInfo dbInfo = viewer.getDBInfo(schema, DBInfo.TABLES | DBInfo.VIEWS);
                    List<DBTable> tables = dbInfo.getTables();
                    Collections.sort(tables);
                    for (DBTable table : tables) {
                        tableModel.addElement(table);
                    }
                } catch (NextSqlException ex) {
                    LOG.error(ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            }
        });

        try {
            List<String> schemas = viewer.getSchemas();
            String schemaName = viewer.getUserSchema();
            Collections.sort(schemas);
            boolean added =  false;
            for (String schema : schemas) {
                if (DefaultSchemaManager.getInstance().isVisible(
                        DefaultDataSourceManager.getInstance().getConnectedDataSource().getName(), schema)) {
                    added = true;
                    schemaCombo.addItem(schema);
                }
            }

            if ((schema == null) || schema.equals(DefaultDBViewer.NO_SCHEMA_NAME)) {
                schema = DefaultDBViewer.NO_SCHEMA_NAME;//viewer.getUserSchema();
            }
            if (!added) {
               schemaCombo.addItem(schema); 
            }
            schemaCombo.setSelectedItem(schema);
        } catch (NextSqlException e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }

        // create table list
        tableList = new JXList(tableModel);
        tableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableList.setCellRenderer(new DBTableCellRenderer());

        // create column list
        columnList = new JXList(columnModel);
        if (singleSelection) {
            columnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        columnList.setCellRenderer(new DBColumnCellRenderer());

        shownColumnList = new JXList(shownColumnModel);
        if (singleSelection) {
            shownColumnList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        } else {
            columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        addDoubleClick();

        shownColumnList.setCellRenderer(new DBColumnCellRenderer());
        shownColumnList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                if ((mouseEvent.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem menuItem = new JMenuItem(new DeselectListAction(shownColumnList));
                    popupMenu.add(menuItem);
                    popupMenu.show((Component) mouseEvent.getSource(), mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });

        tableList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    int index = tableList.getSelectedIndex();
                    if (index == -1) {
                        return;
                    }
                    DBTable table = (DBTable) tableModel.getElementAt(index);
                    try {
                        List<DBColumn> columns = null;
                        try {
                            columns = viewer.getColumns(table.getSchema(), table.getName());
                        } catch (MalformedTableNameException e1) {
                            Show.error("Malformed table name : " + table.getName());
                            return;
                        }
                        Collections.sort(columns);
                        columnModel.clear();
                        shownColumnModel.clear();
                        for (DBColumn column : columns) {
                            columnModel.addElement(column);
                            shownColumnModel.addElement(column);
                        }
                    } catch (NextSqlException e1) {
                        LOG.error(e1.getMessage(), e1);
                        e1.printStackTrace();
                        Show.error(e1);
                    }
                }
            }
        });

        columnList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                shownColumnList.clearSelection();
            }
        });

        scrTable = new JScrollPane(tableList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrTable.setPreferredSize(scrDim);
        scrTable.setMinimumSize(scrDim);
        scrTable.setBorder(new TitledBorder(I18NSupport.getString("parameter.source.tables")));
        scrColumn = new JScrollPane(columnList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrColumn.setPreferredSize(scrDim);
        scrColumn.setMinimumSize(scrDim);
        scrColumn.setBorder(new TitledBorder(I18NSupport.getString("parameter.source.columns")));
        scrShownColumn = new JScrollPane(shownColumnList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrShownColumn.setPreferredSize(scrDim);
        scrShownColumn.setMinimumSize(scrDim);
        scrShownColumn.setBorder(new TitledBorder(I18NSupport.getString("parameter.source.shown.columns")));

        JPanel schemaPanel = new JPanel();
        schemaPanel.setLayout(new BoxLayout(schemaPanel, BoxLayout.X_AXIS));
        schemaPanel.add(new JLabel("Schema"));
        schemaPanel.add(Box.createHorizontalStrut(5));
        schemaPanel.add(schemaCombo);

        add(schemaPanel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(scrTable, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        add(scrColumn, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        if (show) {
            add(scrShownColumn, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
    }

    private  void addDoubleClick() {
        columnList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onDoubleClick();
                }
            }
        });
    }

    protected void onDoubleClick(){
    }

    class DBTableCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                DBTable table = (DBTable) value;
                if (table.getType().equals("VIEW")) {
                    comp.setIcon(ImageUtil.VIEW_IMAGE_ICON);
                    comp.setText(table.getName());
                } else {
                    comp.setIcon(ImageUtil.TABLE_IMAGE_ICON);
                    comp.setText(table.getName());
                }
                value = table.getName();
                list.setToolTipText(value.toString());
            }

            return comp;
        }

    }

    class DBColumnCellRenderer extends DefaultListCellRenderer {

        private final Icon primaryKeyIcon = ImageUtil.getImageIcon("keycolumn");
        private final Icon foreignKeyIcon = ImageUtil.getImageIcon("fkeycolumn");
        private final Icon indexKeyIcon = ImageUtil.getImageIcon("icolumn");
        private final Icon columnIcon = ImageUtil.getImageIcon("column");

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                DBColumn column = (DBColumn) value;
                if (column.isPrimaryKey()) {
                    comp.setIcon(primaryKeyIcon);
                    comp.setText(column.getName());
                } else if (column.isForeignKey()) {
                    comp.setIcon(foreignKeyIcon);
                    comp.setText(column.getName());
                } else if (column.isIndex()) {
                    comp.setIcon(indexKeyIcon);
                    comp.setText(column.getName());    
                } else {
                    comp.setIcon(columnIcon);
                    comp.setText(column.getName());
                }

                value = column.getName();
                list.setToolTipText(value.toString());
            }
            return comp;
        }

    }

    public DBColumn getSelectedColumn() {
        return (DBColumn) columnList.getSelectedValue();
    }

    public List<DBColumn> getSelectedColumns() {
        Object[] array = columnList.getSelectedValues();
        List<DBColumn> result = new ArrayList<DBColumn>();
        for (Object obj : array) {
            result.add((DBColumn)obj);
        }
        return result;
    }

    public List<DBColumn> getAllColumns() {
        Object[] array = columnModel.toArray();
        List<DBColumn> result = new ArrayList<DBColumn>();
        for (Object obj : array) {
            result.add((DBColumn) obj);
        }
        return result;
    }

    public DBColumn getSelectedShownColumn() {
        return (DBColumn) shownColumnList.getSelectedValue();
    }

    public String getJavaTypeForSelectedColumn() {
        DBColumn column = getSelectedColumn();
        return getJavaTypeForColumn(column);
    }

    public String getJavaTypeForSelectedShownColumn() {
        DBColumn column = getSelectedShownColumn();
        return getJavaTypeForColumn(column);
    }

    private String getJavaTypeForColumn(DBColumn column) {
        // dialect
        Dialect dialect = null;
        try {
            dialect = Globals.getDialect();
//            System.out.println("dialect =" + dialect + " columnType=" + column.getType() +
//                    " precision=" + column.getPrecision() + " scale=" + column.getScale());
            return dialect.getJavaType(column.getType(), column.getPrecision(), column.getScale());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            ex.printStackTrace();
            return "";
        }
    }


    private DBTable getTable(String tableName) {
        for (int i = 0, size = tableModel.size(); i < size; i++) {
            DBTable table = (DBTable) tableModel.getElementAt(i);
            if (table.getName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    private DBColumn getColumn(String tableName, String columnName) {
        for (int i = 0, size = columnModel.size(); i < size; i++) {
            DBColumn column = (DBColumn) columnModel.getElementAt(i);
            if (column.getTable().equals(tableName) && column.getName().equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    private DBColumn getShownColumn(String tableName, String columnName) {
        for (int i = 0, size = shownColumnModel.size(); i < size; i++) {
            DBColumn column = (DBColumn) shownColumnModel.getElementAt(i);
            if (column.getTable().equals(tableName) && column.getName().equals(columnName)) {
                return column;
            }
        }
        return null;
    }

    public void setColumns(String source) {

        if ((schema != null) && (!schema.equals("%"))) {
            schemaCombo.setSelectedItem(schema);
        }

        int index = source.indexOf(".");
        int index2 = source.lastIndexOf(".");
        if (index < 1) {
            throw new IllegalArgumentException("Invalid source " + source);
        }

        String tableName = source.substring(0, index);
        String columnName;
        String shownColumnName = null;
        if (index == index2) {
            columnName = source.substring(index + 1);
        } else {
            columnName = source.substring(index + 1, index2);
            shownColumnName = source.substring(index2 + 1);
        }

        DBTable table = getTable(tableName);

        if (table != null) {
            tableList.setSelectedValue(table, true);
            DBColumn column = getColumn(tableName, columnName);
            columnList.setSelectedValue(column, true);
            if (shownColumnName != null) {
                DBColumn shownColumn = getShownColumn(tableName, shownColumnName);
                shownColumnList.setSelectedValue(shownColumn, true);
            }
        }
    }

    public String getSchema() {
        return (String)schemaCombo.getSelectedItem();
    }


}
