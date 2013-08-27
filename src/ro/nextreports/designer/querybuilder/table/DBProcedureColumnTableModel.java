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

import ro.nextreports.designer.dbviewer.common.DBProcedureColumn;
import ro.nextreports.designer.util.I18NSupport;

import java.util.List;
import java.util.ArrayList;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 24-Apr-2009
// Time: 16:12:42

//
public class DBProcedureColumnTableModel extends DefaultTableModel {

    private final String[] columnNames = {
            I18NSupport.getString("view.columns.name"),
            I18NSupport.getString("view.columns.type.return"),
            I18NSupport.getString("view.columns.type"),
            I18NSupport.getString("view.columns.length"),
            I18NSupport.getString("view.columns.precision"),
            I18NSupport.getString("view.columns.scale")
    };

    private List<DBProcedureColumn> elements = new ArrayList<DBProcedureColumn>();

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

    public void addElement(DBProcedureColumn object) {
        elements.add(object);
        fireTableDataChanged();
    }

    public void addElement(DBProcedureColumn object, int index) {
        elements.add(index, object);
        fireTableDataChanged();
    }

    public void addElements(List<DBProcedureColumn> objects) {
        elements.addAll(objects);
        fireTableDataChanged();
    }

    public void deleteElement(int rowIndex) {
        elements.remove(rowIndex);
        fireTableDataChanged();
    }

    public void deleteElemets(List<DBProcedureColumn> objects) {
        elements.removeAll(objects);
        fireTableDataChanged();
    }

    public DBProcedureColumn getObjectForRow(int rowIndex) {
        return elements.get(rowIndex);
    }

    public int getRowForObject(DBProcedureColumn object) {
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
        DBProcedureColumn row = elements.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.getName();
            case 1:
                return row.getReturnType();
            case 2:
                return row.getDataType();
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
