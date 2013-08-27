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


import java.util.Map;
import java.util.List;
import java.io.Serializable;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 24-Aug-2009
// Time: 15:04:46

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

//
public class ParameterValueSelectionDialog extends BaseDialog {

    private ParameterValueSelectionPanel panel;
    private Map<String, Object> parametersValues;
    private boolean okPressed = false;

    public ParameterValueSelectionDialog(ParameterValueSelectionPanel panel) {
        super(panel, I18NSupport.getString("parameter.default.add"), true);
        this.panel = panel;
    }

    protected boolean ok() {
        try {
            panel.getValues();
        } catch (NumberFormatException ex){
            Show.info(I18NSupport.getString("parameter.default.invalid"));
            return false;
        }
        okPressed = true;
        return true;
    }

    public List<Serializable> getValues() {
        return panel.getValues();
    }

    public boolean okPressed() {
        return okPressed;
    }
}

