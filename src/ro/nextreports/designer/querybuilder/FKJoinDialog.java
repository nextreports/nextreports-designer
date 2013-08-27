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

import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 29, 2006
 * Time: 10:39:36 AM
 */
public class FKJoinDialog extends BaseDialog {

    private FKJoinPanel panel;
    private boolean okPressed;

    public FKJoinDialog(FKJoinPanel panel) {
        super(panel, I18NSupport.getString("join.dialog.select"), true);
        this.panel = panel;
        panel.setDialog(this);
        okPressed = false;
        panel.fetch();
    }

    protected boolean ok() {
        okPressed = true;
        DBColumn col = panel.getSelectedColumn();
        if (col == null) {
            Show.info(I18NSupport.getString("join.dialog.column"));
            return false;
        }
        return true;
    }

    public boolean okPressed() {
       return okPressed;
    }


}
