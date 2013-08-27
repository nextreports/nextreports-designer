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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import ro.nextreports.designer.grid.event.GridModelEvent;
import ro.nextreports.designer.grid.event.GridModelListener;
import ro.nextreports.designer.grid.plaf.BasicGridUI;
import ro.nextreports.designer.grid.plaf.GridUI;


/**
 * The JGrid is used to display and edit regular two-dimensional grid of cells.
 * Unlike JTable which is fundamentally vertical in that the structure is
 * determined in the columns, while the rows contain the data. JGrid is
 * symmetrical with respect to the vertical and the horizontal orientations.
 * JGrid also allows for cells to be merged into bigger rectangular arrays,
 * called spans.
 *
 * @author Decebal Suiu
 */
public class JGrid extends JComponent implements Scrollable,
		CellEditorListener, GridModelListener {

	/* Default sizes */
	public static final int DEFAULT_ROW_HEIGHT = 20;
	public static final int DEFAULT_COLUMN_WIDTH = 75;

	protected HeaderModel rowHeaderModel;
	protected HeaderModel columnHeaderModel;
	protected GridModel model;
	protected SelectionModel selectionModel;
	protected SpanModel spanModel;

	protected GridRepaintManager repaintManager;

	private HashMap<Class<Object>, GridCellEditor> editors;
	private HashMap<Class<Object>, GridCellRenderer> renderers;

	private Color gridColor;
	private boolean showGrid = true;

	/**
	 * @see #getUIClassID
	 * @see #readObject
	 */
	private static final String uiClassID = "GridUI";

	// Install UI delegate
	static {
		UIManager.getDefaults().put(uiClassID, BasicGridUI.class.getName());
	}

	/**
	 * Used by the <code>Scrollable</code> interface to determine the initial
	 * visible area.
	 */
	protected Dimension preferredViewportSize;

	/** 
	 * Used to stop recusive call in processKeyBinding.
	 */
	private boolean reentrantCall;

	/** 
	 * If editing, the <code>Component</code> that is handling the editing. 
	 */
	protected Component editorComponent;

	/**
	 * The object that overwrites the screen real estate occupied by the current
	 * cell and allows the user to change its contents.
	 */
	protected GridCellEditor cellEditor;

	/**
	 * Identifies the column of the cell being edited.
	 */
	protected int editingColumn = -1;

	/**
	 * Identifies the row of the cell being edited.
	 */
	protected int editingRow = -1;

	public JGrid() {
		this(10, 10);
	}

	public JGrid(int rows, int columns) {
		this(new DefaultGridModel(rows, columns));
	}

	public JGrid(GridModel gridModel) {
		this(gridModel, new DefaultSpanModel());
	}
	
	public JGrid(GridModel gridModel, SpanModel spanModel) {
		this(gridModel, spanModel, 
				new DefaultHeaderModel(gridModel.getRowCount(), DEFAULT_ROW_HEIGHT, SwingConstants.VERTICAL), 
				new DefaultHeaderModel(gridModel.getColumnCount(), DEFAULT_COLUMN_WIDTH, SwingConstants.HORIZONTAL),
				new DefaultSelectionModel());
	}

	public JGrid(GridModel gridModel, SpanModel spanModel, HeaderModel rowModel, 
			HeaderModel columnModel, SelectionModel selectionModel) {
		create(gridModel, spanModel, rowModel, columnModel, selectionModel);
		updateUI();
	}

	protected void create(GridModel model, SpanModel spanModel, HeaderModel rowModel, 
			HeaderModel columnModel, SelectionModel selectionModel) {
		this.model = model;
		this.spanModel = spanModel;
		this.rowHeaderModel = rowModel;
		this.columnHeaderModel = columnModel;
		this.selectionModel = selectionModel;

		this.model.addGridModelListener(this);
		repaintManager = new GridRepaintManager(this);

		createDefaults();
		updateRepaintManager();
		setOpaque(true);
	}
	
	protected void createDefaults() {
		editors = new HashMap<Class<Object>, GridCellEditor>();
		renderers = new HashMap<Class<Object>, GridCellRenderer>();

		GridCellRenderer defaultRenderer = new DefaultGridCellRenderer();
		renderers.put(Object.class, defaultRenderer);
		GridCellEditor defaultEditor = new DefaultGridCellEditor(new JTextField());
		editors.put(Object.class, defaultEditor);		
	}

	protected void updateRepaintManager() {
		rowHeaderModel.addHeaderModelListener(repaintManager);
		columnHeaderModel.addHeaderModelListener(repaintManager);
		selectionModel.addSelectionModelListener(repaintManager);
		spanModel.addSpanModelListener(repaintManager);
		model.addGridModelListener(repaintManager);
	}

	/**
	 * Returns the color used to draw grid lines.
	 *
	 * @return the <code>Color</code> used to draw grid lines
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * Sets the color used to draw grid lines.
	 *
	 * @param gridColor
	 *            the new <code>Color</code> of the grid lines
	 */
	public void setGridColor(Color gridColor) {
		this.gridColor = gridColor;
		repaint();
	}

	/**
	 * Returns true if the grid draws grid lines.
	 *
	 * @return true if the grid draws grid lines.
	 */
	public boolean getShowGrid() {
		return showGrid;
	}

	/**
	 * Sets wether grid lines should be drawn.
	 *
	 * @param show true if grid lines are to be drawn.
	 */
	public void setShowGrid(boolean show) {
		showGrid = show;
		repaint();
	}

	// end JavaBean properties

	/**
	 * Returns the number of rows in this grid's model.
	 *
	 * @return the number of rows in this grid's model.
	 */
	public int getRowCount() {
		return model.getRowCount();
	}

	/**
	 * Returns the number of columns in this grid's model.
	 *
	 * @return the number of columns in this grid's model.
	 */
	public int getColumnCount() {
		return model.getColumnCount();
	}

	/**
	 * Returns the height (in pixels) of <code>row</code>.
	 *
	 * @param row
	 *            the row whose height is to be returned
	 *
	 * @return the height (in pixels) of <code>row</code>
	 */
	public int getRowHeight(int row) {
		return rowHeaderModel.getSize(row);
	}

	/**
	 * Returns the width (in pixels) of <code>column</code>.
	 *
	 * @param column
	 *            the column whose width is to be returned
	 *
	 * @return the width (in pixels) of <code>column</code>
	 */
	public int getColumnWidth(int column) {
		return columnHeaderModel.getSize(column);
	}

	/**
	 * Sets the height for <code>row</code> to <code>height</code>.
	 *
	 * @param row
	 *            the row whose height is to be changed
	 * @param height
	 *            new row height (in pixels)
	 */
	public void setRowHeight(int row, int height) {
		rowHeaderModel.setSize(row, height);
	}

	/**
	 * Sets the width for <code>column</code> to <code>width</code>.
	 *
	 * @param column
	 *            the column whose width is to be changed
	 * @param width
	 *            new column width (in pixels)
	 */
	public void setColumnWidth(int column, int width) {
		columnHeaderModel.setSize(column, width);
	}

	/** 
	 * Return the top vertical coordinate (in pixels) of row.
	 */
	public int getRowPosition(int row) {
		return rowHeaderModel.getPosition(row);
	}

	/** 
	 * Return the left horizontal coordinate (in pixels) of column.
	 */
	public int getColumnPosition(int column) {
		return columnHeaderModel.getPosition(column);
	}

	/**
	 * Get cell bounds of (row, column).
	 */
	public Rectangle getCellBounds(int row, int column) {
		int rowCount = 1;
		int columnCount = 1;

		if (isCellSpan(row, column)) {
			CellSpan span = spanModel.getSpanOver(row, column);
			row = span.getRow();
			column = span.getColumn();
			rowCount = span.getRowCount();
			columnCount = span.getColumnCount();
		}

		Rectangle cellBounds = new Rectangle();
		cellBounds.y = getRowPosition(row);
		cellBounds.x = getColumnPosition(column);

		// Height and width include spanned rows and columns
		for (int i = 0; i < rowCount; i++) {
			cellBounds.height += getRowHeight(row + i);
		}
		for (int j = 0; j < columnCount; j++) {
			cellBounds.width += getColumnWidth(column + j);
		}

		return cellBounds;
	}

	/** 
	 * Return the row at the specified point. 
	 */
	public int rowAtPoint(Point point) {
		return rowHeaderModel.getIndex(point.y);
	}

	/** 
	 * Return the column at the specified point.
	 */
	public int columnAtPoint(Point point) {
		return columnHeaderModel.getIndex(point.x);
	}

	public boolean isCellSpan(int row, int column) {
		return spanModel.isCellSpan(row, column);
	}
	
	public GridCellRenderer getCellRenderer(int row, int column) {
		Object value = model.getValueAt(row, column);
		Class type = Object.class;
		if (value != null) {
			type = model.getValueAt(row, column).getClass();
		}
		
		return getCellRenderer(type, row, column);
	}

	public GridCellEditor getCellEditor(int row, int column) {
		Object value = model.getValueAt(row, column);
		Class type = Object.class;
		if (value != null) {
			type = model.getValueAt(row, column).getClass();
		}
		
		return getCellEditor(type, row, column);
	}

	public GridCellEditor getCellEditor(Class clazz, int row, int column) {
		GridCellEditor editor = editors.get(clazz);
		if (editor != null) {
			return editor;
		} else {
			return getCellEditor(clazz.getSuperclass(), row, column);
		}
	}

	@SuppressWarnings("unchecked")
	public void setCellEditor(Class clazz, GridCellEditor editor) {
		editors.put(clazz, editor);
	}
	
	public GridCellRenderer getCellRenderer(Class clazz, int row, int column) {
		GridCellRenderer renderer = renderers.get(clazz);
		if (renderer != null) {
			return renderer;
		} else {
			return getCellRenderer(clazz.getSuperclass(), row, column);
		}
	}

	@SuppressWarnings("unchecked")
	public void setCellRenderer(Class clazz, GridCellRenderer renderer) {
		renderers.put(clazz, renderer);
	}

	/**
	 * Prepares the renderer for painting cell(row,column)
	 */
	public Component prepareRenderer(GridCellRenderer renderer, int row, int column) {
		Object value = model.getValueAt(row, column);
		boolean isSelected = isSelected(row, column);
		Cell selectedCell = selectionModel.getSelectedCell();
		boolean hasFocus = (selectedCell != null)
				&& (selectedCell.getRow() == row)
				&& (selectedCell.getColumn() == column)
				&& isFocusOwner();
		return renderer.getRendererComponent(row,
				column, value, isSelected, hasFocus, this);
	}

	/**
	 * Prepares the editor for cell(row, column).
	 */
	public Component prepareEditor(GridCellEditor editor, int row, int column) {
		Object value = model.getValueAt(row, column);
		boolean isSelected = isSelected(row, column);
		return editor.getEditorComponent(row, column,
				value, isSelected, this);		
	}

	public boolean isSelected(int row, int column) {
		return selectionModel.isSelected(row, column);
	}

	public void ensureCellInVisibleRect(int row, int column) {
		Rectangle cellRect = getCellBounds(row, column);
		scrollRectToVisible(cellRect);
	}

	public GridModel getModel() {
		return model;
	}

	public void setModel(GridModel model) {
		this.model.removeGridModelListener(this);
		this.model.removeGridModelListener(repaintManager);
		this.model = model;
		this.model.addGridModelListener(this);
		this.model.addGridModelListener(repaintManager);
		repaintManager.resizeAndRepaint();
	}

	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SelectionModel model) {
		selectionModel.removeSelectionModelListener(repaintManager);
		selectionModel = model;
		selectionModel.addSelectionModelListener(repaintManager);
		repaintManager.repaint();
	}

	public SpanModel getSpanModel() {
		return spanModel;
	}

	public void setSpanModel(SpanModel model) {
		spanModel.removeSpanModelListener(repaintManager);
		spanModel = model;
		spanModel.addSpanModelListener(repaintManager);
		repaintManager.repaint();
	}

	public HeaderModel getRowHeaderModel() {
		return rowHeaderModel;
	}

	public void setRowHeaderModel(HeaderModel model) {
		rowHeaderModel.removeHeaderModelListener(repaintManager);
		rowHeaderModel = model;
		rowHeaderModel.addHeaderModelListener(repaintManager);
		repaintManager.resizeAndRepaint();
	}

	public HeaderModel getColumnHeaderModel() {
		return columnHeaderModel;
	}

	public void setColumnHeaderModel(HeaderModel model) {
		columnHeaderModel.removeHeaderModelListener(repaintManager);
		columnHeaderModel = model;
		columnHeaderModel.addHeaderModelListener(repaintManager);
		repaintManager.resizeAndRepaint();
	}

	/**
	 * Sync row and column sizes between models.
	 */
	public void gridChanged(GridModelEvent event) {
		int eventType = event.getType(); 
		if (eventType == GridModelEvent.ROWS_INSERTED) {
			if (rowHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) rowHeaderModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (columnHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) columnHeaderModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).insertRows(event.getFirstRow(),
						event.getRowCount());
			}
			repaintManager.resizeAndRepaint();
		} else if (eventType == GridModelEvent.ROWS_DELETED) {
			if (rowHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) rowHeaderModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (columnHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) columnHeaderModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).removeRows(event.getFirstRow(),
						event.getRowCount());
			}
			repaintManager.resizeAndRepaint();
		} else if (eventType == GridModelEvent.COLUMNS_INSERTED) {
			if (rowHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) rowHeaderModel).insertColumns(
						event.getFirstColumn(), event.getColumnCount());
			}
			if (columnHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) columnHeaderModel).insertColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).insertColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			repaintManager.resizeAndRepaint();
		} else if (eventType == GridModelEvent.COLUMNS_DELETED) {
			if (rowHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) rowHeaderModel).removeColumns(
						event.getFirstColumn(), event.getColumnCount());
			}
			if (columnHeaderModel instanceof ResizableGrid) {
				((ResizableGrid) columnHeaderModel).removeColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			if (spanModel instanceof ResizableGrid) {
				((ResizableGrid) spanModel).removeColumns(event
						.getFirstColumn(), event.getColumnCount());
			}
			repaintManager.resizeAndRepaint();
		}
	}

	public boolean editCellAt(int row, int column) {
		return editCellAt(row, column, null);
	}

	/**
	 * Programmatically starts editing the cell at <code>row</code> and
	 * <code>column</code>, if the cell is editable.
	 *
	 * @param row
	 *            the row to be edited
	 * @param column
	 *            the column to be edited
	 * @param event
	 *            event to pass into shouldSelectCell
	 * @exception IllegalArgumentException
	 *                If <code>row</code> or <code>column</code> is not in the
	 *                valid range
	 * @return false if for any reason the cell cannot be edited
	 */
	public boolean editCellAt(int row, int column, EventObject event) {
		if ((cellEditor != null) && !cellEditor.stopCellEditing()) {
			return false;
		}

		// Check out of bounds
		if (row < 0 || row >= getRowCount() || column < 0
				|| column >= getColumnCount()) {
			return false;
		}

		if (isCellSpan(row, column)) {
			// Translate cell coords to anchor of span
			CellSpan span = spanModel.getSpanOver(row, column);
			row = span.getRow();
			column = span.getColumn();
		}

		if (!model.isCellEditable(row, column)) {
			return false;
		}

		GridCellEditor editor = getCellEditor(row, column);
		if (editor != null && editor.isCellEditable(event)) {
			editorComponent = prepareEditor(editor, row, column);
			if (editorComponent == null) {
				removeEditor();
				return false;
			}
			editorComponent.setBounds(getCellBounds(row, column));
			add(editorComponent);
			editorComponent.validate();
			editorComponent.requestFocus();

			cellEditor = editor;
			setEditingRow(row);
			setEditingColumn(column);
			editor.addCellEditorListener(this);
			return true;
		}
		
		return false;
	}

	/**
	 * Discards the editor object and frees the real estate it used for cell
	 * rendering.
	 */
	public void removeEditor() {
		if (cellEditor != null) {
			cellEditor.removeCellEditorListener(this);

			requestFocus();
			if (editorComponent != null) {
				remove(editorComponent);
			}

			Rectangle cellRectangle = getCellBounds(editingRow, editingColumn);

			cellEditor = null;
			setEditingColumn(-1);
			setEditingRow(-1);
			editorComponent = null;

			repaint(cellRectangle);
		}
	}

	/**
	 * Sets the <code>editingColumn</code> variable.
	 *
	 * @param column
	 *            the column of the cell to be edited
	 *
	 * @see #editingColumn
	 */
	public void setEditingColumn(int column) {
		editingColumn = column;
	}

	/**
	 * Sets the <code>editingRow</code> variable.
	 *
	 * @param row
	 *            the row of the cell to be edited
	 *
	 * @see #editingRow
	 */
	public void setEditingRow(int row) {
		editingRow = row;
	}

	/**
	 * Returns true if a cell is being edited.
	 *
	 * @return true if the table is editing a cell
	 * @see #editingColumn
	 * @see #editingRow
	 */
	public boolean isEditing() {
		return (cellEditor == null) ? false : true;
	}

	/**
	 * Returns the component that is handling the editing session. If nothing is
	 * being edited, returns null.
	 *
	 * @return Component handling editing session
	 */
	public Component getEditorComponent() {
		return editorComponent;
	}

	/**
	 * Returns the index of the column that contains the cell currently being
	 * edited. If nothing is being edited, returns -1.
	 *
	 * @return the index of the column that contains the cell currently being
	 *         edited; returns -1 if nothing being edited
	 * @see #editingRow
	 */
	public int getEditingColumn() {
		return editingColumn;
	}

	/**
	 * Returns the index of the row that contains the cell currently being
	 * edited. If nothing is being edited, returns -1.
	 *
	 * @return the index of the row that contains the cell currently being
	 *         edited; returns -1 if nothing being edited
	 * @see #editingColumn
	 */
	public int getEditingRow() {
		return editingRow;
	}

	/**
	 * Return the current cell editor
	 */
	public GridCellEditor getCurrentCellEditor() {
		return cellEditor;
	}

	/**
	 * Invoked when editing is finished. The changes are saved and the editor is
	 * discarded.
	 * <p>
	 * Application code will not use these methods explicitly, they are used
	 * internally by JGrid.
	 *
	 * @param event
	 *            the event received
	 * @see CellEditorListener
	 */
	public void editingStopped(ChangeEvent event) {
		// take in the new value
		if (cellEditor != null) {
			Object value = cellEditor.getCellEditorValue();
			model.setValueAt(value, editingRow, editingColumn);
			removeEditor();
			requestFocus();
		}
	}

	/**
	 * Invoked when editing is canceled. The editor object is discarded and the
	 * cell is rendered once again.
	 *
	 * Application code will not use these methods explicitly, they are used
	 * internally by JSpread.
	 *
	 * @param event the event received
	 * @see CellEditorListener
	 */
	public void editingCanceled(ChangeEvent event) {
		removeEditor();
		requestFocus();
	}

	@Override
	protected boolean processKeyBinding(KeyStroke keyStroke, KeyEvent keyEvent,
			int condition, boolean pressed) {
		if (reentrantCall) {
			return false;
		}
		
		reentrantCall = true;
		boolean retValue = super.processKeyBinding(keyStroke, keyEvent, condition, pressed);

		// start editing when the ENTER key is typed
		if (!retValue && (condition == WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) && hasFocus()) {
			// we do not have a binding for the event.
			Component component = getEditorComponent();
			if (component == null) {
				// only attempt to install the editor on a KEY_PRESSED
				if ((keyEvent == null) || (keyEvent.getID() != KeyEvent.KEY_PRESSED)) {
					reentrantCall = false;
					return false;
				}
				// start when an enter is pressed
				int code = keyEvent.getKeyCode();
				if (code != KeyEvent.VK_ENTER) {
					reentrantCall = false;
					return false;
				}
				// try to install the editor
				Cell selectedCell = selectionModel.getSelectedCell();
				int row = selectedCell.getRow();
				int column = selectedCell.getColumn();
				if ((row != -1) && (column != -1) && !isEditing()) {
					if (!editCellAt(row, column)) {
						reentrantCall = false;
						return false;
					}
				}
				component = getEditorComponent();
				if (component == null) {
					reentrantCall = false;
					return false;
				}
			}
		}
		reentrantCall = false;
		
		return retValue;
	}

	/**
	 * Sets the preferred size of the viewport for this table.
	 *
	 * @param size
	 *            a <code>Dimension</code> object specifying the
	 *            <code>preferredSize</code> of a <code>JViewport</code> whose
	 *            view is this spreadsheet
	 * @see Scrollable#getPreferredScrollableViewportSize
	 */
	public void setPreferredScrollableViewportSize(Dimension size) {
		preferredViewportSize = size;
	}

	/**
	 * Returns the preferred size of the viewport for this table.
	 *
	 * @return a <code>Dimension</code> object containing the
	 *         <code>preferredSize</code> of the <code>JViewport</code> which
	 *         displays this table
	 * @see Scrollable#getPreferredScrollableViewportSize
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return preferredViewportSize;
	}

	/**
	 * Returns the scroll increment (in pixels) that completely exposes one new
	 * row or column (depending on the orientation).
	 *
	 * This method is called each time the user requests a unit scroll.
	 *
	 * @param visibleRect
	 *            the view area visible within the viewport
	 * @param orientation
	 *            either <code>SwingConstants.VERTICAL</code> or
	 *            <code>SwingConstants.HORIZONTAL</code>
	 * @param direction
	 *            less than zero to scroll up/left, greater than zero for
	 *            down/right
	 * @return the "unit" increment for scrolling in the specified direction
	 * @see Scrollable#getScrollableUnitIncrement
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return DEFAULT_COLUMN_WIDTH;
		} else {
			return DEFAULT_ROW_HEIGHT;
		}
	}

	/**
	 * Returns <code>visibleRect.height</code> or <code>visibleRect.width</code>
	 * , depending on this spreadsheet's orientation.
	 *
	 * @return <code>visibleRect.height</code> or <code>visibleRect.width</code>
	 *         per the orientation
	 * @see Scrollable#getScrollableBlockIncrement
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingConstants.VERTICAL) {
			return visibleRect.height;
		} else {
			return visibleRect.width;
		}
	}

	/**
	 * Returns false to indicate that the width of the viewport does not
	 * determine the width of the spreadsheet.
	 *
	 * @return false
	 * @see Scrollable#getScrollableTracksViewportWidth
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * Returns false to indicate that the height of the viewport does not
	 * determine the height of the spreadsheet.
	 *
	 * @return false
	 * @see Scrollable#getScrollableTracksViewportHeight
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * Returns the L&F object that renders this component.
	 *
	 * @return GridUI object
	 */
	public GridUI getUI() {
		return (GridUI) ui;
	}

	/**
	 * Sets the L&F object that renders this component.
	 *
	 * @param ui
	 *            the GridUI L&F object
	 * @see UIDefaults#getUI
	 */
	public void setUI(GridUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	/**
	 * Notification from the UIFactory that the L&F has changed.
	 *
	 * @see JComponent#updateUI
	 */
	@Override
	public void updateUI() {
		setUI((GridUI) UIManager.getUI(this));
		resizeAndRepaint();
	}

	/**
	 * Returns a string that specifies the name of the l&f class that renders
	 * this component.
	 *
	 * @return String "GridUI"
	 *
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public Object getValueAt(int row, int column) {
		return model.getValueAt(row, column);
	}

	public void setValueAt(Object value, int row, int column) {
		model.setValueAt(value, row, column);
	}

}
