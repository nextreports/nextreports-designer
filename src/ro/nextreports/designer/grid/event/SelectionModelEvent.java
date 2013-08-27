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
package ro.nextreports.designer.grid.event;


import java.util.EventObject;

import ro.nextreports.designer.grid.SelectionModel;




/**
 * @author Decebal Suiu
 */

public class SelectionModelEvent extends EventObject {


    private boolean isAdjusting;
    private boolean rootSelection;
    private boolean isEmpty;

    public SelectionModelEvent(SelectionModel source, boolean isAdjusting) {
        this(source, isAdjusting, false);
    }

    public SelectionModelEvent(SelectionModel source, boolean isAdjusting, boolean rootSelection) {
        super(source);
        this.isAdjusting = isAdjusting;
        this.rootSelection = rootSelection;
    }

    public boolean isAdjusting() {
        return isAdjusting;
    }

    public boolean isRootSelection() {
        return rootSelection;
    }

    public SelectionModel getSelectionModel() {
        return (SelectionModel) source;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}

