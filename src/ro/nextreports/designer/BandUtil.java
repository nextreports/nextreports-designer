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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.FieldBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.ParameterBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.band.RowElement;
import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 18, 2008
 * Time: 11:33:49 AM
 */
public class BandUtil {

    // maximum rows or columns that can be inserted in layout through an insert action
    public static final int MAX = 30;

    public static Icon getIcon(int row) {
    	
    	ReportGrid grid = Globals.getReportGrid();		
		Cell cell = CellUtil.getCellFromSelectedRow(grid, row);						
		BandElement be = grid.getBandElement(cell);
		RowElement re = getRowElement(LayoutHelper.getReportLayout(), row);
		boolean newPage = (re != null) && re.isStartOnNewPage();
    	
        String bandName = Globals.getReportGrid().getBandName(row);
        String name;
        if (ReportLayout.HEADER_BAND_NAME.equals(bandName)) {
            name = "band_header";
        } else if(ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName)) {
        	name = "band_page_header";
        } else if (ReportLayout.DETAIL_BAND_NAME.equals(bandName)) {
            name = "band_detail";
        } else if (ReportLayout.FOOTER_BAND_NAME.equals(bandName)) {
            name = "band_footer";
        } else if (ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName)) {
        	name = "band_page_footer";
        } else {
            BandLocation detailLocation = Globals.getReportGrid().getBandLocation(ReportLayout.DETAIL_BAND_NAME);
            if (row < detailLocation.getFirstGridRow()) {
                name = "band_group_header";
            } else {
                name = "band_group_footer";
            }
        }
        if (newPage) {
        	name = name + "_newpage";
        }

        return ImageUtil.getImageIcon(name);
    }

    public static Icon getIcon(String bandName) {
        String name;
        if (ReportLayout.HEADER_BAND_NAME.equals(bandName)) {
            name = "band_header";
        } else if (ReportLayout.PAGE_HEADER_BAND_NAME.equals(bandName)) {
        	name = "band_page_header";
        } else if (ReportLayout.DETAIL_BAND_NAME.equals(bandName)) {
            name = "band_detail";
        } else if (ReportLayout.FOOTER_BAND_NAME.equals(bandName)) {
            name = "band_footer";
        } else if(ReportLayout.PAGE_FOOTER_BAND_NAME.equals(bandName)) {
        	name = "band_page_footer";
        } else if (bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX)) {
            name = "band_group_header";
        } else {
            name = "band_group_footer";
        }

        return ImageUtil.getImageIcon(name);
    }

    public static void updateBandElement(CellSpan cellSpan) {
        if ((cellSpan.getRowCount() > 0) && (cellSpan.getColumnCount() > 0)) {
            BandElement bandElement = Globals.getReportGrid().getBandElement(cellSpan.getRow(), cellSpan.getColumn());
            if (bandElement != null) {
                bandElement.setRowSpan(cellSpan.getRowCount());
                bandElement.setColSpan(cellSpan.getColumnCount());
            }
        }
    }

    public static void moveNotEmptyBandElementToTopLeft(CellSpan cellSpan) {
        ReportGrid grid = Globals.getReportGrid();
        BandElement topLeft = grid.getBandElement(cellSpan.getFirstRow(), cellSpan.getFirstColumn());
        BandElement notEmpty = null;
        BandElement notEmptyButBlank = null;
        int row = -1, column = -1, rowBlank = -1, columnBlank = -1;
        for (int i = cellSpan.getFirstRow(); i <= cellSpan.getLastRow(); i++) {
            for (int j = cellSpan.getFirstColumn(); j <= cellSpan.getLastColumn(); j++) {
                BandElement bandElement = grid.getBandElement(i, j);
                if ((bandElement != null) && (bandElement.getText() != null) ) {                	
					if (!"".equals(bandElement.getText())) {
						// element is already top left : nothing to do
						if ((i == cellSpan.getFirstRow()) && (j == cellSpan.getFirstColumn())) {
							return;
						}
						row = i;
						column = j;
						notEmpty = bandElement;
					} else {
						if (notEmptyButBlank == null) {
							notEmptyButBlank = bandElement;
							rowBlank = i;
							columnBlank = j;
						}
					}
                }
            }            
        }
        if (row == -1) {
        	row = rowBlank;
        	column = columnBlank;
        	if ((row != cellSpan.getFirstRow()) || (column != cellSpan.getFirstColumn())) {
        		// blank element is not top left
        		notEmpty = notEmptyButBlank;
        	}
        }
        
        if (notEmpty != null) {
            topLeft = copyBandElement(notEmpty);
            notEmpty = null;
            String bandName = grid.getBandName(row);
            Band band = LayoutHelper.getReportLayout().getBand(bandName);

            int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(cellSpan.getFirstRow());
            band.setElementAt(topLeft, bandRow, cellSpan.getFirstColumn());
            grid.setValueAt(topLeft, cellSpan.getFirstRow(), cellSpan.getFirstColumn());

            bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(row);
            band.setElementAt(null, bandRow, column);
            grid.setValueAt(null, row, column);
        }
    }

    public static BandElement copyBandElement(BandElement from) {
        BandElement current;
        if (from instanceof ColumnBandElement) {
            current = new ColumnBandElement(((ColumnBandElement) from).getColumn());
        } else  if (from instanceof FunctionBandElement) {
            FunctionBandElement fbe = (FunctionBandElement) from;
            current = new FunctionBandElement(fbe.getFunction(), fbe.getColumn(), fbe.isExpression());
        } else  if (from instanceof ExpressionBandElement) {
            ExpressionBandElement ebe = (ExpressionBandElement) from;
            String newExpressionName = getCopiedExpressionName(ebe.getExpressionName());
            current = new ExpressionBandElement(newExpressionName, ebe.getExpression());
            ((ExpressionBandElement)current).setExpressionName(newExpressionName);            
        } else if (from instanceof VariableBandElement) {
            current = new VariableBandElement(((VariableBandElement) from).getVariable());         
        } else if (from instanceof ParameterBandElement) {
            current = new ParameterBandElement(((ParameterBandElement) from).getParameter());
        } else  if (from instanceof ImageBandElement) {
            ImageBandElement ibe = (ImageBandElement) from;
            current = new ImageBandElement(ibe.getImage());
        }  else  if (from instanceof ReportBandElement) {
        	ReportBandElement ibe = (ReportBandElement) from;
            current = new ReportBandElement(ibe.getReport());
        }  else  if (from instanceof HyperlinkBandElement) {
        	HyperlinkBandElement hbe = (HyperlinkBandElement) from;
        	current = new HyperlinkBandElement(hbe.getName(), hbe.getUrl());
        } else {
            current = new BandElement(from.getText());
        }
        copySettings(from, current);
        current.setFormattingConditions(from.getFormattingConditions());
        if (from instanceof FieldBandElement) {
        	((FieldBandElement)current).setPattern(((FieldBandElement)from).getPattern());
        }
        return current;
    }
    
    private static String getCopiedExpressionName(String expressionName) {
    	int i=1;
    	List<String> expressionNames = ReportUtil.getExpressionsNames(LayoutHelper.getReportLayout());
    	String newName = expressionName + "_" + i;
    	while (expressionNames.contains(newName)) {
    		i++;
    		newName = expressionName + "_" + i;
    	}
    	return newName;
    }
    
    public static void copySettings(BandElement from, BandElement to) {
    	if ((from == null) || (to == null)) {
    		return;
    	}
    	to.setBackground(from.getBackground());
    	to.setBorder(from.getBorder());
    	to.setFont(from.getFont());
    	to.setForeground(from.getForeground());
    	to.setHorizontalAlign(from.getHorizontalAlign());
    	to.setVerticalAlign(from.getVerticalAlign());
    	to.setPadding(from.getPadding());    	
    	to.setWrapText(from.isWrapText());
    	to.setTextRotation(from.getTextRotation());
    }

    public static Set<String> getUsedColumns(Report report) {
        Set<String> columns = new HashSet<String>();
        ReportLayout layout;
        if (report == null) {
            layout = LayoutHelper.getReportLayout();
        } else {
            layout = report.getLayout();
        }
        List<Band> bands = layout.getBands();
        for (Band band : bands) {
            int rows = band.getRowCount();
            int cols = band.getColumnCount();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    BandElement bandElement = band.getElementAt(i, j);
                    if (bandElement instanceof ColumnBandElement) {
                        columns.add(((ColumnBandElement) bandElement).getColumn());
                    } else if (bandElement instanceof FunctionBandElement) {
                        columns.add(((FunctionBandElement) bandElement).getColumn());
                    } else if (bandElement instanceof ExpressionBandElement) {
                        columns.add(((ExpressionBandElement) bandElement).getExpressionName());
                    }
                }
            }
        }
        return columns;
    }

    public static Set<String> getNotFoundColumns(Report report, DataSource ds) throws Exception {
        List<String> columns = ReportLayoutUtil.getAllColumnNamesForReport(report, ds);
        Set<String> usedColumns = BandUtil.getUsedColumns(report);
        usedColumns.removeAll(columns);
        ReportLayout layout;
        if (report == null) {
            layout = LayoutHelper.getReportLayout();
        } else {
            layout = report.getLayout();
        }
        usedColumns.removeAll(ReportUtil.getExpressionsNames(layout));        
        return usedColumns;
    }
    
    public static Set<String> getNotFoundColumnsUsedByGroups(Report report, DataSource ds) throws Exception {
        List<String> columns = ReportLayoutUtil.getAllColumnNamesForReport(report, ds);        
        ReportLayout layout;
        if (report == null) {
            layout = LayoutHelper.getReportLayout();
        } else {
            layout = report.getLayout();
        }
        Set<String> usedColumns = ReportLayoutUtil.getAllColumnNamesUsedByGroupes(layout);
        usedColumns.removeAll(columns);                
        return usedColumns;
    }

    /**
     * Insert an element at row and column and return the old element. 
     */
    public static BandElement insertElement(BandElement element, int row, int column) {
    	ReportGrid grid = Globals.getReportGrid();
    	
    	BandElement oldElement = grid.getBandElement(row, column);
        String bandName = grid.getBandName(row);
        int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(row);

        Band band = LayoutHelper.getReportLayout().getBand(bandName);
        if (element != null) {
            CellSpan cellSpan = grid.getSpanModel().getSpanOver(row, column);
        	element.setRowSpan(cellSpan.getRowCount());
        	element.setColSpan(cellSpan.getColumnCount());
        }
        band.setElementAt(element, bandRow, column);
        grid.setValueAt(element, row, column);    	
        
        return oldElement;
    }
    
    // TODO it is enough efficiently implemented ?!
    public static List<BandElement> insertElements(List<BandElement> elements, List<Integer> rows, List<Integer> columns) {
    	List<BandElement> oldElements = new ArrayList<BandElement>();
    	
    	int n = elements.size();
    	for (int i = 0; i < n; i++) {
    		oldElements.add(insertElement(elements.get(i), rows.get(i), columns.get(i)));
    	}
    	
    	return oldElements;
    }

    /**
     * Remove an element at row and column and return the removed element.
     */
    public static BandElement deleteElement(int row, int column) {
    	ReportGrid grid = Globals.getReportGrid();
    	
    	BandElement oldElement = grid.getBandElement(row, column);
        String bandName = grid.getBandName(row);
        
        Band band = LayoutHelper.getReportLayout().getBand(bandName);
        int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(row);
        BandElement empty = new BandElement("");
        band.setElementAt(empty, bandRow, column);
    	grid.setValueAt(empty, row, column);
    	
    	return oldElement;
    }
    
    public static BandElement nullifyElement(int row, int column) {
    	ReportGrid grid = Globals.getReportGrid();
    	
    	BandElement oldElement = grid.getBandElement(row, column);
        String bandName = grid.getBandName(row);
        
        Band band = LayoutHelper.getReportLayout().getBand(bandName);
        int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(row);        
        band.setElementAt(null, bandRow, column);
    	grid.setValueAt(null, row, column);
    	
    	return oldElement;
    }

    public static List<BandElement> deleteElements(List<Integer> rows, List<Integer> columns) {
    	int n = rows.size();
    	if (n == 0) {
    		return new ArrayList<BandElement>();
    	}
    	
    	List<BandElement> oldElements = new ArrayList<BandElement>();
    	for (int i = 0; i < n; i++) {
    		oldElements.add(deleteElement(rows.get(i), columns.get(i)));
    	}
    	
    	return oldElements;
    }

    public static Band getBand(ReportLayout layout, ReportGridCell cell) {
        return getBand(layout, cell.getRow());
    }
    
    public static Band getBand(ReportLayout layout, int gridRow) {
        List<Band> bands = layout.getBands();
        int currentRow = 0;
        for (Band band : bands) {
            int rows = band.getRowCount();
            currentRow += rows;
            if (gridRow < currentRow) {
                return band;
            }
        }
        return null;
    }
    
    public static RowElement getRowElement(ReportLayout layout, int gridRow) {
        List<Band> bands = layout.getBands();
        int currentRow = 0;
        for (Band band : bands) {
            int rows = band.getRowCount();
            for (int i=0; i<rows; i++) {
            	int gr = layout.getGridRow(band.getName(),i);
            	if (gr == gridRow) {
            		return band.getElements().get(i);
            	}
            }			            
        }
        return null;
    }


}
