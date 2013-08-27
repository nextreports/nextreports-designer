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

import javax.swing.AbstractAction;
import javax.swing.Action;

import ro.nextreports.designer.BandLocation;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.GroupIndexGenerator;
import ro.nextreports.designer.GroupPanel;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportGroup;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.FunctionBandElement;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 25, 2008
 * Time: 11:05:20 AM
 */
public class AddGroupAction extends AbstractAction {

    public AddGroupAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("add.group.action.name"));
    }

    public void actionPerformed(ActionEvent event) {
        GroupPanel panel = new GroupPanel();
        BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("add.group.action.name"), true);
        dialog.pack();
        dialog.setLocationRelativeTo(Globals.getMainFrame());
        dialog.setVisible(true);
        if (!dialog.okPressed()) {
            return;
        }

        ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        int groupMask = 0;
        if (panel.hasHeader()) {
            groupMask = ReportLayout.HEADER_GROUP_MASK;
        }
        if (panel.hasFooter()) {
            if (groupMask == 0) {
                groupMask = ReportLayout.FOOTER_GROUP_MASK;
            } else {
                groupMask = groupMask | ReportLayout.FOOTER_GROUP_MASK;
            }
        }
        
        ReportLayout reportLayout = LayoutHelper.getReportLayout();
        ReportGrid reportGrid = Globals.getReportGrid();
        String groupName = String.valueOf(GroupIndexGenerator.getCurrentIndex());
        String groupColumn = panel.getGroupColumn();
        String functionName = panel.getFunction().getName();
        String functionColumn = panel.getFunctionColumn();
        boolean headerOnEveryPage = panel.onEveryPage();
        boolean newPageAfter = panel.isNewPageAfter();

        ReportGroup reportGroup = new ReportGroup(groupName, groupColumn, headerOnEveryPage);
        reportGroup.setNewPageAfter(newPageAfter);
        reportLayout.addGroup(reportGroup, groupMask);
        Globals.getReportDesignerPanel().getStructurePanel().addGroup(groupName, groupMask);

        Band groupHeaderBand = reportLayout.getBand(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX + groupName);
        int headerRow = reportLayout.getPageHeaderBand().getRowCount() + reportLayout.getHeaderBand().getRowCount();
        for (Band band : reportLayout.getGroupHeaderBands()) {
            headerRow += band.getRowCount();
        }
        reportGrid.putBandLocation(groupHeaderBand.getName(), new BandLocation(headerRow, 0));

        if ((groupMask & ReportLayout.HEADER_GROUP_MASK) == ReportLayout.HEADER_GROUP_MASK) {
            Globals.getReportLayoutPanel().getReportGridPanel().insertRow(groupHeaderBand, headerRow);
            BandElement bandElement = new ColumnBandElement(groupColumn);
            int bandRow = reportGrid.getBandLocation(groupHeaderBand.getName()).getRow(headerRow);
            groupHeaderBand.setElementAt(bandElement, bandRow, 0);
            reportGrid.setValueAt(bandElement, headerRow, 0);            
            for (int i=1, size=reportGrid.getColumnCount(); i<size; i++) {
            	BandElement be = new BandElement("");
            	groupHeaderBand.setElementAt(be, bandRow, i);
                reportGrid.setValueAt(be, headerRow, i);
            }
        }

        Band footerGroupBand = reportLayout.getBand(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX + groupName);
        int footerRow = reportGrid.getRowCount() - reportLayout.getFooterBand().getRowCount() - reportLayout.getPageFooterBand().getRowCount();
        for (Band band : reportLayout.getGroupFooterBands()) {
            footerRow -= band.getRowCount();
        }
        reportGrid.putBandLocation(footerGroupBand.getName(), new BandLocation(footerRow, 0));

        if ((groupMask & ReportLayout.FOOTER_GROUP_MASK) == ReportLayout.FOOTER_GROUP_MASK) {
            Globals.getReportLayoutPanel().getReportGridPanel().insertRow(footerGroupBand, footerRow);
            BandElement bandElement = new FunctionBandElement(functionName, functionColumn);
            int bandRow = Globals.getReportGrid().getBandLocation(footerGroupBand.getName()).getRow(footerRow);
            footerGroupBand.setElementAt(bandElement, bandRow, 0);
            reportGrid.setValueAt(bandElement, footerRow, 0);
            for (int i=1, size=reportGrid.getColumnCount(); i<size; i++) {
            	BandElement be = new BandElement("");
            	footerGroupBand.setElementAt(be, bandRow, i);
                reportGrid.setValueAt(be, footerRow, i);
            }
        }
        
        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("edit.group.add")));
    }

}
