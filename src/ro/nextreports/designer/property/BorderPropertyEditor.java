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
package ro.nextreports.designer.property;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.band.Border;
import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

/**
 * @author Decebal Suiu
 */
/**
 * @author alexandru.parvulescu
 * 
 */
public class BorderPropertyEditor extends AbstractPropertyEditor {

	private DefaultCellRenderer label;

	private JButton button;

	private Border border;

	public BorderPropertyEditor() {
		JPanel borderEditor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		editor = borderEditor;
		borderEditor.add("*", label = new DefaultCellRenderer());
		label.setOpaque(false);
		borderEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectBorder();
			}
		});
		borderEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.setText("X");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectNull();
			}
		});
		borderEditor.setOpaque(false);
	}

	public Object getValue() {
		return border;
	}

	public void setValue(Object value) {
		border = (Border) value;
		label.setValue(value);
	}

	protected void selectBorder() {
		Border selectedB = BorderChooser.showDialog(editor, I18NSupport.getString("border.dialog.title"), border);
		if (selectedB != null) {
			Border oldB = border;
			Border newB = selectedB;
			label.setValue(newB);
			border = newB;
			firePropertyChange(oldB, newB);
		}
	}

	protected void selectNull() {
		Object oldPattern = border;
		label.setValue(null);
		border = null;
		firePropertyChange(oldPattern, null);
	}

}
