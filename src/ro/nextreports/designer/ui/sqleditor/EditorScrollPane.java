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
package ro.nextreports.designer.ui.sqleditor;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

/**
 * An extension of <code>javax.swing.JScrollPane</code> that will only take
 * <code>PlainTextArea</code>s for its view. This class has the ability to show
 * line numbers for its text component view.<p>
 *
 * @author Decebal Suiu
 */
class EditorScrollPane extends JScrollPane {

	private JEditorPane editorPane;
	private LineNumberBorder lineNumberBorder;

	/**
	 * Creates a scroll pane with preferred size (width, height).  A default
	 * value will be used for line number color (gray), and the current
	 * line's line number will be highlighted.
	 *
	 * @param width The preferred width of <code>area</code>.
	 * @param height The preferred height of <code>area</code>.
	 * @param area The text area this scroll pane will contain.
	 * @param lineNumbersEnabled Whether line numbers are initially enabled.
	 */
	public EditorScrollPane(int width, int height, JEditorPane area,
			boolean lineNumbersEnabled) {
		this(width, height, area, lineNumbersEnabled, Color.RED);
	}

	/**
	 * Creates a scroll pane with preferred size (width, height).
	 *
	 * @param width The preferred width of <code>area</code>.
	 * @param height The preferred height of <code>area</code>.
	 * @param area The text area this scroll pane will contain.
	 * @param lineNumbersEnabled Whether line numbers are initially enabled.
	 * @param lineNumberColor The color to use for line numbers.
	 */
	public EditorScrollPane(int width, int height, JEditorPane area,
	        boolean lineNumbersEnabled, Color lineNumberColor) {
        super(area);
        setPreferredSize(new Dimension(width, height));

		// Create the text area and set it inside this scrollbar area.
		editorPane = area;

		// Create the line number list for this document.
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
		lineNumberBorder = new LineNumberBorder(this, editorPane, lineNumberColor);
		lineNumberBorder.setFont(getDefaultFont());
		setLineNumbersEnabled(lineNumbersEnabled);

		// Set miscellaneous properties.
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	/**
	 * Returns <code>true</code> if the line numbers are enabled and visible.
	 *
	 * @return Whether or not line numbers are visible.
	 */
	public boolean areLineNumbersEnabled() {
		return lineNumberBorder!=null;
	}

	/**
	 * This method is overridden so that if the user clicks in the line
	 * number border, the caret is moved.<p>
	 *
	 * This method will ONLY work if LineNumberBorder is used
	 * (not LineNumberList).
	 *
	 * @param event The mouse event.
	 */
	public void processMouseEvent(MouseEvent event) {
		if (event.getID() == MouseEvent.MOUSE_CLICKED) {
			int y = getViewport().getViewPosition().y + event.getY();
			int pos = editorPane.viewToModel(new Point(0, y));
			editorPane.setCaretPosition(pos);
		}
		super.processMouseEvent(event);
	}

	/**
	 * Toggles whether or not line numbers are visible.
	 *
	 * @param enabled Whether or not line numbers should be visible.
	 */
	public void setLineNumbersEnabled(boolean enabled) {
	    setViewportBorder(enabled ? lineNumberBorder : null);
	    revalidate();
	}

    public JEditorPane getEditorPane() {
		return editorPane;
	}

	private Font getDefaultFont() {
//        return UIManager.getFont("EditorPane.font");        
        return UIManager.getFont("TextArea.font");        
    }

}
