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
 * Date: Apr 4, 2006
 * Time: 2:48:19 PM
 */
public class SelectionColumnDialog extends BaseDialog {

    private SelectionColumnPanel panel;
    private DBColumn column;
    private DBColumn shownColumn;
    private String javaType;
    private String shownJavaType;

    public SelectionColumnDialog(SelectionColumnPanel panel) {
        super(panel, I18NSupport.getString("column.selection.title"), true);
        this.panel = panel;
    }

    protected boolean ok() {
        column = panel.getSelectedColumn();
        javaType = panel.getJavaTypeForSelectedColumn();
        if (column == null) {
            Show.info(I18NSupport.getString("column.selection.ask"));
            return false;
        }
        shownColumn = panel.getSelectedShownColumn();
        if (shownColumn != null) {
            // the same column has no meaning and gets to an oracle ambiguous column error
            if (shownColumn.getName().equals(column.getName())) {
                Show.info(I18NSupport.getString("column.selection.same"));
                return false;
            }
            shownJavaType = panel.getJavaTypeForSelectedShownColumn();
        }
        return true;
    }

    public DBColumn getColumn() {
        return column;
    }

    public String getJavaType() {
        return javaType;
    }

    public DBColumn getShownColumn() {
        return shownColumn;
    }

    public String getShownJavaType() {
        return shownJavaType;
    }
}
