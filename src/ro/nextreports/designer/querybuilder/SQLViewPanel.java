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
package ro.nextreports.designer.querybuilder;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.datasource.DefaultDataSourceManager;
import ro.nextreports.designer.ui.IntegerTextField;
import ro.nextreports.designer.ui.JLine;
import ro.nextreports.designer.ui.TextHighlighter;
import ro.nextreports.designer.ui.sqleditor.BaseEditorKit;
import ro.nextreports.designer.ui.sqleditor.Editor;
import ro.nextreports.designer.ui.table.TableRowHeader;
import ro.nextreports.designer.util.ColorUtil;
import ro.nextreports.designer.util.CopyTableMouseAdapter;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.MessageUtil;
import ro.nextreports.designer.util.NextReportsUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.SwingUtil;
import ro.nextreports.designer.util.TableUtil;
import ro.nextreports.designer.util.UIActivator;

import ro.nextreports.engine.EngineProperties;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.querybuilder.sql.dialect.CSVDialect;
import ro.nextreports.engine.queryexec.QueryExecutor;
import ro.nextreports.engine.queryexec.QueryResult;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.util.ParameterNotFoundException;
import ro.nextreports.engine.util.QueryUtil;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class SQLViewPanel extends JPanel {

    // default query retrieves all data from authors table
    public static final String DEFAULT_QUERY = "<<SELECT>>";
    
    private static final Log LOG = LogFactory.getLog(SQLViewPanel.class);

    private JXTable resultTable;
    private ResultSetTableModel tableModel;
    private JTextComponent queryArea;
    private JCheckBox maxRowsCheckBox;
    private SQLStatusPanel statusPanel;
    private Thread executorThread;
    private Editor sqlEditor;

    private Action runAction;

    private static String sql;
    private boolean stop = false;

    public SQLViewPanel() {
        super();
        initUI();
    }

    private void initUI() {
        sqlEditor = new Editor();
        this.queryArea = sqlEditor.getEditorPanel().getEditorPane();
        queryArea.setText(DEFAULT_QUERY);

        ActionMap actionMap = sqlEditor.getEditorPanel().getEditorPane().getActionMap();

        // create the toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        toolBar.setBorderPainted(false);

        // add cut action
        Action cutAction = actionMap.get(BaseEditorKit.cutAction);
        cutAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("cut"));
        cutAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("sqlviewpanel.cut"));
        toolBar.add(cutAction);

        // add copy action
        Action copyAction = actionMap.get(BaseEditorKit.copyAction);
        copyAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("copy"));
        copyAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("sqlviewpanel.copy"));
        toolBar.add(copyAction);

        // add paste action
        Action pasteAction = actionMap.get(BaseEditorKit.pasteAction);
        pasteAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("paste"));
        pasteAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("sqlviewpanel.paste"));
        toolBar.add(pasteAction);

        // add separator
        SwingUtil.addCustomSeparator(toolBar);

        // add undo action
        Action undoAction = actionMap.get(BaseEditorKit.undoAction);
        undoAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("undo"));
        undoAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("undo"));
        toolBar.add(undoAction);

        // add redo action
        Action redoAction = actionMap.get(BaseEditorKit.redoAction);
        redoAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("redo"));
        redoAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("redo"));
        toolBar.add(redoAction);

        // add separator
        SwingUtil.addCustomSeparator(toolBar);

        // add find action
        Action findReplaceAction = actionMap.get(BaseEditorKit.findReplaceAction);
        findReplaceAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("find"));
        findReplaceAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("sqleditor.findReplaceActionName"));
        toolBar.add(findReplaceAction);

        // add separator
        SwingUtil.addCustomSeparator(toolBar);

        // add run action
        runAction = new SQLRunAction();
        runAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("run"));
        runAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("query.run.accelerator", "control 4")));
        runAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("run.query") +
                " (" +  ShortcutsUtil.getShortcut("query.run.accelerator.display", "Ctrl 4") + ")");
        // runAction is globally registered in QueryBuilderPanel !
        toolBar.add(runAction);

//        ro.nextreports.designer.util.SwingUtil.registerButtonsForFocus(buttonsPanel);

        // create the table
        resultTable = new JXTable();
        resultTable.setDefaultRenderer(Integer.class, new ToStringRenderer()); // to remove thousand separators
        resultTable.setDefaultRenderer(Long.class, new ToStringRenderer());
        resultTable.setDefaultRenderer(Date.class, new DateRenderer());        
        resultTable.setDefaultRenderer(Double.class, new DoubleRenderer());
        resultTable.addMouseListener(new CopyTableMouseAdapter(resultTable));
        TableUtil.setRowHeader(resultTable);
        resultTable.setColumnControlVisible(true);
//        resultTable.getTableHeader().setReorderingAllowed(false);
        resultTable.setHorizontalScrollEnabled(true);

        // highlight table
        Highlighter alternateHighlighter = HighlighterFactory.createAlternateStriping(Color.WHITE, ColorUtil.PANEL_BACKROUND_COLOR);
        Highlighter nullHighlighter = new TextHighlighter(ResultSetTableModel.NULL_VALUE, Color.YELLOW.brighter());
        Highlighter blobHighlighter = new TextHighlighter(ResultSetTableModel.BLOB_VALUE, Color.GRAY.brighter());
        Highlighter clobHighlighter = new TextHighlighter(ResultSetTableModel.CLOB_VALUE, Color.GRAY.brighter());
        resultTable.setHighlighters(alternateHighlighter, nullHighlighter, blobHighlighter, clobHighlighter);
        resultTable.setBackground(ColorUtil.PANEL_BACKROUND_COLOR);
        resultTable.setGridColor(Color.LIGHT_GRAY);

        resultTable.setRolloverEnabled(true);
        resultTable.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED)); 

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.66);
        split.setOneTouchExpandable(true);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        topPanel.add(toolBar,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(sqlEditor,
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        JScrollPane scrPanel = new JScrollPane(resultTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        statusPanel = new SQLStatusPanel();
        bottomPanel.add(scrPanel,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        bottomPanel.add(statusPanel,
                new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        split.setTopComponent(topPanel);
        split.setBottomComponent(bottomPanel);
        split.setDividerLocation(400);

        setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    public void setQueryString(String sqlString) {
        sql = sqlString;
        queryArea.setText(sqlString);
    }

    public String getQueryString() {
        String s = queryArea.getText();
        if ("".equals(s)) {
            if (sql == null) {
                return "";
            } else {
                return sql;
            }
        } else {
            return s;
        }
    }

    public boolean emptyQueryString() {
        String s = getQueryString();
        return ("".equals(s) || DEFAULT_QUERY.equals(s));
    }

    public void clear() {
        try {
            sql = "";
            queryArea.setText("");
//			tableModel = new ScrollingResultSetTableModel(null);
            tableModel = new CachingResultSetTableModel(null);
            resultTable.setModel(tableModel);
            statusPanel.clear();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(Globals.getMainFrame(), e, I18NSupport.getString("error"),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected String getSql(Report report) {
        String sql;
        if (report == null) {
            sql = queryArea.getText();
        } else {
            if (report.getSql() != null) {
                sql = report.getSql();
            } else {
                sql = report.getQuery().toString();
            }
        }
        if (sql.equals("") || sql.equals(DEFAULT_QUERY)) {
            return null;
        }
        return sql;
    }

    protected ParametersBean selectParameters(Report report) {
        return selectParameters(report, null);
    }


    protected ParametersBean selectParameters(Report report, DataSource runDS) {
        String sql = getSql(report);
        return NextReportsUtil.selectParameters(sql, runDS);
    }


    protected QueryResult runQuery(ParametersBean pBean, boolean useMaxRows) throws Exception {
        return runQuery(Globals.getConnection(), pBean, useMaxRows);
    }

    protected QueryResult runQuery(Connection con, ParametersBean pBean, boolean useMaxRows) throws Exception {
    	DataSource runDS = DefaultDataSourceManager.getInstance().getConnectedDataSource();    	
    	boolean isCsv = runDS.getDriver().equals(CSVDialect.DRIVER_CLASS);    	    	
        QueryExecutor executor = new QueryExecutor(pBean.getQuery(), pBean.getParams(),
                pBean.getParamValues(), con, true, true, isCsv);
        executor.setTimeout(Globals.getQueryTimeout());
        if (useMaxRows) {
            executor.setMaxRows(maxRowsCheckBox.isSelected() ? statusPanel.getMaxRows() : 0);
        } else {
            executor.setMaxRows(Globals.getReportLayoutPanel().getRecords());
        }
        return executor.execute();
    }

    class SQLRunAction extends AbstractAction {

        public void actionPerformed(ActionEvent ev) {
            executorThread = new Thread(new Runnable() {

                public void run() {

                    if (MessageUtil.showReconnect()) {
                        return;
                    }

                    final UIActivator activator = new UIActivator(Globals.getMainFrame(), I18NSupport.getString("running.query"));
                    try {
                        runAction.setEnabled(false);


                        if (queryArea.getText().equals("")) {
                            return;
                        }

                        String sql = getSql(null);
                        try {
                            ParameterManager.getInstance().parametersAreDefined(sql);
                        } catch (ParameterNotFoundException ex) {
                            Show.error(I18NSupport.getString("parameter.undefined", ex.getParamName()));
                            return;
                        }

                        if (QueryUtil.restrictQueryExecution(sql)) {
                            Show.error(I18NSupport.getString("export.action.execute"));
                            return;
                        }

                        if (QueryUtil.isProcedureCall(sql)) {
                            if (!QueryUtil.isValidProcedureCall(sql, DialectUtil.getDialect(Globals.getConnection()))) {
                                Show.error(I18NSupport.getString("export.action.execute.procedure"));
                                return;
                            }
//                            String param = ParameterManager.getInstance().parametersAreForStoredProcedure();
//                            if (param != null) {
//                                Show.error(I18NSupport.getString("parameter.procedure.undefined", param));
//                                return;
//                            }
                        }

                        ParametersBean pBean = selectParameters(null);
                        if (pBean == null) {
                            return;
                        }

                        activator.start(new SQLStopAction());

                        final QueryResult result = runQuery(pBean, true);
                        if (result == null) {
                            if (activator != null) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        activator.stop();
                                    }
                                });
                            }
                            return;
                        }

                        // TODO ??
                        // first reset model
                        // tableModel = new ScrollingResultSetTableModel(null);
                        tableModel = new CachingResultSetTableModel(null, executorThread.isInterrupted());
                        resultTable.setModel(tableModel);

                        //tableModel = new ScrollingResultSetTableModel(result);
                        tableModel = new CachingResultSetTableModel(result, executorThread.isInterrupted());
                        ((CachingResultSetTableModel)tableModel).init();
                        resultTable.setModel(tableModel);
                        //resultTable.packAll();
                        statusPanel.setExecuteTime(result.getExecuteTime());
                        statusPanel.setRows(result.getRowCount());
                        TableRowHeader trh = TableUtil.setRowHeader(resultTable);
                        trh.setBackground(ColorUtil.PANEL_BACKROUND_COLOR);

                        if (result.getRowCount() == 0) {
                            Show.info(I18NSupport.getString("run.query.nodata"));
                        }

                    } catch (InterruptedException e) {
                        Show.dispose();  // close a possible previous dialog message
                        Show.info(Globals.getMainFrame(), I18NSupport.getString("query.cancelled"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Show.error(Globals.getMainFrame(), I18NSupport.getString("error"), e);
                    } catch (Throwable t) {
                    	t.printStackTrace();
                    	LOG.error(t.getMessage(), t);
                    } finally {
                        stop = false;
                        runAction.setEnabled(true);
                        if (activator != null) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    activator.stop();
                                }
                            });
                        }
                    }
                }

            }, "NEXT : " + getClass().getSimpleName());
            executorThread.setPriority(EngineProperties.getRunPriority());
            executorThread.start();
        }
    }

    class SQLStopAction extends AbstractAction {

        public SQLStopAction() {
            super();
            putValue(Action.NAME, I18NSupport.getString("stop.query.execution"));
            putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("stop_execution"));
            putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("stop.query.execution"));
            putValue(Action.LONG_DESCRIPTION, I18NSupport.getString("stop.query.execution"));
            putValue(Action.MNEMONIC_KEY, new Integer('S'));
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        }

        public void actionPerformed(ActionEvent e) {
            if  (stop) {
                Show.disposableInfo(I18NSupport.getString("stop.wait.second"));
                return;
            } else {
                Show.disposableInfo(I18NSupport.getString("stop.wait"));
            }
            if (executorThread != null) {
                stop = true;
                executorThread.interrupt();
                ((CachingResultSetTableModel)tableModel).setStop(true);
            }
        }

    }

    class SQLStatusPanel extends JPanel {

        private final DecimalFormat timeFormat = new DecimalFormat("####.### sec");
        private JTextField maxRowsTextField;
        private JLabel timeLabel;
        private JLabel rowsLabel;

        public SQLStatusPanel() {
            super();
            initUI();
        }

        public void setExecuteTime(long time) {
            timeLabel.setText(timeFormat.format((double) time / 1000));
        }

        public void setRows(long rows) {
            rowsLabel.setText(rows + " " + I18NSupport.getString("rows"));
        }

        public int getMaxRows() {
            int rows;
            try {
                rows = Integer.parseInt(maxRowsTextField.getText());
            } catch (NumberFormatException nfe) {
                //all rows
                rows = 0;
            }
            return rows;
        }

        private void initUI() {
            double[] columns = {
                    TableLayoutConstants.FILL,
                    TableLayoutConstants.PREFERRED,
                    TableLayoutConstants.PREFERRED,
                    TableLayoutConstants.PREFERRED
            };

            double[] rows = {
                    TableLayoutConstants.PREFERRED,
            };

            TableLayout layout = new TableLayout(columns, rows);
            layout.setHGap(6);
            this.setLayout(layout);
            createMaxRowsPanel();

            add(createMaxRowsPanel(), "0, 0");
            add(timeLabel = new JLabel("0 " + I18NSupport.getString("seconds")), "1, 0");
            add(new JLine(), "2, 0");
            add(rowsLabel = new JLabel("0 " + I18NSupport.getString("rows")), "3, 0");
        }


        private JPanel createMaxRowsPanel() {

            JPanel maxRowsPanel = new JPanel();
            double[] columns = {
                    TableLayoutConstants.PREFERRED,
                    3,
                    TableLayoutConstants.PREFERRED
            };

            double[] rows = {
                    TableLayoutConstants.PREFERRED,
            };

            TableLayout layout = new TableLayout(columns, rows);
            maxRowsPanel.setLayout(layout);

            maxRowsCheckBox = new JCheckBox(I18NSupport.getString("max.rows"));
            maxRowsCheckBox.addItemListener(new ItemListener() {

                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        maxRowsTextField.setEditable(true);
                    } else {
                        maxRowsTextField.setEditable(false);
                    }
                }

            });
            maxRowsPanel.add(maxRowsCheckBox, "0, 0");

            maxRowsTextField = new IntegerTextField();
            maxRowsTextField.setText("500");
            maxRowsTextField.setColumns(7);
            maxRowsPanel.add(maxRowsTextField, "2, 0");

            // enable max rows feature
            enableMaxCheck();
            return maxRowsPanel;
        }

        public void clear() {
            timeLabel.setText("0 " + I18NSupport.getString("seconds"));
            rowsLabel.setText("0 " + I18NSupport.getString("rows"));
        }
        
        public void enableMaxCheck() {
        	boolean checked = Globals.isMaxChecked();
        	maxRowsCheckBox.setSelected(checked);
        	if (checked) {
                maxRowsTextField.setEditable(true);
            } else {
                maxRowsTextField.setEditable(false);
            }
        }

    }
    
    public void enableMaxCheck() {
    	statusPanel.enableMaxCheck();
    }
    
   
    /**
     * Overwrite some renderers in JXTable because we need
     * to take care about ResultSetTableModel.NULL_VALUE value
     * (which is a String)!
     */
    private class DateRenderer extends DefaultTableCellRenderer {
        DateFormat formatter;        

        public DateRenderer() {
            super();
        }

        public void setValue(Object value) {      
        	// Timestamp is also a Date, but we want to show also the time for it
        	if (value instanceof Timestamp) {
        		setText((value == null) ? "" : value.equals(ResultSetTableModel.NULL_VALUE) ? ResultSetTableModel.NULL_VALUE : value.toString());
        	} else {
        		if (formatter == null) {
        			formatter = DateFormat.getDateInstance();                
        		}
        		setText((value == null) ? "" : value.equals(ResultSetTableModel.NULL_VALUE) ? ResultSetTableModel.NULL_VALUE : formatter.format(value));
        	}        	           
        }
    }
        

    private class DoubleRenderer extends DefaultTableCellRenderer {
        NumberFormat formatter;

        public DoubleRenderer() {
            super();
        }

        public void setValue(Object value) {
            if (formatter == null) {
                formatter = NumberFormat.getInstance();
            }
            setText((value == null) ? "" :
                    value.equals(ResultSetTableModel.NULL_VALUE) ?
                            ResultSetTableModel.NULL_VALUE : formatter.format(value));
        }
    }
    
    private class ToStringRenderer extends DefaultTableCellRenderer {        

        public ToStringRenderer() {
            super();
        }

        public void setValue(Object value) {           
            setText((value == null) ? "" : value.equals(ResultSetTableModel.NULL_VALUE) ? ResultSetTableModel.NULL_VALUE :value.toString());
        }
    }


    public void doRun() {
        runAction.actionPerformed(null);
    }

    public JEditorPane getEditorPane() {
        return sqlEditor.getEditorPanel().getEditorPane();
    }
}

