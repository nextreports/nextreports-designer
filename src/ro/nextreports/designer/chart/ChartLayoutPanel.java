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

import ro.nextreports.engine.chart.ChartRunner;
import ro.nextreports.engine.chart.ChartType;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.chart.ChartTitle;
import ro.nextreports.engine.util.NameType;
import ro.nextreports.engine.exporter.util.function.GFunction;
import ro.nextreports.engine.exporter.util.function.FunctionFactory;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.HeaderStyle;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import ro.nextreports.designer.FunctionRenderer;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.action.chart.PreviewChartAction;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.RuntimeParametersPanel;
import ro.nextreports.designer.template.chart.action.ApplyTemplateAction;
import ro.nextreports.designer.template.chart.action.ExtractTemplateAction;
import ro.nextreports.designer.ui.MagicButton;
import ro.nextreports.designer.util.DropDownListButton;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.SwingUtil;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * User: mihai.panaitescu
 * Date: 16-Dec-2009
 * Time: 10:39:17
 */
public class ChartLayoutPanel extends JPanel {

    private JLabel functionLabel;
    private JLabel yLabel;
    private JLabel xLabel;
    private DropDownListButton xButton;
    private DropDownListButton yButton;
    private JButton titleButton;
    private JButton mainButton;
    private int titleAlignment = GridBagConstraints.CENTER;
    private JPanel panel;
    private JComboBox functionComboBox;
    private List<NameType> columns;
    private JComboBox dataSourcesComboBox;

    private Dimension columnDim = new Dimension(150, 20);
    private Dimension titleDim = new Dimension(310, 30);
    private Dimension mainDim = new Dimension(310, 260);
    private Dimension comboDim = new Dimension(100, 20);

    private PreviewChartAction previewFlashAction = new PreviewChartAction(ChartRunner.GRAPHIC_FORMAT);
    private PreviewChartAction previewImageAction = new PreviewChartAction(ChartRunner.IMAGE_FORMAT);
    private ApplyTemplateAction applyTemplateAction = new ApplyTemplateAction();
    private ExtractTemplateAction extractTemplateAction = new ExtractTemplateAction();

    public ChartLayoutPanel() {
        init();
    }

    private void init() {

        setLayout(new GridBagLayout());
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

        functionLabel = new JLabel("<html><b>" + I18NSupport.getString("function.title") + "</b></html>");
        List<GFunction> functions = FunctionFactory.getFunctions();
        DefaultComboBoxModel functionComboModel = new DefaultComboBoxModel(functions.toArray());
        functionComboBox = new JComboBox(functionComboModel);
        functionComboBox.setRenderer(new FunctionRenderer());
        functionComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String function = ((GFunction) e.getItem()).getName();
                Globals.getChartDesignerPanel().getChart().setYFunction(function);
            }
        });

        yLabel = new JLabel("<html><b>" + I18NSupport.getString("chart.ycolumn") + "</b></html>");
        yButton = new DropDownListButton(I18NSupport.getString("chart.column.select")) {

            protected String getText(String item) {
                return getShownText(item);
            }

            protected void afterSelection(String item, Boolean marked) {
                List<String> yColumns = Globals.getChartDesignerPanel().getChart().getYColumns();
                if (yColumns == null) {
                    yColumns = new ArrayList<String>();
                }
                if (yColumns.size() == 0) {
                    yColumns.add(item);
                } else {
                    yColumns.set(0, item);
                }
                Globals.getChartDesignerPanel().getChart().setYColumns(yColumns);
                Globals.getChartDesignerPanel().getPropertiesPanel().updateYColumnProperty(item);
                updateFunctions(marked);
            }
        };
        yButton.setHorizontalAlignment(SwingConstants.RIGHT);
        yButton.setArrowTooltip(I18NSupport.getString("chart.column.y.tooltip"));
        yButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Globals.getChartDesignerPanel().selectProperties(ChartPropertyPanel.YCOLUMN_CATEGORY);
            }
        });
        setFixedDimension(yButton, columnDim);

        xLabel = new JLabel("<html><b>" + I18NSupport.getString("chart.xcolumn") + "</b></html>");
        xButton = new DropDownListButton(I18NSupport.getString("chart.column.select")) {

            protected String getText(String item) {
                return getShownText(item);
            }

            protected void afterSelection(String item, Boolean marked) {
                Globals.getChartDesignerPanel().getChart().setXColumn(item);
                Globals.getChartDesignerPanel().getPropertiesPanel().updateXColumnProperty(item);
            }
        };
        xButton.setHorizontalAlignment(SwingConstants.RIGHT);
        xButton.setArrowTooltip(I18NSupport.getString("chart.column.x.tooltip"));
        xButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Globals.getChartDesignerPanel().selectProperties(ChartPropertyPanel.XCOLUMN_CATEGORY);
            }
        });
        setFixedDimension(xButton, columnDim);

        titleButton = new MagicButton(I18NSupport.getString("chart.title"));
        setFixedDimension(titleButton, titleDim);
        titleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Globals.getChartDesignerPanel().selectProperties(ChartPropertyPanel.TITLE_CATEGORY);
            }
        });

        mainButton = new MagicButton();
        setFixedDimension(mainButton, mainDim);
        mainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Globals.getChartDesignerPanel().selectProperties(ChartPropertyPanel.MAIN_CATEGORY);
            }
        });
        mainButton.setSelected(true);

        panel.add(titleButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, titleAlignment,
                GridBagConstraints.NONE, new Insets(10, 10, 5, 10), 0, 0));

        panel.add(functionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
        panel.add(functionComboBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
        panel.add(yLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
        panel.add(yButton.getPanel(), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
        panel.add(mainButton, new GridBagConstraints(1, 3, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0));
        panel.add(xLabel, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 10, 5, 10), 0, 0));
        panel.add(xButton.getPanel(), new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(0, 10, 5, 10), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(0, 10, 1, 1, 0.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        panel.add(new JLabel(), new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));


        add(createToolBar(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 2), 0, 0));
        JScrollPane scr = new JScrollPane(panel);
        add(scr, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));


    }

    private void updateFunctions(Boolean marked) {
        List<GFunction> functions;
        if (marked) {
            // not-number column (only count functions)
            functions = FunctionFactory.getCountFunctions();
        } else {
            functions = FunctionFactory.getFunctions();
        }
        DefaultComboBoxModel functionComboModel = new DefaultComboBoxModel(functions.toArray());
        functionComboBox.setModel(functionComboModel);
        boolean set = setYFunction(Globals.getChartDesignerPanel().getChart().getYFunction());
        if (!set) {
            Globals.getChartDesignerPanel().getChart().setYFunction(functions.get(0).getName());
        }
    }

    private void setFixedDimension(JComponent component, Dimension dim) {
        component.setPreferredSize(dim);
        component.setMinimumSize(dim);
        component.setMaximumSize(dim);
    }

    public void setChart(Chart chart) {
        previewFlashAction.setChart(chart);
        previewImageAction.setChart(chart);
        setTitle(chart.getTitle().getTitle());
        setTitleFont(chart.getTitle().getFont());
        setTitleColor(chart.getTitle().getColor());
        setTitleAlignment(chart.getTitle().getAlignment());
        setMainBackground(chart.getBackground());
        if (chart.getType() != null) {
            setType(chart.getType().getType());
        }
        String xColumn = chart.getXColumn();
        setXColumn(xColumn);
        if (chart.getYColumns().size() > 0) {
            String yColumn = chart.getYColumns().get(0);
            setYColumn(yColumn);
        }
        setXColor(chart.getXColor());
        setYColor(chart.getYColor());
        setYFunction(chart.getYFunction());
    }

    public void refresh() {
        functionComboBox.setSelectedIndex(0);
        refreshX();
        refreshY();
    }

    public void refreshX() {
        setXColumn(null);
    }

    public void refreshY() {
        setYColumn(null);
    }

    public void setTitle(String title) {
        titleButton.setText(title);
        titleButton.setToolTipText(title);
    }

    public void setTitleFont(Font font) {
        titleButton.setFont(font);
    }

    public void setTitleColor(Color color) {
        titleButton.setForeground(color);
    }

    public void setTitleAlignment(byte alignment) {
        if (ChartTitle.LEFT_ALIGNMENT == alignment) {
            titleAlignment = GridBagConstraints.WEST;
            titleButton.setHorizontalAlignment(SwingConstants.LEFT);
        } else if (ChartTitle.CENTRAL_ALIGNMENT == alignment) {
            titleAlignment = GridBagConstraints.CENTER;
            titleButton.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            titleAlignment = GridBagConstraints.EAST;
            titleButton.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        panel.remove(titleButton);
        panel.add(titleButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, titleAlignment,
                GridBagConstraints.NONE, new Insets(10, 10, 5, 10), 0, 0));
        panel.revalidate();
        panel.repaint();
    }

    public void setMainBackground(Color color) {
        mainButton.setBackground(color);
    }

    public void setType(byte type) {
        ImageIcon image = null;
        if (ChartType.PIE == type) {
            image = ImageUtil.getImageIcon("chart_pie_main");
        } else if (ChartType.BAR == type) {
            image = ImageUtil.getImageIcon("chart_bar_main");
        } else if (ChartType.STACKED_BAR == type) {
            image = ImageUtil.getImageIcon("chart_stacked_bar_main");
        } else if (ChartType.HORIZONTAL_BAR == type) {
            image = ImageUtil.getImageIcon("chart_horizontal_bar_main");
        } else if (ChartType.LINE == type) {
            image = ImageUtil.getImageIcon("chart_line_main");
        } else if (ChartType.AREA == type) {
            image = ImageUtil.getImageIcon("chart_area_main");
        }
        if (image != null) {
            mainButton.setIcon(image);
        }        
        reverseAxis(Globals.getChartDesignerPanel().getChart().getType().isHorizontal());
    }

    public void setXColumn(String column) {
        xButton.setText(getShownText(column));
    }

    public void setXColor(Color color) {
        xButton.setForeground(color);
    }

    public void setYColumn(String column) {
        yButton.setText(getShownText(column));
        updateFunctions(getMarked(column));
    }

    public boolean setYFunction(String function) {
        for (int i = 0, size = functionComboBox.getModel().getSize(); i < size; i++) {
            GFunction gfunction = (GFunction) functionComboBox.getModel().getElementAt(i);
            if (gfunction.getName().equals(function)) {
                functionComboBox.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    private String getShownText(String column) {
        if (column == null) {
            return I18NSupport.getString("chart.column.select");
        } else {
            return "$C{" + column + "}";
        }
    }

    public void setYColor(Color color) {
        yButton.setForeground(color);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        toolBar.setBorderPainted(false);
        
        dataSourcesComboBox = new JComboBox();
        dataSourcesComboBox.setPreferredSize(comboDim);
        dataSourcesComboBox.setMinimumSize(comboDim);
        dataSourcesComboBox.setMaximumSize(comboDim);
        dataSourcesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //must reset parameters values because for a different data source
                // we may have different values.
                RuntimeParametersPanel.resetParametersValues();
            }
        });
        
        toolBar.add(dataSourcesComboBox);
        toolBar.add(Box.createHorizontalStrut(5));
        
        toolBar.add(previewFlashAction);
        toolBar.add(previewImageAction);
        SwingUtil.addCustomSeparator(toolBar);
        toolBar.add(applyTemplateAction);
        toolBar.add(extractTemplateAction);
        return toolBar;
    }

    public void setColumns(List<NameType> columns) {
        try {
            this.columns = columns;
            List<String> columnNames = new ArrayList<String>();
            List<Boolean> marked = new ArrayList<Boolean>();
            for (NameType nt : columns) {
                columnNames.add(nt.getName());
                if (!Number.class.isAssignableFrom(Class.forName(nt.getType()))) {
                    marked.add(Boolean.TRUE);
                } else {
                    marked.add(Boolean.FALSE);
                }
            }

            xButton.setItems(columnNames);
            yButton.setItems(columnNames, marked);
        } catch (Exception ex) {
            Show.error(ex);
        }
    }

    public Boolean getMarked(String column) {
    	// columns is null for preview chart action from tree
    	if (columns == null) {
    		return Boolean.FALSE;
    	}
        try {
            for (NameType nt : columns) {
                if (nt.getName().equals(column)) {
                    return !Number.class.isAssignableFrom(Class.forName(nt.getType()));
                }
            }
        } catch (Exception ex) {
            Show.error(ex);
        }
        return Boolean.FALSE;
    }

    // xAxis and yAxis are reversed only visualy in the layout if we change
    // between a vertical and an orizontal chart type
    private void reverseAxis(boolean horizontal) {
        panel.remove(functionLabel);
        panel.remove(functionComboBox);
        panel.remove(yLabel);
        panel.remove(yButton.getPanel());
        panel.remove(xLabel);
        panel.remove(xButton.getPanel());

        if (!horizontal) {
            panel.add(functionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(functionComboBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(yLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(yButton.getPanel(), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(xLabel, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 10, 5, 10), 0, 0));
            panel.add(xButton.getPanel(), new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 10, 5, 10), 0, 0));
        } else {
            panel.add(functionLabel, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(functionComboBox, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(yLabel, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(yButton.getPanel(), new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
            panel.add(xLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 10, 5, 10), 0, 0));
            panel.add(xButton.getPanel(), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 10, 5, 10), 0, 0));
        }
        repaint();
        revalidate();
    }
    
    public void selectConnectedDataSource() {
        DataSource connectedDS = DefaultDataSourceManager.getInstance().getConnectedDataSource();
        if (connectedDS == null) {
            return;
        }
        dataSourcesComboBox.removeAllItems();
        dataSourcesComboBox.setRenderer(new DataSourceComboBoxRenderer());
        for (DataSource ds : DefaultDataSourceManager.getInstance().getDataSources(connectedDS.getDriver())) {
            dataSourcesComboBox.addItem(ds.getName());
        }
        dataSourcesComboBox.setSelectedItem(connectedDS.getName());        
    }
    
    public DataSource getRunDataSource() {
        if (dataSourcesComboBox.getSelectedItem() == null) {
            return DefaultDataSourceManager.getInstance().getConnectedDataSource();
        } else {
            return DefaultDataSourceManager.getInstance().getDataSource((String)dataSourcesComboBox.getSelectedItem());
        }
    }
    
    class DataSourceComboBoxRenderer extends BasicComboBoxRenderer {
        public Component getListCellRendererComponent(JList list,
                                                      Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
                if (-1 < index) {
                    list.setToolTipText(value.toString());
                }
            } else {
                setForeground(list.getForeground());
                if (DefaultDataSourceManager.getInstance().getConnectedDataSource().getName().equals(value)) {
                    setBackground(new Color(204, 255, 255));
                } else {
                    setBackground(list.getBackground());
                }
            }
            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

}
