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
package ro.nextreports.designer.grid.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.action.report.layout.cell.CopyAction;
import ro.nextreports.designer.action.report.layout.cell.CutAction;
import ro.nextreports.designer.action.report.layout.cell.PasteAction;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.CellSpan;
import ro.nextreports.designer.grid.GridCellEditor;
import ro.nextreports.designer.grid.GridCellRenderer;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.grid.SelectionRegion;
import ro.nextreports.designer.grid.SpanModel;

import ro.nextreports.engine.util.ObjectCloner;

/**
 * @author Decebal Suiu
 */
public class BasicGridUI extends GridUI {

    private static boolean installed;

    protected CellRendererPane rendererPane;
    protected MouseInputListener mouseInputListener;
    private GridSelectionAlgorithm selectionAlgorithm;
    private static SelectionRegion shiftRegion;

    public static ComponentUI createUI(JComponent c) {
        return new BasicGridUI();
    }

    public void paintEditor(Graphics g) {
        Component component = grid.getEditorComponent();
        if (component == null) {
            return;
        }

        int editingRow = grid.getEditingRow();
        int editingColumn = grid.getEditingColumn();
        Rectangle cellBounds = grid.getCellBounds(editingRow, editingColumn);
        component.setBounds(cellBounds);
        component.validate();
        component.requestFocus();
    }

    public void paintSpans(Graphics g, int rowMin, int rowMax, int colMin, int colMax) {
        Iterator<?> cell = grid.getSpanModel().getSpanIterator();
        while (cell.hasNext()) {
            CellSpan span = (CellSpan) cell.next();
            Rectangle cellBounds = grid.getCellBounds(span.getRow(), span.getColumn());

            // Only paint cell if visible
            if (span.getLastRow() >= rowMin && span.getLastColumn() >= colMin
                    && span.getFirstRow() <= rowMax
                    && span.getFirstColumn() <= colMax) {
                paintCell(g, cellBounds, span.getRow(), span.getColumn());
                // Paint grid line around cell
                if (grid.getShowGrid()) {
                    g.setColor(grid.getGridColor());
                    g.drawRect(cellBounds.x, cellBounds.y, cellBounds.width,
                            cellBounds.height);
                }
            }
        }
    }

    public void paintGrid(Graphics g, int rowMin, int rowMax, int colMin, int colMax) {
        if (!grid.getShowGrid()) {
            return; // do nothing
        }

        int y1 = grid.getRowPosition(rowMin);
        int y2 = grid.getRowPosition(rowMax) + grid.getRowHeight(rowMax);
        int x1 = grid.getColumnPosition(colMin);
        int x2 = grid.getColumnPosition(colMax) + grid.getColumnWidth(colMax);

        g.setColor(grid.getGridColor());

        // Draw the horizontal lines
        for (int row = rowMin; row <= rowMax; row++) {
            int rowY = grid.getRowPosition(row);
            g.drawLine(x1, rowY, x2, rowY);
        }
        g.drawLine(x1, y2, x2, y2);

        // Draw the vertical gridlines
        for (int col = colMin; col <= colMax; col++) {
            int colX = grid.getColumnPosition(col);
            g.drawLine(colX, y1, colX, y2);
        }
        g.drawLine(x2, y1, x2, y2);
    }

    public void paintCells(Graphics g, int rowMin, int rowMax, int colMin, int colMax) {
        for (int row = rowMin; row <= rowMax; row++) {
            for (int column = colMin; column <= colMax; column++) {
                /* Paint cell if it is atomic */
                if (!grid.isCellSpan(row, column)) {
                    Rectangle cellBounds = grid.getCellBounds(row, column);
                    paintCell(g, cellBounds, row, column);
                }
            }
        }
    }

    public void paintSelection(Graphics g, int rowMin, int rowMax, int colMin, int colMax) {
        for (int row = rowMin; row <= rowMax; row++) {
            for (int column = colMin; column <= colMax; column++) {
                if (!grid.isCellSpan(row, column)) {
                    Rectangle cellBounds = grid.getCellBounds(row, column);
                    if (grid.getSelectionModel().isSelected(row, column)) {
                        g.setColor(Color.RED);
                        g.drawRect((int) cellBounds.getX() + 1, (int) cellBounds.getY() + 1,
                                (int) cellBounds.getWidth() - 2, (int) cellBounds.getHeight() - 2);
                    }
                } else {
                    CellSpan span = grid.getSpanModel().getSpanOver(row, column);
                    if (grid.getSelectionModel().isSelected(span.getRow(), span.getColumn())) {
                        g.setColor(Color.RED);
                        Rectangle cellBounds = grid.getCellBounds(span.getFirstRow(), span.getLastColumn());
                        g.drawRect((int) cellBounds.getX() + 1, (int) cellBounds.getY() + 1,
                                (int) cellBounds.getWidth() - 2, (int) cellBounds.getHeight() - 2);
                    }
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return getMaximumSize(c);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getMaximumSize(c);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        int lastRow = grid.getRowCount() - 1;
        int lastCol = grid.getColumnCount() - 1;

        Rectangle cellBounds = grid.getCellBounds(lastRow, lastCol);
        int width = cellBounds.x + cellBounds.width;
        int height = cellBounds.y + cellBounds.height;

        return new Dimension(width, height);
    }

    @Override
    public void installUI(JComponent c) {
        grid = (JGrid) c;
        selectionAlgorithm = new GridSelectionAlgorithm(grid);

        rendererPane = new CellRendererPane();
        grid.add(rendererPane);

        installDefaults();
        installListeners();
        installKeyboardActions();
    }

    @Override
    public void uninstallUI(JComponent c) {
        grid.remove(rendererPane);
        // TODO unistall dropTargetListener si mouseInputListener
    }

    protected void installDefaults() {
        Color defaultGridColor = UIManager.getColor("Table.gridColor");
        Color defaultForegroundColor = UIManager.getColor("Table.foreground");
        Color defaultBackgroundColor = UIManager.getColor("Table.background");
        Border defaultBorder = UIManager.getBorder("Table.scrollPaneBorder");
        Color defaultSelectionForeground = UIManager
                .getColor("Table.selectionForeground");
        Color defaultSelectionBackground = UIManager
                .getColor("Table.selectionBackground");
        Color defaultFocusCellForeground = UIManager
                .getColor("Table.focusCellForeground");
        Color defaultFocusCellBackground = new Color(153, 153, 204);
        Font defaultFont = UIManager.getFont("Table.font");
        Border defaultGridBorder = UIManager.getBorder("Table.border");
        InputMap inputMap = ObjectCloner.silenceDeepCopy((InputMap) UIManager.get("Table.ancestorInputMap"));
        if (!installed) {
            UIManager.getDefaults().put("Grid.gridColor", defaultGridColor);
            UIManager.getDefaults().put("Grid.foreground",
                    defaultForegroundColor);
            UIManager.getDefaults().put("Grid.background",
                    defaultBackgroundColor);
            UIManager.getDefaults().put("Grid.selectionForegroundColor",
                    defaultSelectionForeground);
            UIManager.getDefaults().put("Grid.selectionBackgroundColor",
                    defaultSelectionBackground);
            UIManager.getDefaults().put("Grid.focusForegroundColor",
                    defaultFocusCellForeground);
            UIManager.getDefaults().put("Grid.focusBackgroundColor",
                    defaultFocusCellBackground);
            UIManager.getDefaults().put("Grid.border", defaultGridBorder);
            UIManager.getDefaults().put("Grid.font", defaultFont);
            UIManager.getDefaults().put("Grid.scrollPaneBorder", defaultBorder);
            UIManager.getDefaults().put("Grid.ancestorInputMap", inputMap);
            installed = true;
        }
        Color foregroundColor = grid.getForeground();
        Color backgroundColor = grid.getBackground();
        Font font = grid.getFont();
        Border border = grid.getBorder();
        Color gridColor = grid.getGridColor();
        if (foregroundColor == null || foregroundColor instanceof UIResource) {
            grid.setForeground(defaultForegroundColor);
        }
        if (backgroundColor == null || backgroundColor instanceof UIResource) {
            grid.setBackground(defaultBackgroundColor);
        }
        if (font == null || font instanceof UIResource) {
            grid.setFont(defaultFont);
        }
        if (gridColor == null || gridColor instanceof UIResource) {
            grid.setGridColor(defaultGridColor);
        }
        if (border == null || border instanceof UIResource) {
            grid.setBorder(defaultGridBorder);
        }
    }   

    /**
     * Paint cell at (row, column).
     */
    protected void paintCell(Graphics g, Rectangle cellBounds, int row, int column) {
        if ((grid.getEditingRow() == row) && (grid.getEditingColumn() == column)) {
            return;
        }

        GridCellRenderer renderer = grid.getCellRenderer(row, column);
        Component rendererComp = grid.prepareRenderer(renderer, row, column);
        rendererPane.paintComponent(g, rendererComp, grid, cellBounds.x,
                cellBounds.y, cellBounds.width, cellBounds.height, true);
    }

    /**
     * Attaches listeners to the JGrid
     */
    protected void installListeners() {
        /*
          DropTargetListener dropTargetListener = createDropTargetListener();
          DropTarget dropTarget = grid.getDropTarget();
          if (dropTarget == null) {
              dropTarget = new DropTarget(grid, dropTargetListener);
          } else {
              try {
                  dropTarget.addDropTargetListener(dropTargetListener);
              } catch (TooManyListenersException e) {
                  // should not happen... swing drop target is multicast
              }
          }
          */
        mouseInputListener = createMouseInputListener();
        grid.addMouseListener(mouseInputListener);
        grid.addMouseMotionListener(mouseInputListener);
        grid.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    shiftRegion = null;
                }
            }
        });
    }

    protected void installKeyboardActions() {
        ActionMap map = getActionMap();
        SwingUtilities.replaceUIActionMap(grid, map);
        InputMap inputMap = getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.replaceUIInputMap(grid,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
    }

    /**
     * Creates the mouse listener for the JGrid.
     */
    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler();
    }

    /*
     protected DropTargetListener createDropTargetListener() {
         return new DropTargetHandler();
     }
     */

    private InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            InputMap inputMap = (InputMap) UIManager.get("Grid.ancestorInputMap");
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");

            inputMap.put((KeyStroke) new ClearCellAction().getValue(Action.ACCELERATOR_KEY), "clearCell");
            inputMap.put((KeyStroke) new CutAction().getValue(Action.ACCELERATOR_KEY), "cutCell");
            inputMap.put((KeyStroke) new CopyAction().getValue(Action.ACCELERATOR_KEY), "copyCell");
            inputMap.put((KeyStroke) new PasteAction().getValue(Action.ACCELERATOR_KEY), "pasteCell");

            return inputMap;
        } else {
            return null;
        }
    }

    private ActionMap getActionMap() {
        ActionMap actionMap = (ActionMap) UIManager.get("Grid.actionMap");
        if (actionMap == null) {
            actionMap = createActionMap();
            if (actionMap != null) {
                UIManager.put("Grid.actionMap", actionMap);
            }
        }

        return actionMap;
    }

    private ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();

        map.put("selectNextColumn", new NavigationAction(1, 0));
        map.put("selectPreviousColumn", new NavigationAction(-1, 0));
        map.put("selectNextRow", new NavigationAction(0, 1));
        map.put("selectPreviousRow", new NavigationAction(0, -1));

        map.put("selectNextColumnExtendSelection", new NavigationAction(1, 0, true));
        map.put("selectPreviousColumnExtendSelection", new NavigationAction(-1, 0, true));
        map.put("selectNextRowExtendSelection", new NavigationAction(0, 1, true));
        map.put("selectPreviousRowExtendSelection", new NavigationAction(0, -1, true));

        map.put("startEditing", new StartEditingAction());
        map.put("cancel", new CancelEditingAction());
        map.put("clearCell", new ClearCellAction());
        map.put("cutCell", new CutAction());
        map.put("copyCell", new CopyAction());
        map.put("pasteCell", new PasteAction());

        return map;
    }

    /**
     * Instantiate it only within subclasses of BasicGridUI.
     */
    protected class MouseInputHandler implements MouseInputListener {

        // Component recieving mouse events during editing.
        // May not be editorComponent.
        private Component dispatchComponent;
        private boolean selectedOnPress;
        private SelectionRegion selectionRegion;
        private Point startPoint = null;
        private Point previousPoint = null;

        public void mouseClicked(MouseEvent event) {
        }

        public void mousePressed(MouseEvent event) {
            startPoint = event.getPoint();

//			System.out.println("MouseInputHandler.mousePressed()");
            if (!event.isShiftDown()) {
                selectionRegion = null;
            }
            if (event.isConsumed()) {
                selectedOnPress = false;
                return;
            }
            selectedOnPress = true;
            adjustFocusAndSelection(event);
        }

        public void mouseReleased(MouseEvent event) {
//			System.out.println("MouseInputHandler.mouseReleased()");
            startPoint = null;
            previousPoint = null;
            if (selectedOnPress) {
                if (shouldIgnore(event)) {
                    return;
                }

                repostEvent(event);
                dispatchComponent = null;
                setValueIsAdjusting(false);

            } else {
                adjustFocusAndSelection(event);
            }
        }

        public void mouseEntered(MouseEvent event) {
        }

        public void mouseExited(MouseEvent event) {
        }

        public void mouseMoved(MouseEvent event) {
        }

        public void mouseDragged(MouseEvent event) {
//			System.out.println("MouseInputHandler.mouseDragged()");
            if (shouldIgnore(event)) {
                return;
            }

            // if CTRL is pressed we are not interested in mouse drag events
            if (event.isControlDown()) {
                return;
            }

            repostEvent(event);

            CellEditor editor = grid.getCurrentCellEditor();
            if (editor == null || editor.shouldSelectCell(event)) {
                Point p = event.getPoint();
                if (startPoint == null) {
                    startPoint = p;
                }
                if (previousPoint == null) {
                    previousPoint = p;
                }
                int row = grid.rowAtPoint(p);
                int column = grid.columnAtPoint(p);
                // The autoscroller can generate drag events outside the Grid's range.
                if ((column == -1) || (row == -1)) {
                    return;
                }

                // update selectionRegion
                if (selectionAlgorithm == null) {
                    // for grid headers
                    selectionAlgorithm = new GridSelectionAlgorithm(grid);
                }
                selectionAlgorithm.update(p, startPoint, previousPoint, selectionRegion);

                selectionRegionChanged(grid, selectionRegion, false);
                previousPoint = p;
            }
        }

        private void setDispatchComponent(MouseEvent event) {
            // Get location
            Point point = event.getPoint();

            // Get editor component
            Component editorComponent = grid.getEditorComponent();

            // Get dispatchComponent
            Point editorPoint = SwingUtilities.convertPoint(grid, point, editorComponent);
            dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent,
                    editorPoint.x, editorPoint.y);
        }

        /* Repost event to dispatchComponent */
        private boolean repostEvent(MouseEvent event) {
            if (dispatchComponent == null) {
                return false;
            }
            MouseEvent editorMouseEvent = SwingUtilities.convertMouseEvent(
                    grid, event, dispatchComponent);
            dispatchComponent.dispatchEvent(editorMouseEvent);

            return true;
        }

        private boolean shouldIgnore(MouseEvent event) {
            return event.isConsumed() ||                   
                    (!(SwingUtilities.isLeftMouseButton(event) && grid.isEnabled()));
        }

        private void setValueIsAdjusting(boolean flag) {
            grid.getSelectionModel().setValueIsAdjusting(flag);
        }

        private void adjustFocusAndSelection(MouseEvent event) {
            if (shouldIgnore(event)) {
                return;
            }

            Point point = event.getPoint();
            int row = grid.rowAtPoint(point);
            int column = grid.columnAtPoint(point);

            // The autoscroller can generate drag events outside range.
            if ((column == -1) || (row == -1)) {
                System.err.println("Out of bounds");
                return;
            }

            if (grid.editCellAt(row, column, event)) {
                setDispatchComponent(event);
                repostEvent(event);
            } else {
                grid.requestFocus();
            }

//			System.out.println("^^^^^ "+selectionRegion);
            if (selectionRegion == null) {
                selectionRegion = new SelectionRegion();
                selectionRegion.setFirstRow(row);
                selectionRegion.setFirstColumn(column);
                selectionRegion.setLastRow(row);
                selectionRegion.setLastColumn(column);
            } else {
                // for shift pressed
                if (row > selectionRegion.getFirstRow()) {
                    selectionRegion.setLastRow(row);
                }
                if (row < selectionRegion.getFirstRow()) {
                    selectionRegion.setLastRow(selectionRegion.getFirstRow());
                    selectionRegion.setFirstRow(row);
                }
                if (row == selectionRegion.getFirstRow()) {
                    selectionRegion.setLastRow(row);
                }
                if (column > selectionRegion.getFirstColumn()) {
                    selectionRegion.setLastColumn(column);
                }
                if (column < selectionRegion.getFirstColumn()) {
                    selectionRegion.setLastColumn(selectionRegion.getFirstColumn());
                    selectionRegion.setFirstColumn(column);
                }
                if (column == selectionRegion.getFirstColumn()) {
                    selectionRegion.setLastColumn(column);
                }
            }

//			System.out.println(">>>> " + selectionRegion);

            Iterator<CellSpan> it = grid.getSpanModel().getSpanIterator();
            while (it.hasNext()) {
                CellSpan span = it.next();
                if (span.getLastRow() >= selectionRegion.getFirstRow()
                        && span.getFirstRow() <= selectionRegion.getLastRow()
                        && span.getLastColumn() >= selectionRegion.getFirstColumn()
                        && span.getFirstColumn() <= selectionRegion.getLastColumn()) {
                    selectionRegion.setFirstRow(Math.min(selectionRegion.getFirstRow(), span.getFirstRow()));
                    selectionRegion.setLastRow(Math.max(selectionRegion.getLastRow(), span.getLastRow()));
                    selectionRegion.setFirstColumn(Math.min(selectionRegion.getFirstColumn(), span.getFirstColumn()));
                    selectionRegion.setLastColumn(Math.max(selectionRegion.getLastColumn(), span.getLastColumn()));
                }
            }

            GridCellEditor editor = grid.getCurrentCellEditor();
            if ((editor == null) || editor.shouldSelectCell(event)) {
                // Update selection model
                setValueIsAdjusting(true);
                if (event.isControlDown()) {
                    if (grid.getSelectionModel().isSelected(row, column)) {
                        Cell cell = new Cell(row, column);
                        boolean isSpan = grid.getSpanModel().isCellSpan(row, column);

                        if (isSpan) {
//							System.out.println("--isSpan");
                            CellSpan cellSpan = grid.getSpanModel().getSpanOver(row, column);
                            for (int i = cellSpan.getFirstRow(); i <= cellSpan.getLastRow(); i++) {
                                for (int j = cellSpan.getFirstColumn(); j <= cellSpan.getLastColumn(); j++) {
                                    grid.getSelectionModel().removeSelectionCell(new Cell(i, j));
                                }
                            }
                        } else {
                            grid.getSelectionModel().removeSelectionCell(cell);
                        }
                    } else {
                        selectionRegionChanged(grid, selectionRegion, true);
                    }
                } else {
                    selectionRegionChanged(grid, selectionRegion, false);
                }

            }
        }


    }

    /*
     protected class DropTargetHandler implements DropTargetListener {

         public void dragEnter(DropTargetDragEvent event) {
             if (canImport(event)) {
                 event.acceptDrag(DnDConstants.ACTION_COPY);
             } else {
                 event.rejectDrag();
             }
         }

         public void dragExit(DropTargetEvent event) {
         }

         public void dragOver(DropTargetDragEvent event) {
             if (canImport(event)) {
                 event.acceptDrag(DnDConstants.ACTION_COPY);
             } else {
                 event.rejectDrag();
             }
         }

         public void drop(DropTargetDropEvent event) {
             if (event.getDropAction() != DnDConstants.ACTION_COPY) {
                 return;
             }
             event.acceptDrop(DnDConstants.ACTION_COPY);

             String data = null;
             try {
                 data = (String) event.getTransferable().getTransferData(GridTransferable.GRID_FLAVOR);
             } catch (UnsupportedFlavorException e) {
                 e.printStackTrace();
             } catch (IOException e) {
                 e.printStackTrace();
             }

             Point point = event.getLocation();
             int row = grid.rowAtPoint(point);
 //			if (row < 0) {
 //				System.out.println("row = " + row);
 //			}
             int column = grid.columnAtPoint(point);
 //			if (column < 0) {
 //				System.out.println("column = " + column);
 //			}

             Object value = null;
             try {
                 // TODO
                 //				value = ReflectionUtil.createInstance(data, String.class, NameGenerator.getUniqueName("elem"));
             } catch (Exception e) {
                 // TODO
                 e.printStackTrace();
             }
 //			System.out.println("value = " + value);

             if (grid.getSpanModel().isCellSpan(row, column)) {
 //				System.out.println("Cell span (" + row + ", " + column + ")");
                 CellSpan cellSpan = grid.getSpanModel().getSpanOver(row, column);
                 grid.setValueAt(value, cellSpan.getRow(), cellSpan.getColumn());
             } else {
                 grid.setValueAt(value, row, column);
             }
             // TODO
             //			grid.getSelectionModel().setSelectionRange(row, column, row, column);
         }

         public void dropActionChanged(DropTargetDragEvent event) {
         }

         private boolean canImport(DropTargetDragEvent event) {
             DataFlavor[] flavors = event.getCurrentDataFlavors();
             for (DataFlavor flavor : flavors) {
                 if (flavor == GridTransferable.GRID_FLAVOR) {
                     // check for empty cell
                     Point point = event.getLocation();
                     //					System.out.println("point = " + point);
                     int row = grid.rowAtPoint(point);
 //					if (row < 0) {
 //						System.out.println("row = " + row);
 //					}
                     int column = grid.columnAtPoint(point);
 //					if (column < 0) {
 //						System.out.println("column = " + column);
 //					}
                     Object value = grid.getValueAt(row, column);
                     //					System.out.println("value = " + value);

                     return (value == null);
                 }
             }

             return false;
         }

     }
     */

    private static class NavigationAction extends AbstractAction {

        private int dx;
        private int dy;
        private boolean shiftPressed;

        private NavigationAction(int dx, int dy) {
            this(dx, dy, false);
        }

        private NavigationAction(int dx, int dy, boolean shiftPressed) {
            this.dx = dx;
            this.dy = dy;
            this.shiftPressed = shiftPressed;
        }

        public void actionPerformed(ActionEvent event) {

            JGrid grid = (JGrid) event.getSource();
            if (grid.isEditing() && !grid.getCurrentCellEditor().stopCellEditing()) {
                return;
            }
            SelectionModel selectionModel = grid.getSelectionModel();
            List<Cell> selectedCells = selectionModel.getSelectedCells();
            int selectedCount = selectedCells.size();
            if (selectedCount == 0) {
                return;
            }

            Cell firstSelectedCell = selectionModel.getSelectedCell();
            Cell lastSelectedCell = selectionModel.getLastSelectedCell();
            int row = lastSelectedCell.getRow();
            int column = lastSelectedCell.getColumn();

            if (shiftPressed) {

                if (shiftRegion == null) {
                    shiftRegion = new SelectionRegion();
                    shiftRegion.setFirstRow(row);
                    shiftRegion.setFirstColumn(column);
                    shiftRegion.setLastRow(row);
                    shiftRegion.setLastColumn(column);
                }
                boolean nextSelection = true;
                if ((dx == 1) && (column < grid.getColumnCount() - 1)) {
                    column++;
                } else if ((dx == -1) && (column > 0)) {
                    column--;
                } else if ((dy == 1) && (row < grid.getRowCount() - 1)) {
                    row++;
                } else if ((dy == -1) && (row > 0)) {
                    row--;
                } else {
                    nextSelection = false;
                }
                if (nextSelection) {
                    GridSelectionAlgorithm selectionAlgorithm = new GridSelectionAlgorithm(grid);

                    selectionAlgorithm.update(row, column,
                            firstSelectedCell.getRow(), firstSelectedCell.getColumn(),
                            lastSelectedCell.getRow(), lastSelectedCell.getColumn(),
                            shiftRegion);

                    selectionRegionChanged(grid, shiftRegion, false);
                    // because the selectionModel is cleared and made from scratch
                    // keep a reference to first and last selected cells
                    selectionModel.setFirstCell(firstSelectedCell);
                    selectionModel.setLastCell(new Cell(row, column));
                }

            } else {
                shiftRegion = null;
                SpanModel spanModel = grid.getSpanModel();
                if (spanModel.isCellSpan(row, column)) {
                    CellSpan span = spanModel.getSpanOver(row, column);

                    row = span.getRow();
                    column = span.getColumn();

                    if ((dx > 0) && (span.getColumnCount() > 1))  {
                       column = span.getLastColumn();
                    }
                    if ((dy > 0) && (span.getRowCount() > 1)) {
                       row = span.getLastRow();
                    }
                }

                row = clipToRange(row + dy, 0, grid.getRowCount());
                column = clipToRange(column + dx, 0, grid.getColumnCount());
                selectionModel.clearSelection();
                if (!grid.isCellSpan(row, column)) {
                    selectionModel.addSelectionCell(new Cell(row, column));
                } else {
                    CellSpan span = spanModel.getSpanOver(row, column);
                    List<Cell> cells = new ArrayList<Cell>();
                    for (int i = 0; i < span.getRowCount(); i++) {
                        for (int j = 0; j < span.getColumnCount(); j++) {
                            cells.add(new Cell(span.getRow() + i, span.getColumn() + j));
                        }
                    }
                    selectionModel.addSelectionCells(cells);
                }
            }
        }

        private int clipToRange(int i, int a, int b) {
            return Math.min(Math.max(i, a), b - 1);
        }

        @Override
        public String toString() {
            return "NavigationAction: " + dx + ", " + dy;
        }

    }

    /**
     * Action to start editing, and pass focus to the editor.
     */
    private static class StartEditingAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            JGrid grid = (JGrid) event.getSource();
            if (!grid.hasFocus()) {
                CellEditor cellEditor = grid.getCurrentCellEditor();
                if ((cellEditor != null) && !cellEditor.stopCellEditing()) {
                    return;
                }
                grid.requestFocus();
            }
            SelectionModel selectionModel = grid.getSelectionModel();
            Cell selectedCell = selectionModel.getSelectedCell();
            int row = selectedCell.getRow();
            int column = selectedCell.getColumn();
            grid.editCellAt(row, column, null);
            Component editorComponent = grid.getEditorComponent();
            if (editorComponent != null) {
                editorComponent.requestFocus();
            }
        }

    }

    private class CancelEditingAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            JGrid grid = (JGrid) event.getSource();
            grid.removeEditor();
            grid.requestFocus();
        }

        @Override
        public boolean isEnabled() {
            return grid.isEditing();
        }

    }

    private static void selectionRegionChanged(JGrid grid, SelectionRegion selectionRegion, boolean add) {
        if (!add) {
            grid.getSelectionModel().clearSelection();
        }
        List<Cell> selectedCells = new ArrayList<Cell>();
        for (int i = selectionRegion.getFirstRow(); i <= selectionRegion.getLastRow(); i++) {
            for (int j = selectionRegion.getFirstColumn(); j <= selectionRegion.getLastColumn(); j++) {
                boolean isSpan = grid.getSpanModel().isCellSpan(i, j);

                //if (!isSpan) {
                Cell cell = new Cell(i, j);
//						System.out.println("cell=" + cell);
                selectedCells.add(cell);
                //                    } else {
                //                        CellSpan cellSpan = grid.getSpanModel().getSpanOver(i, j);
                //                        if ((cellSpan.getRow() == i) && (cellSpan.getGroupColumn() == j)) {
                //                            Cell cell = new Cell(i, j);
                //                            System.out.println("cell=" + cell);
                //                            selectedCells.add(cell);
                //                        }
                //                    }
            }
        }
        grid.getSelectionModel().addSelectionCells(selectedCells);
    }

}
