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

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 3, 2006
 * Time: 5:37:17 PM
 */
public class BrowserDialog extends BaseDialog {

    private BrowserPanel panel;
    private boolean okPressed;

    public BrowserDialog(BrowserPanel panel) {
        super(panel, (panel.getType() == BrowserPanel.REPORT_BROWSER) ?
                I18NSupport.getString("report.browser.open") :
                I18NSupport.getString("chart.browser.open"), true);
        this.panel = panel;
        okPressed = false;
    }

    protected boolean ok() {
        okPressed = true;
        String name = panel.getSelectedName();
        if (name == null) {
            if (panel.getType() == BrowserPanel.REPORT_BROWSER) {
                Show.info(I18NSupport.getString("report.browser.select"));
            } else {
                Show.info(I18NSupport.getString("chart.browser.select"));
            }
            return false;
        }
        return true;
    }

    public boolean okPressed() {
       return okPressed;
    }


}
