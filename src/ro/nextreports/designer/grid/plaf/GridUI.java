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
package ro.nextreports.designer.grid.plaf;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import ro.nextreports.designer.grid.JGrid;


/**
 * Pluggable look and feel interface for JGrid. 
 * 
 * @author Decebal Suiu
 */
public abstract class GridUI extends ComponentUI {
	
	protected JGrid grid;
	
	@Override
	public void paint(Graphics g, JComponent c) {
		if (grid.getRowCount() <= 0 || grid.getColumnCount() <= 0) {
			return; // nothing to paint
		}

		Rectangle clip = g.getClipBounds();
		Point minLocation = clip.getLocation();
		Point maxLocation = new Point(clip.x + clip.width - 1, clip.y
				+ clip.height - 1);
		int rowMin = grid.rowAtPoint(minLocation);
		int rowMax = grid.rowAtPoint(maxLocation);
		// This should never happen.
		if (rowMin == -1) {
			rowMin = 0;
		}
		// If the spread does not have enough rows to fill the view we'll get -1.
		// Replace this with the index of the last row.
		if (rowMax == -1) {
			rowMax = grid.getRowCount() - 1;
		}
		int colMin = grid.columnAtPoint(minLocation);
		int colMax = grid.columnAtPoint(maxLocation);
		// This should never happen.
		if (colMin == -1) {
			colMin = 0;
		}
		// If the spread does not have enough columns to fill the view we'll get -1.
		// Replace this with the index of the last column.
		if (colMax == -1) {
			colMax = grid.getColumnCount() - 1;
		}

		// Paint cells
		paintCells(g, rowMin, rowMax, colMin, colMax);

		// Paint grid
		paintGrid(g, rowMin, rowMax, colMin, colMax);

		// Paint spans
		paintSpans(g, rowMin, rowMax, colMin, colMax);

        // Paint selection
        paintSelection(g, rowMin, rowMax, colMin, colMax);

        // Paint editor
		paintEditor(g);
	}

	public abstract void paintEditor(Graphics g);
	
	public abstract void paintCells(Graphics g, int rowMin, int rowMax, int colMin, int colMax);
	
	/**
	 * Special paint handler for merged cell regions.
	 */
	public abstract void paintSpans(Graphics g, int rowMin, int rowMax, int colMin, int colMax);

	public abstract void paintGrid(Graphics g, int rowMin, int rowMax, int colMin, int colMax);

    public abstract void paintSelection(Graphics g, int rowMin, int rowMax, int colMin, int colMax);

}
