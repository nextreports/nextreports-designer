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

import ro.nextreports.designer.grid.event.HeaderModelEvent;
import ro.nextreports.designer.grid.event.HeaderModelListener;


/**
 * A base for <code>HeaderModel</code> that provides handling of listeners.
 * 
 * @author Decebal Suiu
 */
public abstract class AbstractHeaderModel implements HeaderModel {
	
    /**
      * List of event listeners.
      */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Add listener to model.
     */
    public void addHeaderModelListener(HeaderModelListener listener) {
        listenerList.add(HeaderModelListener.class, listener);
    }

    /**
     * Remove listener from model.
     */
    public void removeHeaderModelListener(HeaderModelListener listener) {
        listenerList.remove(HeaderModelListener.class, listener);
    }

    /**
     * Returns an array of all the listeners of the given type that
     * were added to this model.
     *
     * @return all of the objects receiving <code>listenerType</code>
     *		notifications from this model
     */
    @SuppressWarnings("unchecked")
	public EventListener[] getListeners(Class listenerType) {
        return listenerList.getListeners(listenerType);
    }

    /**
     * Forwards the given notification event to all
     * <code>HeaderModelListeners</code> that registered
     * themselves as listeners for this HeaderModel.
     *
     * @param event  the event to be forwarded
     *
     * @see #addHeaderModelListener
     * @see HeaderModelEvent
     * @see EventListenerList
     */
    public void fireHeaderChanged(HeaderModelEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == HeaderModelListener.class) {
                ((HeaderModelListener) listeners[i + 1]).headerChanged(event);
            }
        }
    }

    /**
    * Notifies all <code>HeaderModelListeners</code> that 
    * registered themselves as listeners for this HeaderModel
    * that the size at index changed.
    */
    public void fireIndexChanged(int index) {
        fireIntervalChanged(index, index);
    }

    /**
    * Notifies all <code>HeaderModelListeners</code> that 
    * registered themselves as listeners for this HeaderModel
    * that the sizes between firstIndex and lastIndex have changed.
    */
    public void fireIntervalChanged(int firstIndex, int lastIndex) {
        fireHeaderChanged(new HeaderModelEvent(this, firstIndex, lastIndex));
    }
    
}
