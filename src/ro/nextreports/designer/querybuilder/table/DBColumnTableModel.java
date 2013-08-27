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
package ro.nextreports.designer.querybuilder.table;


import javax.swing.table.DefaultTableModel;

import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.util.I18NSupport;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 19, 2006
 * Time: 10:11:49 AM
 */
public class DBColumnTableModel extends DefaultTableModel {

    private final String[] columnNames = {
            " ",
            I18NSupport.getString("view.columns.name"),
            I18NSupport.getString("view.columns.type"),
            I18NSupport.getString("view.columns.length"),
            I18NSupport.getString("view.columns.precision"),
            I18NSupport.getString("view.columns.scale")
    };

    private List<DBColumn> elements = new ArrayList<DBColumn>();

    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        // this method is called in the constructor so we must test for null
        if (elements == null) {
            return 0;
        }
        return elements.size();
    }

    public void addElement(DBColumn object) {
        elements.add(object);
        fireTableDataChanged();
    }

    public void addElement(DBColumn object, int index) {
        elements.add(index, object);
        fireTableDataChanged();
    }

    public void addElements(List<DBColumn> objects) {
        elements.addAll(objects);
        fireTableDataChanged();
    }

    public void deleteElement(int rowIndex) {
        elements.remove(rowIndex);
        fireTableDataChanged();
    }

    public void deleteElemets(List<DBColumn> objects) {
        elements.removeAll(objects);
        fireTableDataChanged();
    }

    public DBColumn getObjectForRow(int rowIndex) {
        return elements.get(rowIndex);
    }

    public int getRowForObject(DBColumn object) {
        for (int i = 0; i < elements.size(); i++) {
            if (object.equals(elements.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        DBColumn row = elements.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row;
            case 1:
                return row.getName();
            case 2:
                return row.getType();
            case 3:
                return row.getLength();
            case 4:
                return row.getPrecision();
            case 5:
                return row.getScale();
            default:
                return null;
        }
    }

}
