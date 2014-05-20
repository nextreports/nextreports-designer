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
package ro.nextreports.designer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.BarcodeBandElement;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.band.ParameterBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import org.jdesktop.jxlayer.JXLayer;
import org.pbjar.jxlayer.plaf.ext.transform.DefaultTransformModel;
import org.pbjar.jxlayer.plaf.ext.TransformUI;

import ro.nextreports.designer.action.report.layout.ClearLayoutAction;
import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.action.report.layout.cell.CopyAction;
import ro.nextreports.designer.action.report.layout.cell.CutAction;
import ro.nextreports.designer.action.report.layout.cell.EditChartAction;
import ro.nextreports.designer.action.report.layout.cell.EditReportAction;
import ro.nextreports.designer.action.report.layout.cell.ExtractChartAction;
import ro.nextreports.designer.action.report.layout.cell.ExtractReportAction;
import ro.nextreports.designer.action.report.layout.cell.ImageSizeAction;
import ro.nextreports.designer.action.report.layout.cell.InsertBarcodeAction;
import ro.nextreports.designer.action.report.layout.cell.InsertChartAction;
import ro.nextreports.designer.action.report.layout.cell.InsertColumnAction;
import ro.nextreports.designer.action.report.layout.cell.InsertExpressionAction;
import ro.nextreports.designer.action.report.layout.cell.InsertForReportAction;
import ro.nextreports.designer.action.report.layout.cell.InsertFunctionAction;
import ro.nextreports.designer.action.report.layout.cell.InsertHyperlinkAction;
import ro.nextreports.designer.action.report.layout.cell.InsertImageAction;
import ro.nextreports.designer.action.report.layout.cell.InsertImageColumnAction;
import ro.nextreports.designer.action.report.layout.cell.InsertParameterAction;
import ro.nextreports.designer.action.report.layout.cell.InsertReportAction;
import ro.nextreports.designer.action.report.layout.cell.InsertTextAction;
import ro.nextreports.designer.action.report.layout.cell.InsertVariableAction;
import ro.nextreports.designer.action.report.layout.cell.MergeCellsAction;
import ro.nextreports.designer.action.report.layout.cell.PasteAction;
import ro.nextreports.designer.action.report.layout.cell.UnmergeCellsAction;
import ro.nextreports.designer.action.report.layout.group.AddGroupAction;
import ro.nextreports.designer.action.report.layout.group.EditGroupAction;
import ro.nextreports.designer.action.report.layout.group.RemoveGroupAction;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.DefaultSpanModel;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.grid.event.GridModelListener;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.ui.SteppedComboBox;
import ro.nextreports.designer.util.I18NSupport;

/**
 * @author Decebal Suiu
 */
public class ReportGrid extends JGrid implements GridModelListener {

    private Map<String, BandLocation> bandLocations;
    private AutoFitGridHandler autoFitGridHandler;

    public ReportGrid(ReportGridModel model) {
        super(model);

        bandLocations = new LinkedHashMap<String, BandLocation>();

        autoFitGridHandler = new AutoFitGridHandler(this);
        model.addGridModelListener(autoFitGridHandler);

        setCellRenderer(BandElement.class, new ReportCellRenderer());

        setCellEditor(BandElement.class, new TextCellEditor(new JTextField()));
        setCellEditor(ColumnBandElement.class, new ColumnCellEditor(new SteppedComboBox()));
        setCellEditor(ImageColumnBandElement.class, new ColumnCellEditor(new SteppedComboBox()));
        setCellEditor(VariableBandElement.class, new VariableCellEditor(new SteppedComboBox()));
        setCellEditor(ParameterBandElement.class, new ParameterCellEditor(new SteppedComboBox()));
        setCellEditor(FunctionBandElement.class, new FunctionCellEditor());
        setCellEditor(ExpressionBandElement.class, new ExpressionCellEditor());
        setCellEditor(ImageBandElement.class, new ImageCellEditor());
        setCellEditor(ChartBandElement.class, new ChartCellEditor());
        setCellEditor(BarcodeBandElement.class, new BarcodeCellEditor());
        setCellEditor(ReportBandElement.class, new ReportCellEditor());
        setCellEditor(HyperlinkBandElement.class, new HyperlinkCellEditor());

        addMouseListener(new PopupListener());
    }  

    public List<BandLocation> getBandLocations() {
        return new ArrayList<BandLocation>(bandLocations.values());
    }

    public BandLocation getBandLocation(String bandName) {
        return bandLocations.get(bandName);
    }

    public void putBandLocation(String bandName, BandLocation bandLocation) {
        bandLocations.put(bandName, bandLocation);
    }

    public void clearBandLocations() {
        bandLocations.clear();
    }

    public void emptyBandLocations() {
        for (BandLocation bandLocation : getBandLocations()) {
            bandLocation.setFirstGridRow(0);
            bandLocation.setLastGridRow(0);
            bandLocation.setRowCount(0);
        }
    }

    public String getBandName(int row) {
        Set<String> bandNames = bandLocations.keySet();
        for (String bandName : bandNames) {
            BandLocation bandLocation = getBandLocation(bandName);
            if (bandLocation.containsGridRow(row)) {
                return bandName;
            }
        }

        return null;
    }

    public String getBandName(Cell cell) {
        return getBandName(cell.getRow());
    }

    public BandElement getBandElement(int row, int column) {
        return (BandElement) getValueAt(row, column);
    }

    public BandElement getBandElement(Cell cell) {
        return getBandElement(cell.getRow(), cell.getColumn());
    }

    public void refreshModel(ReportLayout reportLayout) {
        // clear cache
    	autoFitGridHandler.clearCache();

        // set values
        for (Band band : reportLayout.getBands()) {
            setValues(band);
        }
    }

    private void setValues(Band band) {
        BandLocation bandLocation = getBandLocation(band.getName());
        for (int i = 0; i < band.getRowCount(); i++) {
            for (int j = 0; j < band.getColumnCount(); j++) {
                BandElement bandElement = band.getElementAt(i, j);
                setValueAt(bandElement, i + bandLocation.getFirstGridRow(), j);
                if (bandElement != null) {
                    if ((bandElement.getColSpan() > 1) || (bandElement.getRowSpan() > 1)) {
                        CellSpan cellSpan = new CellSpan(i + bandLocation.getFirstGridRow(), j, bandElement.getRowSpan(), bandElement.getColSpan());
                        ((DefaultSpanModel) getSpanModel()).addSpan(cellSpan);
                    }
                }
            }
        }
    }

    private void updatePopupMenu(JPopupMenu popup) {
        boolean selection = getSelectionModel().getSelectedCells().size() > 0;        

        if (selection) {
            int row = getSelectionModel().getSelectedCell().getRow();           
            String bandName = getBandName(row);

            popup.add(new CopyAction());
            popup.add(new CutAction());
            popup.add(new PasteAction());

            popup.addSeparator();

            JMenu insertMenu = new JMenu(I18NSupport.getString("insert.action.name"));
            insertMenu.add(new InsertTextAction());
            insertMenu.add(new InsertVariableAction());
            boolean isStatic = true;
            if (bandName.equals(ReportLayout.DETAIL_BAND_NAME) ||
                bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX) ||
                bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX) ) {
               insertMenu.add(new InsertColumnAction());
               insertMenu.add(new InsertImageColumnAction());
               isStatic = false;
            }
            boolean isHeaderOrFooter = false;
            if (bandName.equals(ReportLayout.FOOTER_BAND_NAME) ||                
                bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX) ||                
                bandName.startsWith(ReportLayout.HEADER_BAND_NAME) ||                
                bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX) ) {               
               isHeaderOrFooter = true;
            }
            insertMenu.add(new InsertExpressionAction(isStatic, isHeaderOrFooter));
            if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX) ||
                bandName.equals(ReportLayout.FOOTER_BAND_NAME) ||
                bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX) ||
                bandName.equals(ReportLayout.HEADER_BAND_NAME)) {
                insertMenu.add(new InsertFunctionAction());
            }
            if (ParameterManager.getInstance().getUsedParameterNames(Globals.getMainFrame().getQueryBuilderPanel().getUserSql()).size() > 0) {
                insertMenu.add(new InsertParameterAction());
            }
            insertMenu.add(new InsertImageAction());
            insertMenu.add(new InsertBarcodeAction());
            insertMenu.add(new InsertHyperlinkAction());
            insertMenu.add(new InsertChartAction());
            insertMenu.add(new InsertReportAction());            
            insertMenu.add(new InsertForReportAction());
            popup.add(insertMenu);

            int column = getSelectionModel().getSelectedCell().getColumn();
            BandElement be = getBandElement(row, column);
            if ((be instanceof ImageBandElement) || (be instanceof ImageColumnBandElement)) {
                popup.add(new ImageSizeAction());
            }
            if (be instanceof ChartBandElement) {
            	popup.add(new EditChartAction());
            	popup.add(new ExtractChartAction());
            }
            if (be instanceof ReportBandElement) {
            	popup.add(new EditReportAction());
            	popup.add(new ExtractReportAction());
            }

            popup.add(new ClearCellAction());

            popup.addSeparator();

//            JMenu columnMenu = new JMenu(I18NSupport.getString("column.action.name"));
//            columnMenu.add(new InsertBeforeColumnAction());
//            columnMenu.add(new InsertAfterColumnAction());
//            columnMenu.add(new RemoveColumnAction());
//            popup.add(columnMenu);
//
//            JMenu rowMenu = new JMenu(I18NSupport.getString("row.action.name"));
//            rowMenu.add(new InsertBeforeRowAction());
//            rowMenu.add(new InsertAfterRowAction());
//            rowMenu.add(new RemoveRowAction());
//            popup.add(rowMenu);
//
//            popup.addSeparator();
        }

        JMenu groupMenu = new JMenu(I18NSupport.getString("group.action.name"));
        groupMenu.add(new AddGroupAction());
        groupMenu.add(new RemoveGroupAction());
        groupMenu.add(new EditGroupAction());
        popup.add(groupMenu);

        popup.addSeparator();

        if (selection) {
            popup.add(new MergeCellsAction());
            popup.add(new UnmergeCellsAction());

            popup.addSeparator();
        }

        popup.add(new ClearLayoutAction());
    }

    class PopupListener extends MouseAdapter {

        private JPopupMenu popup;

        public PopupListener() {
            popup = new JPopupMenu();
        }

        @Override
        public void mousePressed(MouseEvent event) {
            maybeShowPopup(event);
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }

        @SuppressWarnings("unchecked")
		private void maybeShowPopup(MouseEvent event) {
            if (event.isPopupTrigger()) {
                popup.removeAll();
                updatePopupMenu(popup);

                // get the tranform model and the transform of the combobox container
                Container container = ReportGrid.this.getParent();
                while (!(container instanceof JXLayer)) {
                    container = container.getParent();
                }
                JXLayer parentLayer = (JXLayer) container;
                DefaultTransformModel parentModel = (DefaultTransformModel) ((TransformUI) parentLayer.getUI()).getModel();
                AffineTransform parentTransform = parentModel.getTransform(parentLayer);

                Point point = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), parentLayer);
                parentTransform.transform(point, point);

                //Show the popup relative to JXLayer
                popup.show(parentLayer, point.x, point.y);                
            }
        }

    }

}
