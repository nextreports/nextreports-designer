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
package ro.nextreports.designer.property;

import ro.nextreports.engine.condition.BandElementCondition;

import javax.swing.table.AbstractTableModel;

import ro.nextreports.designer.util.I18NSupport;

import java.util.List;
import java.util.ArrayList;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 12:54:06
 */
public class BandElementConditionTableModel extends AbstractTableModel {    

    private final String[] columnNames = {          
            I18NSupport.getString("condition.expression"),            
            I18NSupport.getString("condition.property"),
            I18NSupport.getString("condition.property.value")
    };

    private List<BandElementCondition> elements = new ArrayList<BandElementCondition>();

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

    public void addElement(BandElementCondition object) {
        elements.add(object);
        fireTableDataChanged();
    }

    public void addElement(BandElementCondition object, int index) {
        elements.add(index, object);
        fireTableDataChanged();
    }

    public void addElements(List<BandElementCondition> objects) {
        elements.addAll(objects);
        fireTableDataChanged();
    }

    public void deleteElement(int rowIndex) {
        elements.remove(rowIndex);
        fireTableDataChanged();
    }

    public void deleteElemets(List<BandElementCondition> objects) {
        elements.removeAll(objects);
        fireTableDataChanged();
    }

    public void clear() {
        elements.clear();
        fireTableDataChanged();
    }

    public BandElementCondition getObjectForRow(int rowIndex) {
        return elements.get(rowIndex);
    }

    public int getRowForObject(BandElementCondition object) {
        for (int i = 0; i < elements.size(); i++) {
            if (object.equals(elements.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public List<BandElementCondition> getElements() {
        return elements;
    }

    public void updateObject(int row, BandElementCondition object) {
        elements.set(row, object);
        fireTableDataChanged();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        BandElementCondition row = elements.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.getExpression().getText();           
            case 1:
                return BandElementConditionEditPanel.getProperty(row.getProperty());
            case 2:
                return BandElementConditionEditPanel.getPropertyValueAsString(row.getPropertyValue());
            default:
                return null;
        }
    }   
}
