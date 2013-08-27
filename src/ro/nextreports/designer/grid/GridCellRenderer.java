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

import java.awt.Component;

/**
 * This interface defines the method required by any object that would like to be a 
 * renderer for cells in a <code>JGrid</code>.
 * 
 * @author Decebal Suiu
 */
public interface GridCellRenderer {
	
	/**
	 * Returns the component used for drawing the cell. This method is used to configure 
	 * the renderer appropriately before drawing.
	 * 
	 * @param row			the row index of the cell being drawn
	 * @param column		the column index of the cell being drawn
	 * @param value			the value of the cell to be rendered. It is up to the specific renderer 
	 * 								to interpret and draw the value. For example, if <code>value</code> is 
	 * 								the string "true", it could be rendered as a string or it could be rendered 
	 * 								as a check box that is checked. <code>null</code> is a valid value
	 * @param isSelected	true if the cell is to be rendered with the selection highlighted; otherwise false
	 * @param hasFocus	if true, render cell appropriately. For example, put a special border on 
	 * 								the cell, if the cell can be edited, render in the color used to indicate editing
	 * @param grid			the <code>JGrid</code> that is asking the renderer to draw; 
	 * 								can be <code>null</code>.
	 * 
	 * @return	 				the component used for drawing the cell
	 */
	public Component getRendererComponent(int row, int column, Object value,
			boolean isSelected, boolean hasFocus, JGrid grid);
	
}
