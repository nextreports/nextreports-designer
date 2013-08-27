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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.querybuilder.table.DataObjectTransferHandler;
import ro.nextreports.designer.querybuilder.table.ListDataFlavor;
import ro.nextreports.designer.querybuilder.table.TableDNDRecognizer;
import ro.nextreports.designer.ui.ComboBoxEditor;
import ro.nextreports.designer.ui.eventbus.CircularEventFilter;
import ro.nextreports.designer.ui.eventbus.Subscriber;
import ro.nextreports.designer.ui.list.CheckListEvent;
import ro.nextreports.designer.ui.list.CheckListItem;
import ro.nextreports.designer.ui.table.TableRowHeader;
import ro.nextreports.designer.util.ColorUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.TableUtil;

import ro.nextreports.engine.querybuilder.MyRow;
import ro.nextreports.engine.querybuilder.sql.Column;
import ro.nextreports.engine.querybuilder.sql.ExpressionColumn;
import ro.nextreports.engine.querybuilder.sql.GroupByFunctionColumn;
import ro.nextreports.engine.querybuilder.sql.JoinCriteria;
import ro.nextreports.engine.querybuilder.sql.MatchCriteria;
import ro.nextreports.engine.querybuilder.sql.Order;
import ro.nextreports.engine.querybuilder.sql.SelectQuery;
import ro.nextreports.engine.querybuilder.sql.Table;

/**
 * @author Decebal Suiu
 */
public class DesignerTablePanel extends JPanel {

    public static final String GROUP_BY = "Group By";
    public static final String SUM = "Sum";
    public static final String AVG = "Avg";
    public static final String MIN = "Min";
    public static final String MAX = "Max";
    public static final String COUNT = "Count";

    private static String ASC = I18NSupport.getString("order.ascending");
    private static String DESC = I18NSupport.getString("order.descending");
    // store without internationalization
    private static String ASC_STORE = "Asc";
    private static String DESC_STORE = "Desc";

    private MyTableModel model;
    private JXTable table;
    private TableRowHeader tableRowHeader;
    private JComboBox sortOrderCombo;
    private SelectQuery selectQuery;
    private JPopupMenu popupMenu;

    private DataFlavor listFlavor;
    private DataObjectTransferHandler dndHandler;
    private TableDNDRecognizer dndRecognizer;

    public DesignerTablePanel(SelectQuery selectQuery) {
        super();
        this.selectQuery = selectQuery;
        Globals.getEventBus().subscribe(CheckListEvent.class, null, new ColumnCheckSubscriber());
        Globals.getEventBus().subscribe(GroupByEvent.class, new CircularEventFilter(DesignerTablePanel.this),
                new GroupByCheckSubscriber());

        listFlavor = new ListDataFlavor();
        dndHandler = new DataObjectTransferHandler(listFlavor, selectQuery);
        dndRecognizer = new TableDNDRecognizer();

        initUI();
    }

    private void initUI() {
        // create table
        setLayout(new BorderLayout());
        createTable();
        add(new JScrollPane(table));
        table.setPreferredScrollableViewportSize(new Dimension(200, 70));

        // add popup listener
        MouseListener popupListener = new PopupListener();
        table.addMouseListener(popupListener);

        table.setTransferHandler(dndHandler);
        table.setDropTarget(new DropTarget(table, DataObjectTransferHandler.getDropHandler()));
        table.addMouseListener(dndRecognizer);
        table.addMouseMotionListener(dndRecognizer);
        table.setBackground(ColorUtil.PANEL_BACKROUND_COLOR);
        table.setGridColor(Color.LIGHT_GRAY);
        table.addMouseListener(new DoubleClickListener());
    }

    private void createTable() {
        // create the table
        model = new MyTableModel();
        table = new JXTable(model) {

            private Map<Integer, ComboBoxEditor> editors = new HashMap<Integer, ComboBoxEditor>();

            public boolean getScrollableTracksViewportHeight() {
                return getPreferredSize().height < getParent().getHeight();
            }

            public void changeSelection(int rowIndex, int columnIndex,
                                        boolean toggle, boolean extend) {
                if (!dndRecognizer.isDragged()) {
                    super.changeSelection(rowIndex, columnIndex, toggle, extend);
                }
            }

            public TableCellEditor getCellEditor(int row, int column) {
                if (column != 6) {
                    return super.getCellEditor(row, column);
                }
                ComboBoxEditor editor = editors.get(row);
                if (editor == null) {
                    editor = new ComboBoxEditor(new String[]{"", GROUP_BY, SUM, AVG, MIN, MAX, COUNT});
                    editors.put(row, editor);
                }
                return editor;
            }
        };
        tableRowHeader = TableUtil.setRowHeader(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn col = table.getColumnModel().getColumn(3);
//        col.setCellRenderer(TableCellRenderers.getNewDefaultRenderer(Boolean.class));
        col.setCellRenderer(table.getDefaultRenderer(Boolean.class));
        col.setCellEditor(table.getDefaultEditor(Boolean.class));
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        // no border
        JTextField tf = new JTextField();
        tf.setBorder(BorderFactory.createEmptyBorder());
        table.setDefaultEditor(Object.class, new DefaultCellEditor(tf));

        col = table.getColumnModel().getColumn(4);
//        col.setCellRenderer(TableCellRenderers.getNewDefaultRenderer(Boolean.class));
        JComboBox sortCombo = new JComboBox(new String[]{"", ASC, DESC});
        sortCombo.setBorder(BorderFactory.createEmptyBorder());
        col.setCellEditor(new ComboBoxEditor(sortCombo));

        col = table.getColumnModel().getColumn(5);
        sortOrderCombo = new JComboBox();
        sortOrderCombo.setBorder(BorderFactory.createEmptyBorder());
        col.setCellEditor(new ComboBoxEditor(sortOrderCombo));

        col = table.getColumnModel().getColumn(6);

        table.setSortable(false);
        table.setColumnControlVisible(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setHorizontalScrollEnabled(true);

        // highlight table
        table.setHighlighters(HighlighterFactory.createAlternateStriping(Color.WHITE, ColorUtil.PANEL_BACKROUND_COLOR));

        table.getTableHeader().setReorderingAllowed(false);
        
        table.setRolloverEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED)); 
    }

    public class MyTableModel extends DefaultTableModel {

        private final String[] columnNames = {
                I18NSupport.getString("designer.table"),
                I18NSupport.getString("designer.column"),
                I18NSupport.getString("designer.alias"),
                I18NSupport.getString("designer.output"),
                I18NSupport.getString("designer.sort.type"),
                I18NSupport.getString("designer.sort.order"),
                I18NSupport.getString("designer.group.by"),
                I18NSupport.getString("designer.criteria"),
                I18NSupport.getString("designer.criteria.or")
        };

        private List elements = new ArrayList();

        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 3) {
                return Boolean.class;
            }

            return Object.class;
        }

        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            // this method is called in the constructor so we must test for null
            if (elements == null) {
                return 0;
            }
            return elements.size();
        }

        @SuppressWarnings("unchecked")
        public void addObject(Object object) {
            elements.add(object);
            fireTableDataChanged();
        }

        @SuppressWarnings("unchecked")
        public void addObject(Object object, int index) {
            elements.add(index, object);
            fireTableDataChanged();
        }

        @SuppressWarnings("unchecked")
        public void addObjects(List objects) {
            elements.addAll(objects);
            fireTableDataChanged();
        }

        public void deleteObject(int rowIndex) {
            elements.remove(rowIndex);
            fireTableDataChanged();
        }

        public void deleteObjects(List objects) {
            elements.removeAll(objects);
            fireTableDataChanged();
        }

        public void clear() {
            elements.clear();
            fireTableDataChanged();
        }

        public Object getObjectForRow(int rowIndex) {
            return elements.get(rowIndex);
        }

        public int getRowForObject(Object object) {
            for (int i = 0; i < elements.size(); i++) {
                if (object.equals(elements.get(i))) {
                    return i;
                }
            }

            return -1;
        }

        public int getRowForColumn(Object object) {
            for (int i = 0; i < elements.size(); i++) {
                Column column = ((MyRow) elements.get(i)).column;
                if (object.equals(column)) {
                    return i;
                }
            }
            return -1;
        }

        //  modified from DefaultTableModel to use elements List instead of dataVector
        ///////////////////////////////////////////////////////////////////////////////////////////
        public void moveRow(int start, int end, int to) {
            int shift = to - start;
            int first, last;
            if (shift < 0) {
                first = to;
                last = end;
            } else {
                first = start;
                last = to + end - start;
            }
            rotate(elements, first, last + 1, shift);

            fireTableRowsUpdated(first, last);
        }

        @SuppressWarnings("unchecked")
        private void rotate(List list, int a, int b, int shift) {
            int size = b - a;
            int r = size - shift;
            int g = gcd(size, r);
            for (int i = 0; i < g; i++) {
                int to = i;
                Object tmp = list.get(a + to);
                for (int from = (to + r) % size; from != i; from = (to + r) % size) {
                    list.set(a + to, list.get(a + from));
                    to = from;
                }
                list.set(a + to, tmp);
            }
        }

        private int gcd(int i, int j) {
            return (j == 0) ? i : gcd(j, i % j);
        }

        public void removeRow(int row) {
            elements.remove(row);
            fireTableRowsDeleted(row, row);
        }
        ///////////////////////////////////////////////////////////////////////////////////////////

        public Object getValueAt(int rowIndex, int columnIndex) {
            MyRow row = (MyRow) elements.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    Table table = row.column.getTable();
                    if (table == null) {
                        return "<" + I18NSupport.getString("designer.expression") + ">";
                    } else {
                        return table.getName() + " (" + table.getAlias() + ")";
                    }
                case 1:
                    return row.column.getName();
                case 2:
                    return row.column.getAlias();
                case 3:
                    return Boolean.valueOf(row.output);
                case 4:
                    return getSortType(row.sortType);
                case 5:
                    return row.sortOrder == 0 ? "" : String.valueOf(row.sortOrder);
                case 6:
                    return row.groupBy;
                case 7:
                    return row.criteria;
                case 8:
                    return row.orCriterias.size() == 0 ? "" : row.orCriterias.get(0);
                default:
                    return null;
            }
        }

        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            MyRow row = (MyRow) elements.get(rowIndex);

            JComboBox groupByCombo = (JComboBox) ((DefaultCellEditor) table.getCellEditor(rowIndex, 6)).getComponent();
            switch (columnIndex) {
                case 2:
                    if (value.equals(row.column.getAlias())) {
                        return;
                    }
                    String columnAlias = (String) value;
                    if ("".equals(columnAlias)) {
                        columnAlias = null;
                    }
                    row.column.setAlias(columnAlias);
                    table.packAll();

                    break;
                case 3:
                    row.output = ((Boolean) value).booleanValue();
                    if (row.output == false) {
                        if (!"".equals(groupByCombo.getItemAt(0))) {
                            groupByCombo.insertItemAt("", 0);
                        }
                    } else {
                        if (hasGroupBy() && "".equals(groupByCombo.getSelectedItem())) {
                            row.groupBy = GROUP_BY;
                            if (!selectQuery.containsGroupByColumn(row.column)) {
                                selectQuery.addGroupByColumn(row.column, rowIndex);
                            }
                            model.fireTableCellUpdated(rowIndex, 6);
                        }
                        groupByCombo.removeItem("");
                    }
                    row.column.setOutput(row.output);
                    break;
                case 4:
                    Order order = selectQuery.getOrder(row.column);                    
                    if (ASC.equals(value)) {
                        if (order == null) {
                            selectQuery.addOrder(row.column, true);
                            sortOrderCombo.addItem(String.valueOf(selectQuery.getOrdersCount()));
                        } else {
                            order.setAscending(true);
                        }
                    } else if (DESC.equals(value)) {
                        if (order == null) {
                            selectQuery.addOrder(row.column, false);
                            sortOrderCombo.addItem(String.valueOf(selectQuery.getOrdersCount()));
                        } else {
                            order.setAscending(false);
                        }
                    } else {
                        if (order != null) {
                            sortOrderCombo.removeItem(String.valueOf(selectQuery.getOrdersCount()));
                            selectQuery.removeOrder(order);
                            row.sortOrder = 0;
                            fireTableCellUpdated(rowIndex, 5);
                        }
                    }
                    row.sortType = convertSortType((String) value);
                    table.packAll();

                    break;
                case 5:
                    String v = (String) value;
                    if (v != null) {
                        row.sortOrder = Integer.parseInt(v);
                        Order orderS = selectQuery.getOrder(row.column);
                        if (orderS != null) {
                            orderS.setIndex(row.sortOrder);
                        }
                    }
                    break;
                case 6:
                    row.groupBy = (String) value;
                    if (!"".equals(row.groupBy)) {
                        Globals.getEventBus().publish(new GroupByEvent(DesignerTablePanel.this, true));
                        updateGroupByColumn(groupByCombo, row.output);
                        if (!GROUP_BY.equals(row.groupBy)) {
                            columnAlias = row.groupBy.toUpperCase().concat(" of ").concat(row.column.getName());
                            if (row.column.getAlias() == null) {
                                row.column.setAlias(columnAlias);
                            }
                            Column gbColumn = new GroupByFunctionColumn(row.column, row.groupBy);
                            gbColumn.setAlias(row.column.getAlias());
                            gbColumn.setOutput(row.column.isOutput());
                            selectQuery.changeColumn(row.column, gbColumn);
                            row.column = gbColumn;
                            //System.out.println("*column = " + row.column.getName() + "  " + row.column.getTable());
                            model.fireTableCellUpdated(rowIndex, 6);
                            selectQuery.removeGroupByColumn(row.column);
                        } else if (GROUP_BY.equals(row.groupBy)) {
                            // select a groupBy after a function
                            Column gbColumn;
                            if ((row.column instanceof ExpressionColumn) ||
                                    ((row.column instanceof GroupByFunctionColumn) && ((GroupByFunctionColumn) row.column).isExpression())) {
                                gbColumn = new ExpressionColumn(row.column.getName(), row.column.getAlias());
                            } else {
                                gbColumn = new Column(row.column.getTable(), row.column.getName(), row.column.getAlias(), null);
                            }
                            gbColumn.setAlias(row.column.getAlias());
                            gbColumn.setOutput(row.column.isOutput());
                            selectQuery.changeColumn(row.column, gbColumn);
                            row.column = gbColumn;
                            if (!selectQuery.containsGroupByColumn(row.column)) {
                                selectQuery.addGroupByColumn(row.column, rowIndex);
                            }
                            model.fireTableCellUpdated(rowIndex, 6);
                        }
                    } else if (GROUP_BY.equals(row.groupBy)) {
                        selectQuery.addGroupByColumn(row.column);
                    } else {
                        selectQuery.removeGroupByColumn(row.column);
                    }
                    table.packAll();
                    break;
                case 7:
                    row.criteria = (String) value;
                    if (!"".equals(row.criteria)) {
                        String[] opValue = MatchCriteria.getOperatorValue(row.criteria);
                        MatchCriteria crit;
                        // criteria string contains the oparator and the value
                        if (opValue[0] != null) {
                            if ((opValue[2] == null) || opValue[2].equals("")) {
                                crit = new MatchCriteria(row.column, opValue[0], opValue[1]);
                            } else {
                                crit = new MatchCriteria(row.column, opValue[0], opValue[1], opValue[2]);
                            }
                        } else {
                            crit = new MatchCriteria(row.column, row.criteria);
                        }
                        selectQuery.addCriteria(crit);
                    }
                    table.packAll();

                    break;

                case 8:
                    String orCrit = (String) value;
                    row.orCriterias.add(0, orCrit);
                    if (!"".equals(orCrit)) {
                        String[] opValue = MatchCriteria.getOperatorValue(orCrit);
                        MatchCriteria crit;
                        // criteria string contains the oparator and the value
                        if (opValue[0] != null) {
                            if ((opValue[2] == null) || opValue[2].equals("")) {
                                crit = new MatchCriteria(row.column, opValue[0], opValue[1]);
                            } else {
                                crit = new MatchCriteria(row.column, opValue[0], opValue[1], opValue[2]);
                            }
                        } else {
                            crit = new MatchCriteria(row.column, row.criteria);
                        }
                        selectQuery.addOrCriteria(crit, 0);
                    }
                    table.packAll();
                    break;
            }
        }

        private void updateGroupByColumn(JComboBox groupByCombo, boolean output) {
            if (output) {
                groupByCombo.removeItem("");
            }
            int rowCount = model.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                MyRow row = (MyRow) model.getObjectForRow(i);
                if (row.output && "".equals(row.groupBy)) {
                    row.groupBy = GROUP_BY;
                    model.fireTableCellUpdated(i, 6);
                    selectQuery.addGroupByColumn(row.column);
                }
                if (output && row.output) {                    
                    JComboBox groupByCombo2 = (JComboBox) ((DefaultCellEditor) table.getCellEditor(i, 6)).getComponent();
                    groupByCombo2.removeItem("");
                }
            }
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                return true;
            }
            if (columnIndex == 3) {
                return true;
            }
            if (columnIndex == 4) {
                return true;
            }
            if ((columnIndex == 5) && (!"".equals(getValueAt(rowIndex, 4)))) {
                return true;
            }
            if (columnIndex == 6) {
                return true;
            }
            if (columnIndex == 7) {
                return false;
            }
            if (columnIndex >= 8) {
                return false;
            }

            return false;
        }

    }

    class ColumnCheckSubscriber implements Subscriber {

        public void inform(EventObject ev) {
            CheckListEvent clEvent = (CheckListEvent) ev;
            CheckListItem clItem = clEvent.getCheckListItem();

            Column column = (Column) clItem.getObject();
            boolean selected = clItem.isSelected();
            if (selected) {
                MyRow row = new MyRow();
                row.column = column;
                row.output = true;
                MatchCriteria mc = selectQuery.getMatchCriteria(column);
                if (mc != null) {
                    row.criteria = mc.getOperator() + " " + mc.getFullValue();
                }

                MatchCriteria orMc = selectQuery.getOrMatchCriteria(column, 0);
                if (orMc != null) {
                    row.orCriterias.set(0, orMc.getOperator() + " " + orMc.getFullValue());
                }

                // if there are other columns selected and have a groupBy
                // (need to test only first column) we set also a "Group By"
                // on the new selected column
                boolean addGroupBy = false;
                if (model.getRowCount() > 0) {
                    MyRow r = (MyRow) model.getObjectForRow(0);
                    if (!"".equals(r.groupBy)) {
                        row.groupBy = GROUP_BY;
                        addGroupBy = true;
                    }
                }

                model.addObject(row);
                tableRowHeader = TableUtil.setRowHeader(table);
                selectQuery.addColumn(column);
                if (addGroupBy) {
                    selectQuery.addGroupByColumn(column);
                }
            } else {
                MyRow row = new MyRow();
                row.column = column;
                int rowIndex = model.getRowForObject(row);
                if (rowIndex != -1) {
                    model.deleteObject(rowIndex);
                    tableRowHeader = TableUtil.setRowHeader(table);
                }
                selectQuery.removeColumnAndDependencies(column);
            }
            table.packAll();
        }

    }

    class GroupByCheckSubscriber implements Subscriber {

        public void inform(EventObject ev) {
            GroupByEvent gbEvent = (GroupByEvent) ev;
            if (gbEvent.isGroupByChecked()) {
                int rowCount = model.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    JComboBox groupByCombo = (JComboBox) ((DefaultCellEditor) table.getCellEditor(i, 6)).getComponent();
                    MyRow row = (MyRow) model.getObjectForRow(i);
                    if (row.output) {
                        groupByCombo.removeItem("");
                    }
                    row.groupBy = GROUP_BY;
                    model.fireTableCellUpdated(i, 6);
                    selectQuery.addGroupByColumn(row.column);
                }
            } else {
                int rowCount = model.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    JComboBox groupByCombo = (JComboBox) ((DefaultCellEditor) table.getCellEditor(i, 6)).getComponent();
                    if (!"".equals(groupByCombo.getItemAt(0))) {
                        groupByCombo.insertItemAt("", 0);
                    }
                    MyRow row = (MyRow) model.getObjectForRow(i);
                    row.groupBy = "";

                    if (row.column instanceof GroupByFunctionColumn) {
                        if (((GroupByFunctionColumn) row.column).isExpression()) {
                            Column expColumn = new ExpressionColumn(row.column.getName(), row.column.getAlias());
                            selectQuery.changeColumn(row.column, expColumn);
                            row.column = expColumn;
                            // for GroupBy columns with function, reset function
                        } else {
                            Column column = new Column(row.column.getTable(), row.column.getName(), row.column.getAlias(), null);
                            selectQuery.changeColumn(row.column, column);
                            row.column = column;
                        }
                    }

                    model.fireTableCellUpdated(i, 6);
                }
                selectQuery.removeAllGroupByColumns();
            }
        }

    }

    class PopupListener extends MouseAdapter {

        public void mousePressed(MouseEvent ev) {
            showPopup(ev);
        }

        public void mouseReleased(MouseEvent ev) {
            showPopup(ev);
        }

        private void showPopup(MouseEvent ev) {
            if (ev.isPopupTrigger()) {
                // get the point from the mouse event
                Point mousePoint = ev.getPoint();

                // convert the point into a row
                final int row = table.rowAtPoint(mousePoint);

                popupMenu = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem(I18NSupport.getString("designer.add.expression"));
                menuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        MyRow expression = new MyRow();
                        expression.column = new ExpressionColumn("");
                        expression.output = true;

                        ExpressionEditAction editAction = new ExpressionEditAction(expression.column);
                        editAction.actionPerformed(e);
                        if (!editAction.isOkPressed()) {
                            return;
                        }

                        model.addObject(expression);
                        selectQuery.addColumn(expression.column);

                        // select the row
                        int lastRow = table.getRowCount();
                        table.setRowSelectionInterval(lastRow - 1, lastRow - 1);
                        if (tableRowHeader != null) {
                            tableRowHeader.updateUI();
                        }
                        table.packAll();
                    }

                });
                popupMenu.add(menuItem);

                if (row > -1) {
                    // select the row
                    table.setRowSelectionInterval(row, row);
                    final MyRow myRow = (MyRow) model.getObjectForRow(row);
                    boolean add = true;
                    String title = I18NSupport.getString("designer.add.criteria");
                    if (myRow.criteria != null) {
                        add = false;
                        title = I18NSupport.getString("designer.edit.criteria");
                    }
                    final boolean fAdd = add;

                    JMenuItem menuItemCriteria = new JMenuItem(title);
                    menuItemCriteria.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            CriteriaPanel cp = new CriteriaPanel(myRow, selectQuery, fAdd);
                            CriteriaDialog dlg = new CriteriaDialog(cp);
                            dlg.pack();
                            dlg.setResizable(false);
                            Show.centrateComponent(Globals.getMainFrame(), dlg);
                            dlg.setVisible(true);

                            MatchCriteria criteria = dlg.getCriteria();
                            if (criteria != null) {
                                myRow.criteria = criteria.getOperator() + " " + criteria.getFullValue();
                                model.fireTableCellUpdated(row, 7);
                                table.packAll();
                            }
                        }

                    });
                    popupMenu.add(menuItemCriteria);

                    JMenuItem menuItemRemoveCriteria = new JMenuItem(I18NSupport.getString("designer.delete.criteria"));
                    if (!add) {
                        menuItemRemoveCriteria.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                selectQuery.removeMatchCriteria(myRow.column);
                                myRow.criteria = null;
                                model.fireTableCellUpdated(row, 7);
                                table.packAll();
                            }

                        });
                        popupMenu.add(menuItemRemoveCriteria);
                    }

                    // Or criteria
                    boolean addOr = true;
                    String titleOr = I18NSupport.getString("designer.add.criteria.or");
                    if ((myRow.orCriterias.size() > 0) && (myRow.orCriterias.get(0) != null)) {
                        addOr = false;
                        titleOr = I18NSupport.getString("designer.edit.criteria.or");
                    }
                    final boolean fAddOr = addOr;                    

                    JMenuItem menuItemOrCriteria = new JMenuItem(titleOr);
                    menuItemOrCriteria.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            CriteriaPanel cp = new CriteriaPanel(myRow, selectQuery, fAddOr, true);
                            CriteriaDialog dlg = new CriteriaDialog(cp, true);
                            dlg.pack();
                            dlg.setResizable(false);
                            Show.centrateComponent(Globals.getMainFrame(), dlg);
                            dlg.setVisible(true);

                            MatchCriteria criteria = dlg.getCriteria();
                            if (criteria != null) {
                                if (fAddOr) {
                                    myRow.orCriterias.add(0, criteria.getOperator() + " " + criteria.getFullValue());
                                } else {
                                    myRow.orCriterias.set(0, criteria.getOperator() + " " + criteria.getFullValue());
                                }
                                model.fireTableCellUpdated(row, 8);
                                table.packAll();
                            }
                        }

                    });
                    popupMenu.add(menuItemOrCriteria);

                    JMenuItem menuItemRemoveOrCriteria = new JMenuItem(I18NSupport.getString("designer.delete.criteria.or"));
                    if (!addOr) {
                        menuItemRemoveOrCriteria.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                selectQuery.removeOrMatchCriteria(myRow.column, 0);
                                myRow.orCriterias.remove(0);
                                model.fireTableCellUpdated(row, 8);
                                table.packAll();
                            }

                        });
                        popupMenu.add(menuItemRemoveOrCriteria);
                    }

                    ////

                    if (myRow == null) {
                        popupMenu.show(ev.getComponent(), mousePoint.x, mousePoint.y);
                        return;
                    }
                    if (!(myRow.column instanceof ExpressionColumn) &&
                            !((myRow.column instanceof GroupByFunctionColumn) && ((GroupByFunctionColumn) myRow.column).isExpression())) {
                        popupMenu.show(ev.getComponent(), mousePoint.x, mousePoint.y);
                        return;
                    } else {
                        popupMenu = new JPopupMenu();

                        popupMenu.add(menuItemCriteria);
                        if (!add) {
                            popupMenu.add(menuItemRemoveCriteria);
                        }
                        popupMenu.add(menuItemOrCriteria);
                        if (!addOr) {
                            popupMenu.add(menuItemRemoveOrCriteria);
                        }
                        
                        final Column expressionColumn = myRow.column;
                        menuItem = new JMenuItem(I18NSupport.getString("designer.edit.expression"));
                        menuItem.addActionListener(new ExpressionEditAction(expressionColumn));
                        popupMenu.add(menuItem);

                        menuItem = new JMenuItem(I18NSupport.getString("designer.delete.expression"));
                        menuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                model.deleteObject(row);
                                selectQuery.removeColumnAndDependencies(expressionColumn);
                                tableRowHeader.updateUI();
                            }

                        });
                        popupMenu.add(menuItem);
                        popupMenu.show(ev.getComponent(), mousePoint.x, mousePoint.y);
                        return;
                    }

                } else {
                    popupMenu.show(ev.getComponent(), mousePoint.x, mousePoint.y);
                }
            }
        }

    }

    class ExpressionEditAction implements ActionListener {

        private Column expressionColumn;
        private boolean okPressed = false;

        public ExpressionEditAction(Column expressionColumn) {

            if (!(expressionColumn instanceof ExpressionColumn) &&
                    !((expressionColumn instanceof GroupByFunctionColumn) && ((GroupByFunctionColumn) expressionColumn).isExpression())) {
                throw new IllegalArgumentException(expressionColumn + " is not an expression");
            }
            this.expressionColumn = expressionColumn;
        }

        public void actionPerformed(ActionEvent e) {
            String expression = expressionColumn.getName();
//            String result = (String) JOptionPane.showInputDialog(Globals.getMainFrame(), null, "Expression",
//                    JOptionPane.QUESTION_MESSAGE, null, null, expression);

            final JTextArea textArea = new JTextArea(expression, 10, 30);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(true);

            Object msg[] = {"", new JScrollPane(textArea)};
//
            int option = JOptionPane.showOptionDialog(Globals.getMainFrame(),
                    msg, I18NSupport.getString("designer.expression"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

            if (option == JOptionPane.YES_OPTION) {
                String result = textArea.getText();
                if (expressionColumn instanceof ExpressionColumn) {
                    ((ExpressionColumn) expressionColumn).setExpression(result);
                } else if (expressionColumn instanceof GroupByFunctionColumn) {
                    ((GroupByFunctionColumn) expressionColumn).setExpression(result);
                }
                okPressed = true;
            }

            table.packAll();
        }

        public boolean isOkPressed() {
            return okPressed;
        }

    }

    class DoubleClickListener extends MouseAdapter {

        public void mouseClicked(MouseEvent ev) {
            if (ev.getClickCount() == 2) {
                Point mousePoint = ev.getPoint();
                // convert the point into a row
                final int row = table.rowAtPoint(mousePoint);
                if (row == -1) return;
                final int column = table.columnAtPoint(mousePoint);
                final MyRow myRow = (MyRow) model.getObjectForRow(row);
                if (column == 7) {
//                if ((column == 7) && !(myRow.column instanceof ExpressionColumn) &&
//                        !((myRow.column instanceof GroupByFunctionColumn) && ((GroupByFunctionColumn) myRow.column).isExpression())) {
                    // select the row
                    table.setRowSelectionInterval(row, row);

                    boolean add = true;
                    if (myRow.criteria != null) {
                        add = false;
                    }
                    CriteriaPanel cp = new CriteriaPanel(myRow, selectQuery, add);
                    CriteriaDialog dlg = new CriteriaDialog(cp);
                    dlg.pack();
                    dlg.setResizable(false);
                    Show.centrateComponent(Globals.getMainFrame(), dlg);
                    dlg.setVisible(true);

                    MatchCriteria criteria = dlg.getCriteria();
                    if (criteria != null) {
                        myRow.criteria = criteria.getOperator() + " " + criteria.getFullValue();
                        model.fireTableCellUpdated(row, 7);
                        table.packAll();
                    }
                  } else if (column == 8) {  
//                } else if ((column == 8) && !(myRow.column instanceof ExpressionColumn) &&
//                        !((myRow.column instanceof GroupByFunctionColumn) && ((GroupByFunctionColumn) myRow.column).isExpression())) {
                    // select the row
                    table.setRowSelectionInterval(row, row);

                    boolean add = true;
                    if ((myRow.orCriterias.size() > 0) && (myRow.orCriterias.get(0) != null)) {
                        add = false;
                    }
                    CriteriaPanel cp = new CriteriaPanel(myRow, selectQuery, add, true);
                    CriteriaDialog dlg = new CriteriaDialog(cp, true);
                    dlg.pack();
                    dlg.setResizable(false);
                    Show.centrateComponent(Globals.getMainFrame(), dlg);
                    dlg.setVisible(true);

                    MatchCriteria criteria = dlg.getCriteria();
                    if (criteria != null) {
                        if (add) {
                            myRow.orCriterias.add(0, criteria.getOperator() + " " + criteria.getFullValue());
                        } else {
                            myRow.orCriterias.set(0, criteria.getOperator() + " " + criteria.getFullValue());
                        }
                        model.fireTableCellUpdated(row, 8);
                        table.packAll();
                    }
                } else if ((column == 1) && ((myRow.column instanceof ExpressionColumn) ||
                        ((myRow.column instanceof GroupByFunctionColumn) && ((GroupByFunctionColumn) myRow.column).isExpression()))) {
                    final Column expressionColumn = myRow.column;
                    new ExpressionEditAction(expressionColumn).actionPerformed(null);
                }

            }
        }
    }

    void removeColumn(Column column) {
        model.removeRow(model.getRowForColumn(column));
        selectQuery.removeColumnAndDependencies(column);
        if (tableRowHeader != null) {
            tableRowHeader.updateUI();
        }
    }

    void removeJoin(JoinCriteria criteria) {
        selectQuery.removeCriteria(criteria);
        if (tableRowHeader != null) {
            tableRowHeader.updateUI();
        }
    }

    void clear() {
        sortOrderCombo.removeAllItems();

        model.clear();
        selectQuery.clear();
        if (tableRowHeader != null) {
            tableRowHeader.updateUI();
        }
    }

    public String getQueryString() {
        return selectQuery.toString();
    }

    public List<MyRow> getRows() {
        List<MyRow> rows = new ArrayList<MyRow>();
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            MyRow row = (MyRow) model.getObjectForRow(i);
            rows.add(row);
        }
        return rows;
    }

    public void updateRows(List<MyRow> rows) {
        int index = 0;
        for (MyRow row : rows) {
            int rowCount = model.getRowCount();
            if ((row.column instanceof ExpressionColumn) ||
                    ((row.column instanceof GroupByFunctionColumn) && ((GroupByFunctionColumn) row.column).isExpression())) {
                model.addObject(row, index);
                selectQuery.addColumn(row.column, index);
                if ((row.groupBy != null) && !row.groupBy.equals("")) {
                    selectQuery.addGroupByColumn(row.column);
                }
                updateRow(row, index);
            } else {
                for (int i = 0; i < rowCount; i++) {
                    MyRow mRow = (MyRow) model.getObjectForRow(i);
                    if (mRow.equals(row)) {
                        updateRow(row, i);
                        break;
                    }
                }
            }
            index++;
        }
        model.fireTableDataChanged();
        table.packAll();
    }

    private void updateRow(MyRow row, int index) {
        if (row.column.getAlias() != null) {
            model.setValueAt(row.column.getAlias(), index, 2);
        }
        model.setValueAt(row.output, index, 3);
        if (row.sortType != null) {
            model.setValueAt(getSortType(row.sortType), index, 4);
        }
        model.setValueAt(String.valueOf(row.sortOrder), index, 5);
        if (row.groupBy != null) {
            model.setValueAt(row.groupBy, index, 6);
        }
        if (row.criteria != null) {
            model.setValueAt(row.criteria, index, 7);
        }
        if (row.orCriterias != null) {
            if (row.orCriterias.size() > 0) {
                model.setValueAt(row.orCriterias.get(0), index, 8);
            }
        }
    }

    public static void fetchOrders() {
        ASC = I18NSupport.getString("order.ascending");
        DESC = I18NSupport.getString("order.descending");
    }

    private boolean hasGroupBy() {
        for (int i = 0, size = model.getRowCount(); i < size; i++) {
            String groupBy = (String) model.getValueAt(i, 6);
            if (!"".equals(groupBy)) {
                return true;
            }
        }
        return false;
    }

    private String convertSortType(String sortType) {
        if (ASC.equals(sortType)) {
            return ASC_STORE;
        } else if (DESC.equals(sortType)) {
            return DESC_STORE;
        // for old queries/reports created with Ascending, Ascendent, Descending, Descendent
        } else if (sortType.startsWith(ASC_STORE)) {
            return ASC_STORE;
        } else if (sortType.startsWith(DESC_STORE)) {
            return DESC_STORE;
        } else {
            return sortType;
        }
    }

    private String getSortType(String convertedSortType) {
        // we use startsWith and not equals for old queries/reports created with Ascending, Ascendent, Descending, Descendent
        if (convertedSortType.startsWith(ASC_STORE)) {
            return ASC;
        } else if (convertedSortType.startsWith(DESC_STORE)) {
            return DESC;
        } else if ("".equals(convertedSortType)) {
            return convertedSortType;
        } else {
            throw new IllegalArgumentException("SavedSortType=" + convertedSortType);
        }
    }

    // must update first item "" (if exists) for drag & drop rows
    public void updateGroupByItems(int dragRow, int dropRow) {
        MyRow drag_row = (MyRow) model.getObjectForRow(dragRow);
        JComboBox groupByComboDrag = (JComboBox) ((DefaultCellEditor) table.getCellEditor(dragRow, 6)).getComponent();

        MyRow drop_row = (MyRow) model.getObjectForRow(dropRow);
        JComboBox groupByComboDrop = (JComboBox) ((DefaultCellEditor) table.getCellEditor(dropRow, 6)).getComponent();

        String sDrag = (String) groupByComboDrag.getItemAt(0);
        String sDrop = (String) groupByComboDrop.getItemAt(0);

        if ("".equals(sDrag) && !sDrag.equals(sDrop)) {
            groupByComboDrop.insertItemAt("", 0);
            groupByComboDrag.removeItem("");
        }

        if ("".equals(sDrop) && !sDrop.equals(sDrag)) {
            groupByComboDrag.insertItemAt("", 0);
            groupByComboDrop.removeItem("");
        }
    }

}
