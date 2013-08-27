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

import ro.nextreports.designer.BandLocation;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.GroupIndexGenerator;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.ReportGridPanel;
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
 * Date: Nov 28, 2008
 * Time: 11:38:53 AM
 */
public class RemoveGroupAction extends AbstractAction {

    public RemoveGroupAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("remove.group.action.name"));
    }

    public void actionPerformed(ActionEvent event) {
        List<ReportGroup> groups = LayoutHelper.getReportLayout().getGroups();
        if ((groups == null) || (groups.size() == 0)) {
            return;
        }

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        SelectionGroupPanel panel = new SelectionGroupPanel(false);
        BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("remove.group.action.name"), true);
        dialog.pack();
        dialog.setLocationRelativeTo(Globals.getMainFrame());
        dialog.setVisible(true);
        if (!dialog.okPressed()) {
            return;
        }        

        String groupName = panel.getGroup().getName();
        ReportGrid reportGrid = Globals.getReportGrid();
        ReportGridPanel reportGridPanel = Globals.getReportLayoutPanel().getReportGridPanel();
        
        BandLocation footerLocation = reportGrid.getBandLocation(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX + groupName);
        reportGridPanel.removeRows(footerLocation.getFirstGridRow(), footerLocation.getRowCount());
        BandLocation headerLocation = reportGrid.getBandLocation(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX + groupName);
        reportGridPanel.removeRows(headerLocation.getFirstGridRow(), headerLocation.getRowCount());
        LayoutHelper.getReportLayout().removeGroup(groupName);
        Globals.getReportDesignerPanel().getStructurePanel().deleteGroup(groupName);

        // update group index
        int i = Integer.parseInt(groupName);
        int index = GroupIndexGenerator.getLastIndex();
        if (i == index) {
            if (LayoutHelper.getReportLayout().getGroups().size() == 0) {
                GroupIndexGenerator.resetCurrentIndex();
            } else {
                GroupIndexGenerator.setCurrentIndex(index - 1);
            }
        }

        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.group.remove")));
    }
    
}
