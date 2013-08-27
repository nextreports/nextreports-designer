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
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JPanel;

/**
 * @author Decebal Suiu
 */
public class EditorPanel extends JPanel {

    private JEditorPane editorPane;
    private EditorScrollPane scrollPane;
    
    public EditorPanel() {
    	this(0, 0);
	}

    public EditorPanel(int width, int height) {
    	super();
    	
        editorPane = new JEditorPane();
        editorPane.setEditorKit(new BaseEditorKit());
				
        // must set a monospaced font, otherwise the caret position will be incorrect !!
        // but 'monospaced' font is ugly and I use 'Courier New'; you must install on Linux 'ttf-mscorefonts-installer'
        // (Installer for Microsoft TrueType core fonts)
        editorPane.setFont(new Font("Courier New", Font.PLAIN, 12));
        CurrentLineHighlighter.install(editorPane);

        scrollPane = new EditorScrollPane(width, height, editorPane, true, null);
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        
        editorPane.requestFocus();
    }
    
	public JEditorPane getEditorPane() {
        return editorPane;
    }
    
	public String getText() {
        return editorPane.getText();
    }

    public void setText(String text) {
        editorPane.setText(text);
    }

}
