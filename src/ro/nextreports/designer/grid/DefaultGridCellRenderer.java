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

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JLabel;

/**
 * Generic implementation of <code>GridCellRenderer</code>. This implementation
 * applies the cell's format to the contents.
 * Refer to the <a href="http://java.sun.com/j2se/1.4.2/docs/api/javax/swing/table/DefaultTableCellRenderer.html#override"> 
 * Implementation note </a> for further details.
 * 
 * @author Decebal Suiu
 */
public class DefaultGridCellRenderer extends JLabel implements GridCellRenderer {

	public Component getRendererComponent(int row, int column, Object value,
			boolean isSelected, boolean hasFocus, JGrid grid) {
		if (value == null) {
			value = "";
		}

        if ("".equals(value)) {
            setBackground(Color.WHITE);
        }

//        if (isSelected) {
//			setForeground(grid.getSelectionForegroundColor());
//			setBackground(grid.getSelectionBackgroundColor());
//        } else {
//			setForeground(Color.BLACK);
//			setBackground(Color.WHITE);
//		}
		setValue(value);
		
		return this;
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public void validate() {
	}

	@Override
	public void revalidate() {
	}

	@Override
	public void repaint(long tm, int x, int y, int width, int height) {
	}

	@Override
	public void repaint(Rectangle r) {
	}

	@Override
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
	}

	@Override
	public void firePropertyChange(String propertyName, boolean oldValue,
			boolean newValue) {
	}

	/**
	 * Sets the <code>String</code> object for the cell being rendered to
	 * <code>value</code>.
	 * 
	 * @param value
	 *            the string value for this cell; if value is <code>null</code>
	 *            it sets the text value to an empty string
	 * @see JLabel#setText
	 */
	protected void setValue(Object value) {
		setText((value == null) ? "" : value.toString());
	}
	
}
