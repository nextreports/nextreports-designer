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

import ro.nextreports.designer.grid.event.SpanModelEvent;
import ro.nextreports.designer.grid.event.SpanModelListener;


/**
 * A base for <code>SpanModel</code> that provides handling of listeners.
 * 
 * @author Decebal Suiu
 */
public abstract class AbstractSpanModel implements SpanModel {
	
	/**
	 * List of event listeners
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Add listener to model
	 */
	public void addSpanModelListener(SpanModelListener listener) {
		listenerList.add(SpanModelListener.class, listener);
	}

	/**
	 * Remove listener from model
	 */
	public void removeSpanModelListener(SpanModelListener listener) {
		listenerList.remove(SpanModelListener.class, listener);
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
	 * <code>SpanModelListeners</code> that registered themselves as listeners
	 * for this SpanModel.
	 * 
	 * @param event
	 *            the event to be forwarded
	 * 
	 * @see #addSpanModelListener
	 * @see SpanModelEvent
	 * @see EventListenerList
	 */
	public void fireSpanChanged(SpanModelEvent event) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SpanModelListener.class) {
				((SpanModelListener) listeners[i + 1]).spanChanged(event);
			}
		}
	}

	/**
	 * Notifies all <code>SpanModelListeners</code> that registered themselves
	 * as listeners for this SpanModel that a span was added
	 */
	public void fireCellSpanAdded(CellSpan newSpan) {
		SpanModelEvent event = new SpanModelEvent(this, SpanModelEvent.SPAN_ADDED,
				newSpan.getRow(), newSpan.getColumn(), 1, 1, newSpan
						.getRowCount(), newSpan.getColumnCount());
		fireSpanChanged(event);
	}

	/**
	 * Notifies all <code>SpanModelListeners</code> that registered themselves
	 * as listeners for this SpanModel that a span was removed
	 */
	public void fireCellSpanRemoved(CellSpan removedSpan) {
		SpanModelEvent event = new SpanModelEvent(this,
				SpanModelEvent.SPAN_REMOVED, removedSpan.getRow(), removedSpan
						.getColumn(), removedSpan.getRowCount(), removedSpan
						.getColumnCount(), 1, 1);
		fireSpanChanged(event);
	}

	/**
	 * Notifies all <code>SpanModelListeners</code> that registered themselves
	 * as listeners for this SpanModel that a span was updated
	 */
	public void fireCellSpanUpdated(int anchorRow, int anchorColumn,
			int oldRowCount, int oldColumnCount, int newRowCount,
			int newColumnCount) {
		SpanModelEvent event = new SpanModelEvent(this,
				SpanModelEvent.SPAN_UPDATED, anchorRow, anchorColumn,
				oldRowCount, oldColumnCount, newRowCount, newColumnCount);
		fireSpanChanged(event);
	}
	
}
