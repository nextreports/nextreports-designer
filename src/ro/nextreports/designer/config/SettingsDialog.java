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
package ro.nextreports.designer.config;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

//
public class SettingsDialog extends BaseDialog {

    public SettingsDialog(SettingsPanel basePanel) {
        super(basePanel, I18NSupport.getString("settings.action"));
    }

    public int getTimeout() throws SettingsException {
        return ((SettingsPanel) basePanel).getTimeout();
    }

    public boolean getHTMLAccessibility() {
        return ((SettingsPanel) basePanel).getHTMLAccessibility();
    }
    
    public boolean getMaxRowsChecked() {
        return ((SettingsPanel) basePanel).getMaxRowsChecked();
    }

    public boolean getA4Warning() {
        return ((SettingsPanel) basePanel).getA4Warning();
    }

    public boolean isRulerVisible() {
        return ((SettingsPanel) basePanel).isRulerVisible();
    }

    public String getOracleClientPath() {
        return ((SettingsPanel) basePanel).getOracleClientPath();
    }

    public String getFontDirectories() {
        return ((SettingsPanel) basePanel).getFontDirectories();
    }

    public char getCsvDelimiter() throws SettingsException {
        return ((SettingsPanel) basePanel).getCsvDelimiter();
    }

    public String getRulerUnit() {
        return ((SettingsPanel) basePanel).getRulerUnit();
    }

    protected boolean ok() {
        try {
            getTimeout();
        } catch (SettingsException e) {
            Show.error(I18NSupport.getString("settings.action.invalid.timeout"));
            return false;
        }
        try {
            getCsvDelimiter();
        } catch (SettingsException e) {
            Show.error(I18NSupport.getString("settings.action.invalid.csv.delimiter"));
            return false;
        }
        return true;

    }
}

