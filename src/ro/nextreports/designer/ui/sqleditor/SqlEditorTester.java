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

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import org.jdesktop.swingx.JXEditorPane;

import ro.nextreports.designer.ui.sqleditor.syntax.SyntaxDocument;
import ro.nextreports.designer.ui.sqleditor.syntax.Token;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

/**
 * @author Decebal Suiu
 */
// TODO move or remove
class SqlEditorTester extends JFrame {

	private JXEditorPane editorPane;
	private JLabel caretPositionLabel;
	private JLabel tokenLabel;

	public SqlEditorTester() {
    	// set look and feel
    	PlasticLookAndFeel lookAndFeel = new PlasticXPLookAndFeel();
    	PlasticLookAndFeel.setCurrentTheme(new ExperienceBlue());
    	try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		initComponents();
	}

	private void initComponents() {
		caretPositionLabel = new JLabel("Caret Position");
		
		editorPane = new JXEditorPane();
		editorPane.setEditorKit(new BaseEditorKit());
//		editorPane.setEditorKit(new SqlEditorKit());
		
//		editorPane.setFont(UIManager.getFont("TextAreaUI"));
        editorPane.setFont(new Font("Courier New", Font.PLAIN, 12));
		editorPane.addCaretListener(new CaretListener() {
			
			public void caretUpdate(CaretEvent event) {
				caretPositionLabel.setText(Integer.toString(event.getDot()));
				tokenLabel.setText(null);

				if (editorPane.getDocument() instanceof SyntaxDocument) {
					SyntaxDocument syntaxDocument = (SyntaxDocument) editorPane.getDocument();
					Token token = syntaxDocument.getTokenAt(event.getDot());
					if (token == null) {
						return;
					}
					
					try {
						String text = syntaxDocument.getText(token.start, Math.min(token.length, 40));
						if (token.length > 40) {
							text += "...";
						}
						tokenLabel.setText(token.toString() + ": " + text);
					} catch (BadLocationException e) {
					}
				}
			}
			
		});

		CurrentLineHighlighter.install(editorPane);
		
		tokenLabel = new JLabel();
		tokenLabel.setText("Token under cursor");

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		/*
		JToolBar toolBar = new JToolBar();
		Action findReplaceAction = editorPane.getActionMap().get(BaseEditorKit.findReplaceAction);
		findReplaceAction.putValue(Action.SMALL_ICON, ImageUtil.getImageIcon("find.png"));
		MagicButton button = new MagicButton(findReplaceAction);
		button.setText("");
		toolBar.add(button);
		add(toolBar, constraints);
		*/

		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth = 2;
		constraints.gridy = 1;
//		add(new JScrollPane(editorPane), constraints);
		add(new EditorScrollPane(400, 200, editorPane, true, null), constraints);
		
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.gridy = 2; 
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(caretPositionLabel, constraints);
		
		constraints.fill = GridBagConstraints.NONE;
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.EAST;
		add(tokenLabel, constraints);
		
		setTitle("SqlEditor Tester");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setSize(600, 400);
		setLocationRelativeTo(null);
		
		editorPane.requestFocus();
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				new SqlEditorTester().setVisible(true);
			}
			
		});
	}

}
