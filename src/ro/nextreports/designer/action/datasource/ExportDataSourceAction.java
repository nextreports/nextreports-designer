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
package ro.nextreports.designer.action.datasource;


import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSourceDialog;
import ro.nextreports.designer.datasource.DataSourcePanel;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Oct 7, 2008
 * Time: 1:34:20 PM
 */
public class ExportDataSourceAction extends AbstractAction {

    public ExportDataSourceAction() {
        this(true);
    }

    public ExportDataSourceAction(boolean fullName) {
        if (fullName) {
            putValue(Action.NAME, I18NSupport.getString("export.data.source"));
        } else {
            putValue(Action.NAME, I18NSupport.getString("import.data.source.small"));
        }
        putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("database_export"));
        putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("export.data.source.desc"));
        putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("export.data.source.desc"));
    }

    public void actionPerformed(ActionEvent ev) {
        BaseDialog dialog = new DataSourceDialog(new DataSourcePanel());
        dialog.pack();
        Show.centrateComponent(Globals.getMainFrame(), dialog);
        dialog.setVisible(true);
    }

}
