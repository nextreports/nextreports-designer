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
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.InputEvent;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import ro.nextreports.designer.action.report.layout.cell.FormatPainterAction;
import ro.nextreports.designer.action.report.layout.cell.FormatPickerAction;
import ro.nextreports.designer.action.report.layout.export.ExportToCsvAction;
import ro.nextreports.designer.action.report.layout.export.ExportToDocxAction;
import ro.nextreports.designer.action.report.layout.export.ExportToExcelAction;
import ro.nextreports.designer.action.report.layout.export.ExportToHtmlAction;
import ro.nextreports.designer.action.report.layout.export.ExportToPdfAction;
import ro.nextreports.designer.action.report.layout.export.ExportToRtfAction;
import ro.nextreports.designer.action.report.layout.export.ExportToTsvAction;
import ro.nextreports.designer.action.report.layout.export.ExportToTxtAction;
import ro.nextreports.designer.action.report.layout.export.ExportToXmlAction;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.querybuilder.RuntimeParametersPanel;
import ro.nextreports.designer.template.report.action.ApplyTemplateAction;
import ro.nextreports.designer.template.report.action.ExtractTemplateAction;
import ro.nextreports.designer.ui.IntegerTextField;
import ro.nextreports.designer.ui.zoom.ZoomEvent;
import ro.nextreports.designer.ui.zoom.ZoomEventListener;
import ro.nextreports.designer.util.DropDownButton;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.SwingUtil;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class ReportLayoutPanel extends JPanel implements ChangeListener {

    public static final int PANEL_WIDTH = 700;
    public static final int PANEL_HEIGHT = 500;

    private ReportGridPanel reportGridPanel;
    private JToggleButton widthButton;

    private JCheckBox maxRecordsCheckBox;
    private JTextField maxRecordsTextField;
    private Dimension dim = new Dimension(40, 20);
    private Dimension spinnerDim = new Dimension(50, 20);
    private Dimension comboDim = new Dimension(100, 20);
    private JComboBox dataSourcesComboBox;

    private int minZoomValue = 50;
    private int maxZoomValue = 300;
    private int zoomDelta = 10;
    //initial value ,min,max, step
    private SpinnerModel spinnerModel = new SpinnerNumberModel(100, minZoomValue, maxZoomValue, zoomDelta);

    private EventListenerList zoomListenerList = new EventListenerList(); 

    public ReportLayoutPanel() {
        super();
        initComponents();
        initZoomListeners();
    }

    public ReportGridPanel getReportGridPanel() {
        return reportGridPanel;
    }

    public ReportGrid getReportGrid() {
        return reportGridPanel.getGrid();
    }

    public void refresh() {
        getReportGridPanel().setReportLayout(LayoutHelper.getReportLayout());
        getReportGrid().getSelectionModel().clearSelection();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());

        add(createToolBar(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        reportGridPanel = new ReportGridPanel(0, 0);      
        add(reportGridPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
    }

    private void initZoomListeners() {
        addZoomListener(reportGridPanel);
        // zoom with CTRL & mouse wheel
        reportGridPanel.addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                if ((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK) {
                    int clicks = e.getWheelRotation();
                    int zoomValue;
                    if (clicks < 0) {
                        // zoom in
                        zoomValue= ((Integer)spinnerModel.getValue()).intValue() + zoomDelta;
                    } else {
                        // zoom out
                        zoomValue= ((Integer)spinnerModel.getValue()).intValue() - zoomDelta ;
                    }
                    if (zoomValue < minZoomValue) {
                        zoomValue = minZoomValue;
                    } else if (zoomValue > maxZoomValue) {
                        zoomValue = maxZoomValue;
                    }
                    spinnerModel.setValue(zoomValue);
                    Integer percentage = (Integer) spinnerModel.getValue();
                    ZoomEvent zoomEvent = new ZoomEvent(percentage / 100f);
                    fireZoomEvent(zoomEvent);
                }
            }
        });
    }

    public void updateUseSize() {
        widthButton.setSelected(LayoutHelper.getReportLayout().isUseSize());
    }

    public void removeEditor() {
        reportGridPanel.getGrid().removeEditor();
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        toolBar.setBorderPainted(false);

        Action widthAction = new AbstractAction() {
            public void actionPerformed(ActionEvent event) {

                if (!widthButton.isSelected()) {
                    int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                            I18NSupport.getString("width.action.loose"), "",
                            JOptionPane.YES_NO_OPTION);
                    if (option != JOptionPane.YES_OPTION) {
                        widthButton.setSelected(true);
                        return;
                    }
                }

                LayoutHelper.getReportLayout().setUseSize(widthButton.isSelected());
                // repaint headers to show/hide ruler
                reportGridPanel.repaintHeaders();
                ReportLayoutUtil.updateColumnWidth(Globals.getReportGrid());

                if (!widthButton.isSelected()) {
                    Globals.getReportDesignerPanel().refresh();
                }

            }
        };
        widthAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("width"));
        widthAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("width.action"));
        widthButton = new JToggleButton(widthAction);
        toolBar.add(widthButton);

        SwingUtil.addCustomSeparator(toolBar);

        toolBar.add(new FormatPickerAction());
        toolBar.add(new FormatPainterAction());
        toolBar.add(new ApplyTemplateAction(true));
        toolBar.add(new ExtractTemplateAction());

        SwingUtil.addCustomSeparator(toolBar);

        toolBar.add(Globals.getReportUndoManager().getUndoAction());
        toolBar.add(Globals.getReportUndoManager().getRedoAction());

        SwingUtil.addCustomSeparator(toolBar);

        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.X_AXIS));
        previewPanel.setOpaque(false);
        maxRecordsCheckBox = new JCheckBox(I18NSupport.getString("max.records"));
        maxRecordsCheckBox.setOpaque(false);
        maxRecordsCheckBox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    maxRecordsTextField.setEditable(true);
                } else {
                    maxRecordsTextField.setEditable(false);
                }
            }

        });
        previewPanel.add(maxRecordsCheckBox);
        previewPanel.add(Box.createHorizontalStrut(5));
        maxRecordsTextField = new IntegerTextField();
        maxRecordsTextField.setText("10");
        maxRecordsTextField.setEditable(false);
        maxRecordsTextField.setPreferredSize(dim);
        maxRecordsTextField.setMinimumSize(dim);
        maxRecordsTextField.setMaximumSize(dim);
        previewPanel.add(maxRecordsTextField);

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

        previewPanel.add(Box.createHorizontalStrut(5));

        previewPanel.add(dataSourcesComboBox);
        previewPanel.add(Box.createHorizontalStrut(5));

        toolBar.add(previewPanel);

        DropDownButton dropDownButton = new DropDownButton();
        dropDownButton.getPopupMenu().add(new ExportToExcelAction(null));
        dropDownButton.getPopupMenu().add(new ExportToPdfAction(null));
        dropDownButton.getPopupMenu().add(new ExportToDocxAction(null));
        dropDownButton.getPopupMenu().add(new ExportToRtfAction(null));
        dropDownButton.getPopupMenu().add(new ExportToCsvAction(null));
        dropDownButton.getPopupMenu().add(new ExportToTsvAction(null));
        dropDownButton.getPopupMenu().add(new ExportToXmlAction(null));
        dropDownButton.getPopupMenu().add(new ExportToTxtAction(null));
        dropDownButton.setAction(new ExportToHtmlAction(null));
        dropDownButton.addToToolBar(toolBar);        

        JSpinner zoomSpinner = new JSpinner(spinnerModel);
        zoomSpinner.setPreferredSize(spinnerDim);
        zoomSpinner.setMinimumSize(spinnerDim);
        zoomSpinner.setMaximumSize(spinnerDim);
        zoomSpinner.addChangeListener(this);

        JPanel zPanel = new JPanel();
        zPanel.setLayout(new BoxLayout(zPanel, BoxLayout.X_AXIS));
        zPanel.setOpaque(false);
        zPanel.add(Box.createHorizontalGlue());
        zPanel.add(new JLabel(I18NSupport.getString("zoom")));
        zPanel.add(Box.createHorizontalStrut(2));
        zPanel.add(zoomSpinner);
        zPanel.add(Box.createHorizontalStrut(2));
        zPanel.add(new JLabel("%"));
        zPanel.add(Box.createHorizontalStrut(5));
        toolBar.add(zPanel);

        return toolBar;
    }

    // spinner change event
    public void stateChanged(ChangeEvent e) {
        Integer percentage = (Integer) spinnerModel.getValue();
        ZoomEvent zoomEvent = new ZoomEvent(percentage / 100f);
        fireZoomEvent(zoomEvent);
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

    // 0 means all records
    public int getRecords() {
        if (maxRecordsCheckBox.isSelected()) {
            int records;
            try {
                records = Integer.parseInt(maxRecordsTextField.getText());
            } catch (NumberFormatException nfe) {
                records = 0;
            }
            return records;
        } else {
            return 0;
        }
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
    
    public void resetRunDataSource() {
    	if (dataSourcesComboBox != null) {
    		dataSourcesComboBox.removeAllItems();
    	}
    }

    private void addZoomListener(ZoomEventListener listener) {
        zoomListenerList.add(ZoomEventListener.class, listener);
    }

    private void removeZoomListener(ZoomEventListener listener) {
        zoomListenerList.remove(ZoomEventListener.class, listener);
    }

    private void fireZoomEvent(ZoomEvent evt) {
        Object[] listeners = zoomListenerList.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i] == ZoomEventListener.class) {
                ((ZoomEventListener)listeners[i+1]).notifyZoom(evt); 
            }
        }
    }

}
