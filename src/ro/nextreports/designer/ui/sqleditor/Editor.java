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

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Element;

/**
 * @author Decebal Suiu
 */
public class Editor extends JPanel implements CaretListener, KeyListener {

    private EditorPanel editorPanel;
    private StatusBar statusBar;

    public Editor() {
        this(0, 0);
    }

    public Editor(int width, int height) {
        super();

        editorPanel = new EditorPanel(width, height);
        statusBar = new StatusBar();
//        boolean isInsertMode = editorPanel.getTextMode() == PlainTextArea.INSERT_MODE;
        boolean isInsertMode = false;
//        System.out.println("isInsertMode = " + isInsertMode);
        statusBar.setOverwriteModeIndicatorEnabled(!isInsertMode);

        setLayout(new BorderLayout());
        add(editorPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        editorPanel.getEditorPane().addCaretListener(this);
        editorPanel.getEditorPane().addKeyListener(this);
    }

    public EditorPanel getEditorPanel() {
		return editorPanel;
	}

	public String getText() {
        return editorPanel.getText();
    }

    public void setText(String text) {
        editorPanel.setText(text);
    }

    /**
     * Called when cursor in text editor changes position.
     *
     * @param event The caret event.
     */
    public void caretUpdate(CaretEvent event) {
        // Update row/column information in status field.
        int caretPosition = editorPanel.getEditorPane().getCaretPosition();
        Element map = editorPanel.getEditorPane().getDocument().getDefaultRootElement();
        int lineNumber = map.getElementIndex(caretPosition);
        int lineStartOffset = map.getElement(lineNumber).getStartOffset();
        statusBar.setRowAndColumn(lineNumber + 1, caretPosition - lineStartOffset + 1);
    }

    /**
     * Called whenever the user presses a key in the text area.
     *
     * @param event The key event.
     */
    public void keyPressed(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            // If they're releasing the Insert key, toggle between
            // insert/overwrite mode for all editors OTHER THAN the one in
            // which the key was pressed (it is done for that one already).
            case KeyEvent.VK_INSERT : {
//                boolean isInsertMode = editorPanel.getTextMode() == PlainTextArea.INSERT_MODE;
                boolean isInsertMode = false;
                statusBar.setOverwriteModeIndicatorEnabled(isInsertMode);
                break;
            }
            // If they're releasing the Caps Lock key, toggle caps lock
            // in the status bar to reflect the actual state.
            case KeyEvent.VK_CAPS_LOCK : {
                try {
                    boolean state = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
                    statusBar.setCapsLockIndicatorEnabled(state);
                } catch (UnsupportedOperationException e) {
                	// ignore
                }
                break;
            }
            default:
        }
    }

    public void keyReleased(KeyEvent evet) {
    }

    public void keyTyped(KeyEvent event) {
    }

}
