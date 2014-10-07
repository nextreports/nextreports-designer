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


import java.sql.Connection;
import java.util.Map;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.DefaultKeyHandler;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 3, 2006
 * Time: 2:16:40 PM
 */
public class RuntimeParametersDialog extends BaseDialog {

    private RuntimeParametersPanel panel;
    private Map<String, Object> parametersValues;
    private boolean okPressed = false;
    private final DefaultKeyHandler keyHandler = new DefaultKeyHandler();

    public RuntimeParametersDialog(RuntimeParametersPanel panel) {
        super(panel, I18NSupport.getString("runtime.parameters.values"), true);
        this.panel = panel;
        keyHandler.mapEnterKeyAction(okAction);
        keyHandler.registerComponent(this);
    }

    protected boolean ok() {

        try {
            parametersValues = panel.getParametersValues();
        } catch (RuntimeParameterException e) {
            Show.info(e.getMessage());
            return false;
        }
        okPressed = true;
        return true;
    }

    public Map<String, Object> getParametersValues() {
        return parametersValues;
    }

    public boolean okPressed() {
        return okPressed;
    }
    
    public Connection getTemporaryConnection() {
    	return panel.getTemporaryConnection();
    }
}
