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
package ro.nextreports.designer.chart;

import ro.nextreports.engine.util.NameType;

import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.*;
import java.util.List;
import java.awt.*;

/**
 * User: mihai.panaitescu
 * Date: 07-Jan-2010
 * Time: 14:44:02
 */
public class ChartColumnListCellRenderer extends BasicComboBoxRenderer {

    private List<NameType> columns;

    public ChartColumnListCellRenderer(List<NameType> columns) {
        this.columns = columns;
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setForeground(list.getForeground());
            setBackground(list.getBackground());
        }
        if (value != null) {
            setText(value.toString());
        } else {
            setText("");
        }
        try {
            if ((index >= 0) && (columns.get(index).getType() != null)) {
                if (!Number.class.isAssignableFrom(Class.forName(columns.get(index).getType()))) {
                    setFocusable(false);
                    setEnabled(false);
                } else {
                    setFocusable(true);
                    setEnabled(true);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }
}
