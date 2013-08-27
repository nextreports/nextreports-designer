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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

/**
 * Used by <code>EditorScrollPane</code> to display line numbers for a text
 * area. This component is capable of displaying line numbers for any
 * <code>JEditorPane</code> 
 * You can also choose the line number font, color.
 *
 * @author Decebal Suiu
 */
class LineNumberBorder implements Border, CaretListener, DocumentListener,
		PropertyChangeListener, ChangeListener {

	private static final int MIN_CELL_WIDTH = 24;
	private static final int RHS_BORDER_WIDTH = 8;

	private static final Color DEFAULT_FOREGROUND = new Color(128, 128, 128);
    private static final Color DEFAULT_BORDER_LINE_COLOR = Color.LIGHT_GRAY;

	private JEditorPane editorPane;
	private JScrollPane scrollPane;

	private Font font;
	private Color foreground;
	private Color background;

	private int currentLine; // The last line the caret was on.

	private Insets insets;
	private int cellHeight;	// The height of a "cell" for a line number when word wrap is off.
	private int ascent;	// The ascent to use when painting line numbers.

	private int currentNumLines;

    private Color borderLineColor = DEFAULT_BORDER_LINE_COLOR;

	/**
	 * Constructs a new <code>LineNumberBorder</code> using default values for
	 * line number color (gray).
	 *
	 * @param scrollPane The scroll pane using this border as the viewport
	 *                   border.
	 * @param editorPane The text component for which line numbers will be
	 *                 displayed.
	 */
	public LineNumberBorder(JScrollPane scrollPane, JEditorPane editorPane) {
		this(scrollPane, editorPane, DEFAULT_FOREGROUND);
	}

	/**
	 * Constructs a new <code>LineNumberBorder</code>.
	 *
	 * @param scrollPane The scroll pane using this border as the viewport
	 *                   border.
	 * @param editorPane The text component for which line numbers will be
	 *                 displayed.
	 * @param numberColor The color to use for the line numbers.  If
	 *                    <code>null</code>, a default is used.
	 */
	public LineNumberBorder(JScrollPane scrollPane, JEditorPane editorPane,
			Color numberColor) {
		this.editorPane = editorPane;
		this.scrollPane = scrollPane;

		setForeground(numberColor != null ? numberColor : DEFAULT_FOREGROUND);
		Color bg = editorPane.getBackground();
		setBackground(bg == null ? Color.WHITE : bg);

		editorPane.addCaretListener(this);
		editorPane.addPropertyChangeListener(this);
		scrollPane.getViewport().addChangeListener(this);
		editorPane.getDocument().addDocumentListener(this);

		currentLine = 1;

		setFont(null); // default font.
		insets = new Insets(0, 0, 0, 0);

		updateCellHeights();
		updateCellWidths();
	}

	/**
	 * Called whenever the caret changes position; highlight the correct line
	 * number.
	 *
	 * @param event The caret event.
	 */
	public void caretUpdate(CaretEvent event) {
		int caretPosition = editorPane.getCaretPosition();

			int line = editorPane.getDocument().getDefaultRootElement().
				getElementIndex(caretPosition) + 1;
			if (currentLine != line) {
				currentLine = line;
				scrollPane.repaint();
			}
	}

	public void changedUpdate(DocumentEvent event) {
	}

	/**
	 * Returns the background color of the line number list.
	 *
	 * @return The background color.
	 * @see #setBackground
	 */
	public Color getBackground() {
		return background;
	}

	/**
	 * Returns the insets of this border.
	 *
	 * @param component This parameter is ignored.
	 * @return The insets of this border.
	 */
	public Insets getBorderInsets(Component component) {
		return insets;
	}

	/**
	 * Returns the font used for the line numbers.
	 *
	 * @return The font.
	 * @see #setFont
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Returns the foreground color of the line number list.
	 *
	 * @return The foreground color.
	 * @see #setForeground
	 */
	public Color getForeground() {
		return foreground;
	}

	/**
	 * Returns the color to use to paint line numbers.
	 *
	 * @return The color used when painting line numbers.
	 * @see #setLineNumberColor
	 */
	public Color getLineNumberColor() {
		return getForeground();
	}

	/**
	 * Called whenever a character is input (key is typed) in the text
	 * document we're line-numbering.
	 *
	 * @param event The document event.
	 */
	public void insertUpdate(DocumentEvent event) {
		int newNumLines = editorPane.getDocument().getDefaultRootElement().getElementCount();
		if (newNumLines > currentNumLines) {
			// Adjust the amount of space the line numbers take up,
			// if necessary.
			if (newNumLines/10 > currentNumLines / 10) {
				updateCellWidths();
			}
			currentNumLines = newNumLines;
		}
	}

	/**
	 * Returns whether this border is opaque.
	 *
	 * @return Whether this border is opaque.
	 */
	public boolean isBorderOpaque() {
		return true;
	}

	/**
	 * Paints the line numbers.
	 *
	 * @param c The text area.
	 * @param g The graphics context.
	 * @param x The x-coordinate of the border.
	 * @param y The y-coordinate of the border.
	 * @param width The width of the border.
	 * @param height The height of the border.
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Element root = editorPane.getDocument().getDefaultRootElement();
		Rectangle visibleRect = editorPane.getVisibleRect();

		if (visibleRect == null) {
			return;
		}

		// Fill in the background the same color as the text component.
		g.setColor(getBackground());
		g.fillRect(x, y, insets.left,height);
		g.setFont(font);

		// Get the first and last lines to paint.
		int topLine = visibleRect.y / cellHeight + 1;
		int bottomLine = Math.min(topLine + visibleRect.height / cellHeight,
				root.getElementCount()) + 1;

		// Get where to start painting (top of the row), and where to paint
		// the line number (drawString expects y == baseline).
		// We need to be "scrolled up" up just enough for the missing part of
		// the first line.
		int actualTopY = y - (visibleRect.y%cellHeight);
		Insets textAreaInsets = editorPane.getInsets();
		if (textAreaInsets != null) {
			actualTopY += textAreaInsets.top;
		}
		int y2 = actualTopY + ascent;

		// Paint the "border" line.
		g.setColor(borderLineColor);
		g.drawLine(x + insets.left - 4, 0, x + insets.left - 4, visibleRect.height + 1);

		g.setColor(getForeground());
		FontMetrics metrics = g.getFontMetrics();
		int rhs = x + insets.left - RHS_BORDER_WIDTH;
		for (int i= topLine; i < bottomLine; i++) {
			String number = Integer.toString(i);
			int w = (int) metrics.getStringBounds(number, g).getWidth();
			g.drawString(number, rhs - w, y2);
			y2 += cellHeight;
		}
	}

	/**
	 * Called whenever the text area fires a property change event.
	 *
	 * @param event The event.
	 */
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();

		// If they changed the background color of the text area.
		if (name.equals("background")) {
			Color bg = editorPane.getBackground();
			setBackground(bg == null ? Color.WHITE : bg);
			scrollPane.repaint();
			return;
		}

		// If they change the text area's font, we need to update cell heights
		// to match the font's height.
		if (name.equals("font")) {
			updateCellHeights();
			return;
		}

		// If they change the text area's syntax scheme (i.e., it is an
		// SyntaxTextArea), update cell heights.
		// TODO
//		if (name.equals(SyntaxTextArea.SYNTAX_SCHEME_PROPERTY)) {
//			updateCellHeights();
//		}
	}

	/**
	 * Called whenever a character is removed (ie backspace, delete) in the
	 * text document we're line-numbering.
	 */
	public void removeUpdate(DocumentEvent event) {
		int newNumLines = editorPane.getDocument().getDefaultRootElement().getElementCount();
		if (newNumLines < currentNumLines) { // Used to be <=
			// Adjust the amount of space the line numbers take up, if necessary.
			if (newNumLines / 10 < currentNumLines / 10) {
				updateCellWidths();
			}
			currentNumLines = newNumLines;
			// Need to repaint in case they removed a line by pressing delete.
			scrollPane.repaint();
		}
	}

	/**
	 * Sets the background color of the line number list.
	 *
	 * @param background The background color.
	 * @see #getBackground
	 */
	public void setBackground(Color background) {
		if (background != null && !background.equals(this.background)) {
			this.background = background;
		}
	}

	/**
	 * Sets the font used to render the line numbers.
	 *
	 * @param font The <code>java.awt.Font</code> to use to to render the line
	 *             numbers.  If <code>null</code>, a 10-point monospaced font
	 *             will be used.
	 * @see #getFont
	 */
	public void setFont(Font font) {
		if (font == null) {
			font = new Font("monospaced", Font.PLAIN, 10);
		}

		this.font = font;
	}

	/**
	 * Sets the foreground color of the line number list.
	 *
	 * @param foreground The foreground color.
	 * @see #getForeground
	 */
	public void setForeground(Color foreground) {
		if (foreground != null && !foreground.equals(this.foreground)) {
			this.foreground = foreground;
		}
	}

	/**
	 * Sets the color to use to paint line numbers.
	 *
	 * @param color The color to use when painting line numbers.
	 * @see #getLineNumberColor
	 */
	public void setLineNumberColor(Color color) {
		setForeground(color);
	}

	/**
	 * Messages from the viewport.
	 *
	 * @param event The change event. 
	 */
	public void stateChanged(ChangeEvent event) {
		scrollPane.repaint();
	}

    /**
     * Returns the color to use to paint border line.
     *
     * @return The color used when painting border line.
     * @see #setBorderLineColor
     */
    public Color getBorderLineColor() {
        return borderLineColor;
    }

    /**
     * Sets the color of the border line.
     *
     * @param lineColor The border line color.
     * @see #getBorderLineColor
     */
    public void setBorderLineColor(Color lineColor) {
        this.borderLineColor = lineColor;
    }

	/**
	 * Changes the height of the cells in the JList so that they are as tall as
	 * the height of a line of text in the text area.
	 */
	private void updateCellHeights() {
		FontMetrics fontMetrics = editorPane.getFontMetrics(editorPane.getFont());
		cellHeight = fontMetrics.getHeight();
		ascent = fontMetrics.getMaxAscent();
	}

	/**
	 * Changes the width of the cells in the JList so you can see every digit
	 * of each.
	 */
	private void updateCellWidths() {
		// Adjust the amount of space the line numbers take up, if necessary.
		Font font = getFont();
		if (font != null) {
			FontMetrics fontMetrics = editorPane.getFontMetrics(font);
			int count = 0;
			int numLines = editorPane.getDocument().getDefaultRootElement().getElementCount();
			while (numLines >= 10) {
				numLines = numLines/10;
				count++;
			}
			insets.left = Math.max(fontMetrics.charWidth('9') * (count + 2) + 5,
					MIN_CELL_WIDTH);
		}
	}
	
	protected Rectangle getVisibleEditorRect() {
		Rectangle alloc = editorPane.getBounds();
		if ((alloc.width > 0) && (alloc.height > 0)) {
			alloc.x = alloc.y = 0;
			Insets insets = editorPane.getInsets();
			alloc.x += insets.left;
			alloc.y += insets.top;
			alloc.width -= insets.left + insets.right;
			alloc.height -= insets.top + insets.bottom;
			return alloc;
		}
		
		return null;
	}

}
