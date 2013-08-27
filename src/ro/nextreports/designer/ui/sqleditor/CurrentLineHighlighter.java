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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/** 
 * This class can be used to highlight the current line for any JTextComponent. 
 * 
 * @author Decebal Suiu 
 */ 
public class CurrentLineHighlighter { 
	
    private static final String LINE_HIGHLIGHT = "lineHighlight"; 
    private static final String PREVIOUS_CARET = "previousCaret";
    
    private static Color color = new Color(255, 255, 204); 
//    private static Color color = new Color(232, 242, 254); 
 
    private CurrentLineHighlighter() {
    } 
 
    public static void install(JTextComponent component) { 
        try { 
            Object tag = component.getHighlighter().addHighlight(0, 0, painter); 
            component.putClientProperty(LINE_HIGHLIGHT, tag); 
            component.putClientProperty(PREVIOUS_CARET, new Integer(component.getCaretPosition())); 
            component.addCaretListener(caretListener); 
            component.addMouseListener(mouseListener); 
            component.addMouseMotionListener(mouseListener); 
        } catch(BadLocationException e) {
        	// ignore
        } 
    } 
 
    public static void uninstall(JTextComponent c) { 
        c.putClientProperty(LINE_HIGHLIGHT, null); 
        c.putClientProperty(PREVIOUS_CARET, null); 
        c.removeCaretListener(caretListener); 
        c.removeMouseListener(mouseListener); 
        c.removeMouseMotionListener(mouseListener); 
    } 
 
    /** 
     * Fetches the previous caret location, stores the current caret location, 
     * If the caret is on another line, repaint the previous line and the current line 
     * 
     * @param c the text component 
     */ 
    private static void currentLineChanged(JTextComponent c) {
        try { 
            int previousCaret = ((Integer) c.getClientProperty(PREVIOUS_CARET)).intValue(); 
            Rectangle prev = c.modelToView(previousCaret); 
            Rectangle r = c.modelToView(c.getCaretPosition()); 
            c.putClientProperty(PREVIOUS_CARET, new Integer(c.getCaretPosition())); 
 
            if ((prev != null) && (prev.y != r.y)) { 
                c.repaint(0, prev.y, c.getWidth(), r.height); 
                c.repaint(0, r.y, c.getWidth(), r.height); 
            } 
        } catch (BadLocationException e) {
        	// ignore
        } 
    } 

    private static CaretListener caretListener = new CaretListener() {
    	
        public void caretUpdate(CaretEvent event) { 
            currentLineChanged((JTextComponent) event.getSource()); 
        }
        
    }; 
 
    private static MouseInputAdapter mouseListener = new MouseInputAdapter() { 
    	
    	@Override
        public void mousePressed(MouseEvent event) { 
            currentLineChanged((JTextComponent) event.getSource()); 
        }
        
    	@Override
        public void mouseDragged(MouseEvent e) { 
            currentLineChanged((JTextComponent) e.getSource()); 
        }
        
    }; 
  
    private static Highlighter.HighlightPainter painter = new Highlighter.HighlightPainter() {
    	
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            try { 
                Rectangle r = c.modelToView(c.getCaretPosition()); 
//                if (c.getSelectedText() == null) { 
                	// no selection
                	g.setColor(color); 
                	g.fillRect(0, r.y, c.getWidth(), r.height);
//                } else { 
                	// selection
//                	c.repaint(0, r.y, c.getWidth(), r.height);
//                }
            } catch (BadLocationException e) {
            	// ignore
            } 
        }
        
    }; 
    
 }
