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

import ro.nextreports.designer.grid.event.SelectionModelEvent;
import ro.nextreports.designer.grid.event.SelectionModelListener;


/**
 * A base for <code>SelectionModel</code> that provides handling of listeners.
 *
 * @author Decebal Suiu
 */

public abstract class AbstractSelectionModel implements SelectionModel {

    /**
     * List of event listeners
     */
    protected EventListenerList listenerList = new EventListenerList();

    protected boolean isAdjusting = false;

    public void addSelectionModelListener(SelectionModelListener listener) {
        listenerList.add(SelectionModelListener.class, listener);
    }

    public void removeSelectionModelListener(SelectionModelListener listener) {
        listenerList.remove(SelectionModelListener.class, listener);
    }

    /**
     * Returns an array of all the listeners of the given type that
     * <p/>
     * were added to this model.
     *
     * @return all of the objects receiving <code>listenerType</code>
     *         <p/>
     *         notifications from this model
     */
    @SuppressWarnings("unchecked")
    public EventListener[] getListeners(Class listenerType) {
        return listenerList.getListeners(listenerType);
    }


    public boolean getValueIsAdjusting() {
        return isAdjusting;
    }


    public void setValueIsAdjusting(boolean isAdjusting) {
        this.isAdjusting = isAdjusting;
    }


    /**
     * Forwards the given notification event to all
     * <p/>
     * <code>SelectionModelListeners</code> that registered
     * <p/>
     * themselves as listeners for this SelectionModel.
     *
     * @param event the event to be forwarded
     * @see #addSelectionModelListener
     * @see SelectionModelEvent
     * @see EventListenerList
     */
    public void fireSelectionChanged(SelectionModelEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SelectionModelListener.class) {
                ((SelectionModelListener) listeners[i + 1]).selectionChanged(event);
            }
        }
    }

    /**
     * Notifies all <code>SelectionModelListeners</code> that
     * registered themselves as listeners for this SelectionModel
     * that the selection has changed
     */
    public void fireSelectionChanged() {
        fireSelectionChanged(false);
    }

    public void fireSelectionChanged(boolean rootSelection) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SelectionModelListener.class) {
                ((SelectionModelListener) listeners[i + 1]).selectionChanged(
                        new SelectionModelEvent(this, isAdjusting, rootSelection));
            }
        }
    }

    public void fireEmptySelection() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == SelectionModelListener.class) {
                SelectionModelEvent sme = new SelectionModelEvent(this, isAdjusting, false);
                sme.setEmpty(true);
                ((SelectionModelListener) listeners[i + 1]).selectionChanged(sme);
            }
        }
    }


}

