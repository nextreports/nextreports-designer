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

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.border.CompoundBorder;

import ro.nextreports.designer.grid.DefaultGridCellRenderer;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.property.CustomLineBorder;

import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.Border;
import ro.nextreports.engine.band.Padding;

/**
 * @author Decebal Suiu
 */
class ReportCellRenderer extends DefaultGridCellRenderer {

	@Override
	public Component getRendererComponent(int row, int column, Object value,
			boolean isSelected, boolean hasFocus, JGrid grid) {
		super.getRendererComponent(row, column, value, isSelected, hasFocus, grid);
		BandElement element = (BandElement) value;
              
        setText(element.getText());
		//if (!isSelected) {        
            setForeground(element.getForeground());
			setBackground(element.getBackground());
		//}
		Padding padding = element.getPadding();
        Border border = element.getBorder();
        if (border != null) {
            javax.swing.border.Border outer = new CustomLineBorder(border);
            javax.swing.border.Border inner = null;
            if (padding != null) {
                inner = BorderFactory.createEmptyBorder(padding.getTop(), padding.getLeft(),
                        padding.getBottom(), padding.getRight());
            } else {
                inner = BorderFactory.createEmptyBorder(0, 0, 0, 0);
            }
            CompoundBorder cBorder = new CompoundBorder(outer, inner);
            setBorder(cBorder);
        } else {
            if (padding != null) {
                setBorder(BorderFactory.createEmptyBorder(padding.getTop(), padding.getLeft(),
                        padding.getBottom(), padding.getRight()));
            }
        }

        setFont(element.getFont());
		setHorizontalAlignment(element.getHorizontalAlign());
        setVerticalAlignment(element.getVerticalAlign());

        return this;
	}

}
