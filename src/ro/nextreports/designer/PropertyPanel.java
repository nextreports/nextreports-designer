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

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.action.undo.ModifyElementsEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.DefaultGridModel;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.grid.event.SelectionModelEvent;
import ro.nextreports.designer.grid.event.SelectionModelListener;
import ro.nextreports.designer.property.BorderPropertyEditor;
import ro.nextreports.designer.property.CustomSizePropertyEditor;
import ro.nextreports.designer.property.ExtendedColorPropertyEditor;
import ro.nextreports.designer.property.FieldPatternPropertyEditor;
import ro.nextreports.designer.property.FormattingConditionsPropertyEditor;
import ro.nextreports.designer.property.HideWhenExpressionPropertyEditor;
import ro.nextreports.designer.property.HyperlinkPropertyEditor;
import ro.nextreports.designer.property.ImagePropertyEditor;
import ro.nextreports.designer.property.PaddingPropertyEditor;
import ro.nextreports.designer.property.RowFormattingConditionsPropertyEditor;
import ro.nextreports.designer.property.SqlPropertyEditor;
import ro.nextreports.designer.property.TemplatePropertyEditor;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.FieldBandElement;
import ro.nextreports.engine.band.ForReportBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.band.Hyperlink;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.PaperSize;
import ro.nextreports.engine.band.RowElement;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.condition.RowFormattingConditions;
import ro.nextreports.engine.exporter.ResultExporter;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import com.l2fprod.common.beans.editor.IntegerPropertyEditor;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

/**
 * @author Decebal Suiu
 */
public class PropertyPanel extends PropertySheetPanel implements SelectionModelListener {

    private String PAGE_FORMAT_NAME = "PageFormat";
    private String PAGE_FORMAT_PARAM_NAME = I18NSupport.getString("property.page.format");
    private String TEMPLATE_NAME = "TemplateName";
    private String TEMPLATE_PARAM_NAME = I18NSupport.getString("property.template.name");
    private String TEMPLATE_SHEET_NAME = "Sheet";
    private String TEMPLATE_SHEET_PARAM_NAME = I18NSupport.getString("property.template.sheet");
    private String CUSTOM_PAGE_FORMAT_DEF_NAME = "CustomPageFormat";
    private String CUSTOM_PAGE_FORMAT_DEF_PARAM_NAME = I18NSupport.getString("property.page.custom");
    private String PAGE_PADDING_NAME = "PagePadding";
    private String PAGE_PADDING_PARAM_NAME = I18NSupport.getString("property.page.padding");
    private String ORIENTATION_NAME = "Orientation";
    private String ORIENTATION_PARAM_NAME = I18NSupport.getString("property.orientation");
    private String REPORT_TYPE_NAME = "Type";
    private String REPORT_TYPE_PARAM_NAME = I18NSupport.getString("property.report.type");    
    private String HEADER_NAME = "HeaderPerPage";
    private String HEADER_PARAM_NAME = I18NSupport.getString("property.header.on.every.page");
    private String BG_IMAGE_NAME = "BgImage";
    private String BG_IMAGE_PARAM_NAME = I18NSupport.getString("property.background.image");
    private String TEXT_PARAM_NAME = I18NSupport.getString("property.text");
    private String FONT_PARAM_NAME = I18NSupport.getString("property.font");
    private String BACKGROUND_PARAM_NAME = I18NSupport.getString("property.background");
    private String FOREGROUND_PARAM_NAME = I18NSupport.getString("property.foreground");
    private String ALIGNMENT_PARAM_NAME = I18NSupport.getString("property.allignment");
    private String V_ALIGNMENT_PARAM_NAME = I18NSupport.getString("property.vertical.allignment");
    private String PATTERN_PARAM_NAME = I18NSupport.getString("property.pattern");
    private String WIDTH_PARAM_NAME = I18NSupport.getString("property.width");
    private String HEIGHT_PARAM_NAME = I18NSupport.getString("property.height");
    private String PADDING_PARAM_NAME = I18NSupport.getString("property.padding");
    private String BORDER_PARAM_NAME = I18NSupport.getString("property.border");
    private String WRAPTEXT_PARAM_NAME = I18NSupport.getString("property.wrapText");
    private String TEXT_ROTATION_PARAM_NAME = I18NSupport.getString("property.textRotation");
    private String REPEATED_PARAM_NAME = I18NSupport.getString("property.repeatedValue");
    private String HIDE_WHEN_EXPRESSION_PARAM_NAME = I18NSupport.getString("property.hide.when.expression");
    private String URL_PARAM_NAME = I18NSupport.getString("property.url");
    private String CONDITION_NAME = "Condition";
    private String CONDITION_PARAM_NAME = I18NSupport.getString("property.condition");
    private String ROW_CONDITION_NAME = "RowCondition";
    private String ROW_CONDITION_PARAM_NAME = I18NSupport.getString("property.condition");
    private String ROW_NEW_PAGE_NAME = "RowNewPage";
    private String ROW_NEW_PAGE_PARAM_NAME = I18NSupport.getString("property.newpage");
    //private String FOR_REPORT_NAME = "ForReport";
    private String FOR_REPORT_PARAM_NAME = I18NSupport.getString("property.forreport.sql");

    private String HTML_ACC_HEADERS = I18NSupport.getString("property.accessibility.html.headers");
    private String HTML_ACC_ID = I18NSupport.getString("property.accessibility.html.id");
    private String HTML_ACC_SCOPE = I18NSupport.getString("property.accessibility.html.scope");

    private String HTML_SCOPE_NONE = " ";
    private String HTML_SCOPE_ROW = "row";
    private String HTML_SCOPE_COL = "col";
    
    private static Log LOG = LogFactory.getLog(PropertyPanel.class);

    private List<ReportGridCell> reportGridCells;
    private List<Integer> rows;
    private String formattingCellBand;
    
    private PropertyEditorRegistry editorRegistry;
    private boolean isInit;

    private String PORTRAIT = I18NSupport.getString("export.properties.portrait");
    private String LANDSCAPE = I18NSupport.getString("export.properties.landscape");
    
    private String DEFAULT_TYPE = I18NSupport.getString("property.report.default");
    private String ALARM_TYPE = I18NSupport.getString("property.report.alarm");
    private String TABLE_TYPE = I18NSupport.getString("property.report.table");
    private String INDICATOR_TYPE = I18NSupport.getString("property.report.indicator");
    private String DISPLAY_TYPE = I18NSupport.getString("property.report.display");
    
    private String CENTER = I18NSupport.getString("property.allignment.center");
    private String LEFT = I18NSupport.getString("property.allignment.left");
    private String RIGHT = I18NSupport.getString("property.allignment.right");

    private String TOP = I18NSupport.getString("property.vertical.allignment.top");
    private String MIDDLE = I18NSupport.getString("property.vertical.allignment.middle");
    private String BOTTOM = I18NSupport.getString("property.vertical.allignment.bottom");
    
    private boolean ignoreEvent;

    public PropertyPanel() {
        super();
        setDescriptionVisible(false);
        setToolBarVisible(false);
        if (Globals.getAccessibilityHtml()) {
            setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);
        }
        //setSortingCategories(true);
        
        reportGridCells = new ArrayList<ReportGridCell>();
        rows = new ArrayList<Integer>();
        editorRegistry = (PropertyEditorRegistry) getEditorFactory();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
		if (ignoreEvent) {
			return;
		}

        if (isInit) {
        	return;
        }
        
        Property prop = (Property) event.getSource();
        String propName = prop.getName();

        // report properties
        if (ORIENTATION_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();
            LayoutHelper.getReportLayout().setOrientation(getOrientation(propValue));
            return;
        } else  if (REPORT_TYPE_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();
            LayoutHelper.getReportLayout().setReportType(getReportType(propValue));
            return;    
        } else if (HEADER_NAME.equals(propName)) {
            Boolean propValue = (Boolean) prop.getValue();
            LayoutHelper.getReportLayout().setHeaderOnEveryPage(propValue);
            return;
        } else if (PAGE_FORMAT_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();            
            LayoutHelper.getReportLayout().setPageFormat(propValue);
            List<Property> props = getReportProperties(LayoutHelper.getReportLayout());
            setProperties(props.toArray(new Property[props.size()]));
            return;
        } else if (TEMPLATE_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();            
            LayoutHelper.getReportLayout().setTemplateName(propValue);           
            return;        
        } else if (TEMPLATE_SHEET_NAME.equals(propName)) {
            Integer propValue = (Integer) prop.getValue();            
            LayoutHelper.getReportLayout().setTemplateSheet(propValue);           
            return;    
        }  else if (CUSTOM_PAGE_FORMAT_DEF_NAME.equals(propName)) {
        	PaperSize propValue = (PaperSize) prop.getValue();            
            LayoutHelper.getReportLayout().setPaperSize(propValue);
            return;
        } else if (PAGE_PADDING_NAME.equals(propName)) {
            Padding propValue = (Padding) prop.getValue();            
            LayoutHelper.getReportLayout().setPagePadding(propValue);
            return;
        } else if (BG_IMAGE_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();
            LayoutHelper.getReportLayout().setBackgroundImage(propValue);
            return;
        } 
        
        // row properties
        for (Integer i : rows) {  
        	if (ROW_CONDITION_NAME.equals(propName)) {
        		RowFormattingConditions propValue = (RowFormattingConditions) prop.getValue();
        		RowElement element = BandUtil.getRowElement(LayoutHelper.getReportLayout(), i);
        		element.setFormattingConditions(propValue);
        	} else if (ROW_NEW_PAGE_NAME.equals(propName)) {
        		Boolean newPage = (Boolean) prop.getValue();
        		RowElement element = BandUtil.getRowElement(LayoutHelper.getReportLayout(), i);
        		element.setStartOnNewPage(newPage);
        	}
        }	

        ReportGrid reportGrid = Globals.getReportGrid();
        DefaultGridModel reportGridModel = (DefaultGridModel) reportGrid.getModel();
    	List<BandElement> oldElements = new ArrayList<BandElement>();
    	List<BandElement> elements = new ArrayList<BandElement>();
    	List<Integer> rows = new ArrayList<Integer>();
    	List<Integer> columns = new ArrayList<Integer>();            	
        for (ReportGridCell reportGridCell : reportGridCells) {            	
            BandElement element = reportGridCell.getValue();                
            oldElements.add(ObjectCloner.silenceDeepCopy(element));
            int row = reportGridCell.getRow();
            rows.add(row);
            int column = reportGridCell.getColumn();
            columns.add(column);
            
            try {
                if (TEXT_PARAM_NAME.equals(propName)) {
                    String propValue = (String) prop.getValue();
                    element.setText(propValue);
                } else if (FONT_PARAM_NAME.equals(propName)) {
                    Font propValue = (Font) prop.getValue();
                    element.setFont(propValue);
                } else if (BACKGROUND_PARAM_NAME.equals(propName)) {
                    Color propValue = (Color) prop.getValue();
                    element.setBackground(propValue);
                } else if (FOREGROUND_PARAM_NAME.equals(propName)) {
                    Color propValue = (Color) prop.getValue();
                    element.setForeground(propValue);
                } else if (ALIGNMENT_PARAM_NAME.equals(propName)) {
                    String propValue = (String) prop.getValue();
                    if (CENTER.equals(propValue)) {
                        element.setHorizontalAlign(BandElement.CENTER);
                    } else if (RIGHT.equals(propValue)) {
                        element.setHorizontalAlign(BandElement.RIGHT);                        
                    } else {
                        element.setHorizontalAlign(BandElement.LEFT);                        
                    }
                } else if (V_ALIGNMENT_PARAM_NAME.equals(propName)) {
                    String propValue = (String) prop.getValue();
                    if (MIDDLE.equals(propValue)) {
                        element.setVerticalAlign(BandElement.MIDDLE);
                    } else if (TOP.equals(propValue)) {
                        element.setVerticalAlign(BandElement.TOP);
                    } else {
                        element.setVerticalAlign(BandElement.BOTTOM);                        
                    }
                } else if (PATTERN_PARAM_NAME.equals(propName)) {
                    String propValue = (String) prop.getValue();
                    ((FieldBandElement) element).setPattern(propValue);
                } else if (WIDTH_PARAM_NAME.equals(propName)) {
                    Integer propValue = (Integer) prop.getValue();
                    if (element instanceof ImageBandElement) {
                    	((ImageBandElement) element).setWidth(propValue);
                    } else {
                    	((ImageColumnBandElement) element).setWidth(propValue);
                    }
                } else if (HEIGHT_PARAM_NAME.equals(propName)) {
                    Integer propValue = (Integer) prop.getValue();
                    if (element instanceof ImageBandElement) {
                    	((ImageBandElement) element).setHeight(propValue);
                    } else {
                    	((ImageColumnBandElement) element).setHeight(propValue);
                    }
                } else if (URL_PARAM_NAME.equals(propName)) {
                    Hyperlink propValue = (Hyperlink) prop.getValue();
                    ((HyperlinkBandElement) element).setHyperlink(propValue);
                } else if (PADDING_PARAM_NAME.equals(propName)) {
					Padding propValue = (Padding) prop.getValue();
					element.setPadding(propValue);
				} else if (BORDER_PARAM_NAME.equals(propName)) {
					ro.nextreports.engine.band.Border propValue = (ro.nextreports.engine.band.Border) prop.getValue();
					element.setBorder(propValue);
				} else if (CONDITION_NAME.equals(propName)) {
					FormattingConditions propValue = (FormattingConditions) prop.getValue();
					element.setFormattingConditions(propValue);
				} else if (HTML_ACC_HEADERS.equals(propName)) {
                    String propValue = (String) prop.getValue();
                    if ((propValue != null) && propValue.trim().equals("")) {
                        propValue = null;
                    }
                    element.setHtmlAccHeaders(propValue);
                } else if (HTML_ACC_ID.equals(propName)) {
                    String propValue = (String) prop.getValue();
                    if ((propValue != null) && propValue.trim().equals("")) {
                        propValue = null;
                    }
                    element.setHtmlAccId(propValue);
                } else if (HTML_ACC_SCOPE.equals(propName)) {
                    String propValue = (String) prop.getValue();
                    if (HTML_SCOPE_NONE.equals(propValue)) {
                        element.setHtmlAccScope(null);
                    } else {
                        element.setHtmlAccScope(propValue);
                    }
                } else if(WRAPTEXT_PARAM_NAME.equals(propName)) {
                    Boolean propValue = (Boolean)prop.getValue();
                    element.setWrapText(propValue);
                } else if(TEXT_ROTATION_PARAM_NAME.equals(propName)) {
                    Short propValue = (Short)prop.getValue();
                    element.setTextRotation(propValue);
                } else if(REPEATED_PARAM_NAME.equals(propName)) {
                    Boolean propValue = (Boolean)prop.getValue();
                    element.setRepeatedValue(propValue);
                } else if(HIDE_WHEN_EXPRESSION_PARAM_NAME.equals(propName)) {
                    String propValue = (String)prop.getValue();
                    if ((propValue != null) && propValue.trim().equals("")) {
                        propValue = null;
                    }
                    element.setHideWhenExpression(propValue);
                } else if (ROW_CONDITION_NAME.equals(propName)) {
                	// nothing to do here
                } else if (ROW_NEW_PAGE_NAME.equals(propName)) {
                	// nothing to do here
                } else if (FOR_REPORT_PARAM_NAME.equals(propName)) {
                	String propValue = (String) prop.getValue();
                	((ForReportBandElement)element).setSql(propValue);
                } else {
					throw new RuntimeException("Invalid property name '" + propName + "'");
				}
                
                elements.add(ObjectCloner.silenceDeepCopy(element));
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
        
        int n = elements.size(); 
        if (n > 0) {
        	ignoreEvent = true;
        	for (int i = 0; i < n; i++) {
        		reportGridModel.fireGridCellUpdated(rows.get(i), columns.get(i));
        	}
        	Globals.getReportUndoManager().addEdit(new ModifyElementsEdit(elements, oldElements, rows, columns));
        	ignoreEvent = false;
        }
    }

    public void refresh() {
        setProperties(new Property[0]);
    }

    public void selectionChanged(SelectionModelEvent event) {    	
		if (ignoreEvent) {
			return;
		}

        if (event.isRootSelection()) {
            List<Property> props = getReportProperties(LayoutHelper.getReportLayout());
            setProperties(props.toArray(new Property[props.size()]));
            return;
        }

        try {
            isInit = true;
            SelectionModel selectionModel = (SelectionModel) event.getSource();
            List<Cell> selectedCells = selectionModel.getSelectedCells();
            List<Integer> selectedRows = selectionModel.getSelectedRows();                       
            if (selectedCells.size() == 0) {             	     
            	if (selectedRows.size() > 0) {
                	rows.clear();
                	rows.addAll(selectedRows);
                	List<Property> props = getRowProperties();
                	setProperties(props.toArray(new Property[props.size()]));
                	return;
                }
            	setProperties(new Property[0]);            	
                return;
            }
                        
            reportGridCells.clear();
            for (Cell cell : selectedCells) {
                BandElement element = (BandElement) Globals.getReportGrid().getValueAt(cell.getRow(), cell.getColumn());
                if (element != null) {
                    reportGridCells.add(new ReportGridCell(element, cell.getRow(), cell.getColumn()));
                }
                formattingCellBand = BandUtil.getBand(LayoutHelper.getReportLayout(),cell.getRow()).getName();
            }
            
            List<Property> props = getFilteredProperties();
            setProperties(props.toArray(new Property[props.size()]));
        } catch (Throwable e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        } finally {
            isInit = false;
        }
    }
    
    private List<Property> getFilteredProperties() {
        List<Property> props = new ArrayList<Property>();
        if (reportGridCells.size() == 0) {
            return props;
        }

        boolean multipleSelection = (reportGridCells.size() > 1);
//        System.out.println("multipleSelection = " + multipleSelection);
        
        // text
        if (!multipleSelection) {
            props.add(getTextProperty());
        }
        
        // font
        props.add(getFontProperty());
        
        // background
        props.add(getBackgroundProperty());
        
        // foreground
        props.add(getForegroundProperty());
 
        // alignment
        props.add(getAlignmentProperty());

        // vertical alignment
        props.add(getVerticalAlignmentProperty());
        
        // pattern
        //@todo a way to see that all reportGridCells have the same type?
        //if (!multipleSelection) {
            if (reportGridCells.get(0).getValue() instanceof FieldBandElement) {
                props.add(getPatternProperty());
            }
        //}

        if ((reportGridCells.get(0).getValue() instanceof ImageBandElement) ||
        	(reportGridCells.get(0).getValue() instanceof ImageColumnBandElement) )	{
            props.add(getWidthProperty());
            props.add(getHeightProperty());
        }
        
        // padding
        props.add(getPaddingProperty());               

        // border
        props.add(getBorderProperty());

        // wrap text
        props.add(getWrapTextProperty());
        
        // text rotation
        props.add(getTextRotationProperty());

        // repeated value
        if ((reportGridCells.get(0).getValue() instanceof ColumnBandElement) ||
            (reportGridCells.get(0).getValue() instanceof ExpressionBandElement)) {
            props.add(getRepeatedProperty());
        }

        String bandName = BandUtil.getBand(LayoutHelper.getReportLayout(), reportGridCells.get(0)).getName();
        boolean isStaticBand = !(bandName.equals(ReportLayout.DETAIL_BAND_NAME) ||
                        bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX) ||
                        bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX));
        boolean isFooterBand = bandName.equals(ReportLayout.FOOTER_BAND_NAME) ||                
                bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX);
        props.add(getHideWhenExpressionProperty(isStaticBand, isFooterBand, bandName));

        if (reportGridCells.get(0).getValue() instanceof HyperlinkBandElement) {
            props.add(getHyperlinkProperty());
        }

        if (!multipleSelection) {
            if (reportGridCells.get(0).getValue() instanceof ColumnBandElement) {
                String column = ((ColumnBandElement)reportGridCells.get(0).getValue()).getColumn();
                props.add(getConditionProperty(ReportLayoutUtil.getColumnType(column)));
            } else if (reportGridCells.get(0).getValue() instanceof FunctionBandElement) {
                props.add(getConditionProperty(null));  // type is java.lang.Double
            } else if (reportGridCells.get(0).getValue() instanceof ExpressionBandElement) {
                String expression = ((ExpressionBandElement)reportGridCells.get(0).getValue()).getExpression();
                props.add(getConditionProperty(ReportLayoutUtil.getExpressionType(expression)));
            }
        } else {
        	String type = null;
        	boolean sameType = true;
        	for (ReportGridCell cell : reportGridCells) {
        		String cellType = null;
        		if (cell.getValue() instanceof ColumnBandElement) {
        			 String column = ((ColumnBandElement)cell.getValue()).getColumn();
        			 cellType = ReportLayoutUtil.getColumnType(column);
        		} else if (cell.getValue() instanceof FunctionBandElement) {
        			cellType = "java.lang.Double";
        		} else if (cell.getValue() instanceof ExpressionBandElement) {
                    String expression = ((ExpressionBandElement)cell.getValue()).getExpression();
                    cellType = ReportLayoutUtil.getExpressionType(expression);
                }
        		if (cellType == null) {
        			// a cell that is not column, function or expression
        			sameType = false;
        			break;
        		} 
        		if (type == null) {
        			type = cellType;
        		} else if (!type.equals(cellType)) {
        			sameType = false;
        			break;
        		}
        	}        	
        	if (sameType) {        		
        		 props.add(getConditionProperty(type));
        	}
        }
        
		if (reportGridCells.get(0).getValue() instanceof ForReportBandElement) {
			props.add(getForReportSqlProperty());
		}

        // html accesibility properties
        if (Globals.getAccessibilityHtml()) {
            props.add(getHtmlAccHeadersProperty());
            props.add(getHtmlAccIdProperty());
            props.add(getHtmlAccScopeProperty());
        }

        return props;
    }

    ///// report properties

    private List<Property> getReportProperties(ReportLayout reportLayout) {
        List<Property> props = new ArrayList<Property>();
        props.add(getReportTypeProperty(reportLayout));
        props.add(getPageFormatProperty(reportLayout));
        if (ReportLayout.CUSTOM.equals(reportLayout.getPageFormat())) {
        	props.add(getPaperSizeProperty(reportLayout));
        }        
        props.add(getOrientationProperty(reportLayout));
        props.add(getPagePaddingProperty(reportLayout));
        props.add(getHeaderProperty(reportLayout));
        props.add(getBackgroundImageProperty(reportLayout));
        props.add(getTemplateProperty(reportLayout));
        props.add(getTemplateSheetProperty(reportLayout));
        return props;
    }
    
    private List<Property> getRowProperties() {
        List<Property> props = new ArrayList<Property>();
        props.add(getRowConditionProperty());  
        props.add(getRowNewPageProperty());
        return props;
    }

    private Property getPageFormatProperty(ReportLayout reportLayout) {
        DefaultProperty pageFormatProp = new DefaultProperty();
        pageFormatProp.setName(PAGE_FORMAT_NAME);
        pageFormatProp.setDisplayName(PAGE_FORMAT_PARAM_NAME);
        pageFormatProp.setType(String.class);
        ComboBoxPropertyEditor editor = new ComboBoxPropertyEditor();
        editor.setAvailableValues(ReportLayout.getPageFormats());
        pageFormatProp.setValue(reportLayout.getPageFormat());
        editorRegistry.registerEditor(pageFormatProp, editor);
        return pageFormatProp;
    }
    
    private Property getPaperSizeProperty(ReportLayout reportLayout) {
        DefaultProperty paperSizeProp = new DefaultProperty();
        paperSizeProp.setName(CUSTOM_PAGE_FORMAT_DEF_NAME);
        paperSizeProp.setDisplayName(CUSTOM_PAGE_FORMAT_DEF_PARAM_NAME);
        paperSizeProp.setType(PaperSize.class);        
        paperSizeProp.setValue(reportLayout.getPaperSize());
        CustomSizePropertyEditor sizeEditor = new CustomSizePropertyEditor();
        editorRegistry.registerEditor(paperSizeProp, sizeEditor);
        return paperSizeProp;
    }
    
    private Property getPagePaddingProperty(ReportLayout reportLayout) {
        DefaultProperty pagePaddingProp = new DefaultProperty();
        pagePaddingProp.setName(PAGE_PADDING_NAME);
        pagePaddingProp.setDisplayName(PAGE_PADDING_PARAM_NAME);
        pagePaddingProp.setType(Padding.class);        
        pagePaddingProp.setValue(reportLayout.getPagePadding());
        PaddingPropertyEditor paddingEditor = new PaddingPropertyEditor();
        editorRegistry.registerEditor(pagePaddingProp, paddingEditor);
        return pagePaddingProp;
    }

    private Property getOrientationProperty(ReportLayout reportLayout) {
        DefaultProperty orientationProp = new DefaultProperty();
        orientationProp.setName(ORIENTATION_NAME);
        orientationProp.setDisplayName(ORIENTATION_PARAM_NAME);
        orientationProp.setType(String.class);
        ComboBoxPropertyEditor editor = new ComboBoxPropertyEditor();
        editor.setAvailableValues(new String[] { PORTRAIT, LANDSCAPE });
        setOrientation(reportLayout.getOrientation(), orientationProp);
        editorRegistry.registerEditor(orientationProp, editor);
        return orientationProp;
    }        

    private int getOrientation(String orientation) {
        if (LANDSCAPE.equals(orientation)) {
            return ResultExporter.LANDSCAPE;
        } else {
            return ResultExporter.PORTRAIT;
        }
    }

    private void setOrientation(int orientation, Property orientationProp) {
        String orientationS;
        switch (orientation) {
            case ResultExporter.LANDSCAPE:
                orientationS = LANDSCAPE;
                break;
            default:
                orientationS = PORTRAIT;
                break;
        }
        orientationProp.setValue(orientationS);
    }
    
    private Property getReportTypeProperty(ReportLayout reportLayout) {
        DefaultProperty reportTypeProp = new DefaultProperty();
        reportTypeProp.setName(REPORT_TYPE_NAME);
        reportTypeProp.setDisplayName(REPORT_TYPE_PARAM_NAME);
        reportTypeProp.setType(String.class);
        ComboBoxPropertyEditor editor = new ComboBoxPropertyEditor();
        editor.setAvailableValues(new String[] { DEFAULT_TYPE, ALARM_TYPE, TABLE_TYPE, INDICATOR_TYPE, DISPLAY_TYPE });
        setReportType(reportLayout.getReportType(), reportTypeProp);
        editorRegistry.registerEditor(reportTypeProp, editor);
        return reportTypeProp;
    }
    
    private int getReportType(String type) {
        if (ALARM_TYPE.equals(type)) {
            return ResultExporter.ALARM_TYPE;
        } else if (TABLE_TYPE.equals(type)){
            return ResultExporter.TABLE_TYPE;
        } else if (INDICATOR_TYPE.equals(type)){
            return ResultExporter.INDICATOR_TYPE;
        } else if (DISPLAY_TYPE.equals(type)){
            return ResultExporter.DISPLAY_TYPE;    
        } else {
        	return ResultExporter.DEFAULT_TYPE;
        }
    }
    
    private void setReportType(int type, Property typeProp) {
        String typeS;
        switch (type) {
            case ResultExporter.ALARM_TYPE:
            	typeS = ALARM_TYPE;
                break;
            case ResultExporter.TABLE_TYPE:
            	typeS = TABLE_TYPE;
                break;
            case ResultExporter.INDICATOR_TYPE:
            	typeS = INDICATOR_TYPE;
                break;    
            case ResultExporter.DISPLAY_TYPE:
            	typeS = DISPLAY_TYPE;
                break;        
            default:
            	typeS = DEFAULT_TYPE;
                break;
        }
        typeProp.setValue(typeS);
    }

     private Property getHeaderProperty(ReportLayout reportLayout) {
        DefaultProperty headerPop = new DefaultProperty();
        headerPop.setName(HEADER_NAME);
        headerPop.setDisplayName(HEADER_PARAM_NAME);
        headerPop.setType(Boolean.class);
        headerPop.setValue(reportLayout.isHeaderOnEveryPage());
        return headerPop;
    }
     
     private Property getBackgroundImageProperty(ReportLayout reportLayout) {
         DefaultProperty imageProp = new DefaultProperty();
         imageProp.setName(BG_IMAGE_NAME);
         imageProp.setDisplayName(BG_IMAGE_PARAM_NAME);
         imageProp.setType(String.class);
         imageProp.setValue(reportLayout.getBackgroundImage());
         ImagePropertyEditor imageEditor = new ImagePropertyEditor();
         editorRegistry.registerEditor(imageProp, imageEditor);
         return imageProp;
     }
     
     private Property getTemplateProperty(ReportLayout reportLayout) {
         DefaultProperty templateProp = new DefaultProperty();
         templateProp.setName(TEMPLATE_NAME);
         templateProp.setDisplayName(TEMPLATE_PARAM_NAME);
         templateProp.setType(String.class);
         templateProp.setValue(reportLayout.getTemplateName());
         TemplatePropertyEditor imageEditor = new TemplatePropertyEditor();
         editorRegistry.registerEditor(templateProp, imageEditor);
         return templateProp;
     }
     
     private Property getTemplateSheetProperty(ReportLayout reportLayout) {
         DefaultProperty sheetProp = new DefaultProperty();
         sheetProp.setName(TEMPLATE_SHEET_NAME);
         sheetProp.setDisplayName(TEMPLATE_SHEET_PARAM_NAME);
         sheetProp.setType(Integer.class);
         sheetProp.setValue(reportLayout.getTemplateSheet());
         return sheetProp;
     }

    //// end report properties

    private Property getTextProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(TEXT_PARAM_NAME);
        textProp.setDisplayName(TEXT_PARAM_NAME);
        textProp.setType(String.class);
        BandElement be = reportGridCells.get(0).getValue();
        textProp.setValue(be.getText());
        if ((be instanceof FieldBandElement) || (be instanceof ImageBandElement)) {
        	textProp.setEditable(false);        	
        }
        if (Globals.getAccessibilityHtml()) {
            textProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        
        return textProp;
    }
    
    private Property getForReportSqlProperty() {
        DefaultProperty queryProp = new DefaultProperty();
        queryProp.setName(FOR_REPORT_PARAM_NAME);
        queryProp.setDisplayName(FOR_REPORT_PARAM_NAME);
        queryProp.setType(String.class);
        queryProp.setValue(getUniqueForReportSql());
        if (Globals.getAccessibilityHtml()) {
        	queryProp.setCategory(I18NSupport.getString("property.category.main"));
        }                
        SqlPropertyEditor sqlEditor = new SqlPropertyEditor();
        editorRegistry.registerEditor(queryProp, sqlEditor);
        return queryProp;
    }

    private Property getFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(FONT_PARAM_NAME);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(getUniqueFont());
        if (Globals.getAccessibilityHtml()) {
            fontProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        
        return fontProp;
    }

    private Property getWrapTextProperty() {
        DefaultProperty wrapProp = new DefaultProperty();
        wrapProp.setName(WRAPTEXT_PARAM_NAME);
        wrapProp.setDisplayName(WRAPTEXT_PARAM_NAME);
        wrapProp.setType(Boolean.class);
        wrapProp.setValue(getUniqueWrapText());
        if (Globals.getAccessibilityHtml()) {
            wrapProp.setCategory(I18NSupport.getString("property.category.main"));
        }

        return wrapProp;
    }
    
    private Property getTextRotationProperty() {
        DefaultProperty rotationProp = new DefaultProperty();
        rotationProp.setName(TEXT_ROTATION_PARAM_NAME);
        rotationProp.setDisplayName(TEXT_ROTATION_PARAM_NAME);
        rotationProp.setType(Short.class);
        rotationProp.setValue(getUniqueTextRotation());
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new Short[] { -90, 0, 90 });
        if (Globals.getAccessibilityHtml()) {
            rotationProp.setCategory(I18NSupport.getString("property.category.main"));
        }               
        editorRegistry.registerEditor(rotationProp, alignmentEditor);
        return rotationProp;
    }

    private Property getRepeatedProperty() {
        DefaultProperty repeatedProp = new DefaultProperty();
        repeatedProp.setName(REPEATED_PARAM_NAME);
        repeatedProp.setDisplayName(REPEATED_PARAM_NAME);
        repeatedProp.setType(Boolean.class);
        repeatedProp.setValue(getUniqueRepeated());
        if (Globals.getAccessibilityHtml()) {
            repeatedProp.setCategory(I18NSupport.getString("property.category.main"));
        }

        return repeatedProp;
    }

    private Property getHideWhenExpressionProperty(boolean isStaticBand, boolean isFooterBand, String bandName) {
        DefaultProperty hideWhenExpression = new DefaultProperty();
        hideWhenExpression.setName(HIDE_WHEN_EXPRESSION_PARAM_NAME);
        hideWhenExpression.setDisplayName(HIDE_WHEN_EXPRESSION_PARAM_NAME);
        hideWhenExpression.setType(String.class);
        hideWhenExpression.setValue(getUniqueHideWhenExpression());
        if (Globals.getAccessibilityHtml()) {
            hideWhenExpression.setCategory(I18NSupport.getString("property.category.main"));
        }
        HideWhenExpressionPropertyEditor hideEditor = new HideWhenExpressionPropertyEditor(isStaticBand, isFooterBand, bandName);
        editorRegistry.registerEditor(hideWhenExpression, hideEditor);

        return hideWhenExpression;
    }


    private Property getBackgroundProperty() {
        DefaultProperty backgroundProp = new DefaultProperty();
        backgroundProp.setName(BACKGROUND_PARAM_NAME);
        backgroundProp.setDisplayName(BACKGROUND_PARAM_NAME);
        backgroundProp.setType(Color.class);
        backgroundProp.setValue(getUniqueBackground());
        if (Globals.getAccessibilityHtml()) {
            backgroundProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(backgroundProp, colorEditor);
        
        return backgroundProp;
    }
    
    private Property getForegroundProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(FOREGROUND_PARAM_NAME);
        foregroundProp.setDisplayName(FOREGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        foregroundProp.setValue(getUniqueForeground());
        if (Globals.getAccessibilityHtml()) {
            foregroundProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }

    private Property getAlignmentProperty() {
        DefaultProperty alignmentProp = new DefaultProperty();
        alignmentProp.setName(ALIGNMENT_PARAM_NAME);
        alignmentProp.setDisplayName(ALIGNMENT_PARAM_NAME);
        alignmentProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[] { LEFT, CENTER, RIGHT });
        int alignment = getUniqueAllignment();
        switch (alignment) {
            case BandElement.CENTER:
                alignmentProp.setValue(CENTER);
                break;
            case BandElement.RIGHT:
                alignmentProp.setValue(RIGHT);
                break;
            case BandElement.LEFT:
                alignmentProp.setValue(LEFT);
                break;
            default:
                alignmentProp.setValue(null);
        }
        if (Globals.getAccessibilityHtml()) {
            alignmentProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        editorRegistry.registerEditor(alignmentProp, alignmentEditor);
        
        return alignmentProp;
    }

    private Property getVerticalAlignmentProperty() {
        DefaultProperty alignmentProp = new DefaultProperty();
        alignmentProp.setName(V_ALIGNMENT_PARAM_NAME);
        alignmentProp.setDisplayName(V_ALIGNMENT_PARAM_NAME);
        alignmentProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[] { TOP, MIDDLE, BOTTOM });
        int alignment = getUniqueVAllignment();
        switch (alignment) {
            case BandElement.TOP:
                alignmentProp.setValue(TOP);
                break;
            case BandElement.MIDDLE:
                alignmentProp.setValue(MIDDLE);
                break;
            case BandElement.BOTTOM:
                alignmentProp.setValue(BOTTOM);
                break;
            default:
                alignmentProp.setValue(null);
        }
        if (Globals.getAccessibilityHtml()) {
            alignmentProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        editorRegistry.registerEditor(alignmentProp, alignmentEditor);

        return alignmentProp;
    }
    
    private Property getPatternProperty() {
        DefaultProperty patternProp = new DefaultProperty();
        patternProp.setName(PATTERN_PARAM_NAME);
        patternProp.setDisplayName(PATTERN_PARAM_NAME);
        patternProp.setType(String.class);
        patternProp.setValue(((FieldBandElement) reportGridCells.get(0).getValue()).getPattern());
        if (Globals.getAccessibilityHtml()) {
            patternProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        FieldPatternPropertyEditor patternEditor = new FieldPatternPropertyEditor();
        editorRegistry.registerEditor(patternProp, patternEditor);
        
        return patternProp;
    }

    private Property getWidthProperty() {
        DefaultProperty widthProp = new DefaultProperty();
        widthProp.setName(WIDTH_PARAM_NAME);
        widthProp.setDisplayName(WIDTH_PARAM_NAME);
        widthProp.setType(Integer.class);
        if (reportGridCells.get(0).getValue() instanceof ImageBandElement) {
        	widthProp.setValue(((ImageBandElement) reportGridCells.get(0).getValue()).getWidth());
        } else {
        	widthProp.setValue(((ImageColumnBandElement) reportGridCells.get(0).getValue()).getWidth());
        }
        if (Globals.getAccessibilityHtml()) {
            widthProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        IntegerPropertyEditor widthEditor = new IntegerPropertyEditor();
        editorRegistry.registerEditor(widthProp, widthEditor);

        return widthProp;
    }

    private Property getHeightProperty() {
        DefaultProperty heightProp = new DefaultProperty();
        heightProp.setName(HEIGHT_PARAM_NAME);
        heightProp.setDisplayName(HEIGHT_PARAM_NAME);
        heightProp.setType(Integer.class);
        if (reportGridCells.get(0).getValue() instanceof ImageBandElement) {
        	heightProp.setValue(((ImageBandElement) reportGridCells.get(0).getValue()).getHeight());
        } else {
        	heightProp.setValue(((ImageColumnBandElement) reportGridCells.get(0).getValue()).getHeight());
        }
        if (Globals.getAccessibilityHtml()) {
            heightProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        IntegerPropertyEditor heightEditor = new IntegerPropertyEditor();
        editorRegistry.registerEditor(heightProp, heightEditor);

        return heightProp;
    }

    private Property getHyperlinkProperty() {
        DefaultProperty urlProp = new DefaultProperty();
        urlProp.setName(URL_PARAM_NAME);
        urlProp.setDisplayName(URL_PARAM_NAME);
        urlProp.setType(Hyperlink.class);
        urlProp.setValue(((HyperlinkBandElement) reportGridCells.get(0).getValue()).getHyperlink());
        if (Globals.getAccessibilityHtml()) {
            urlProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        HyperlinkPropertyEditor hyperlinkEditor = new HyperlinkPropertyEditor();
        editorRegistry.registerEditor(urlProp, hyperlinkEditor);

        return urlProp;
    }

    private Property getPaddingProperty() {
        DefaultProperty paddingProp = new DefaultProperty();
        paddingProp.setName(PADDING_PARAM_NAME);
        paddingProp.setDisplayName(PADDING_PARAM_NAME);
        paddingProp.setType(Padding.class);
        paddingProp.setValue(getUniquePadding());
        if (Globals.getAccessibilityHtml()) {
            paddingProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        PaddingPropertyEditor paddingEditor = new PaddingPropertyEditor();
        editorRegistry.registerEditor(paddingProp, paddingEditor);
        
        return paddingProp;
    }
    private Property getBorderProperty() {
        DefaultProperty borderProp = new DefaultProperty();
        borderProp.setName(BORDER_PARAM_NAME);
        borderProp.setDisplayName(BORDER_PARAM_NAME);
        borderProp.setType(ro.nextreports.engine.band.Border.class);
        borderProp.setValue(getUniqueBorder());
        if (Globals.getAccessibilityHtml()) {
            borderProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        BorderPropertyEditor borderEditor = new BorderPropertyEditor();
        editorRegistry.registerEditor(borderProp, borderEditor);
        
        return borderProp;
    }


    private Property getConditionProperty(String type) {
        DefaultProperty conditionProp = new DefaultProperty();
        conditionProp.setName(CONDITION_NAME);
        conditionProp.setDisplayName(CONDITION_PARAM_NAME);
        conditionProp.setType(FormattingConditions.class);
        conditionProp.setValue(getUniqueBorder());
        conditionProp.setValue(reportGridCells.get(0).getValue().getFormattingConditions());
        if (Globals.getAccessibilityHtml()) {
            conditionProp.setCategory(I18NSupport.getString("property.category.main"));
        }        
        FormattingConditionsPropertyEditor conditionEditor = new FormattingConditionsPropertyEditor(type, formattingCellBand);
        editorRegistry.registerEditor(conditionProp, conditionEditor);

        return conditionProp;
    }
    
    private Property getRowConditionProperty() {
        DefaultProperty conditionProp = new DefaultProperty();
        conditionProp.setName(ROW_CONDITION_NAME);
        conditionProp.setDisplayName(ROW_CONDITION_PARAM_NAME);
        conditionProp.setType(FormattingConditions.class);        
        conditionProp.setValue(BandUtil.getRowElement(LayoutHelper.getReportLayout(), rows.get(0)).getFormattingConditions());
        if (Globals.getAccessibilityHtml()) {
            conditionProp.setCategory(I18NSupport.getString("property.category.main"));
        }
        RowFormattingConditionsPropertyEditor conditionEditor = new RowFormattingConditionsPropertyEditor(rows);
        editorRegistry.registerEditor(conditionProp, conditionEditor);

        return conditionProp;
    }
    
    private Property getRowNewPageProperty() {
        DefaultProperty newPageProp = new DefaultProperty();
        newPageProp.setName(ROW_NEW_PAGE_NAME);
        newPageProp.setDisplayName(ROW_NEW_PAGE_PARAM_NAME);
        newPageProp.setType(Boolean.class);        
        newPageProp.setValue(BandUtil.getRowElement(LayoutHelper.getReportLayout(), rows.get(0)).isStartOnNewPage());
        if (Globals.getAccessibilityHtml()) {
            newPageProp.setCategory(I18NSupport.getString("property.category.main"));
        }        
        return newPageProp;
    }

    private Property getHtmlAccHeadersProperty() {
        DefaultProperty accHeaders = new DefaultProperty();
        accHeaders.setName(HTML_ACC_HEADERS);
        accHeaders.setDisplayName(HTML_ACC_HEADERS);
        accHeaders.setType(String.class);
        accHeaders.setValue(getUniqueHtmlAccHeaders());
        if (Globals.getAccessibilityHtml()) {
            accHeaders.setCategory(I18NSupport.getString("property.category.accessibility.html"));
        }

        return accHeaders;
    }

    private Property getHtmlAccIdProperty() {
        DefaultProperty accId = new DefaultProperty();
        accId.setName(HTML_ACC_ID);
        accId.setDisplayName(HTML_ACC_ID);
        accId.setType(String.class);
        accId.setValue(getUniqueHtmlAccId());
        if (Globals.getAccessibilityHtml()) {
            accId.setCategory(I18NSupport.getString("property.category.accessibility.html"));
        }

        return accId;
    }

    private Property getHtmlAccScopeProperty() {
        DefaultProperty scopeProp = new DefaultProperty();
        scopeProp.setName(HTML_ACC_SCOPE);
        scopeProp.setDisplayName(HTML_ACC_SCOPE);
        scopeProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[] { HTML_SCOPE_NONE, HTML_SCOPE_ROW, HTML_SCOPE_COL });
        String scope = getUniqueHtmlAccScope();
        if (HTML_SCOPE_ROW.equals(scope)) {
            scopeProp.setValue(HTML_SCOPE_ROW);
        } else if (HTML_SCOPE_COL.equals(scope)) {
            scopeProp.setValue(HTML_SCOPE_COL);
        } else {
            scopeProp.setValue(null);
        }
        if (Globals.getAccessibilityHtml()) {
            scopeProp.setCategory(I18NSupport.getString("property.category.accessibility.html"));
        }
        editorRegistry.registerEditor(scopeProp, alignmentEditor);

        return scopeProp;
    }

    private Font getUniqueFont() {
        Font font = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                font = element.getFont();
                continue;
            }
            if (!font.equals(element.getFont())) {
                return null;
            }
        }
        
        return font;
    }

    private Boolean getUniqueWrapText() {
        Boolean wrap = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                wrap = Boolean.valueOf(element.isWrapText());
                continue;
            }
            if (!wrap.equals(element.isWrapText())) {
                return null;
            }
        }

        return wrap;
    }
    
    private Short getUniqueTextRotation() {
        Short rotation = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                rotation = element.getTextRotation();
                continue;
            }
            if (rotation != element.getTextRotation()) {
                return null;
            }
        }

        return rotation;
    }

    private Boolean getUniqueRepeated() {
        Boolean repeated = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                repeated = Boolean.valueOf(element.isRepeatedValue());
                continue;
            }
            if (!repeated.equals(element.isRepeatedValue())) {
                return null;
            }
        }

        return repeated;
    }

    private String getUniqueHideWhenExpression() {
        String hideWhenExpression = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                hideWhenExpression = element.getHideWhenExpression();
                continue;
            }
            if ((hideWhenExpression == null) || !hideWhenExpression.equals(element.getHideWhenExpression())) {
                return null;
            }
        }

        return hideWhenExpression;
    }
    
    private Color getUniqueBackground() {
        Color background = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                background = element.getBackground();
                continue;
            }
            if (!background.equals(element.getBackground())) {
                return null;
            }
        }
        
        return background;
    }
    
    private Color getUniqueForeground() {
        Color foreground = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                foreground = element.getForeground();
                continue;
            }
            if (!foreground.equals(element.getForeground())) {
                return null;
            }
        }
        
        return foreground;
    }

    private Padding getUniquePadding() {
        Padding padding = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                padding = element.getPadding();
                if (padding == null) {
                    return null;
                } else {
                    continue;
                }
            }
            if (!padding.equals(element.getPadding())) {
                return null;
            }
        }
        
        return padding;
    }
    private ro.nextreports.engine.band.Border getUniqueBorder() {
        ro.nextreports.engine.band.Border border = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                border = element.getBorder();
                if (border == null) {
                    return null;
                } else {
                    continue;
                }
            }
            if (!border.equals(element.getBorder())) {
                return null;
            }
        }
        return border;
    }
    
    private int getUniqueAllignment() {
        int allignment = -1;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                allignment = element.getHorizontalAlign();
                continue;
            }
            if (allignment != element.getHorizontalAlign()) {
                return -1;
            }
        }
        
        return allignment;
    }

    private int getUniqueVAllignment() {
        int allignment = -1;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                allignment = element.getVerticalAlign();
                continue;
            }
            if (allignment != element.getVerticalAlign()) {
                return -1;
            }
        }

        return allignment;
    }

    private String getUniqueHtmlAccHeaders() {
        String accHeaders = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                accHeaders = element.getHtmlAccHeaders();
                continue;
            }
            if ((accHeaders == null) || !accHeaders.equals(element.getHtmlAccHeaders())) {
                return null;
            }
        }

        return accHeaders;
    }

    private String getUniqueHtmlAccId() {
        String accId = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                accId = element.getHtmlAccId();
                continue;
            }
            if ((accId == null) || !accId.equals(element.getHtmlAccId())) {
                return null;
            }
        }

        return accId;
    }

    private String getUniqueHtmlAccScope() {
        String accScope = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            BandElement element = reportGridCells.get(i).getValue();
            if (i == 0) {
                accScope = element.getHtmlAccScope();
                continue;
            }
            if ((accScope == null) || !accScope.equals(element.getHtmlAccScope())) {
                return null;
            }
        }

        return accScope;
    }
    
    private String getUniqueForReportSql() {
        String sql = null;
        int n = reportGridCells.size();
        for (int i = 0; i < n; i++) {
            ForReportBandElement element = (ForReportBandElement)reportGridCells.get(i).getValue();
            if (i == 0) {
            	sql = element.getSql();
                continue;
            }
            if ((sql == null) || !sql.equals(element.getSql())) {
                return null;
            }
        }

        return sql;
    }
    
}
