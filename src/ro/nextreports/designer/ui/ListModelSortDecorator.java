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
package ro.nextreports.designer.ui;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Decebal Suiu
 */
public class ListModelSortDecorator implements ListModel, ListDataListener {

    private ListModel realModel;

    private int indexes[];

    public ListModelSortDecorator(ListModel model) {
        this.realModel = model;
        realModel.addListDataListener(this);
        allocate();
    }

    public void intervalAdded(ListDataEvent e) {
        allocate();
    }

    public void intervalRemoved(ListDataEvent e) {
        allocate();
    }

    public void contentsChanged(ListDataEvent e) {
        allocate();
    }

    private void allocate() {
        indexes = new int[getSize()];

        for (int i = 0; i < indexes.length; ++i) {
            indexes[i] = i;
        }
    }

    public void sort() {
        int rowCount = getSize();

        for (int i = 0; i < rowCount; i++) {
            for (int j = i + 1; j < rowCount; j++) {
                if (compare(indexes[i], indexes[j]) < 0) {
                    swap(i, j);
                }
            }
        }
    }

    public void swap(int i, int j) {
        int tmp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = tmp;
    }

    public int compare(int i, int j) {
        Object io = realModel.getElementAt(i);
        Object jo = realModel.getElementAt(j);

        int c = jo.toString().compareTo(io.toString());

        return (c < 0) ? -1 : ((c > 0) ? 1 : 0);
    }

    /**
     * Returns the length of the list.
     */
    public int getSize() {
        return realModel.getSize();
    }

    /**
     * Returns the value at the specified index.
     */
    public Object getElementAt(int index) {
        return realModel.getElementAt(indexes[index]);
    }

    /**
     * Add a listener to the list that's notified each time a change to the data
     * model occurs.
     *
     * @param l
     *                 the ListDataListener
     */
    public void addListDataListener(ListDataListener l) {
        realModel.addListDataListener(l);
    }

    /**
     * Remove a listener from the list that's notified each time a change to the
     * data model occurs.
     *
     * @param l
     *                 the ListDataListener
     */
    public void removeListDataListener(ListDataListener l) {
        realModel.removeListDataListener(l);
    }

}

