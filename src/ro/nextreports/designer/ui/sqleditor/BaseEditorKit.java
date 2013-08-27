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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import javax.swing.text.JTextComponent.KeyBinding;

import ro.nextreports.designer.ui.sqleditor.syntax.SqlEditorKit;


/**
 * @author Decebal Suiu
 */
public class BaseEditorKit extends SqlEditorKit {

	/** Find/Replace action. */
    public static final String findReplaceAction = "find-replace";

    private JEditorPane target;
    
	public BaseEditorKit() {
		super();
	}

	@Override
	public void install(JEditorPane target) {
		super.install(target);
		this.target = target;
	}

	@Override
	protected Action[] getCustomActions() {
		return new Action[] {
				new FindReplaceAction(target)
		};
	}

	@Override
	protected KeyBinding[] getCustomKeyBindings() {
    	return new JTextComponent.KeyBinding[] {
    			new JTextComponent.KeyBinding(KeyStroke.getKeyStroke("control F"), findReplaceAction),
    	};
	}

	public static class FindReplaceAction extends TextAction {

		private JDialog dialog;
		private JTextComponent target;
		
		public FindReplaceAction(JTextComponent target) {
			super(findReplaceAction);
			this.target = target;
		}

		public void actionPerformed(ActionEvent event) {
			if (dialog == null) {
				if (target == null) {
					target = getTextComponent(event);
				}
				Window window = SwingUtilities.getWindowAncestor(target);
				if (window instanceof Frame) {
					dialog = new FindReplaceDialog((Frame) window, target);
				} else {
					// it's a dialog
					dialog = new FindReplaceDialog((Dialog) window, target);
				}
			} else if (dialog.isVisible()) {
				return;
			}
			
			dialog.pack();
			dialog.setLocationRelativeTo(dialog.getParent());
			dialog.setVisible(true);
		}
		
	}
	
}
