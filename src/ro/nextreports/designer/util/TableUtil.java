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
package ro.nextreports.designer.util;

import java.awt.Container;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;

import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.WildCardColumn;
import ro.nextreports.engine.querybuilder.sql.Table;
import ro.nextreports.engine.querybuilder.sql.dialect.Dialect;
import ro.nextreports.engine.persistence.TablePersistentObject;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.util.DialectUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.ui.list.CheckListItem;
import ro.nextreports.designer.ui.table.TableRowHeader;

/**
 * This class contains utility methods for doing common tasks with JTables.
 *
 * @author Decebal Suiu
 */

public class TableUtil {

    private static final Log LOG = LogFactory.getLog(TableUtil.class);

    /**
     * Creates row header for table with row number (starting with 1) displayed.
     */
    public static TableRowHeader setRowHeader(JTable table) {
        return setRowHeader(table, -1);
    }

    /**
     * Creats a row header for the given table. The row number is displayed to
     * the left of the table ( starting with row 1).
     *
     * @param table       the table to create the row header for
     * @param headerWidth the number of characters to size the header
     */
    public static TableRowHeader setRowHeader(JTable table, int headerWidth) {
        TableRowHeader result = null;
        Container p = table.getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                result = new TableRowHeader(table);
                scrollPane.setRowHeaderView(result);
            }
        }
        return result;
    }


    /**
     * Creates row header for table with row number (starting with 1) displayed.
     */
    public static void removeRowHeader(JTable table) {
        Container p = table.getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                scrollPane.setRowHeader(null);
            }
        }
    }

    private static List<CheckListItem> loadTable(Table table) throws Exception {        

        List<CheckListItem> result = new LinkedList<CheckListItem>();
        List<DBColumn> columns = Globals.getDBViewer().getColumns(table.getSchemaName(), table.getName());

        SortedSet<DBColumn> set = new TreeSet<DBColumn>();

        // generic all columns
        String allColumns = "* (All columns)";
        DBColumn allColumn = new DBColumn(table.getSchemaName(), table.getName(), allColumns, null, false, false, null, 0, 0, 0);
        set.add(allColumn);
        for (DBColumn column : columns) {            
            set.add(column);
        }

        for (DBColumn dbColumn : set) {
            String sortedColumnName = dbColumn.getName();
            CheckListItem item = new CheckListItem();
            item.setText(sortedColumnName);
            Column column;
            if (allColumns.equals(sortedColumnName)) {
                column = new WildCardColumn(table, getJavaTypeForColumn(dbColumn));
            } else {
                column = new Column(table, sortedColumnName, getJavaTypeForColumn(dbColumn));
            }
            if (dbColumn.isPrimaryKey()) {
                item.setIcon(ImageUtil.getImageIcon("keycolumn"));
                column.setpKey(true);
            } else if (dbColumn.isForeignKey()) {
                item.setIcon(ImageUtil.getImageIcon("fkeycolumn"));
                column.setfKey(true);
            } else {
                item.setIcon(ImageUtil.getImageIcon("column"));
            }
            item.setObject(column);
            result.add(item);
        }

        return result;
    }

    public static String getJavaTypeForColumn(DBColumn column) {
        // dialect
        Dialect dialect = null;
        try {
            dialect = Globals.getDialect();
//            System.out.println("dialect =" + dialect + " columnType=" + column.getType() +
//                    " precision=" + column.getPrecision() + " scale=" + column.getScale());
            return dialect.getJavaType(column.getType(), column.getPrecision(), column.getScale());
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }


    public static Map<String, List<CheckListItem>> getItemMap(Report report) throws Exception {
        Map<String, List<CheckListItem>> itemMap = new HashMap<String, List<CheckListItem>>();
        if (report.getTables() == null) {
            return itemMap;
        }
        for (TablePersistentObject tpo : report.getTables()) {
            Table table = tpo.getTable();
            try {
                table.setDialect(DialectUtil.getDialect(Globals.getConnection()));
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                LOG.error(e.getMessage(), e);
            }
            String key = table.getAlias();
            if(key == null) {
                key =table.getName();
            }
            itemMap.put(key, TableUtil.loadTable(table));
        }
        return itemMap;
    }

    public static Map<String, List<CheckListItem>> getItemMap(Table table) throws Exception {
        List<CheckListItem> items = TableUtil.loadTable(table);
        Map<String, List<CheckListItem>> itemMap = new HashMap<String, List<CheckListItem>>();
        String key = table.getAlias();
        if (key == null) {
            key = table.getName();
        }
        itemMap.put(key, items);
        return itemMap;
    }

}

