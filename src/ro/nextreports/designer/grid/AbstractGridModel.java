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

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import ro.nextreports.designer.grid.event.GridModelEvent;
import ro.nextreports.designer.grid.event.GridModelListener;


/**
 * This abstract class provides default implementations for most of the methods
 * in the <code>GridModel</code> interface. It takes care of the management of
 * listeners and provides some conveniences for generating
 * <code>GridModelEvents</code> and dispatching them to the listeners. To create
 * a concrete <code>GridModel</code> as a subclass of
 * <code>AbstractGridModel</code> you need only provide implementations for the
 * following three methods:
 * 
 * <pre>
 * public int getRowCount();
 * 
 * public int getColumnCount();
 * 
 * public Object getValueAt(int row, int column);
 * </pre>
 * 
 * @author Decebal Suiu
 */
public abstract class AbstractGridModel implements GridModel {

	/**
	 * List of event listeners
	 */
	protected EventListenerList listenerList = new EventListenerList();

	public void addGridModelListener(GridModelListener listener) {
		listenerList.add(GridModelListener.class, listener);
	}

	public void removeGridModelListener(GridModelListener listener) {
		listenerList.remove(GridModelListener.class, listener);
	}

	/**
	 * Returns an array of all the listeners of the given type that were added
	 * to this model.
	 * 
	 * @return all of the objects receiving <code>listenerType</code>
	 *         notifications from this model
	 */
	@SuppressWarnings("unchecked")
	public EventListener[] getListeners(Class listenerType) {
		return listenerList.getListeners(listenerType);
	}

	/**
	 * Forwards the given notification event to all
	 * <code>GridModelListeners</code> that registered themselves as listeners
	 * for this GridModel.
	 * 
	 * @param event
	 *            the event to be forwarded
	 * 
	 * @see #addGridModelListener
	 * @see GridModelEvent
	 * @see EventListenerList
	 */
	public void fireGridChanged(GridModelEvent event) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == GridModelListener.class) {
				((GridModelListener) listeners[i + 1]).gridChanged(event);
			}
		}
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the cell at <code>row</code> and
	 * <code>column</code> has been updated
	 */
	public void fireGridCellUpdated(int row, int column) {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.CELLS_UPDATED, row, column, row, column);
		fireGridChanged(event);
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the rows in the range
	 * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
	 */
	public void fireGridRowsInserted(int firstRow, int lastRow) {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.ROWS_INSERTED, firstRow, 0, lastRow,
				getColumnCount() - 1);
		fireGridChanged(event);
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the rows in the range
	 * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
	 */
	public void fireGridRowsUpdated(int firstRow, int lastRow) {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.ROWS_UPDATED, firstRow, 0, lastRow, this
						.getColumnCount() - 1);
		fireGridChanged(event);
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the rows in the range
	 * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
	 */
	public void fireGridRowsDeleted(int firstRow, int lastRow) {
		GridModelEvent e = new GridModelEvent(this,
				GridModelEvent.ROWS_DELETED, firstRow, 0, lastRow, this
						.getColumnCount() - 1);
		fireGridChanged(e);
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the columns in the range
	 * <code>[firstColumn, lastColumn]</code>, inclusive, have been inserted.
	 */
	public void fireGridColumnsInserted(int firstColumn, int lastColumn) {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.COLUMNS_INSERTED, 0, firstColumn, this
						.getRowCount() - 1, lastColumn);
		fireGridChanged(event);
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the columns in the range
	 * <code>[firstColumn, lastColumn]</code>, inclusive, have been inserted.
	 */
	public void fireGridColumnsUpdated(int firstColumn, int lastColumn) {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.COLUMNS_UPDATED, 0, firstColumn, this
						.getRowCount() - 1, lastColumn);
		fireGridChanged(event);
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the columns in the range
	 * <code>[firstColumn, lastColumn]</code>, inclusive, have been deleted.
	 */
	public void fireGridColumnsDeleted(int firstColumn, int lastColumn) {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.COLUMNS_DELETED, 0, firstColumn, this
						.getRowCount() - 1, lastColumn);
		fireGridChanged(event);
	}

	/**
	 * Notifies all <code>GridModelListeners</code> that registered themselves
	 * as listeners for this GridModel that the entire model has changed
	 */
	public void fireGridModelChanged() {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.MODEL_CHANGED, 0, 0, this.getRowCount() - 1,
				this.getColumnCount() - 1);
		fireGridChanged(event);
	}

    //@todo fire for bulk selection
    public void fireGridColumnResized(int row, int column) {
		GridModelEvent event = new GridModelEvent(this,
				GridModelEvent.COLUMNS_RESIZED, row, column, row,
				column);
		fireGridChanged(event);
	}
	
}
