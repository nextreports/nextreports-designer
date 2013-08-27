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

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Nov 13, 2008
 * Time: 4:08:45 PM
 */
public class GridHeaderRenderer extends JLabel implements GridCellRenderer {

    public static Color SELECTION_COLOR = new Color(163, 208, 252);

    public GridHeaderRenderer() {
        super();
    }

    /**
     * @see GridCellRenderer#getRendererComponent(int,
     *      int, Object, boolean, boolean, JGrid)
     */
    public Component getRendererComponent(int row, int column, Object value,
                                          boolean isSelected, boolean hasFocus, JGrid grid) {
        // TODO
//			if (isSelected) {
//				...
//			} else {
//				setForeground(grid.getForeground());
//				setBackground(grid.getBackground());
//			}

        // this needs to be updated if the LaF changes
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//	        selectedBorder = BorderFactory.createRaisedBevelBorder();
        setFont(UIManager.getFont("TableHeader"));
//	        selectedFont = UIManager.getFont("TableHeader"));//.deriveFont(normalFont.getStyle() | Font.BOLD);
        setForeground(UIManager.getColor("TableHeader.foreground"));        
        if (grid.getSelectionModel().isColumnSelected(column))  {                        
            setBackground(SELECTION_COLOR);
        } else {
            setBackground(UIManager.getColor("TableHeader.background"));
        }
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setText((value == null) ? "" : value.toString());

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
     * @param value the string value for this cell; if value is <code>null</code>
     *              it sets the text value to an empty string
     * @see JLabel#setText
     */
    protected void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
		}

	}
