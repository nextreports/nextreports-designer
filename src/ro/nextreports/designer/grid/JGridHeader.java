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
package ro.nextreports.designer.grid;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.grid.plaf.BasicGridHeaderUI;
import ro.nextreports.designer.grid.plaf.GridUI;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.ReportGroup;
import ro.nextreports.engine.exporter.ResultExporter;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Decebal Suiu
 */
public class JGridHeader extends JGrid {

    private static final String uiClassID = "GridHeaderUI";
    private boolean resize;
    private boolean allowSelection;

    private Popup popup;
    private JToolTip toolTip;

    public static final int MINIMUM_COLUMN_SIZE = 10;
    public static final int MAXIMUM_COLUMN_SIZE = 1500;

    // Install UI delegate
    static {
        UIManager.getDefaults().put(uiClassID, BasicGridHeaderUI.class.getName());
    }

    public JGridHeader(final JGrid viewport, final int orientation, boolean resize, boolean allowSelection) {
        this.resize = resize;
        this.allowSelection=allowSelection;
        toolTip = createToolTip();
        spanModel = new DefaultSpanModel();
//		setCellRenderer(Object.class, new GridHeaderRenderer()); // moved down
        selectionModel = new DefaultSelectionModel();

        if (orientation == SwingConstants.HORIZONTAL) {
            columnHeaderModel = viewport.getColumnHeaderModel();
            model = new ColumnHeaderGridModel(columnHeaderModel);
            rowHeaderModel = new DefaultHeaderModel(1, DEFAULT_ROW_HEIGHT,
                    SwingConstants.VERTICAL);
        } else {
            rowHeaderModel = viewport.getRowHeaderModel();
            model = new RowHeaderGridModel(rowHeaderModel);
            columnHeaderModel = new DefaultHeaderModel(1, DEFAULT_COLUMN_WIDTH,
                    SwingConstants.HORIZONTAL);

            // tooltip for group band
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent event) {
                    show(event);
                }

                private void show(MouseEvent event) {
                    Point p = event.getPoint();
                    int row = Globals.getReportGrid().rowAtPoint(p);
                    int column = Globals.getReportGrid().columnAtPoint(p);
                    String bandName = Globals.getReportGrid().getBandName(row);
                    int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(row);
                    String groupName = null;
                    if (bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX)) {
                        groupName = bandName.substring(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX.length());
                    } else if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX)) {
                        groupName = bandName.substring(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX.length());
                    }
                    if (groupName != null) {
                        ReportGroup group = LayoutHelper.getReportLayout().getGroup(groupName);
                        setToolTipText(group.getColumn());
                    } else {
                        setToolTipText(null);
                    }
                }
            });
        }

        create(model, spanModel, rowHeaderModel, columnHeaderModel, selectionModel);
        setCellRenderer(Object.class, new GridHeaderRenderer()); // !? review createDefaults ?!
        updateUI();
    }

    @Override
    public GridUI getUI() {
        return (GridUI) ui;
    }

    @Override
    public void setUI(GridUI gridUI) {
        if (ui != gridUI) {
            super.setUI(gridUI);
            repaint();
        }
    }

    @Override
    public void updateUI() {

        // register(or not) listener for selection
        if  (allowSelection) {
            setUI(new BasicGridHeaderUI());
        } else  {
            setUI(new BasicGridHeaderUI() {
                @Override
                protected MouseInputListener createMouseInputListener() {
                    return null;
                }
            });
        }

        // register listener for cell resize
        if (resize) {
            setUI(new BasicGridHeaderUI() {
                protected MouseInputListener createMouseInputListener() {
                    return new HeaderResizeMouseInputListener();
                }
            });
        }
        
        repaintManager.resizeAndRepaint();
    }

    @Override
    public String getUIClassID() {
        return "GridHeaderUI";
    }


    protected void showToolTip(MouseEvent event) {

        toolTip.setTipText(getToolTipText(event));

        //  Trick is to hide a previous popup before showing a new one
        if (popup != null) popup.hide();

        Point pt = getLocationOnScreen();
        int x = pt.x + event.getPoint().x;
        int y = pt.y - 20;

        PopupFactory factory = PopupFactory.getSharedInstance();
        popup = factory.getPopup(this, toolTip, x, y);
        popup.show();
    }


    private static class RowHeaderGridModel extends AbstractGridModel {

        private HeaderModel headerModel;

        public RowHeaderGridModel(HeaderModel rowModel) {
            headerModel = rowModel;
        }

        public Object getValueAt(int row, int column) {

            String bandName = Globals.getReportGrid().getBandName(row);
            int bandRow = Globals.getReportGrid().getBandLocation(bandName).getRow(row);

            String result;
            if (bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX)) {
                result = I18NSupport.getString("band.group.header.name").substring(0, 1).toUpperCase() + bandName.substring(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX.length());
            } else if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX)) {
                result = I18NSupport.getString("band.group.footer.name").substring(0, 1).toUpperCase() + bandName.substring(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX.length());
            } else if (bandName.equals(ReportLayout.PAGE_HEADER_BAND_NAME) || bandName.equals(ReportLayout.PAGE_FOOTER_BAND_NAME)) {
            	result = String.valueOf("Z" + bandRow);            
            } else {
                result = String.valueOf(I18NSupport.getString("band." + bandName.toLowerCase() + ".name").substring(0, 1).toUpperCase() + bandRow);
            }
            return result;
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void setValueAt(Object value, int row, int column) {
        }

        public int getRowCount() {
            return headerModel.getCount();
        }

        public int getColumnCount() {
            return 1;
        }

    }

    private static class ColumnHeaderGridModel extends AbstractGridModel {

        private HeaderModel headerModel;

        public ColumnHeaderGridModel(HeaderModel columnModel) {
            headerModel = columnModel;
        }

        public Object getValueAt(int row, int column) {
            return String.valueOf(column);
        }

        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void setValueAt(Object value, int row, int column) {
        }

        public int getRowCount() {
            return 1;
        }

        public int getColumnCount() {
            return headerModel.getCount();
        }

    }

    protected class HeaderResizeMouseInputListener implements MouseInputListener {

        private boolean dragging = false;
        private boolean start = false;
        private Rectangle r;
        // Give user some leeway for selections.
        private final int PROX_DIST = 3;

        private int row;
        private int column;
        private int width;
        private String oldTooltip;

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {            
            start = true;
            oldTooltip = getToolTipText();
            if (getCursor() != Cursor.getDefaultCursor()) {
                // If cursor is set for resizing, allow dragging.
                if (LayoutHelper.getReportLayout().isUseSize()) {
                    dragging = true;
                    // take care of double clicks (seen as two mouse pressed followed by mouse released actions)
                    // meaning the width remains the same
                    if (start) {
                        Point p = e.getPoint();
                        p.setLocation(p.x - PROX_DIST - 1, p.y);
                        row = rowAtPoint(p);
                        column = columnAtPoint(p);
                        r = getCellBounds(row, column);
                        width = r.width;
                    }
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (dragging & LayoutHelper.getReportLayout().isUseSize()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        int total = 0;
                        List<Integer> columnsWidth = new ArrayList<Integer>();
                        for (int i = 0, n = getColumnCount(); i < n; i++) {
                            if (i == column) {
                                total += width;
                                columnsWidth.add(width);
                            } else {
                                total += getColumnWidth(i);
                                columnsWidth.add(getColumnWidth(i));
                            }
                        }
                        LayoutHelper.getReportLayout().setColumnsWidth(columnsWidth);
                        // repaint headers
                        Globals.getReportLayoutPanel().getReportGridPanel().repaintHeaders();
                        Globals.getReportDesignerPanel().refresh();

                        if (Globals.getA4Warning()) {
                            if (ResultExporter.A4_LANDSCAPE_PIXELS < total) {
                                Show.info(I18NSupport.getString("width.action.exceed.landscape"));
                            } else if (ResultExporter.A4_PORTRAIT_PIXELS < total) {
                                Show.info(I18NSupport.getString("width.action.exceed.portrait"));
                            }
                        }
                    }
                });
                dragging = false;

                // popup may be null -> on double click
                if (popup != null) {
                    popup.hide();
                }
                setToolTipText(oldTooltip);
                oldTooltip = null;
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            if (dragging) {                
                Point p = e.getPoint();
                // adjust the point to left with the PROX_DIST : so we know that the left cell from the
                // mouse will be resized (otherwise can be the left or the right one : undeterministic)
                p.setLocation(p.x - PROX_DIST - 1, p.y);
                if (start) {
                    start = false;
                    row = rowAtPoint(p);
                    column = columnAtPoint(p);
                    r = getCellBounds(row, column);
                }
                int type = getCursor().getType();
                switch (type) {
                    case Cursor.E_RESIZE_CURSOR:
                        if (p.x >= r.x + r.width) {
                            width = p.x - r.x;
                        } else if (p.x <= r.x) {
                            width = r.width;
                        } else {
                            width = p.x - r.x;
                        }
                        break;
                    default:
                }
                if (width < MINIMUM_COLUMN_SIZE) {
                    width = MINIMUM_COLUMN_SIZE;
                } else if (width > MAXIMUM_COLUMN_SIZE) {
                    width = MAXIMUM_COLUMN_SIZE;
                }
                setToolTipText(I18NSupport.getString("width.text") + " : " + String.valueOf(width));
                showToolTip(e);
            }
        }

        public void mouseMoved(MouseEvent e) {
            Point p = e.getPoint();
            if (!isOverRect(p)) {
                if (getCursor() != Cursor.getDefaultCursor()) {
                    // If cursor is not over rect reset it to the default.
                    setCursor(Cursor.getDefaultCursor());
                }
                return;
            }
            // Locate cursor relative to center of rect.
            int outcode = getOutcode(p);
            int row = rowAtPoint(p);
            int column = columnAtPoint(p);
            Rectangle r = getCellBounds(row, column);
            switch (outcode) {
                case Rectangle.OUT_RIGHT:
                    if (Math.abs(p.x - (r.x + r.width)) < PROX_DIST) {
                        if (LayoutHelper.getReportLayout().isUseSize()) {
                            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                        }
                    }
                    break;
                default:    // center
                    setCursor(Cursor.getDefaultCursor());
            }
        }

        //
        // Make a smaller Rectangle and use it to locate the
        // cursor relative to the Rectangle center.
        private int getOutcode(Point p) {
            int row = rowAtPoint(p);
            int column = columnAtPoint(p);
            Rectangle r = (Rectangle) getCellBounds(row, column).clone();
            r.grow(-PROX_DIST, -PROX_DIST);
            return r.outcode(p.x, p.y);
        }

        //
        // Make a larger Rectangle and check to see if the
        // cursor is over it.         
        private boolean isOverRect(Point p) {
            int row = rowAtPoint(p);
            int column = columnAtPoint(p);
            Rectangle r = (Rectangle) getCellBounds(row, column).clone();
            r.grow(PROX_DIST, PROX_DIST);
            return r.contains(p);
        }

    }
   
}
