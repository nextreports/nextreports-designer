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
package ro.nextreports.designer;

import ro.nextreports.engine.band.BandElement;

import java.util.List;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 25-Sep-2009
// Time: 10:38:05

import ro.nextreports.designer.grid.Cell;

// Paste context for multiple cells paste
public class PasteContext {

    private List<BandElement> elements;
    private List<Cell> cells;
    private List<Integer> columnSizes;
    
    public PasteContext() {    	
    }

    public PasteContext(List<BandElement> elements, List<Cell> cells, List<Integer> columnSizes) {
        this.elements = elements;
        this.cells = cells;
        this.columnSizes = columnSizes;
    }

    public List<BandElement> getElements() {
        return elements;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public List<Integer> getColumnSizes() {
        return columnSizes;
    }

    public String toString() {
        return "PasteContext{" +
                "elements=" + elements +
                ", cells=" + cells +
                ", columnSizes=" + columnSizes + 
                '}';
    }
}
