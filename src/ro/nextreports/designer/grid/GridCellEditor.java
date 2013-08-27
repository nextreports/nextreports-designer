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
import javax.swing.CellEditor;

/**
 * This interface defines the method that editors must implement to be used as
 * an editor by <code>JGrid</code>.
 * 
 * @author Decebal Suiu
 */
public interface GridCellEditor extends CellEditor {

	/**
	 * <p>
	 * Sets an initial <code>value</code> for the editor. This will cause the
	 * editor to <code>stopEditing</code> and lose any partially edited value if
	 * the editor is editing when this method is called.
	 * </p>
	 * 
	 * <p>
	 * Returns the component that should be added to the client's
	 * <code>Component</code> hierarchy. Once installed in the client's
	 * hierarchy this component will then be able to draw and receive user
	 * input.
	 * </p>
	 * 
	 * @param row
	 *            the row of the cell being edited
	 * @param column
	 *            the column of the cell being edited
	 * @param value
	 *            the value of the cell to be edited; it is up to the specific
	 *            editor to interpret and draw the value. For example, if value
	 *            is the string "true", it could be rendered as a string or it
	 *            could be rendered as a check box that is checked.
	 *            <code>null</code> is a valid value
	 * @param isSelected
	 *            true if the cell is to be rendered with highlighting
	 * @param grid
	 *            the <code>JGrid</code> that is asking the editor to edit; can
	 *            be <code>null</code>
	 * 
	 * @return the component for editing
	 */
	public Component getEditorComponent(int row, int column, Object value,
			boolean isSelected, JGrid grid);
	
}
