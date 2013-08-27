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
package ro.nextreports.designer.action.report.layout;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportGroup;
import ro.nextreports.engine.ReportLayout;

import javax.swing.*;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.GroupIndexGenerator;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.DefaultGridModel;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 21, 2008
 * Time: 2:47:09 PM
 */
public class ClearLayoutAction extends AbstractAction {

    private boolean silent = false;

    public ClearLayoutAction() {
        this(false);
    }

    public ClearLayoutAction(boolean silent) {
        super();
        putValue(Action.NAME, I18NSupport.getString("clear.all.action.name"));
        this.silent = silent;
    }

    public void actionPerformed(ActionEvent event) {

        if (!silent) {
            int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                    I18NSupport.getString("clear.all.action.message"),
                    I18NSupport.getString("clear.all.action.name"),
                    JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        ReportLayout newLayout = null;

        Globals.getReportGrid().getSelectionModel().clearSelection();
        DefaultGridModel gridModel = (DefaultGridModel)Globals.getReportGrid().getModel();

        gridModel.removeRows(0, Globals.getReportLayoutPanel().getReportGridPanel().getRowCount());
        gridModel.removeColumns(0, Globals.getReportLayoutPanel().getReportGridPanel().getColumnCount());
        Globals.getReportLayoutPanel().getReportGridPanel().repaintHeaders();

        Globals.getReportGrid().emptyBandLocations();

        List<ReportGroup> groups = LayoutHelper.getReportLayout().getGroups();
        if (groups != null) {
            for (ReportGroup group : groups) {
                Globals.getReportDesignerPanel().getStructurePanel().deleteGroup(group.getName());
            }
            GroupIndexGenerator.resetCurrentIndex();
        }

        LayoutHelper.reset();

        if (!silent) {
            Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.clear")));
        }

    }

}
