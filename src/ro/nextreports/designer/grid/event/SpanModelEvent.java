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

import ro.nextreports.designer.grid.SpanModel;


/**
 * @author Decebal Suiu
 */
public class SpanModelEvent extends EventObject {
	
    /**
     * The entire model was changed
     */
    public static int MODEL_CHANGED = 0;
    public static int SPAN_ADDED = 1;
    public static int SPAN_REMOVED = 2;
    public static int SPAN_UPDATED = 3;

    private int type;
    private int anchorRow;
    private int anchorColumn;
    private int oldRowCount;
    private int oldColumnCount;
    private int newRowCount;
    private int newColumnCount;

    /**
     * Constructs a new event for a change to a <code>SpanModel</code>
     */
    public SpanModelEvent(
        SpanModel source,
        int type,
        int anchorRow,
        int anchorColumn,
        int oldRowCount,
        int oldColumnCount,
        int newRowCount,
        int newColumnCount) {
        super(source);
        this.type = type;
        this.anchorRow = anchorRow;
        this.anchorColumn = anchorColumn;
        this.oldRowCount = oldRowCount;
        this.oldColumnCount = oldColumnCount;
        this.newRowCount = newRowCount;
        this.newColumnCount = newColumnCount;
    }

    /**
     *  Returns the anchor (top) row of the span event
     */
    public int getAnchorRow() {
        return anchorRow;
    }

    /**
     * Returns the anchor (leftmost) column of the span
     */
    public int getAnchorColumn() {
        return anchorColumn;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_REMOVED</code> events
     */
    public int getOldRowCount() {
        return oldRowCount;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_REMOVED</code> events
     */
    public int getOldColumnCount() {
        return oldColumnCount;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_ADDED</code> events
     */
    public int getNewRowCount() {
        return newRowCount;
    }

    /**
     * Valid for <code>SPAN_UPDATED</code> and <code>SPAN_ADDED</code> events
     */
    public int getNewColumnCount() {
        return newColumnCount;
    }

    /**
     * Returns the type of the event
     */
    public int getType() {
        return type;
    }
    
}
