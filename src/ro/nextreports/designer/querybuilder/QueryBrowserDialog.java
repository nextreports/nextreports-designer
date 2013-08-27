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
 * Date: Apr 13, 2006
 * Time: 11:05:25 AM
 */
public class QueryBrowserDialog extends BaseDialog {

    private QueryBrowserPanel panel;
    private boolean okPressed;

    public QueryBrowserDialog(QueryBrowserPanel panel) {
        super(panel, I18NSupport.getString("query.browser.open"), true);
        this.panel = panel;
        this.panel.setParent(this);
        okPressed = false;
    }

    protected boolean ok() {
        okPressed = true;
        String name = panel.getSelectedName();
        if (name == null) {
            Show.info(I18NSupport.getString("query.browser.select"));
            return false;
        }
        return true;
    }

    public boolean okPressed() {
       return panel.isDoubleClick() || okPressed;
    }


}
