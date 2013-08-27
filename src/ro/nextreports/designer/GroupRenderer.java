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

import ro.nextreports.engine.ReportGroup;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 28, 2008
 * Time: 11:46:59 AM
 */
public class GroupRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        if (value != null) {
            ReportGroup group = (ReportGroup) value;
            value = I18NSupport.getString("band.group.header.name").substring(0, 1).toUpperCase() +
                    group.getName() + " (" + group.getColumn() + ")";
        }

        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

}
