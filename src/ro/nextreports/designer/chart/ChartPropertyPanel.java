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
package ro.nextreports.designer.chart;

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;

import ro.nextreports.engine.chart.ChartTitle;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartType;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.exporter.util.function.FunctionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.property.ExtendedColorPropertyEditor;
import ro.nextreports.designer.property.FieldPatternPropertyEditor;
import ro.nextreports.designer.property.SqlPropertyEditor;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import javax.swing.*;

import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * User: mihai.panaitescu
 * Date: 15-Dec-2009
 * Time: 13:30:08
 */
public class ChartPropertyPanel extends PropertySheetPanel {

    public static final int TITLE_CATEGORY = 1;
    public static final int MAIN_CATEGORY = 2;
    public static final int XCOLUMN_CATEGORY = 4;
    public static final int YCOLUMN_CATEGORY = 8;

    private String TITLE_TEXT = "TITLE_TEXT";
    private String TITLE_TEXT_PARAM_NAME = I18NSupport.getString("property.text");
    private String TITLE_FONT = "TITLE_FONT";
    private String MAIN_FONT = "MAIN_FONT";
    private String FONT_PARAM_NAME = I18NSupport.getString("property.font");
    private String TITLE_FOREGROUND = "TITLE_FOREGROUND";
    private String TITLE_FOREGROUND_PARAM_NAME = I18NSupport.getString("property.foreground");
    private String TITLE_ALIGNMENT = "TITLE_ALIGNMENT";
    private String ALIGNMENT_PARAM_NAME = I18NSupport.getString("property.allignment");
    private String CHART_BACKGROUND = "CHART_BACKGROUND";
    private String BACKGROUND_PARAM_NAME = I18NSupport.getString("property.background");
    private String CHART_TYPE = "CHART_TYPE";
    private String TYPE_PARAM_NAME = I18NSupport.getString("property.chart.type");
    private String CHART_STYLE = "CHART_STYLE";
    private String STYLE_PARAM_NAME = I18NSupport.getString("property.chart.style");
    private String CHART_TRANSPARENCY = "CHART_TRANSPARENCY";
    private String TRANSPARENCY_PARAM_NAME = I18NSupport.getString("property.chart.transparency");
    private String STYLE_GRID_X = "STYLR_GRID_X";
    private String STYLE_GRID_Y = "STYLR_GRID_Y";
    private String STYLE_GRID_PARAM_NAME = I18NSupport.getString("property.style.grid");        
    private String TOOLTIP_MESSAGE = "TOOLTIP_MESSAGE";
    private String TOOLTIP_MESSAGE_PARAM_NAME = I18NSupport.getString("property.chart.message");
    private String CHART_FOREGROUND = "CHART_FOREGROUND";
    private String CHART_FOREGROUND_2 = "CHART_FOREGROUND_2";
    private String CHART_FOREGROUND_3 = "CHART_FOREGROUND_3";
    private String CHART_FOREGROUND_4 = "CHART_FOREGROUND_4";
    private String CHART_FOREGROUND_5 = "CHART_FOREGROUND_5";
    private String CHART_FOREGROUND_6 = "CHART_FOREGROUND_6";
    private String CHART_FOREGROUND_7 = "CHART_FOREGROUND_7";
    private String CHART_FOREGROUND_8 = "CHART_FOREGROUND_8";
    private String CHART_FOREGROUND_9 = "CHART_FOREGROUND_9";
    private String CHART_FOREGROUND_10 = "CHART_FOREGROUND_10";
    private String FOREGROUND_PARAM_NAME = I18NSupport.getString("property.chart.color");
    private String X_AXIS_COLOR = "X_AXIS_COLOR";
    private String Y_AXIS_COLOR = "Y_AXIS_COLOR";
    private String X_AXIS_LABEL_FONT = "X_AXIS_LABEL_FONT";
    private String Y_AXIS_LABEL_FONT = "Y_AXIS_LABEL_FONT";
    private String X_COLUMN_COL = "X_COLUMN_COL";
    private String Y_COLUMN_COL = "Y_COLUMN_COL";
    private String Y_COLUMN_COL_2 = "Y_COLUMN_COL_2";
    private String Y_COLUMN_COL_3 = "Y_COLUMN_COL_3";
    private String Y_COLUMN_COL_4 = "Y_COLUMN_COL_4";
    private String Y_COLUMN_COL_5 = "Y_COLUMN_COL_5";
    private String Y_COLUMN_COL_6 = "Y_COLUMN_COL_6";
    private String Y_COLUMN_COL_7 = "Y_COLUMN_COL_7";
    private String Y_COLUMN_COL_8 = "Y_COLUMN_COL_8";
    private String Y_COLUMN_COL_9 = "Y_COLUMN_COL_9";
    private String Y_COLUMN_COL_10 = "Y_COLUMN_COL_10";
    private String COLUMN_COL_PARAM_NAME = I18NSupport.getString("property.chart.column.col");    
    private String Y_DYNAMIC_COLUMN_QUERY = "Y_DYNAMIC_COLUMN_QUERY";
    private String Y_DYNAMIC_COL_PARAM_NAME = I18NSupport.getString("property.chart.column.col.dynamic");
    private String Y_COLUMN_LEGEND = "Y_COLUMN_LEGEND";
    private String Y_COLUMN_LEGEND_2 = "Y_COLUMN_LEGEND_2";
    private String Y_COLUMN_LEGEND_3 = "Y_COLUMN_LEGEND_3";
    private String Y_COLUMN_LEGEND_4 = "Y_COLUMN_LEGEND_4";
    private String Y_COLUMN_LEGEND_5 = "Y_COLUMN_LEGEND_5";
    private String Y_COLUMN_LEGEND_6 = "Y_COLUMN_LEGEND_6";
    private String Y_COLUMN_LEGEND_7 = "Y_COLUMN_LEGEND_7";
    private String Y_COLUMN_LEGEND_8 = "Y_COLUMN_LEGEND_8";
    private String Y_COLUMN_LEGEND_9 = "Y_COLUMN_LEGEND_9";
    private String Y_COLUMN_LEGEND_10 = "Y_COLUMN_LEGEND_10";
    private String COLUMN_LEGEND_PARAM_NAME = I18NSupport.getString("property.chart.column.legend");
    private String X_COLUMN_COLOR = "X_COLUMN_COLOR";
    private String Y_COLUMN_COLOR = "Y_COLUMN_COLOR";
    private String COLUMN_COLOR_PARAM_NAME = I18NSupport.getString("property.chart.column.color");
    private String X_COLUMN_ORIENTATION = "X_COLUMN_ORIENTATION";
    private String COLUMN_ORIENTATION_PARAM_NAME = I18NSupport.getString("property.chart.column.orientation");
    private String X_LEGEND_TEXT = "X_LEGEND_TEXT";
    private String Y_LEGEND_TEXT = "Y_LEGEND_TEXT";
    private String LEGEND_PARAM_NAME = I18NSupport.getString("property.chart.legend");
    private String X_LEGEND_FONT = "X_LEGEND_FONT";
    private String Y_LEGEND_FONT = "Y_LEGEND_FONT";
    private String X_LEGEND_COLOR = "X_LEGEND_COLOR";
    private String Y_LEGEND_COLOR = "Y_LEGEND_COLOR";
    private String X_LEGEND_ALIGNMENT = "X_LEGEND_ALIGNMENT";
    private String Y_LEGEND_ALIGNMENT = "Y_LEGEND_ALIGNMENT";    
    private String Y_DUAL_LEGEND_TEXT = "Y_DUAL_LEGEND_TEXT";       
    private String Y_DUAL_LEGEND_FONT = "Y_DUAL_LEGEND_FONT";    
    private String Y_DUAL_LEGEND_COLOR = "Y_DUAL_LEGEND_COLOR";            
    private String Y2COUNT_PARAM_NAME = I18NSupport.getString("property.chart.y2Count");
    private String Y2COUNT_LABEL = "Y2COUNT";
    private String X_PATTERN = "X_PATTERN";
    private String X_PATTERN_PARAM_NAME = I18NSupport.getString("property.pattern");
    private String GRID_COLOR_PARAM_NAME = I18NSupport.getString("property.chart.grid.color");
    private String X_GRID_COLOR = "X_GRID_COLOR";
    private String Y_GRID_COLOR = "Y_GRID_COLOR";
    private String X_SHOW_GRID = "X_SHOW_GRID";
    private String Y_SHOW_GRID = "Y_SHOW_GRID";
    private String SHOW_GRID_NAME = I18NSupport.getString("property.chart.grid.show");
    private String X_SHOW_LABEL = "X_SHOW_LABEL";
    private String Y_SHOW_LABEL = "Y_SHOW_LABEL";
    private String SHOW_LABEL_NAME = I18NSupport.getString("property.chart.column.show");
    private String SHOW_Y_VALUES_LABEL = "Y_SHOW_VALUES";
    private String SHOW_Y_VALUES_NAME = I18NSupport.getString("property.chart.values.show");
    private String STARTING_FROM_ZERO_LABEL = "STARTING_FROM_ZERO";
    private String STARTING_FROM_ZERO_NAME = I18NSupport.getString("property.chart.values.startingFromZero");
    private String SHOW_Y_DUAL_AXIS_LABEL = "Y_SHOW_DUAL_AXIS";
    private String SHOW_Y_DUAL_AXIS_NAME = I18NSupport.getString("property.chart.dualAxis.show");
    private String Y_TOOLTIP_PATTERN = "Y_TOOLTIP_PATTERN";
    private String Y_TOOLTIP_PATTERN_PARAM_NAME = I18NSupport.getString("property.pattern");

    private String CENTER = I18NSupport.getString("property.allignment.center");
    private String LEFT = I18NSupport.getString("property.allignment.left");
    private String RIGHT = I18NSupport.getString("property.allignment.right");

    private String BAR = I18NSupport.getString("new.chart.bar");
    private String BAR_COMBO = I18NSupport.getString("new.chart.bar.combo");
    private String HORIZONTAL_BAR = I18NSupport.getString("new.chart.horizontalbar");
    private String STACKED_BAR = I18NSupport.getString("new.chart.stackedbar");
    private String STACKED_BAR_COMBO = I18NSupport.getString("new.chart.stackedbar.combo");
    private String HORIZONTAL_STACKED_BAR = I18NSupport.getString("new.chart.horizontalstackedbar");
    private String PIE = I18NSupport.getString("new.chart.pie");
    private String LINE = I18NSupport.getString("new.chart.line");
    private String AREA = I18NSupport.getString("new.chart.area");
    private String BUBBLE = I18NSupport.getString("new.chart.bubble");

    private String STYLE_NORMAL = I18NSupport.getString("new.chart.style");
    private String STYLE_BAR_GLASS = I18NSupport.getString("new.chart.style.bar.glass");
    private String STYLE_BAR_CYLINDER = I18NSupport.getString("new.chart.style.bar.cylinder");
    private String STYLE_BAR_PARALLELIPIPED = I18NSupport.getString("new.chart.style.bar.parallelipiped");
    private String STYLE_BAR_DOME = I18NSupport.getString("new.chart.style.bar.dome");
    private String STYLE_LINE_DOT_ANCHOR = I18NSupport.getString("new.chart.style.line.dot.anchor");
    private String STYLE_LINE_DOT_BOW = I18NSupport.getString("new.chart.style.line.dot.bow");
    private String STYLE_LINE_DOT_STAR = I18NSupport.getString("new.chart.style.line.dot.start");
    private String STYLE_LINE_DOT_SOLID = I18NSupport.getString("new.chart.style.line.dot.solid");
    private String STYLE_LINE_DOT_HOLLOW = I18NSupport.getString("new.chart.style.line.dot.hollow");

    private String ORIENTATION_HORIZONTAL = I18NSupport.getString("new.chart.label.orientation.horizontal");
    private String ORIENTATION_VERTICAL = I18NSupport.getString("new.chart.label.orientation.vertical");
    private String ORIENTATION_DIAGONAL = I18NSupport.getString("new.chart.label.orientation.diagonal");
    private String ORIENTATION_HALF_DIAGONAL = I18NSupport.getString("new.chart.label.orientation.diagonal.half");

    private String NONE_TRANSPARENCY = I18NSupport.getString("new.chart.transparency.none");
    private String LOW_TRANSPARENCY = I18NSupport.getString("new.chart.transparency.low");
    private String AVG_TRANSPARENCY = I18NSupport.getString("new.chart.transparency.average");
    private String HIGH_TRANSPARENCY = I18NSupport.getString("new.chart.transparency.high");
    
    private String LINE_STYLE_LINE = I18NSupport.getString("property.lineStyle.line");
    private String LINE_STYLE_DOT = I18NSupport.getString("property.lineStyle.dot");
    private String LINE_STYLE_DASH = I18NSupport.getString("property.lineStyle.dash");
    
    private String X_GRID_STYLE = "X_GRID_STYLE";
    private String Y_GRID_STYLE = "Y_GRID_STYLE";

    private Property xAxisColumnProperty;
    private Property yAxisColumnProperty;
    private Property styleProperty;
    private PropertyEditorRegistry editorRegistry;

    private Chart chart;
    private List<NameType> columns = new ArrayList<NameType>();

    private static Log LOG = LogFactory.getLog(ChartPropertyPanel.class);

    public ChartPropertyPanel() {
        this(new Chart());
    }

    public ChartPropertyPanel(Chart chart) {
        super();
        setDescriptionVisible(false);
        setToolBarVisible(false);
        setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);
        editorRegistry = (PropertyEditorRegistry) getEditorFactory();
        this.chart = chart;
        List<Property> props = getFilteredProperties();
        setProperties(props.toArray(new Property[props.size()]));
    }


    @Override
    public void propertyChange(PropertyChangeEvent event) {

        Property prop = (Property) event.getSource();
        String propName = prop.getName();
        ChartTitle chartTitle = chart.getTitle();
        ChartLayoutPanel layoutPanel = Globals.getChartLayoutPanel();
        try {
            if (TITLE_TEXT.equals(propName)) {
                String propValue = (String) prop.getValue();
                chartTitle.setTitle(propValue);
                layoutPanel.setTitle(propValue);
            } else if (TITLE_FONT.equals(propName)) {
                Font propValue = (Font) prop.getValue();
                chartTitle.setFont(propValue);
                layoutPanel.setTitleFont(propValue);
            } else if (TITLE_FOREGROUND.equals(propName)) {
                Color propValue = (Color) prop.getValue();
                chartTitle.setColor(propValue);
                layoutPanel.setTitleColor(propValue);
            } else if (TITLE_ALIGNMENT.equals(propName)) {
                String propValue = (String) prop.getValue();
                byte alignment = getAlignment(propValue);
                chartTitle.setAlignment(alignment);
                layoutPanel.setTitleAlignment(alignment);
            } else if (CHART_BACKGROUND.equals(propName)) {
                Color propValue = (Color) prop.getValue();
                chart.setBackground(propValue);
                layoutPanel.setMainBackground(propValue);
            } else if (CHART_FOREGROUND.equals(propName)) {
                adjustForegroundColors(prop, 0);
            } else if (CHART_FOREGROUND_2.equals(propName)) {
                adjustForegroundColors(prop, 1);
            } else if (CHART_FOREGROUND_3.equals(propName)) {
                adjustForegroundColors(prop, 2);
            } else if (CHART_FOREGROUND_4.equals(propName)) {
                adjustForegroundColors(prop, 3);
            } else if (CHART_FOREGROUND_5.equals(propName)) {
                adjustForegroundColors(prop, 4);
            } else if (CHART_FOREGROUND_6.equals(propName)) {
                adjustForegroundColors(prop, 5);
            }  else if (CHART_FOREGROUND_7.equals(propName)) {
                adjustForegroundColors(prop, 6);
            }  else if (CHART_FOREGROUND_8.equals(propName)) {
                adjustForegroundColors(prop, 7);
            }  else if (CHART_FOREGROUND_9.equals(propName)) {
                adjustForegroundColors(prop, 8);
            }  else if (CHART_FOREGROUND_10.equals(propName)) {
                adjustForegroundColors(prop, 9);
            } else if (CHART_TYPE.equals(propName)) {
                String type = (String) prop.getValue();
                ChartType chartType = getChartType(type);
                chart.setType(chartType);
                layoutPanel.setType(chartType.getType());
                updateStyleProperty();
            } else if (CHART_STYLE.equals(propName)) {
                String style = (String) prop.getValue();
                chart.getType().setStyle(getStyle(style));
            } else if (CHART_TRANSPARENCY.equals(propName)) {
                String transparency = (String) prop.getValue();
                chart.setTransparency(getTransparency(transparency));
            } else if (STYLE_GRID_X.equals(propName)) {
                String style = (String) prop.getValue();
                chart.setStyleGridX(getGridStyle(style));   
            } else if (STYLE_GRID_Y.equals(propName)) {
                String style = (String) prop.getValue();
                chart.setStyleGridY(getGridStyle(style));        
            } else if (TOOLTIP_MESSAGE.equals(propName)) {
                String message = (String) prop.getValue();
                chart.setTooltipMessage(message);    
            } else if (X_AXIS_COLOR.equals(propName)) {
                Color color = (Color) prop.getValue();
                chart.setxAxisColor(color);                                        
            } else if (X_COLUMN_COL.equals(propName)) {
                String column = (String) prop.getValue();
                chart.setXColumn(column);
                layoutPanel.setXColumn(column);
            } else if (X_COLUMN_COLOR.equals(propName)) {
                Color color = (Color) prop.getValue();
                chart.setXColor(color);
                layoutPanel.setXColor(color);
            } else if (X_COLUMN_ORIENTATION.equals(propName)) {
                String orientation = (String) prop.getValue();
                chart.setXorientation(getXOrientation(orientation));
            } else if (X_AXIS_LABEL_FONT.equals(propName)) {
                Font font = (Font) prop.getValue();
                chart.setXLabelFont(font);
            } else if (X_LEGEND_TEXT.equals(propName)) {
                String legend = (String) prop.getValue();
                chart.getXLegend().setTitle(legend);
            } else if (X_LEGEND_FONT.equals(propName)) {
                Font font = (Font) prop.getValue();
                chart.getXLegend().setFont(font);
            } else if (X_LEGEND_COLOR.equals(propName)) {
                Color color = (Color) prop.getValue();
                chart.getXLegend().setColor(color);
            } else if (X_LEGEND_ALIGNMENT.equals(propName)) {
                String propValue = (String) prop.getValue();
                byte alignment = getAlignment(propValue);
                chart.getXLegend().setAlignment(alignment);
            } else if (X_PATTERN.equals(propName)) {
                String propValue = (String) prop.getValue();
                chart.setXPattern(propValue);
            } else if (X_GRID_COLOR.equals(propName)) {
                Color propValue = (Color) prop.getValue();
                chart.setXGridColor(propValue);
            } else if (X_SHOW_GRID.equals(propName)) {
                Boolean propValue = (Boolean) prop.getValue();
                chart.setXShowGrid(propValue);
            } else if (X_SHOW_LABEL.equals(propName)) {
                Boolean propValue = (Boolean) prop.getValue();
                chart.setXShowLabel(propValue);
            } else if (Y_AXIS_COLOR.equals(propName)) {
                Color color = (Color) prop.getValue();
                chart.setyAxisColor(color);      
            } else if (Y_COLUMN_COL.equals(propName)) {
                String column = (String) prop.getValue();
                adjustYColumns(prop, 0);
                layoutPanel.setYColumn(column);
            } else if (Y_COLUMN_COL_2.equals(propName)) {
                adjustYColumns(prop, 1);
            } else if (Y_COLUMN_COL_3.equals(propName)) {
                adjustYColumns(prop, 2);
            } else if (Y_COLUMN_COL_4.equals(propName)) {
                adjustYColumns(prop, 3);
            } else if (Y_COLUMN_COL_5.equals(propName)) {
                adjustYColumns(prop, 4);
            } else if (Y_COLUMN_COL_6.equals(propName)) {
                adjustYColumns(prop, 5);
            } else if (Y_COLUMN_COL_7.equals(propName)) {
                adjustYColumns(prop, 6);
            } else if (Y_COLUMN_COL_8.equals(propName)) {
                adjustYColumns(prop, 7);
            } else if (Y_COLUMN_COL_9.equals(propName)) {
                adjustYColumns(prop, 8);
            } else if (Y_COLUMN_COL_10.equals(propName)) {
                adjustYColumns(prop, 9);
            } else if (Y_DYNAMIC_COLUMN_QUERY.equals(propName)) {
                String query = (String) prop.getValue();
                chart.setYColumnQuery(query);                     
            } else if (Y_COLUMN_LEGEND.equals(propName)) {
                adjustYColumnsLegends(prop, 0);
            } else if (Y_COLUMN_LEGEND_2.equals(propName)) {
                adjustYColumnsLegends(prop, 1);
            } else if (Y_COLUMN_LEGEND_3.equals(propName)) {
                adjustYColumnsLegends(prop, 2);
            } else if (Y_COLUMN_LEGEND_4.equals(propName)) {
                adjustYColumnsLegends(prop, 3);
            } else if (Y_COLUMN_LEGEND_5.equals(propName)) {
                adjustYColumnsLegends(prop, 4);
            } else if (Y_COLUMN_LEGEND_6.equals(propName)) {
                adjustYColumnsLegends(prop, 5);
            } else if (Y_COLUMN_LEGEND_7.equals(propName)) {
                adjustYColumnsLegends(prop, 6);
            } else if (Y_COLUMN_LEGEND_8.equals(propName)) {
                adjustYColumnsLegends(prop, 7);
            } else if (Y_COLUMN_LEGEND_9.equals(propName)) {
                adjustYColumnsLegends(prop, 8);
            } else if (Y_COLUMN_LEGEND_10.equals(propName)) {
                adjustYColumnsLegends(prop, 9);
            } else if (Y_COLUMN_COLOR.equals(propName)) {
                Color color = (Color) prop.getValue();
                chart.setYColor(color);
                layoutPanel.setYColor(color);                
            } else if (Y_LEGEND_TEXT.equals(propName)) {
                String legend = (String) prop.getValue();
                chart.getYLegend().setTitle(legend);
            } else if (Y_LEGEND_FONT.equals(propName)) {
                Font font = (Font) prop.getValue();
                chart.getYLegend().setFont(font);
            } else if (Y_LEGEND_COLOR.equals(propName)) {
                Color color = (Color) prop.getValue();
                chart.getYLegend().setColor(color);
            } else if (Y_LEGEND_ALIGNMENT.equals(propName)) {
                String propValue = (String) prop.getValue();
                byte alignment = getAlignment(propValue);
                chart.getYLegend().setAlignment(alignment);
            } else if (Y_DUAL_LEGEND_TEXT.equals(propName)) {
                String legend = (String) prop.getValue();
                chart.getyDualLegend().setTitle(legend);
            } else if (Y_DUAL_LEGEND_FONT.equals(propName)) {
                Font font = (Font) prop.getValue();
                chart.getyDualLegend().setFont(font);
            } else if (Y_DUAL_LEGEND_COLOR.equals(propName)) {
                Color color = (Color) prop.getValue();
                chart.getyDualLegend().setColor(color);    
            } else if (Y_GRID_COLOR.equals(propName)) {
                Color propValue = (Color) prop.getValue();
                chart.setYGridColor(propValue);
            } else if (Y_SHOW_GRID.equals(propName)) {
                Boolean propValue = (Boolean) prop.getValue();
                chart.setYShowGrid(propValue);
            } else if (Y_SHOW_LABEL.equals(propName)) {
                Boolean propValue = (Boolean) prop.getValue();
                chart.setYShowLabel(propValue);
            } else if (Y_AXIS_LABEL_FONT.equals(propName)) {
                Font font = (Font) prop.getValue();
                chart.setYLabelFont(font);                    
            } else if (SHOW_Y_VALUES_LABEL.equals(propName)) {
                Boolean propValue = (Boolean) prop.getValue();
                chart.setShowYValuesOnChart(propValue);
            } else if (STARTING_FROM_ZERO_LABEL.equals(propName)) {
                Boolean propValue = (Boolean) prop.getValue();
                chart.setStartingFromZero(propValue);    
            } else if (SHOW_Y_DUAL_AXIS_LABEL.equals(propName)) {
                Boolean propValue = (Boolean) prop.getValue();
                chart.setShowDualAxis(propValue);    
            } else if (Y2COUNT_LABEL.equals(propName)) {
                Integer propValue = (Integer) prop.getValue();
                chart.setY2SeriesCount(propValue);        
            } else if (Y_TOOLTIP_PATTERN.equals(propName)) {
                String propValue = (String) prop.getValue();
                chart.setYTooltipPattern(propValue);
            } else if (MAIN_FONT.equals(propName)) {
                Font propValue = (Font) prop.getValue();
                chart.setFont(propValue);                
            } 
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void adjustForegroundColors(Property prop, int index) {
        Color propValue = (Color) prop.getValue();
        List<Color> color = chart.getForegrounds();        
        if (color.size() > index) {
            color.set(index, propValue);
        } else {
            color.add(index, propValue);
        }
        chart.setForegrounds(color);
    }

    private void adjustYColumns(Property prop, int index) {
        String propValue = (String) prop.getValue();
        List<String> columns = chart.getYColumns();
        if (columns.size() > index) {
            columns.set(index, propValue);
        } else if (index > columns.size()) {
            prop.setValue(null);
            return;
        } else {
            columns.add(index, propValue);
        }

        // if already a numeric function is selected we do not allow for non-numeric columns
        // for first column (index == 0) the function is modified to COUNT
        if (index > 0) {
            if (Globals.getChartLayoutPanel().getMarked(propValue) &&
                    !FunctionFactory.isCountFunction(chart.getYFunction())) {
                prop.setValue(null);
                Show.info(I18NSupport.getString("chart.undefined.ycolumn.type"));
                return;
            }
        }

        chart.setYColumns(columns);
    }

    private void adjustYColumnsLegends(Property prop, int index) {
        String propValue = (String) prop.getValue();
        List<String> legends = chart.getYColumnsLegends();
        if (legends.size() > index) {
            legends.set(index, propValue);
        } else if (index > legends.size()) {
            prop.setValue(null);
            return;
        } else {
            legends.add(index, propValue);
        }
        chart.setYColumnsLegends(legends);
    }


    public void selectProperties(int category) {
        List<Property> props = getFilteredProperties(category);
        setProperties(props.toArray(new Property[props.size()]));
    }

    public void refresh() {
        setProperties(new Property[0]);
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
        List<Property> props = getFilteredProperties();
        setProperties(props.toArray(new Property[props.size()]));
    }

    private Property getTitleTextProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(TITLE_TEXT);
        textProp.setDisplayName(TITLE_TEXT_PARAM_NAME);
        textProp.setType(String.class);
        textProp.setValue(chart.getTitle().getTitle());
        textProp.setCategory(I18NSupport.getString("property.category.chart.title"));
        return textProp;
    }       
    
    private Property getYDynamicColumnQueryProperty() {
        DefaultProperty queryProp = new DefaultProperty();
        queryProp.setName(Y_DYNAMIC_COLUMN_QUERY);
        queryProp.setDisplayName(Y_DYNAMIC_COL_PARAM_NAME);
        queryProp.setType(String.class);
        queryProp.setValue(chart.getYColumnQuery());
        queryProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        
        SqlPropertyEditor sqlEditor = new SqlPropertyEditor();
        editorRegistry.registerEditor(queryProp, sqlEditor);
        return queryProp;
    }

    private Property getTitleFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(TITLE_FONT);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(chart.getTitle().getFont());
        fontProp.setCategory(I18NSupport.getString("property.category.chart.title"));
        return fontProp;
    }

    private Property getTitleForegroundProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(TITLE_FOREGROUND);
        foregroundProp.setDisplayName(TITLE_FOREGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        foregroundProp.setValue(chart.getTitle().getColor());                
        foregroundProp.setCategory(I18NSupport.getString("property.category.chart.title"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }

    private Property getTitleAlignmentProperty() {
        DefaultProperty alignmentProp = new DefaultProperty();
        alignmentProp.setName(TITLE_ALIGNMENT);
        alignmentProp.setDisplayName(ALIGNMENT_PARAM_NAME);
        alignmentProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[]{LEFT, CENTER, RIGHT});
        byte alignment = chart.getTitle().getAlignment();
        setAlignment(alignment, alignmentProp);
        alignmentProp.setCategory(I18NSupport.getString("property.category.chart.title"));
        editorRegistry.registerEditor(alignmentProp, alignmentEditor);
        return alignmentProp;
    }

    private Property getBackgroundProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(CHART_BACKGROUND);
        foregroundProp.setDisplayName(BACKGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        foregroundProp.setValue(chart.getBackground());
        foregroundProp.setCategory(I18NSupport.getString("property.category.chart.main"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }

    private Property getForegroundProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(CHART_FOREGROUND);
        foregroundProp.setDisplayName(FOREGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        foregroundProp.setValue(chart.getForegrounds().get(0));
        foregroundProp.setCategory(I18NSupport.getString("property.category.chart.main"));
        for (int i = 2; i <= Chart.COLORS.length; i++) {
            foregroundProp.addSubProperty(getForegroundProperty(i));
        }
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }

    private Property getForegroundProperty(int index) {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(CHART_FOREGROUND + "_" + index);
        foregroundProp.setDisplayName(FOREGROUND_PARAM_NAME + " " + index);
        foregroundProp.setType(Color.class);
        Color color = (chart.getForegrounds().size() > index - 1) ? chart.getForegrounds().get(index - 1) : Chart.COLORS[index - 1];
        foregroundProp.setValue(color);
        foregroundProp.setCategory(I18NSupport.getString("property.category.chart.main"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }
    
    private Property getTooltipMessageProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(TOOLTIP_MESSAGE);
        textProp.setDisplayName(TOOLTIP_MESSAGE_PARAM_NAME);
        textProp.setType(String.class);
        textProp.setValue(chart.getTooltipMessage());
        textProp.setCategory(I18NSupport.getString("property.category.chart.main"));
        return textProp;
    }

    private Property getTypeProperty() {
        DefaultProperty typeProp = new DefaultProperty();
        typeProp.setName(CHART_TYPE);
        typeProp.setDisplayName(TYPE_PARAM_NAME);
        typeProp.setType(String.class);
        ComboBoxPropertyEditor typeEditor = new ComboBoxPropertyEditor();
        typeEditor.setAvailableValues(new String[]{BAR, BAR_COMBO, HORIZONTAL_BAR, STACKED_BAR, STACKED_BAR_COMBO, HORIZONTAL_STACKED_BAR, PIE, LINE, AREA, BUBBLE});
        typeEditor.setAvailableIcons(new Icon[]{
                ImageUtil.getImageIcon("chart_bar"),
                ImageUtil.getImageIcon("chart_bar_combo"),
                ImageUtil.getImageIcon("chart_horizontal_bar"),
                ImageUtil.getImageIcon("chart_stacked_bar"),
                ImageUtil.getImageIcon("chart_stacked_bar_combo"),
                ImageUtil.getImageIcon("chart_horizontal_stacked_bar"),
                ImageUtil.getImageIcon("chart_pie"),
                ImageUtil.getImageIcon("chart_line"),
                ImageUtil.getImageIcon("chart_area"),
                ImageUtil.getImageIcon("chart_bubble")}); 
        JComboBox cb = (JComboBox)typeEditor.getCustomEditor();
        cb.setMaximumRowCount(10);
        ChartType chartType = chart.getType();
        byte type = ChartType.NONE;
        if (chartType != null) {
            type = chartType.getType();
        }
        setChartType(type, typeProp);
        typeProp.setCategory(I18NSupport.getString("property.category.main"));

        editorRegistry.registerEditor(typeProp, typeEditor);

        return typeProp;
    }

    private Property getStyleProperty() {
        DefaultProperty styleProp = new DefaultProperty();
        styleProp.setName(CHART_STYLE);
        styleProp.setDisplayName(STYLE_PARAM_NAME);
        styleProp.setType(String.class);
        ComboBoxPropertyEditor styleEditor = new ComboBoxPropertyEditor();
        String[] availableValues = new String[]{STYLE_NORMAL};
        Object type = getTypeProperty().getValue();
        if (BAR.equals(type) || STACKED_BAR.equals(type) || HORIZONTAL_STACKED_BAR.equals(type)
        		|| BAR_COMBO.equals(type) || STACKED_BAR_COMBO.equals(type)) {
            availableValues = new String[]{STYLE_NORMAL, STYLE_BAR_GLASS, STYLE_BAR_CYLINDER,
                    STYLE_BAR_PARALLELIPIPED, STYLE_BAR_DOME};
        } else if (LINE.equals(type)) {
            availableValues = new String[]{STYLE_NORMAL, STYLE_LINE_DOT_SOLID, STYLE_LINE_DOT_HOLLOW,
                    STYLE_LINE_DOT_ANCHOR, STYLE_LINE_DOT_BOW, STYLE_LINE_DOT_STAR};
        }
        styleEditor.setAvailableValues(availableValues);

        ChartType chartType = chart.getType();
        setStyle(chartType.getStyle(), styleProp);
        styleProp.setCategory(I18NSupport.getString("property.category.main"));

        editorRegistry.registerEditor(styleProp, styleEditor);

        return styleProp;
    }

    private Property getTransparencyProperty() {
        DefaultProperty transparencyProp = new DefaultProperty();
        transparencyProp.setName(CHART_TRANSPARENCY);
        transparencyProp.setDisplayName(TRANSPARENCY_PARAM_NAME);
        transparencyProp.setType(String.class);
        ComboBoxPropertyEditor transparencyEditor = new ComboBoxPropertyEditor();
        String[] availableValues = new String[]{NONE_TRANSPARENCY, LOW_TRANSPARENCY, AVG_TRANSPARENCY, HIGH_TRANSPARENCY};
        transparencyEditor.setAvailableValues(availableValues);
        setTransparency(chart.getTransparency(), transparencyProp);
        transparencyProp.setCategory(I18NSupport.getString("property.category.main"));
        editorRegistry.registerEditor(transparencyProp, transparencyEditor);
        return transparencyProp;
    }
    
    private Property getStyleGridXProperty() {
        DefaultProperty styleProp = new DefaultProperty();
        styleProp.setName(STYLE_GRID_X);
        styleProp.setDisplayName(STYLE_GRID_PARAM_NAME);
        styleProp.setType(String.class);
        ComboBoxPropertyEditor styleEditor = new ComboBoxPropertyEditor();
        String[] availableValues = new String[]{LINE_STYLE_LINE, LINE_STYLE_DOT, LINE_STYLE_DASH};
        styleEditor.setAvailableValues(availableValues);
        setGridStyle(chart.getStyleGridX(), styleProp);
        styleProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        editorRegistry.registerEditor(styleProp, styleEditor);
        return styleProp;
    }
    
    private Property getStyleGridYProperty() {
        DefaultProperty styleProp = new DefaultProperty();
        styleProp.setName(STYLE_GRID_Y);
        styleProp.setDisplayName(STYLE_GRID_PARAM_NAME);
        styleProp.setType(String.class);
        ComboBoxPropertyEditor styleEditor = new ComboBoxPropertyEditor();
        String[] availableValues = new String[]{LINE_STYLE_LINE, LINE_STYLE_DOT, LINE_STYLE_DASH};
        styleEditor.setAvailableValues(availableValues);
        setGridStyle(chart.getStyleGridY(), styleProp);
        styleProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        editorRegistry.registerEditor(styleProp, styleEditor);
        return styleProp;
    }
    
    private Property getMainFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(MAIN_FONT);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(chart.getFont());
        fontProp.setCategory(I18NSupport.getString("property.category.chart.main"));
        return fontProp;
    }

    private Property getXAxisColumnProperty(List<NameType> columns) {
        DefaultProperty columnProp = new DefaultProperty();
        columnProp.setName(X_COLUMN_COL);
        columnProp.setDisplayName(COLUMN_COL_PARAM_NAME);
        columnProp.setType(String.class);
        ComboBoxPropertyEditor columnEditor = new ComboBoxPropertyEditor();
        List<String> names = new ArrayList<String>();
        for (NameType nt : columns) {
            names.add(nt.getName());
        }
        columnEditor.setAvailableValues(names.toArray(new String[names.size()]));
        String column = chart.getXColumn();
        columnProp.setValue(column);
        columnProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        editorRegistry.registerEditor(columnProp, columnEditor);
        return columnProp;
    }

    private Property getXAxisLabelColorProperty() {
        DefaultProperty colorProp = new DefaultProperty();
        colorProp.setName(X_COLUMN_COLOR);
        colorProp.setDisplayName(COLUMN_COLOR_PARAM_NAME);
        colorProp.setType(Color.class);
        colorProp.setValue(chart.getXColor());
        colorProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(colorProp, colorEditor);
        
        return colorProp;
    }
    
    private Property getXAxisColorProperty() {
        DefaultProperty colorProp = new DefaultProperty();
        colorProp.setName(X_AXIS_COLOR);
        colorProp.setDisplayName(FOREGROUND_PARAM_NAME);
        colorProp.setType(Color.class);
        colorProp.setValue(chart.getxAxisColor());
        colorProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(colorProp, colorEditor);
        
        return colorProp;
    }
        
    private Property getXAxisOrientationProperty() {
        DefaultProperty orientationProp = new DefaultProperty();
        orientationProp.setName(X_COLUMN_ORIENTATION);
        orientationProp.setDisplayName(COLUMN_ORIENTATION_PARAM_NAME);
        orientationProp.setType(String.class);
        ComboBoxPropertyEditor orientationEditor = new ComboBoxPropertyEditor();
        orientationEditor.setAvailableValues(new String[]{ORIENTATION_HORIZONTAL, ORIENTATION_VERTICAL,
                ORIENTATION_DIAGONAL, ORIENTATION_HALF_DIAGONAL});

        byte xOrientation = chart.getXorientation();
        setXOrientation(xOrientation, orientationProp);
        orientationProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));

        editorRegistry.registerEditor(orientationProp, orientationEditor);

        return orientationProp;
    }

    private Property getXPatternProperty() {
        DefaultProperty patternProp = new DefaultProperty();
        patternProp.setName(X_PATTERN);
        patternProp.setDisplayName(X_PATTERN_PARAM_NAME);
        patternProp.setType(String.class);
        patternProp.setValue(chart.getXPattern());
        patternProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        FieldPatternPropertyEditor patternEditor = new FieldPatternPropertyEditor();
        editorRegistry.registerEditor(patternProp, patternEditor);
        return patternProp;
    }
    
    private Property getXLabelFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(X_AXIS_LABEL_FONT);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(chart.getXLabelFont());
        fontProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        return fontProp;
    }
    
    private Property getYAxisColorProperty() {
        DefaultProperty colorProp = new DefaultProperty();
        colorProp.setName(Y_AXIS_COLOR);
        colorProp.setDisplayName(FOREGROUND_PARAM_NAME);
        colorProp.setType(Color.class);
        colorProp.setValue(chart.getyAxisColor());
        colorProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(colorProp, colorEditor);
        
        return colorProp;
    }


    private Property getYAxisColumnProperty(List<NameType> columns) {
        DefaultProperty columnProp = new DefaultProperty();
        columnProp.setName(Y_COLUMN_COL);
        columnProp.setDisplayName(COLUMN_COL_PARAM_NAME);
        columnProp.setType(String.class);
        ComboBoxPropertyEditor columnEditor = new ComboBoxPropertyEditor();
        JComboBox combo = (JComboBox) columnEditor.getCustomEditor();
        combo.setRenderer(new ChartColumnListCellRenderer(columns));
        List<String> names = new ArrayList<String>();
        for (NameType nt : columns) {
            names.add(nt.getName());
        }
        columnEditor.setAvailableValues(names.toArray(new String[names.size()]));
        String column = null;
        if ((chart.getYColumns() != null) && (chart.getYColumns().size() > 0)) {
            column = chart.getYColumns().get(0);
        }
        columnProp.setValue(column);
        columnProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        editorRegistry.registerEditor(columnProp, columnEditor);
        for (int i = 2; i <= 10; i++) {
            columnProp.addSubProperty(getYAxisColumnProperty(getListWithSelect(columns), i));
        }
        return columnProp;
    }

    private Property getYAxisColumnProperty(List<NameType> columns, int index) {
        DefaultProperty columnProp = new DefaultProperty();
        columnProp.setName(Y_COLUMN_COL + "_" + index);
        columnProp.setDisplayName(COLUMN_COL_PARAM_NAME + " " + index);
        columnProp.setType(String.class);
        ComboBoxPropertyEditor columnEditor = new ComboBoxPropertyEditor();
        JComboBox combo = (JComboBox) columnEditor.getCustomEditor();
        combo.setRenderer(new ChartColumnListCellRenderer(columns));
        List<String> names = new ArrayList<String>();
        for (NameType nt : columns) {
            names.add(nt.getName());
        }
        columnEditor.setAvailableValues(names.toArray(new String[names.size()]));
        String column = (chart.getYColumns().size() > index - 1) ? chart.getYColumns().get(index - 1) : null;
        columnProp.setValue(column);
        columnProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        editorRegistry.registerEditor(columnProp, columnEditor);
        return columnProp;
    }


    private Property getYAxisLabelColorProperty() {
        DefaultProperty colorProp = new DefaultProperty();
        colorProp.setName(Y_COLUMN_COLOR);
        colorProp.setDisplayName(COLUMN_COLOR_PARAM_NAME);
        colorProp.setType(Color.class);
        colorProp.setValue(chart.getYColor());
        colorProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(colorProp, colorEditor);
        
        return colorProp;
    }
    
    private Property getYLabelFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(Y_AXIS_LABEL_FONT);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(chart.getYLabelFont());
        fontProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        return fontProp;
    }

    private Property getXLegendProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(X_LEGEND_TEXT);
        textProp.setDisplayName(LEGEND_PARAM_NAME);
        textProp.setType(String.class);
        textProp.setValue(chart.getXLegend().getTitle());
        textProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        textProp.addSubProperty(getXLegendFontProperty());
        textProp.addSubProperty(getXLegendColorProperty());
        //textProp.addSubProperty(getXLegendAlignmentProperty());
        return textProp;
    }

    private Property getXLegendFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(X_LEGEND_FONT);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(chart.getXLegend().getFont());
        fontProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        return fontProp;
    }

    private Property getXLegendColorProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(X_LEGEND_COLOR);
        foregroundProp.setDisplayName(FOREGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        foregroundProp.setValue(chart.getXLegend().getColor());
        foregroundProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }

    private Property getXLegendAlignmentProperty() {
        DefaultProperty alignmentProp = new DefaultProperty();
        alignmentProp.setName(X_LEGEND_ALIGNMENT);
        alignmentProp.setDisplayName(ALIGNMENT_PARAM_NAME);
        alignmentProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[]{LEFT, CENTER, RIGHT});
        byte alignment = chart.getXLegend().getAlignment();
        setAlignment(alignment, alignmentProp);
        alignmentProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        editorRegistry.registerEditor(alignmentProp, alignmentEditor);
        return alignmentProp;
    }

    private Property getXGridColorProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(X_GRID_COLOR);
        textProp.setDisplayName(GRID_COLOR_PARAM_NAME);
        textProp.setType(Color.class);
        textProp.setValue(chart.getXGridColor());
        textProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(textProp, colorEditor);
        
        return textProp;
    }

    private Property getXShowGridProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(X_SHOW_GRID);
        showProp.setDisplayName(SHOW_GRID_NAME);
        showProp.setType(Boolean.class);
        showProp.setValue(chart.getXShowGrid());
        showProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        showProp.addSubProperty(getXGridColorProperty());
        showProp.addSubProperty(getStyleGridXProperty());
        return showProp;
    }

    private Property getXShowLabelProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(X_SHOW_LABEL);
        showProp.setDisplayName(SHOW_LABEL_NAME);
        showProp.setType(Boolean.class);
        showProp.setValue(chart.getXShowLabel());
        showProp.setCategory(I18NSupport.getString("property.category.chart.xcolumn"));
        showProp.addSubProperty(getXLabelFontProperty());
        showProp.addSubProperty(getXAxisLabelColorProperty());
        showProp.addSubProperty(getXAxisOrientationProperty());
        showProp.addSubProperty(getXPatternProperty());
        return showProp;
    }
    
    private Property getYShowValuesProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(SHOW_Y_VALUES_LABEL);
        showProp.setDisplayName(SHOW_Y_VALUES_NAME);
        showProp.setType(Boolean.class);
        showProp.setValue(chart.getShowYValuesOnChart());
        showProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));        
        return showProp;
    }
    
    private Property getYStartingFromZeroProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(STARTING_FROM_ZERO_LABEL);
        showProp.setDisplayName(STARTING_FROM_ZERO_NAME);
        showProp.setType(Boolean.class);
        showProp.setValue(chart.getStartingFromZero());
        showProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));        
        return showProp;
    }
    
    private Property getShowDualAxisProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(SHOW_Y_DUAL_AXIS_LABEL);
        showProp.setDisplayName(SHOW_Y_DUAL_AXIS_NAME);
        showProp.setType(Boolean.class);
        showProp.setValue(chart.getShowDualAxis());
        showProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));    
        showProp.addSubProperty(getYDualLegendProperty());
        showProp.addSubProperty(getY2SeriesCountProperty());
        return showProp;
    }
    
    private Property getY2SeriesCountProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(Y2COUNT_LABEL);
        showProp.setDisplayName(Y2COUNT_PARAM_NAME);
        showProp.setType(Integer.class);
        showProp.setValue(chart.getY2SeriesCount());
        showProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));            
        return showProp;
    }
    
    private Property getYTooltipPatternProperty() {
        DefaultProperty patternProp = new DefaultProperty();
        patternProp.setName(Y_TOOLTIP_PATTERN);
        patternProp.setDisplayName(Y_TOOLTIP_PATTERN_PARAM_NAME);
        patternProp.setType(String.class);
        patternProp.setValue(chart.getYTooltipPattern());
        patternProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        FieldPatternPropertyEditor patternEditor = new FieldPatternPropertyEditor();
        editorRegistry.registerEditor(patternProp, patternEditor);
        return patternProp;
    }

    private Property getYLegendProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(Y_LEGEND_TEXT);
        textProp.setDisplayName(LEGEND_PARAM_NAME);
        textProp.setType(String.class);
        textProp.setValue(chart.getYLegend().getTitle());
        textProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        textProp.addSubProperty(getYLegendFontProperty());
        textProp.addSubProperty(getYLegendColorProperty());
        //textProp.addSubProperty(getYLegendAlignmentProperty());
        return textProp;
    }

    private Property getYLegendFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(Y_LEGEND_FONT);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(chart.getYLegend().getFont());
        fontProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        return fontProp;
    }

    private Property getYLegendColorProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(Y_LEGEND_COLOR);
        foregroundProp.setDisplayName(FOREGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        foregroundProp.setValue(chart.getYLegend().getColor());
        foregroundProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }

    private Property getYLegendAlignmentProperty() {
        DefaultProperty alignmentProp = new DefaultProperty();
        alignmentProp.setName(Y_LEGEND_ALIGNMENT);
        alignmentProp.setDisplayName(ALIGNMENT_PARAM_NAME);
        alignmentProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[]{LEFT, CENTER, RIGHT});
        byte alignment = chart.getYLegend().getAlignment();
        setAlignment(alignment, alignmentProp);
        alignmentProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        editorRegistry.registerEditor(alignmentProp, alignmentEditor);
        return alignmentProp;
    }
    
    private Property getYDualLegendProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(Y_DUAL_LEGEND_TEXT);
        textProp.setDisplayName(LEGEND_PARAM_NAME);
        textProp.setType(String.class);
        String title = "";
        if (chart.getyDualLegend() != null) {
        	title = chart.getyDualLegend().getTitle();
        }
        textProp.setValue(title);
        textProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        textProp.addSubProperty(getYDualLegendFontProperty());
        textProp.addSubProperty(getYDualLegendColorProperty());       
        return textProp;
    }

    private Property getYDualLegendFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(Y_DUAL_LEGEND_FONT);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        if (chart.getyDualLegend() != null) {
        	fontProp.setValue(chart.getyDualLegend().getFont());
        }
        fontProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        return fontProp;
    }

    private Property getYDualLegendColorProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(Y_DUAL_LEGEND_COLOR);
        foregroundProp.setDisplayName(FOREGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        if (chart.getyDualLegend() != null) {
        	foregroundProp.setValue(chart.getyDualLegend().getColor());
        }
        foregroundProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        
        return foregroundProp;
    }

    private Property getYGridColorProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(Y_GRID_COLOR);
        textProp.setDisplayName(GRID_COLOR_PARAM_NAME);
        textProp.setType(Color.class);
        textProp.setValue(chart.getYGridColor());
        textProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(textProp, colorEditor);
        
        return textProp;
    }

    private Property getYShowGridProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(Y_SHOW_GRID);
        showProp.setDisplayName(SHOW_GRID_NAME);
        showProp.setType(Boolean.class);
        showProp.setValue(chart.getYShowGrid());
        showProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        showProp.addSubProperty(getYGridColorProperty());
        showProp.addSubProperty(getStyleGridYProperty());
        return showProp;
    }

    private Property getYShowLabelProperty() {
        DefaultProperty showProp = new DefaultProperty();
        showProp.setName(Y_SHOW_LABEL);
        showProp.setDisplayName(SHOW_LABEL_NAME);
        showProp.setType(Boolean.class);
        showProp.setValue(chart.getYShowLabel());
        showProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        showProp.addSubProperty(getYLabelFontProperty());
        showProp.addSubProperty(getYAxisLabelColorProperty());
        return showProp;
    }

    private Property getYColumnLegendProperty() {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(Y_COLUMN_LEGEND);
        textProp.setDisplayName(COLUMN_LEGEND_PARAM_NAME);
        textProp.setType(String.class);

        String legend = null;
        if ((chart.getYColumnsLegends() != null) && (chart.getYColumnsLegends().size() > 0)) {
            legend = chart.getYColumnsLegends().get(0);
        }
        textProp.setValue(legend);
        textProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        for (int i = 2; i <= 10; i++) {
            textProp.addSubProperty(getYColumnLegendProperty(i));
        }
        return textProp;
    }

    private Property getYColumnLegendProperty(int index) {
        DefaultProperty textProp = new DefaultProperty();
        textProp.setName(Y_COLUMN_LEGEND + "_" + index);
        textProp.setDisplayName(COLUMN_LEGEND_PARAM_NAME + " " + index);
        textProp.setType(String.class);
        String legend = (chart.getYColumnsLegends().size() > index - 1) ? chart.getYColumnsLegends().get(index - 1) : null;
        textProp.setValue(legend);
        textProp.setCategory(I18NSupport.getString("property.category.chart.ycolumn"));
        return textProp;
    }        

    private List<Property> getFilteredProperties(int category) {
        List<Property> props = new ArrayList<Property>();
        if ((category & TITLE_CATEGORY) == TITLE_CATEGORY) {
            props.add(getTitleTextProperty());
            props.add(getTitleFontProperty());
            props.add(getTitleForegroundProperty());
            props.add(getTitleAlignmentProperty());
        }

        if ((category & MAIN_CATEGORY) == MAIN_CATEGORY) {
            props.add(getTypeProperty());
            props.add(getBackgroundProperty());
            props.add(getForegroundProperty());
            props.add(styleProperty = getStyleProperty());
            props.add(getTooltipMessageProperty());
            props.add(getTransparencyProperty());
            props.add(getMainFontProperty());            
        }

        if ((category & XCOLUMN_CATEGORY) == XCOLUMN_CATEGORY) {
            props.add(xAxisColumnProperty = getXAxisColumnProperty(columns));
            props.add(getXShowLabelProperty());
            props.add(getXShowGridProperty());
            props.add(getXLegendProperty());
            props.add(getXAxisColorProperty());
        }

        if ((category & YCOLUMN_CATEGORY) == YCOLUMN_CATEGORY) {
            props.add(yAxisColumnProperty = getYAxisColumnProperty(columns));
            props.add(getYColumnLegendProperty());
            props.add(getYDynamicColumnQueryProperty());
            props.add(getYShowLabelProperty());
            props.add(getYShowGridProperty());
            props.add(getYLegendProperty());
            props.add(getYTooltipPatternProperty());
            props.add(getYShowValuesProperty());
            props.add(getYAxisColorProperty());
            props.add(getShowDualAxisProperty());   
            props.add(getYStartingFromZeroProperty());
        }

        return props;
    }

    private List<Property> getFilteredProperties() {
        return getFilteredProperties(TITLE_CATEGORY | MAIN_CATEGORY | XCOLUMN_CATEGORY | YCOLUMN_CATEGORY);
    }

    private ChartType getChartType(String type) {
        if (BAR.equals(type)) {
            return new ChartType(ChartType.BAR);
        } else if (BAR_COMBO.equals(type)) {
            return new ChartType(ChartType.BAR_COMBO);    
        } else if (HORIZONTAL_BAR.equals(type)) {
            return new ChartType(ChartType.HORIZONTAL_BAR);
        } else if (STACKED_BAR.equals(type)) {
            return new ChartType(ChartType.STACKED_BAR);
        } else if (STACKED_BAR_COMBO.equals(type)) {
            return new ChartType(ChartType.STACKED_BAR_COMBO);    
        } else if (HORIZONTAL_STACKED_BAR.equals(type)) {
            return new ChartType(ChartType.HORIZONTAL_STACKED_BAR);
        } else if (PIE.equals(type)) {
            return new ChartType(ChartType.PIE);
        } else if (LINE.equals(type)) {
            return new ChartType(ChartType.LINE);
        }  else if (AREA.equals(type)) {
            return new ChartType(ChartType.AREA);
        }  else if (BUBBLE.equals(type)) {
            return new ChartType(ChartType.BUBBLE);    
        } else {
            return new ChartType(ChartType.NONE);
        }
    }

    private void setChartType(byte type, Property typeProp) {
        String typeS;
        switch (type) {
            case ChartType.BAR:
                typeS = BAR;
                break;
            case ChartType.BAR_COMBO:
                typeS = BAR_COMBO;
                break;    
            case ChartType.HORIZONTAL_BAR:
                typeS = HORIZONTAL_BAR;
                break;
            case ChartType.STACKED_BAR:
                typeS = STACKED_BAR;
                break;
            case ChartType.STACKED_BAR_COMBO:
                typeS = STACKED_BAR_COMBO;
                break;    
            case ChartType.HORIZONTAL_STACKED_BAR:
                typeS = HORIZONTAL_STACKED_BAR;
                break;    
            case ChartType.PIE:
                typeS = PIE;
                break;
            case ChartType.LINE:
                typeS = LINE;
                break;
            case ChartType.AREA:
                typeS = AREA;
                break;
            case ChartType.BUBBLE:
                typeS = BUBBLE;
                break;    
            default:
                typeS = null;
                break;
        }
        typeProp.setValue(typeS);
    }

    private byte getStyle(String style) {
        if (STYLE_BAR_GLASS.equals(style)) {
            return ChartType.STYLE_BAR_GLASS;
        } else if (STYLE_BAR_CYLINDER.equals(style)) {
            return ChartType.STYLE_BAR_CYLINDER;
        } else if (STYLE_BAR_PARALLELIPIPED.equals(style)) {
            return ChartType.STYLE_BAR_PARALLELIPIPED;
        } else if (STYLE_BAR_DOME.equals(style)) {
            return ChartType.STYLE_BAR_DOME;
        } else if (STYLE_LINE_DOT_SOLID.equals(style)) {
            return ChartType.STYLE_LINE_DOT_SOLID;
        } else if (STYLE_LINE_DOT_HOLLOW.equals(style)) {
            return ChartType.STYLE_LINE_DOT_HOLLOW;
        } else if (STYLE_LINE_DOT_ANCHOR.equals(style)) {
            return ChartType.STYLE_LINE_DOT_ANCHOR;
        } else if (STYLE_LINE_DOT_BOW.equals(style)) {
            return ChartType.STYLE_LINE_DOT_BOW;
        } else if (STYLE_LINE_DOT_STAR.equals(style)) {
            return ChartType.STYLE_LINE_DOT_STAR;
        } else {
            return ChartType.STYLE_NORMAL;
        }
    }

    private void setStyle(byte style, Property styleProp) {
        String styleS;
        switch (style) {
            case ChartType.STYLE_BAR_GLASS:
                styleS = STYLE_BAR_GLASS;
                break;
            case ChartType.STYLE_BAR_CYLINDER:
                styleS = STYLE_BAR_CYLINDER;
                break;
            case ChartType.STYLE_BAR_PARALLELIPIPED:
                styleS = STYLE_BAR_PARALLELIPIPED;
                break;
            case ChartType.STYLE_BAR_DOME:
                styleS = STYLE_BAR_DOME;
                break;
            case ChartType.STYLE_LINE_DOT_SOLID:
                styleS = STYLE_LINE_DOT_SOLID;
                break;
            case ChartType.STYLE_LINE_DOT_HOLLOW:
                styleS = STYLE_LINE_DOT_HOLLOW;
                break;
            case ChartType.STYLE_LINE_DOT_ANCHOR:
                styleS = STYLE_LINE_DOT_ANCHOR;
                break;
            case ChartType.STYLE_LINE_DOT_BOW:
                styleS = STYLE_LINE_DOT_BOW;
                break;
            case ChartType.STYLE_LINE_DOT_STAR:
                styleS = STYLE_LINE_DOT_STAR;
                break;
            default:
                styleS = STYLE_NORMAL;
                break;
        }
        styleProp.setValue(styleS);
    }

    private void updateStyleProperty() {
        List<Property> props = getFilteredProperties(MAIN_CATEGORY);
        int index = props.indexOf(styleProperty);
        props.remove(styleProperty);
        props.add(index, styleProperty = getStyleProperty());
        setProperties(props.toArray(new Property[props.size()]));
    }

    public void updateXColumnProperty(String column) {
        List<Property> props = getFilteredProperties(XCOLUMN_CATEGORY);
        int index = props.indexOf(xAxisColumnProperty);
        props.remove(xAxisColumnProperty);
        props.add(index, xAxisColumnProperty = getXAxisColumnProperty(columns));
        setProperties(props.toArray(new Property[props.size()]));
    }

    public void updateYColumnProperty(String column) {
        List<Property> props = getFilteredProperties(YCOLUMN_CATEGORY);
        int index = props.indexOf(yAxisColumnProperty);
        props.remove(yAxisColumnProperty);
        props.add(index, yAxisColumnProperty = getYAxisColumnProperty(columns));
        setProperties(props.toArray(new Property[props.size()]));
    }

    private byte getXOrientation(String orientation) {
        if (ORIENTATION_DIAGONAL.equals(orientation)) {
            return Chart.DIAGONAL;
        } else if (ORIENTATION_HALF_DIAGONAL.equals(orientation)) {
            return Chart.HALF_DIAGONAL;
        } else if (ORIENTATION_VERTICAL.equals(orientation)) {
            return Chart.VERTICAL;
        } else {
            return Chart.HORIZONTAL;
        }
    }

    private void setXOrientation(byte orientation, Property styleProp) {
        String orientationS;
        switch (orientation) {
            case Chart.VERTICAL:
                orientationS = ORIENTATION_VERTICAL;
                break;
            case Chart.DIAGONAL:
                orientationS = ORIENTATION_DIAGONAL;
                break;
            case Chart.HALF_DIAGONAL:
                orientationS = ORIENTATION_HALF_DIAGONAL;
                break;
            default:
                orientationS = ORIENTATION_HORIZONTAL;
                break;
        }
        styleProp.setValue(orientationS);
    }

    private byte getTransparency(String transparency) {
        if (LOW_TRANSPARENCY.equals(transparency)) {
            return Chart.LOW_TRANSPARENCY;
        } else if (AVG_TRANSPARENCY.equals(transparency)) {
            return Chart.AVG_TRANSPARENCY;
        } else if (HIGH_TRANSPARENCY.equals(transparency)) {
            return Chart.HIGH_TRANSPARENCY;
        } else {
            return Chart.NONE_TRANSPARENCY;
        }
    }

    private void setTransparency(byte transparency, Property transparencyProp) {
        String transparencyS;
        switch (transparency) {
            case Chart.LOW_TRANSPARENCY:
                transparencyS = LOW_TRANSPARENCY;
                break;
            case Chart.AVG_TRANSPARENCY:
                transparencyS = AVG_TRANSPARENCY;
                break;
            case Chart.HIGH_TRANSPARENCY:
                transparencyS = HIGH_TRANSPARENCY;
                break;
            default:
                transparencyS = NONE_TRANSPARENCY;
                break;
        }
        transparencyProp.setValue(transparencyS);
    }
    
    private byte getGridStyle(String style) {
        if (LINE_STYLE_DOT.equals(style)) {
            return Chart.LINE_STYLE_DOT;
        } else if (LINE_STYLE_DASH.equals(style)) {
            return Chart.LINE_STYLE_DASH;        
        } else {
            return Chart.LINE_STYLE_LINE;
        }
    }

    private void setGridStyle(byte style, Property styleProp) {
        String styleS;
        switch (style) {
            case Chart.LINE_STYLE_DOT:
                styleS = LINE_STYLE_DOT;
                break;
            case Chart.LINE_STYLE_DASH:
                styleS = LINE_STYLE_DASH;
                break;
            case Chart.LINE_STYLE_LINE:
            default:            
                styleS = LINE_STYLE_LINE;
                break;
        }
        styleProp.setValue(styleS);
    }

    private byte getAlignment(String alignment) {
        if (CENTER.equals(alignment)) {
            return ChartTitle.CENTRAL_ALIGNMENT;
        } else if (RIGHT.equals(alignment)) {
            return ChartTitle.RIGHT_ALIGNMENT;
        } else {
            return ChartTitle.LEFT_ALIGNMENT;
        }
    }

    private void setAlignment(byte alignment, Property alignmentprop) {
        String alignmentS;
        switch (alignment) {
            case ChartTitle.CENTRAL_ALIGNMENT:
                alignmentS = CENTER;
                break;
            case ChartTitle.RIGHT_ALIGNMENT:
                alignmentS = RIGHT;
                break;
            case ChartTitle.LEFT_ALIGNMENT:
                alignmentS = LEFT;
                break;
            default:
                alignmentS = CENTER;
                break;
        }
        alignmentprop.setValue(alignmentS);
    }

    public List<NameType> getColumns() {
        return columns;
    }

    public void resetColumns() {
        columns = new ArrayList<NameType>();
    }

    public void setColumns(List<NameType> columns) {
        this.columns = columns;
        List<Property> props = getFilteredProperties(XCOLUMN_CATEGORY);
        int index = props.indexOf(xAxisColumnProperty);
        props.remove(xAxisColumnProperty);
        props.add(index, xAxisColumnProperty = getXAxisColumnProperty(columns));
        setProperties(props.toArray(new Property[props.size()]));

        props = getFilteredProperties(YCOLUMN_CATEGORY);
        int index2 = props.indexOf(yAxisColumnProperty);
        props.remove(yAxisColumnProperty);
        props.add(index2, yAxisColumnProperty = getYAxisColumnProperty(columns));
        setProperties(props.toArray(new Property[props.size()]));
    }

    private List<NameType> getListWithSelect(List<NameType> columns) {
        List<NameType> newColumns = new ArrayList<NameType>();
        newColumns.add(new NameType(null, null));
        newColumns.addAll(columns);
        return newColumns;
    }       

}
