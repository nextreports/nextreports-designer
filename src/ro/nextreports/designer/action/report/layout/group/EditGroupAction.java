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
package ro.nextreports.designer.action.report.layout.group;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.SelectionGroupPanel;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportGroup;
import ro.nextreports.engine.ReportLayout;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 2, 2008
 * Time: 11:04:32 AM
 */
public class EditGroupAction extends AbstractAction {

    public EditGroupAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("edit.group.action.name"));
    }

    public void actionPerformed(ActionEvent event) {
        List<ReportGroup> groups = LayoutHelper.getReportLayout().getGroups();
        if ((groups == null) || (groups.size() == 0)) {
            return;
        }

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        SelectionGroupPanel panel = new SelectionGroupPanel(true);
        BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("edit.group.action.name"), true);
        dialog.pack();
        dialog.setLocationRelativeTo(Globals.getMainFrame());
        dialog.setVisible(true);
        if (!dialog.okPressed()) {
            return;
        }

        ReportGroup reportGroup = panel.getGroup();
        String groupName = reportGroup.getName();
        String oldGroupColumn = reportGroup.getColumn();
        String groupColumn = panel.getGroupColumn();
        boolean headerOnEveryPage = panel.onEveryPage();
        boolean newPageAfter = panel.isNewPageAfter();
        LayoutHelper.getReportLayout().editGroup(groupName, groupColumn, headerOnEveryPage, newPageAfter);
        
        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.group.modify")));
    }
    
}
