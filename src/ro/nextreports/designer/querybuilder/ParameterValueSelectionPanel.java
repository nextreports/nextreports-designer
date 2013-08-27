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

import ro.nextreports.engine.queryexec.QueryParameter;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.JDateTimePicker;

import java.io.Serializable;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 24-Aug-2009
// Time: 14:45:50

//
public class ParameterValueSelectionPanel extends JPanel {

    private static String DELIM = ";";

    private String type;
    private JComponent component;

    private Dimension dim = new Dimension(200, 22);

    public ParameterValueSelectionPanel(String type) {
        this.type = type;        
        if (QueryParameter.DATE_VALUE.equals(type) || QueryParameter.TIME_VALUE.equals(type) ||
                QueryParameter.TIMESTAMP_VALUE.equals(type)) {
            component = new JDateTimePicker();
            component.setMinimumSize(dim);
            component.setPreferredSize(dim);
        } else if (QueryParameter.BOOLEAN_VALUE.equals(type)) {
            component = new JCheckBox();
        } else {
            component = new JTextField();
            component.setMinimumSize(dim);
            component.setPreferredSize(dim);
        }

        add(new JLabel(I18NSupport.getString("parameter.default.value")));
        add(component);
    }

    // number and string values can be separated by semicolon delimiter
    public List<Serializable> getValues() throws NumberFormatException {
        List<Serializable> list = new ArrayList<Serializable>();
        if (QueryParameter.DATE_VALUE.equals(type) || QueryParameter.TIME_VALUE.equals(type) ||
                QueryParameter.TIMESTAMP_VALUE.equals(type)) {
            Serializable value = ((JDateTimePicker) component).getDate();
            list.add(value);
        } else if (QueryParameter.BOOLEAN_VALUE.equals(type)) {
            Serializable value = ((JCheckBox)component).isSelected();
            list.add(value);
        } else {
            String text = ((JTextField) component).getText();
            String[] values = text.split(DELIM);            
            for (String v : values) {
                Serializable value;
                if (QueryParameter.INTEGER_VALUE.equals(type)) {
                    value = Integer.parseInt(v);
                } else if (QueryParameter.BYTE_VALUE.equals(type)) {
                    value = Byte.parseByte(v);
                } else if (QueryParameter.SHORT_VALUE.equals(type)) {
                    value = Short.parseShort(v);
                } else if (QueryParameter.LONG_VALUE.equals(type)) {
                    value = Long.parseLong(v);
                } else if (QueryParameter.FLOAT_VALUE.equals(type)) {
                    value = Float.parseFloat(v);
                } else if (QueryParameter.DOUBLE_VALUE.equals(type)) {
                    value = Double.parseDouble(v);
                } else if (QueryParameter.BIGDECIMAL_VALUE.equals(type)) {
                    value = new BigDecimal(v);
                } else { // String
                    value = v;
                }
                list.add(value);
            }
        }
        return list;
    }
}
