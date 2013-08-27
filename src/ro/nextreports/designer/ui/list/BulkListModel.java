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
package ro.nextreports.designer.ui.list;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

/**
 * This custom ListModel provides three methods to handle multiple objects:
 *
 * public void addAll( Object[] objects )
 * public void clear()
 * public Object[] toArray()
 *
 * In the DefaultListModel, to add 100 objects addElement() must be invoked 100 times.
 * In the custom model, the addAll() method can be invoked only once.
 * If an application operates on multiple objects, performance can almost always be
 * improved by writing a custom model.
 *
 * @author Decebal Suiu
 */
public class BulkListModel extends AbstractListModel {

    protected ArrayList list;

    public BulkListModel(int size) {
        list = new ArrayList(size);
    }

    public Object getElementAt(int i) {
        return (list.get(i));
    }

    public int getSize() {
        return (list.size());
    }

    public void addElement(Object o) {
        list.add(o);
        fireIntervalAdded(this, list.size() - 1, list.size() - 1);
    }

    public void removeElement(int i) {
        list.remove(i);
        fireIntervalRemoved(this, i, i);
    }

    public void addAll(Object[] objects) {
        for (int i = 0; (i < objects.length); i++) {
            list.add(objects[i]);
        }
        fireIntervalAdded(this, list.size() - objects.length, list.size() - 1);
    }

    public void clear() {
        int size = list.size();
        list.clear();
        fireIntervalRemoved(this, 0, size - 1);
    }

    public Object[] toArray() {
        return (list.toArray());
    }

}
