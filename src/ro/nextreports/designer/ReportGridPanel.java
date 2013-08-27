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

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.TreePath;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import org.jdesktop.jxlayer.JXLayer;
import org.pbjar.jxlayer.demo.TransformUtils;
import org.pbjar.jxlayer.demo.QualityHints;
import org.pbjar.jxlayer.plaf.ext.transform.DefaultTransformModel;
import org.pbjar.jxlayer.plaf.ext.TransformUI;

import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.JGridHeader;
import ro.nextreports.designer.grid.ResizableGrid;
import ro.nextreports.designer.grid.SpanModel;
import ro.nextreports.designer.grid.event.SelectionModelEvent;
import ro.nextreports.designer.grid.event.SelectionModelListener;
import ro.nextreports.designer.ui.Rule;
import ro.nextreports.designer.ui.zoom.ZoomEvent;
import ro.nextreports.designer.ui.zoom.ZoomEventListener;
import ro.nextreports.designer.util.ImageUtil;

/**
 * @author Decebal Suiu
 */
public class ReportGridPanel extends JPanel implements ZoomEventListener {

    private JScrollPane scrollGrid;
    private ReportGrid grid;
    private JGridHeader columnHeader;
    private JGridHeader rowHeader;
    private boolean showHeader = true; // header display flag

    private JXLayer rowHeaderLayer;
    private JXLayer colHeaderLayer;
    private JXLayer gridLayer;

    private double zoom = 1.0;

    public ReportGridPanel(int rows, int columns) {
        super();
        initComponents(new ReportGrid(new ReportGridModel(rows, columns)));
    }

    public ReportGrid getGrid() {
        return grid;
    }

    public int getRowCount() {
        return grid.getRowCount();
    }

    public int getColumnCount() {
        return grid.getColumnCount();
    }

    public void insertRows(int row, int column, int rowCount, boolean after) {
        adjustBandLocations(row, rowCount);
        int insertRow = row;
        if (after) {
            if (Globals.getReportGrid().isCellSpan(row, column)) {
                CellSpan cellSpan = Globals.getReportGrid().getSpanModel().getSpanOver(row, column);
                insertRow = insertRow + cellSpan.getRowCount();
            } else {
                insertRow++;
            }
        }
        ((ResizableGrid) grid.getModel()).insertRows(insertRow, rowCount);
        repaintHeaders();
        insertBandRows(insertRow, rowCount);
    }

    // used by tree popup action
    // special case if band has no rows
    public void insertRow(Band band) {
        int row = Globals.getReportGrid().getBandLocation(band.getName()).getLastGridRow();
        insertRow(band, row);
    }

    public void insertRow(Band band, int row) {
        if (band.getRowCount() == 0) {
            Globals.getReportGrid().putBandLocation(band.getName(), new BandLocation(row, 1));
            adjustAfterBandLocations(band.getName(), 1);
        } else {
            adjustBandLocations(row - 1, 1);
        }
        ((ResizableGrid) grid.getModel()).insertRows(row, 1);
        if (grid.getColumnCount() == 0) {
            ((ResizableGrid) grid.getModel()).insertColumns(0, 1);
        }
        repaintHeaders();
        if (band.getRowCount() == 0) {
            band.setElements(new ArrayList<List<BandElement>>());
            band.insertRow(0);
            band.setColumnCount(Globals.getReportGrid().getColumnCount());
        } else {
            insertBandRows(row, 1);
        }
    }

    public void removeRows(int row, int rowCount) {
        removeBandRows(row, rowCount);
        ((ResizableGrid) grid.getModel()).removeRows(row, rowCount);
        adjustBandLocations(row, -rowCount);
        repaintHeaders();
    }

    public void insertColumns(int row, int column, int columnCount, boolean after) {
        int insertedColumn = column;
        if (after) {
            if (Globals.getReportGrid().isCellSpan(row, column)) {
                CellSpan cellSpan = Globals.getReportGrid().getSpanModel().getSpanOver(row, column);
                insertedColumn = insertedColumn + cellSpan.getColumnCount();
            } else {
                insertedColumn++;
            }
        }
        ((ResizableGrid) grid.getModel()).insertColumns(insertedColumn, columnCount);
        repaintHeaders();
        insertBandColumns(insertedColumn, columnCount);

        // update column width array
        ReportLayoutUtil.updateColumnWidth(Globals.getReportGrid());
    }

    public void removeColumns(int column, int columnCount) {
        removeBandColumns(column, columnCount);
        ((ResizableGrid) grid.getModel()).removeColumns(column, columnCount);
        repaintHeaders();
    }

    /**
     * Return true if headers are enabled.
     * @return true if headers are enabled
     */
    public boolean getShowHeader() {
        return showHeader;
    }

    /**
     * Set whether headers should be displayed.
     *
     * @param showHeader true means the header is shown
     */
    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
        if (showHeader) {
            // Attach column headers
            columnHeader = new ReportColumnGridHeader(grid);            

            JPanel columnHeaderPanel = new JPanel();
            boolean isMetric = Globals.UNIT_CM.equals(Globals.getRulerUnit());
            Rule hRule = new Rule(Rule.HORIZONTAL, isMetric);
            hRule.setPreferredHeight(Rule.SIZE + 1);
            columnHeaderPanel.setLayout(new GridBagLayout());
            if (LayoutHelper.getReportLayout().isUseSize() &&
                    Globals.isRulerVisible()) {
                columnHeaderPanel.add(hRule, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }

            colHeaderLayer = TransformUtils.createTransformJXLayer(columnHeader, zoom, new QualityHints());
            columnHeaderPanel.add(colHeaderLayer, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            columnHeaderPanel.add(new JLabel(), new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

            scrollGrid.setColumnHeaderView(columnHeaderPanel);

            // Attach row headers            
            rowHeader = new ReportRowGridHeader(grid);
            rowHeader.setColumnWidth(0, 50);
            rowHeader.setCellRenderer(Object.class, new ReportGridHeaderRowRenderer());
            rowHeaderLayer = TransformUtils.createTransformJXLayer(rowHeader, zoom, new QualityHints());
            JViewport viewport = new JViewport();
            viewport.setView(rowHeaderLayer);
            viewport.setPreferredSize(rowHeaderLayer.getPreferredSize());
            scrollGrid.setRowHeaderView(viewport);

            // Attach upper left corner : this corner selects the root tree (report properties)
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JLabel iconLabel = new JLabel(ImageUtil.getImageIcon("properties"));            
            panel.add(iconLabel, BorderLayout.CENTER);
            panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JTree tree = Globals.getReportDesignerPanel().getStructurePanel().getStructureTree();
		            tree.setSelectionPath(new TreePath(tree.getModel().getRoot()));
				}            	
            });
            panel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            scrollGrid.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, panel);
        } else {
            rowHeaderLayer = null;
            colHeaderLayer = null;
            scrollGrid.setRowHeaderView(null);
            scrollGrid.setColumnHeaderView(null);
        }
        // scroll headers to current view position
        scrollHeaders();

        repaint();
    }

    public void repaintHeaders() {
        boolean oldShowHeader = showHeader;
        setShowHeader(false);
        setShowHeader(oldShowHeader);
    }

    public void scrollHeaders() {
        scrollGrid.getRowHeader().setViewPosition(scrollGrid.getViewport().getViewPosition());
        scrollGrid.getColumnHeader().setViewPosition(scrollGrid.getViewport().getViewPosition());
    }

    public void setReportLayout(ReportLayout reportLayout) {
        // clear old data
        ((ResizableGrid) grid.getModel()).removeRows(0, getRowCount());
        ((ResizableGrid) grid.getModel()).removeColumns(0, getColumnCount());

        createBandLocations(reportLayout);

        // insert empty rows and columns 
        ((ResizableGrid) grid.getModel()).insertRows(0, reportLayout.getRowCount());
        ((ResizableGrid) grid.getModel()).insertColumns(0, reportLayout.getColumnCount());

        // refresh grid's model
        grid.refreshModel(reportLayout);
        repaintHeaders();
    }

    private void createBandLocations(ReportLayout reportLayout) {
        grid.clearBandLocations();

//        // update header band location
//        Band headerBand = reportLayout.getHeaderBand();
//        int headerRows = headerBand.getRowCount();
//        System.out.println("headerRows = " + headerRows);
//        grid.putBandLocation(headerBand.getName(), new BandLocation(0, headerRows));
//
//    	// update detail band location
//        Band detailBand = reportLayout.getDetailBand();
//        int detailRows = detailBand.getRowCount();
//        System.out.println("detailRows = " + detailRows);
//        grid.putBandLocation(detailBand.getName(), new BandLocation(headerRows, detailRows));
//
//    	// update footer band location
//        Band footerBand = reportLayout.getFooterBand();
//        int footerRows = footerBand.getRowCount();
//        System.out.println("footerRows = " + footerRows);
//        grid.putBandLocation(footerBand.getName(), new BandLocation(headerRows + detailRows, footerRows));

        int rowSum = 0;
        for (Band band : reportLayout.getBands()) {
            int rows = band.getRowCount();
            grid.putBandLocation(band.getName(), new BandLocation(rowSum, rows));
            rowSum += rows;
        }
    }

    private void adjustBandLocations(int row, int value) {
        if (value == 0) {
            return;
        }
        String bandName = grid.getBandName(row);
        BandLocation bandLocation = grid.getBandLocation(bandName);
        bandLocation.adjustRowCount(value);
        adjustAfterBandLocations(bandName, value);
    }

    private void adjustAfterBandLocations(String bandName, int value) {
        ReportLayout reportLayout = LayoutHelper.getReportLayout();
        List<String> bandNamesAfter = reportLayout.getBandNamesAfter(bandName);
        for (String bandNameAfter : bandNamesAfter) {
            BandLocation bandLocation = grid.getBandLocation(bandNameAfter);
            if (bandLocation != null) {
                bandLocation.adjustBorder(value);
            }
        }
    }

    protected void setGrid(ReportGrid grid) {
        this.grid = grid;
        gridLayer = TransformUtils.createTransformJXLayer(this.grid, zoom, new QualityHints());
        scrollGrid.setViewportView(gridLayer);

        // Add headers to scrollTable
        setShowHeader(showHeader);
    }

    private void initComponents(final ReportGrid grid) {
        // Add grid to panel
        scrollGrid = new JScrollPane();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 100));
        add(scrollGrid, BorderLayout.CENTER);

        setGrid(grid);
        grid.getSelectionModel().addSelectionModelListener(new SelectionModelListener() {
            public void selectionChanged(SelectionModelEvent event) {
                for (Cell cell : event.getSelectionModel().getSelectedCells()) {
                    rowHeader.getSelectionModel().addSelectionCell(new Cell(cell.getRow(), 0));
                    columnHeader.getSelectionModel().addSelectionCell(new Cell(0, cell.getColumn()));
                }
            }
        });        
    }

    private void insertBandRows(int row, int rowCount) {
    	SpanModel spanModel = Globals.getReportGrid().getSpanModel();
        String bandName = grid.getBandName(row);        
        if (bandName != null) {
            Band band = LayoutHelper.getReportLayout().getBand(bandName);
            int bandRow = grid.getBandLocation(bandName).getRow(row);
            if (band != null) {
                for (int i = bandRow; i < bandRow + rowCount; i++) {
                    band.insertRow(i);
                }
                int columns = Globals.getReportGrid().getColumnCount();
                for(int i=row; i<row+rowCount;  i++) {
                	for (int j=0; j<columns; j++) {                		
                		if (!spanModel.isCellSpan(i, j)) {
                			BandUtil.insertElement(new BandElement(""), i, j);
                		}
                	}
                }
            }                        
        }
    }

    private void insertBandColumns(int column, int columnCount) {    	
    	SpanModel spanModel = Globals.getReportGrid().getSpanModel();
        List<Band> bands = LayoutHelper.getReportLayout().getBands();
        for (Band band : bands) {
            for (int i = column; i < column + columnCount; i++) {
                band.insertColumn(i);
            }            
        }
        int rows = Globals.getReportGrid().getRowCount();
        for(int i=0; i<rows;  i++) {
        	for (int j=column; j<column+columnCount; j++) {        		
        		if (!spanModel.isCellSpan(i, j)) {
        			BandUtil.insertElement(new BandElement(""), i, j);
        		}
        	}
        }
    }

    private void removeBandRows(int row, int rowCount) {
        String bandName = grid.getBandName(row);
        if (bandName != null) {
            Band band = LayoutHelper.getReportLayout().getBand(bandName);
            if (band != null) {
                int bandRow = grid.getBandLocation(bandName).getRow(row);
                for (int i = bandRow + rowCount - 1; i >= bandRow; i--) {
                    band.removeRow(i);
                }
            }
        }
    }

    private void removeBandColumns(int column, int columnCount) {
        List<Band> bands = LayoutHelper.getReportLayout().getNotEmptyBands();
        for (Band band : bands) {
            for (int i = column + columnCount - 1; i >= column; i--) {
                band.removeColumn(i);
            }
        }
    }

    public void notifyZoom(ZoomEvent event) {
        zoom = event.getZoom();
        DefaultTransformModel model = (DefaultTransformModel) ((TransformUI) gridLayer.getUI()).getModel();
        model.setScale(zoom);
        gridLayer.repaint();

        repaintHeaders();
    }

    public void addMouseWheelListener(MouseWheelListener listener) {
        scrollGrid.addMouseWheelListener(listener);
    }

	public JGridHeader getRowHeader() {
		return rowHeader;
	}
	
	public JGridHeader getColumnHeader() {
		return columnHeader;
	}
        
}
