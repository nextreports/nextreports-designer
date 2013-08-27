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

import ro.nextreports.designer.grid.HeaderModel;


/**
 * @author Decebal Suiu
 */
public class HeaderModelEvent extends EventObject {
	
    private int firstIndex;
    private int lastIndex;

    /**
     * The entries in the range <code>[firstIndex, lastIndex]</code> have 
     * been changed.
     */
    public HeaderModelEvent(HeaderModel source, int firstIndex, int lastIndex) {
        super(source);
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
    }

    /**
     * Returns the first index of the interval that changed.
     * 
     * @return	first index of interval change
     */
    public int getFirstIndex() {
        return firstIndex;
    }

    /**
     * Returns the last index of the interval that changed.
     * 
     * @return	last index of interval change
     */
    public int getLastIndex() {
        return lastIndex;
    }
    
}
