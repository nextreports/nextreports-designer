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
package ro.nextreports.designer;

import ro.nextreports.engine.exporter.util.function.GFunction;
import ro.nextreports.engine.exporter.util.function.AbstractGFunction;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 25, 2008
 * Time: 11:21:20 AM
 */
public class FunctionRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        if (value != null) {
            GFunction function = (GFunction) value;
            value = getDisplayName(function);
        }

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }


    private String getDisplayName(GFunction function) {
        String message;
        if (AbstractGFunction.NOOP.equals(function.getName())) {
            message = I18NSupport.getString("group.function.noop");
        } else if (AbstractGFunction.SUM.equals(function.getName())) {
            message = I18NSupport.getString("group.function.sum");
        } else if (AbstractGFunction.MIN.equals(function.getName())) {
            message = I18NSupport.getString("group.function.min");
        } else if (AbstractGFunction.MAX.equals(function.getName())) {
            message = I18NSupport.getString("group.function.max");
        } else if (AbstractGFunction.AVERAGE.equals(function.getName())) {
            message = I18NSupport.getString("group.function.average");
        } else if (AbstractGFunction.COUNT.equals(function.getName())) {
            message = I18NSupport.getString("group.function.count");
        } else if (AbstractGFunction.COUNT_DISTINCT.equals(function.getName())) {
            message = I18NSupport.getString("group.function.countdistinct");
        } else {
            message = function.getName();
        }
        return message;
    }
}
