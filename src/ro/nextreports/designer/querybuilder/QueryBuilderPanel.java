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

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.sql.Connection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXPanel;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.WorkspaceManager;
import ro.nextreports.designer.action.query.OpenQueryPerspectiveAction;
import ro.nextreports.designer.chart.ChartPropertyPanel;
import ro.nextreports.designer.datasource.DataSource;
import ro.nextreports.designer.dbviewer.common.DBProcedure;
import ro.nextreports.designer.dbviewer.common.DBProcedureColumn;
import ro.nextreports.designer.i18n.action.I18nManager;
import ro.nextreports.designer.querybuilder.datatransfer.DBProcTransferable;
import ro.nextreports.designer.querybuilder.datatransfer.DBTableTransferable;
import ro.nextreports.designer.ui.GlobalHotkeyManager;
import ro.nextreports.designer.ui.eventbus.CircularEventFilter;
import ro.nextreports.designer.ui.eventbus.Subscriber;
import ro.nextreports.designer.ui.list.CheckListBox;
import ro.nextreports.designer.ui.list.CheckListItem;
import ro.nextreports.designer.util.DecoratedScrollPane;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.ShortcutsUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.SwingUtil;
import ro.nextreports.designer.util.TableUtil;
import ro.nextreports.designer.util.TreeUtil;

import ro.nextreports.engine.ReleaseInfoAdapter;
import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.chart.Chart;
import ro.nextreports.engine.util.ProcUtil;
import ro.nextreports.engine.util.DialectUtil;
import ro.nextreports.engine.exporter.util.ParametersBean;
import ro.nextreports.engine.persistence.TablePersistentObject;
import ro.nextreports.engine.querybuilder.MyRow;
import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.JoinCriteria;
import ro.nextreports.engine.querybuilder.sql.MatchCriteria;
import ro.nextreports.engine.querybuilder.sql.SelectQuery;
import ro.nextreports.engine.querybuilder.sql.Table;
import ro.nextreports.engine.queryexec.QueryParameter;
import ro.nextreports.engine.queryexec.QueryResult;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class QueryBuilderPanel extends JXPanel {

    private DBBrowserTree dbBrowserTree;
    private DBTablesDesktopPane desktop = new DBTablesDesktopPane(this);
    private JXPanel browserPanel;
    private JTabbedPane tabbedPane;
    private ParametersPanel parametersPanel;
    private SQLViewPanel sqlView;
    private JToggleButton groupByButton;
    private JToggleButton distinctButton;
    private boolean groupBy;
    private DesignerTablePanel designPanel;
    private boolean cleaned = true;
    private boolean hasDesigner = false;
    /**
     * key = java.lang.Character - prima litera din numele unei tabele
     * value = java.lang.Integer - ultimul index alocat unui alias
     */
    private Map<Character, Integer> letterIndexes = new HashMap<Character, Integer>();
    private boolean synchronizedPanels = true;
    // if a query was modified in the editor and we return to designer , the query
    // is restored from the designer and when we return to the sql panel (editor)
    // the table of results must be cleared
    private boolean resetTable = false;

    private static final Log LOG = LogFactory.getLog(QueryBuilderPanel.class);

    /**
     * key = table alias
     * value = table name
     */
    private Map<String, String> tableNamesMap = new HashMap<String, String>();

    private SelectQuery selectQuery = new SelectQuery();

    public QueryBuilderPanel() {
        super();
        setLayout(new BorderLayout());
        dbBrowserTree = new DBBrowserTree();

        Globals.getEventBus().subscribe(GroupByEvent.class, new CircularEventFilter(desktop),
                new GroupByCheckSubscriber());

        initUI();
    }

    private void initUI() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(250);
        split.setOneTouchExpandable(true);

        JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        toolBar.setBorderPainted(false);

        // add refresh action
        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("refresh");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("querybuilder.refresh");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    if (Globals.getConnection() == null) {
                        return;
                    }

                    // refresh tables, views, procedures
                    TreeUtil.refreshDatabase();

                    // add new queries to tree
                    TreeUtil.refreshQueries();

                    // add new reports to tree
                    TreeUtil.refreshReports();

                    // add new charts to tree
                    TreeUtil.refreshCharts();

                } catch (Exception ex) {
                    Show.error(ex);
                }
            }

        });

        // add expand action
        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("expandall");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("querybuilder.expand.all");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                TreeUtil.expandAll(dbBrowserTree);
            }

        });

        // add collapse action
        toolBar.add(new AbstractAction() {

            public Object getValue(String key) {
                if (AbstractAction.SMALL_ICON.equals(key)) {
                    return ImageUtil.getImageIcon("collapseall");
                } else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
                    return I18NSupport.getString("querybuilder.collapse.all");
                }

                return super.getValue(key);
            }

            public void actionPerformed(ActionEvent e) {
                TreeUtil.collapseAll(dbBrowserTree);
            }

        });

        // add properties button
        /*
          JButton propButton = new MagicButton(new AbstractAction() {

              public Object getValue(String key) {
                  if (AbstractAction.SMALL_ICON.equals(key)) {
                      return ImageUtil.getImageIcon("properties");
                  }

                  return super.getValue(key);
              }

              public void actionPerformed(ActionEvent e) {
                  DBBrowserPropertiesPanel joinPanel = new DBBrowserPropertiesPanel();
                  JDialog dlg = new DBBrowserPropertiesDialog(joinPanel);
                  dlg.pack();
                  dlg.setResizable(false);
                  Show.centrateComponent(Globals.getMainFrame(), dlg);
                  dlg.setVisible(true);
              }

          });
          propButton.setToolTipText(I18NSupport.getString("querybuilder.properties"));
          */
        //browserButtonsPanel.add(propButton);

//        ro.nextreports.designer.util.SwingUtil.registerButtonsForFocus(browserButtonsPanel);

        browserPanel = new JXPanel(new BorderLayout());
        browserPanel.add(toolBar, BorderLayout.NORTH);

        // browser tree
        JScrollPane scroll = new JScrollPane(dbBrowserTree);
        browserPanel.add(scroll, BorderLayout.CENTER);
        split.setLeftComponent(browserPanel);

        tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
//        tabbedPane.putClientProperty(Options.EMBEDDED_TABS_KEY, Boolean.TRUE); // look like eclipse

        JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split2.setResizeWeight(0.66);
        split2.setOneTouchExpandable(true);

        // desktop pane
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        desktop.setDropTarget(new DropTarget(desktop, DnDConstants.ACTION_MOVE,
                new DesktopPaneDropTargetListener(), true));

        // create the toolbar
        JToolBar toolBar2 = new JToolBar();
        toolBar2.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar2.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        toolBar2.setBorderPainted(false);

        Action distinctAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (distinctButton.isSelected()) {
                    selectQuery.setDistinct(true);
                } else {
                    selectQuery.setDistinct(false);
                }
            }

        };
        distinctAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("query.distinct"));
        distinctAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("querybuilder.distinct"));
        toolBar2.add(distinctButton = new JToggleButton(distinctAction));

        Action groupByAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                groupBy = groupByButton.isSelected();
                Globals.getEventBus().publish(new GroupByEvent(QueryBuilderPanel.this.desktop, groupBy));
            }

        };
        groupByAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("query.group_by"));
        groupByAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("querybuilder.group.by"));
        toolBar2.add(groupByButton = new JToggleButton(groupByAction));

        Action clearAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                clear(false);
            }

        };
        clearAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("clear"));
        clearAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("querybuilder.clear"));
        toolBar2.add(clearAction);

        // add separator
        SwingUtil.addCustomSeparator(toolBar2);

        // add run button
        Action runQueryAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                selectSQLViewTab();
                sqlView.doRun();
            }

        };
        runQueryAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("run"));
        KeyStroke ks = KeyStroke.getKeyStroke(ShortcutsUtil.getShortcut("query.run.accelerator", "control 4"));
        runQueryAction.putValue(Action.SHORT_DESCRIPTION, I18NSupport.getString("run.query") +
                " (" + ShortcutsUtil.getShortcut("query.run.accelerator.display", "Ctrl 4") + ")");
        runQueryAction.putValue(Action.ACCELERATOR_KEY, ks);
        toolBar2.add(runQueryAction);
        // register run query shortcut
        GlobalHotkeyManager hotkeyManager = GlobalHotkeyManager.getInstance();
        InputMap inputMap = hotkeyManager.getInputMap();
        ActionMap actionMap = hotkeyManager.getActionMap();
        inputMap.put((KeyStroke) runQueryAction.getValue(Action.ACCELERATOR_KEY), "runQueryAction");
        actionMap.put("runQueryAction", runQueryAction);

        JScrollPane scroll2 = new JScrollPane(desktop, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll2.setPreferredSize(DBTablesDesktopPane.PREFFERED_SIZE);
        DecoratedScrollPane.decorate(scroll2);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        topPanel.add(toolBar2,
                new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        topPanel.add(scroll2,
                new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));


        split2.setTopComponent(topPanel);
        designPanel = new DesignerTablePanel(selectQuery);
        split2.setBottomComponent(designPanel);
        split2.setDividerLocation(400);

        tabbedPane.addTab(I18NSupport.getString("querybuilder.query.designer"), ImageUtil.getImageIcon("designer"), split2);
        tabbedPane.setMnemonicAt(0, 'D');


        sqlView = new SQLViewPanel();
        sqlView.getEditorPane().setDropTarget(new DropTarget(sqlView.getEditorPane(), DnDConstants.ACTION_MOVE,
                new SQLViewDropTargetListener(), true));
        tabbedPane.addTab(I18NSupport.getString("querybuilder.query.editor"), ImageUtil.getImageIcon("sql"), sqlView);
        tabbedPane.setMnemonicAt(1, 'E');

        split.setRightComponent(tabbedPane);

        // register a change listener
        tabbedPane.addChangeListener(new ChangeListener() {

            // this method is called whenever the selected tab changes
            public void stateChanged(ChangeEvent ev) {
                if (ev.getSource() == QueryBuilderPanel.this.tabbedPane) {
                    // get current tab
                    int sel = QueryBuilderPanel.this.tabbedPane.getSelectedIndex();
                    if (sel == 1) { // sql view
                        String query;
                        if (!synchronizedPanels) {
                            query = sqlView.getQueryString();
                            synchronizedPanels = true;
                        } else {
                            if (Globals.getConnection() != null) {
                                query = getSelectQuery().toString();
                            } else {
                                query = "";
                            }
//							if (query.equals("")) {
//								query = sqlView.getQueryString();
//							}
                        }
                        if (resetTable) {
                            sqlView.clear();
                            resetTable = false;
                        }
                        //System.out.println("query="+query);
                        sqlView.setQueryString(query);
                    } else if (sel == 0) { // design view
                        if (queryWasModified(false)) {
                            Object[] options = {I18NSupport.getString("optionpanel.yes"), I18NSupport.getString("optionpanel.no")};
                            String m1 = I18NSupport.getString("querybuilder.lost");
                            String m2 = I18NSupport.getString("querybuilder.continue");
                            int option = JOptionPane.showOptionDialog(Globals.getMainFrame(),
                                    "<HTML>" + m1 + "<BR>" + m2 + "</HTML>", I18NSupport.getString("querybuilder.confirm"),
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null, options, options[1]);

                            if (option != JOptionPane.YES_OPTION) {
                                synchronizedPanels = false;
                                tabbedPane.setSelectedIndex(1);
                            } else {
                                resetTable = true;
                            }
                        }
                    } else if (sel == 2) { // report view

                    }
                }
            }

        });

//        this.add(split, BorderLayout.CENTER);

        parametersPanel = new ParametersPanel();
    }

    public void initWorkspace() {
        this.putClientProperty(WorkspaceManager.QUERY_CONTENT, tabbedPane);
        this.putClientProperty(WorkspaceManager.QUERY_EXPLORER, browserPanel);
        this.putClientProperty(WorkspaceManager.QUERY_PARAMETERS, parametersPanel);

        WorkspaceManager.getInstance().setCurrentWorkspace(WorkspaceManager.QUERY_WORKSPACE);
    }

    public void addQuery(String name, String path) {
        try {
            dbBrowserTree.addQuery(name, path);
        } catch (Exception e) {
            Show.error(e);
        }
    }

    public void addReport(String name, String path) {
        try {
            dbBrowserTree.addReport(name, path);
        } catch (Exception e) {
            Show.error(e);
        }
    }

    public void addFolder(String name, String absPath, byte type, boolean onRoot) {
        try {
            dbBrowserTree.addFolder(name, absPath, type, onRoot);
        } catch (Exception e) {
            Show.error(e);
        }
    }
    
    public void addChart(String name, String path) {
        try {
            dbBrowserTree.addChart(name, path);
        } catch (Exception e) {
            Show.error(e);
        }
    }

    public void addDataSource(String name) {
        try {
            dbBrowserTree.addDataSource(name);
        } catch (Exception e) {
            Show.error(e);
        }
    }

    public void modifyDataSource(String oldName, String name) {
        try {
            dbBrowserTree.modifyDataSource(oldName, name);
        } catch (Exception e) {
            Show.error(e);
        }
    }

    public boolean foundQuery(String name) {
        dbBrowserTree.loadQueries();
        return (dbBrowserTree.searchNode(name) != null);
    }

    public void refreshTreeOnRestore() {
        dbBrowserTree.refreshTreeOnRestore();
    }

    public SelectQuery getSelectQuery() {
        try {
            selectQuery.setDialect(DialectUtil.getDialect(Globals.getConnection()));
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
        }
        return selectQuery;
    }

    public void setSelectQuery(SelectQuery selectQuery) {
        this.selectQuery = selectQuery;
    }

    public void tableColumnRemoved(String tableAlias, String tableName, String columnName) {
        desktop.tableColumnRemoved(tableAlias, tableName, columnName);
    }

    public void allTableColumnsRemoved() {
        desktop.allTableColumnsRemoved();
    }

    private int getIndexForLetter(char letter) {
        Integer index = letterIndexes.get(letter);

        if (index == null) {
            index = new Integer(1);
        } else {
            index = new Integer(index.intValue() + 1);
        }

        letterIndexes.put(letter, index);

        return index.intValue();
    }

    class DesktopPaneDropTargetListener extends DropTargetAdapter {

        public void dragEnter(DropTargetDragEvent dtde) {
            if (isDragOk(dtde)) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (isDragOk(dtde)) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }

        public void drop(DropTargetDropEvent dtde) {
            if ((dtde.getDropAction() & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);

                Transferable t = dtde.getTransferable();
                Point point = dtde.getLocation();

                try {
                    Table dbTable = (Table) t.getTransferData(DBTableTransferable.DATA_FLAVOR);
                    String tableName = dbTable.getName();

                    String tableAlias = null;
                    Set entrySey = tableNamesMap.entrySet();

                    for (Iterator it = entrySey.iterator(); it.hasNext();) {
                        Map.Entry entry = (Map.Entry) it.next();

                        if (tableName.equals(entry.getValue())) {
                            if (!desktop.containsIFrame(entry.getKey() + " (" + dbTable.getSchemaName() + "." + tableName + ")")) {
                                // am gasit un alias pentru tabela asta care nu a fost alocat
                                // titlului nici unei ferestre interne
                                tableAlias = (String) entry.getKey();
                                break;
                            }
                        }
                    }

                    if (tableAlias == null) {
                        // nu am nici un alias liber, creez unul nou
                        tableAlias = tableName.substring(0, 1) + getIndexForLetter(tableName.charAt(0));
                        tableNamesMap.put(tableAlias, tableName);
                    }

                    cleaned = false;
                    Table table = new Table(tableName, tableAlias);
                    table.setSchemaName(dbTable.getSchemaName());
                    try {
                        table.setDialect(DialectUtil.getDialect(Globals.getConnection()));
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        LOG.error(e.getMessage(), e);
                    }
                    Map<String, List<CheckListItem>> itemMap = TableUtil.getItemMap(table);
                    DBTableInternalFrame iframe = new DBTableInternalFrame(desktop, table, itemMap);
                    itemMap.clear();
                    iframe.setSize(CheckListBox.tableDim);
                    computeInternalFrameLocation(iframe, point);
                    desktop.add(iframe);
                    desktop.clearBackgroundImage();

                    try {
                        iframe.setSelected(true);
                    } catch (PropertyVetoException e) {
                        LOG.error(e.getMessage(), e);
                        e.printStackTrace();
                    }

                    iframe.setVisible(true);

                    scroll(iframe);

                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    Show.error(e);
                    e.printStackTrace();
                }

                dtde.dropComplete(true);
            }
        }

        // verifica daca obiectul mutat cu drag peste desktop pane poate fi acceptat
        private boolean isDragOk(DropTargetDragEvent dtde) {
            if (dtde.getCurrentDataFlavors().length == 0) {
                return false;
            }

            if (dtde.getCurrentDataFlavors()[0].equals(DBTableTransferable.DATA_FLAVOR)) {
                return true;
            }

            return false;
        }

        // calculeaza punctul in care va aparea fereastra cu coloanele tabelei selectate
        private void computeInternalFrameLocation(JInternalFrame iframe, Point point) {
            int fwidth = iframe.getWidth();
            int px = point.x;
            int py = point.y;
            iframe.setLocation(px - (fwidth / 2), py - 10);
        }

    }

    class SQLViewDropTargetListener extends DropTargetAdapter {

        public void dragEnter(DropTargetDragEvent dtde) {
            if (isDragOk(dtde)) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }

        public void dragOver(DropTargetDragEvent dtde) {
            if (isDragOk(dtde)) {
                dtde.acceptDrag(DnDConstants.ACTION_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }

        public void drop(DropTargetDropEvent dtde) {
            if ((dtde.getDropAction() & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);

                Transferable t = dtde.getTransferable();
                Point point = dtde.getLocation();

                try {
                    DBProcedure proc = (DBProcedure) t.getTransferData(DBProcTransferable.DATA_FLAVOR);
                    List<DBProcedureColumn> columns = Globals.getDBViewer().getProcedureColumns(proc.getSchema(), proc.getCatalog(), proc.getName());
                    if (!Globals.getDBViewer().isValidProcedure(columns)) {
                        Show.info(I18NSupport.getString("procedure.invalid"));
                        return;
                    } else {
                        StringBuilder sb = new StringBuilder("call ");
                        boolean order = Globals.getDialect().schemaBeforeCatalog();
                        if (!order) {
                            if (proc.getCatalog() != null) {
                                sb.append(proc.getCatalog()).append(".");
                            }
                        }
                        if (!"%".equals(proc.getSchema())) {
                            sb.append(proc.getSchema()).append(".");
                        }
                        if (order) {
                            if (proc.getCatalog() != null) {
                                sb.append(proc.getCatalog()).append(".");
                            }
                        }
                        sb.append(proc.getName());
                        sb.append("(");
                        int index = 1;
                        for (int i = 0, size = columns.size(); i < size; i++) {
                            DBProcedureColumn col = columns.get(i);
                            if (ProcUtil.IN.equals(col.getReturnType())) {
                                sb.append("${P").append(index).append("}");
                                index++;
                                if (i < size - 1) {
                                    sb.append(", ");
                                }
                            } else if (ProcUtil.OUT.equals(col.getReturnType())) {
                                if (ProcUtil.REF_CURSOR.equals(col.getDataType())) {
                                    sb.append("?");
                                    if (i < size - 1) {
                                        sb.append(" , ");
                                    }
                                }
                            }
                        }
                        sb.append(")");
                        sqlView.setQueryString(sb.toString());
                    }

                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    e.printStackTrace();
                }

                dtde.dropComplete(true);
            }
        }

        // verifica daca obiectul mutat cu drag peste desktop pane poate fi acceptat
        private boolean isDragOk(DropTargetDragEvent dtde) {
            if (dtde.getCurrentDataFlavors().length == 0) {
                return false;
            }
            if (dtde.getCurrentDataFlavors()[0].equals(DBProcTransferable.DATA_FLAVOR)) {
                return true;
            }
            return false;
        }

    }


    class GroupByCheckSubscriber implements Subscriber {

        public void inform(EventObject ev) {
            GroupByEvent gbEvent = (GroupByEvent) ev;
            if (groupBy == gbEvent.isGroupByChecked()) {
                return; // ignore
            }
            groupByButton.setSelected(true);
        }

    }

    public DesignerTablePanel getDesignPanel() {
        return designPanel;
    }

    public List<MyRow> getRows() {
        return getDesignPanel().getRows();
    }

    public void clear(boolean silent) {

        if (!silent) {
            int option = JOptionPane.showConfirmDialog(Globals.getMainFrame(),
                    I18NSupport.getString("querybuilder.clear.message"),
                    I18NSupport.getString("querybuilder.clear"),
                    JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        cleaned = true;
        groupBy = false;
        groupByButton.setSelected(false);
        distinctButton.setSelected(false);
        desktop.clear();
        designPanel.clear();
        sqlView.clear();
        Globals.setCurrentQueryName(null);
        Globals.setCurrentQueryAbsolutePath(null);
    }

    public List<TablePersistentObject> getTables() {
        return desktop.getAllTables();
    }

    public boolean queryWasModified(boolean testDesignerSelected) {

        // if you are in the designer tab we are sure the query is not modified
        if (testDesignerSelected && designerTabSelected()) {
            return false;
        }

        // User does not entered yet in SQL View tab
        if (SQLViewPanel.DEFAULT_QUERY.equals(sqlView.getQueryString())) {
            return false;
        }

        return !sqlView.getQueryString().equals(designPanel.getQueryString());
    }

    public String getUserSql() {
        return sqlView.getQueryString();
    }

    public void setUserSql(String sql) {
        hasDesigner = false;
        sqlView.setQueryString(sql);
    }

    public void setParameters(List<QueryParameter> parameters) {
        parametersPanel.set(parameters);
    }

    public void addParameter(QueryParameter param) {
        parametersPanel.addParameter(param);
    }

    public void drawDesigner(Report report, Map<String, List<CheckListItem>> itemMap) {
        hasDesigner = true;
        desktop.clearBackgroundImage();
        final SelectQuery query = report.getQuery();

        final List<DBTableInternalFrame> frames = new ArrayList<DBTableInternalFrame>();
//		System.out.println("tables=" + report.getTables().size());
        for (TablePersistentObject tpo : report.getTables()) {
            Table table = tpo.getTable();

            // refresh letterIndexes with the max value
            String tableAlias = table.getAlias();
            Character key = tableAlias.charAt(0);
            Integer value = Integer.parseInt(tableAlias.substring(1));
            //System.out.println("key="+key + " value="+value);
            Integer oldValue = letterIndexes.get(key);
            if ((oldValue == null) || (oldValue < value)) {
                letterIndexes.put(key, value);
            }

            //System.out.println(table);
            DBTableInternalFrame iframe = new DBTableInternalFrame(desktop, table, itemMap);
            iframe.setSize(tpo.getDim());
            iframe.setLocation(tpo.getPoint());
            desktop.add(iframe);
            frames.add(iframe);
            iframe.setVisible(true);
            scroll(iframe);
        }

        // select the columns in the order they are in the select
        List<Column> columns = query.getColumns();
        for (Column column : columns) {
            Table table = column.getTable();
            for (DBTableInternalFrame frame : frames) {
                if (frame.getTable().equals(table)) {
                    frame.selectColumn(column);
                    break;
                }
            }
        }

        if (query.isDistinct()) {
            distinctButton.setSelected(true);
            selectQuery.setDistinct(true);
        }

//        List<Column> groupByColumns = query.getGroupByColumns();
//        if (groupByColumns.size() > 0) {
//            groupByMenu.setSelected(true);
//        }

        if (query.hasNotNullGroupByColumn()) {
            groupByButton.setSelected(true);
        }

        getDesignPanel().updateRows(report.getRows());
        // update match criterias (which have parameters) in the selectquery
        List<MatchCriteria> mcList = query.getParameterMatchCriterias();
        selectQuery.updateParameterMatchCriterias(mcList);

        List<MatchCriteria> mcOrList = query.getOrParameterMatchCriterias(0);
        selectQuery.updateOrParameterMatchCriterias(mcOrList, 0);

        selectQuery.setOrders(report.getQuery().getOrders());


        List<JoinCriteria> joins = query.getJoins();
        for (JoinCriteria crit : joins) {
            Column source = crit.getSource();
            Column destination = crit.getDestination();

            Object[] src = getFramePosition(frames, source);
            Object[] dest = getFramePosition(frames, destination);

            JoinLine joinLine = new JoinLine((DBTableInternalFrame) src[0], (Integer) src[1],
                    (DBTableInternalFrame) dest[0], (Integer) dest[1]);
            joinLine.setJoinCriteria(crit);
            desktop.addJoinLineWithCriteria(joinLine);
            desktop.repaint();
        }

        // this is necessary because we can load a report when we are inside SQL View Panel
        setUserSql(selectQuery.toString());

    }

    public void selectTreeNode(String name, byte type) {
        if (dbBrowserTree != null) {
            dbBrowserTree.selectNode(name, type);
        }
    }

    public void selectTreeNode(String name, String path, byte type) {
        if (dbBrowserTree != null) {
            dbBrowserTree.selectNode(name, path, type);
        }
    }

    private Object[] getFramePosition(List<DBTableInternalFrame> frames, Column column) {
        Table table = column.getTable();
        Object[] result = new Object[2];
        for (DBTableInternalFrame frame : frames) {
            if (frame.getTable().equals(table)) {
                result[0] = frame;
                result[1] = frame.getIndex(column);
            }
        }
        return result;
    }

    public void loadReport(ReportLayout reportLayout) {
        try {
            reportLayout.initBandsListenerList();
            // get parameters definition from system
            Map<String, QueryParameter> params = new HashMap<String, QueryParameter>();
            ParameterManager paramManager = ParameterManager.getInstance();
            List<String> paramNames = paramManager.getParameterNames();
            for (String paramName : paramNames) {
                QueryParameter param = paramManager.getParameter(paramName);
                if (param == null) {
                    throw new Exception("Parameter '" + paramName + "' is not defined.");
                }
                params.put(paramName, param);
            }
            //reportLayout.setColumnNames(ReportLayoutUtil.getSelectedColumnsForReport(reportLayout));            
            LayoutHelper.setReportLayout(reportLayout);
            Globals.getReportLayoutPanel().updateUseSize();
            Globals.getReportLayoutPanel().removeEditor();
            Globals.getReportDesignerPanel().refresh();
            Globals.getReportLayoutPanel().selectConnectedDataSource();
            // change workspace
            WorkspaceManager.getInstance().setCurrentWorkspace(WorkspaceManager.REPORT_WORKSPACE);
        } catch (Exception e) {
            Show.error(e);
        }
    }

    public void loadChart(Chart chart) {
        try {
            Globals.getChartDesignerPanel().setChart(chart);
            Globals.getChartDesignerPanel().refresh();
            Globals.getChartLayoutPanel().selectConnectedDataSource();
            // change workspace
            WorkspaceManager.getInstance().setCurrentWorkspace(WorkspaceManager.CHART_WORKSPACE);
            Globals.getChartDesignerPanel().selectProperties(ChartPropertyPanel.MAIN_CATEGORY);
        } catch (Exception e) {
            Show.error(e);
        }
    }

    public void emptyReportAndChart() {
        //Globals.getReportDesignerPanel().clear();
        (new OpenQueryPerspectiveAction()).actionPerformed(null);
        Globals.getMainMenuBar().enableLayoutPerspective(false);
        Globals.getMainToolBar().enableLayoutPerspective(false);        
    }

    public void refreshSql() {
        int index = tabbedPane.getSelectedIndex();
        selectSQLViewTab();
        tabbedPane.setSelectedIndex(index);
    }

    public void selectSQLViewTab() {
        tabbedPane.setSelectedIndex(1);
    }

    public void selectDesignerTab() {
        tabbedPane.setSelectedIndex(0);
    }

    public boolean designerTabSelected() {
        return (tabbedPane.getSelectedIndex() == 0);
    }

    public boolean isCleaned() {
        boolean result;
        //System.out.println(">>>>>>>>>>> " + sqlView.getQueryString());
        if (sqlView.emptyQueryString()) {
            result = cleaned;
        } else {
            result = false;
        }
        // just tables with no selections!
        if (result) {
            if (desktop.getAllFrames().length > 0) {
                result = false;
            }
        }
        return result;
    }

    public void newQuery() {
        emptyReportAndChart();
        clear(true);
        Globals.setCurrentReportName(null);
        Globals.setCurrentReportAbsolutePath(null);
        Globals.setCurrentChartName(null);
        Globals.setCurrentChartAbsolutePath(null);
        ParameterManager.getInstance().clearParameters();
        I18nManager.getInstance().clear();
        parametersPanel.set(new ArrayList<QueryParameter>());
        selectDesignerTab();
    }

    @SuppressWarnings("unchecked")
    public Report createReport(String name) {
        Report report = new Report();
        report.setName(name);
        report.setVersion(ReleaseInfoAdapter.getVersionNumber());
        if (queryWasModified(true)) {
            report.setSql(getUserSql());
        } else {
            report.setQuery(getSelectQuery());
            report.setTables(getTables());
            report.setRows(getRows());
        }
        LinkedList<QueryParameter> parameters = (LinkedList) ParameterManager.getInstance().getParameters();
        // Saved the parameters in the order inside sql!
//		LinkedList<QueryParameter> parameters = new LinkedList();
//		ParameterManager manager = ParameterManager.getInstance();
//		String[] paramNames = (new Query(getUserSql())).getParameterNames();
//		for (String pname : paramNames) {
//			QueryParameter qp = manager.getParameter(pname);
//			if (!parameters.contains(qp)) {
//				parameters.add(qp);
//			}
//		}

        report.setParameters(parameters);                
        return report;
    }

    public QueryResult runQuery(ParametersBean pBean, boolean useMaxRows) throws Exception {
        return runQuery(Globals.getConnection(), pBean, useMaxRows);
    }

    public QueryResult runQuery(Connection con, ParametersBean pBean, boolean useMaxRows) throws Exception {
        return sqlView.runQuery(con, pBean, useMaxRows);
    }

    public ParametersBean selectParameters(Report report, DataSource runDS) {
        return sqlView.selectParameters(report, runDS);
    }

    public DBTableInternalFrame addTableToDesktop(String schema, String tableName, Dimension dim, Point location) {
        String tableAlias = tableName.substring(0, 1) + getIndexForLetter(tableName.charAt(0));
        tableNamesMap.put(tableAlias, tableName);
        Table table = new Table(tableName, tableAlias);
        table.setSchemaName(schema);
        try {
            table.setDialect(DialectUtil.getDialect(Globals.getConnection()));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            LOG.error(e.getMessage(), e);
        }
        Map<String, List<CheckListItem>> itemMap = null;
        //@todo error
        try {
            itemMap = TableUtil.getItemMap(table);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            e.printStackTrace();
        }
        DBTableInternalFrame iframe = new DBTableInternalFrame(desktop, table, itemMap);
        itemMap.clear();
        iframe.setSize(dim);
        iframe.setLocation(location);
        desktop.add(iframe);
        iframe.setVisible(true);
        return iframe;
    }

    public void addJoin(DBTableInternalFrame source, int sourceIndex, DBTableInternalFrame dest, int destIndex) {
//        System.out.println("sIndex=" + sourceIndex);
//        System.out.println("destIndex=" + destIndex);
        source.selectRow(sourceIndex);
        dest.selectRow(destIndex);
        JoinLine joinLine = new JoinLine(source, sourceIndex, dest, destIndex);
        desktop.addJoinLine(joinLine);
        scroll(dest);
    }

    // When a new frame appears in desktop we must scroll to see the full panel
    public void scroll(DBTableInternalFrame iframe) {
        int padding = 5;
        Rectangle r = new Rectangle(iframe.getX(), iframe.getY(), iframe.getWidth() + padding, iframe.getHeight() + padding);
        int x = Math.max(iframe.getX() + iframe.getWidth() + padding, desktop.getWidth());
        int y = Math.max(iframe.getY() + iframe.getHeight() + padding, desktop.getHeight());
        Dimension dim = new Dimension(x, y);
        desktop.setPreferredSize(dim);
        desktop.scrollRectToVisible(r);
        desktop.revalidate();
    }

    public DBBrowserTree getTree() {
        return dbBrowserTree;
    }

    public boolean hasDesigner() {
        return hasDesigner;
    }

    class MnemonicTabAction implements ActionListener {
        int index;

        public MnemonicTabAction(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            tabbedPane.setSelectedIndex(index);
            tabbedPane.requestFocus();
        }
    }

    public void setTabMnemonicAt(int index, int keyCode) {
        ActionListener action = new MnemonicTabAction(index);
        KeyStroke stroke = KeyStroke.getKeyStroke(keyCode, ActionEvent.ALT_MASK);
        tabbedPane.registerKeyboardAction(action, stroke, JTabbedPane.WHEN_IN_FOCUSED_WINDOW);
    }
    
    public void enableMaxCheck() {
    	sqlView.enableMaxCheck();
    }

}
