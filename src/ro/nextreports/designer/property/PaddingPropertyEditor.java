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

import ro.nextreports.engine.band.Padding;
import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

/**
 * @author Decebal Suiu
 */
public class PaddingPropertyEditor extends AbstractPropertyEditor {

	private DefaultCellRenderer label;

	private JButton button;

	private Padding padding;

	public PaddingPropertyEditor() {
		JPanel paddingEditor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		editor = paddingEditor;
		paddingEditor.add("*", label = new DefaultCellRenderer());
		label.setOpaque(false);
		paddingEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectPadding();
			}

		});
		paddingEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.setText("X");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectNull();
			}

		});
		paddingEditor.setOpaque(false);
	}

	public Object getValue() {
		return padding;
	}

	public void setValue(Object value) {
		padding = (Padding) value;
		label.setValue(value);
	}

	protected void selectPadding() {
		Padding selectedPadding = PaddingChooser.showDialog(editor, I18NSupport.getString("padding.dialog.title"), padding);
		if (selectedPadding != null) {
			Padding oldPadding = padding;
			Padding newPadding = selectedPadding;
			label.setValue(newPadding);
			padding = newPadding;
			firePropertyChange(oldPadding, newPadding);
		}
	}

	protected void selectNull() {
		Object oldPattern = padding;
		label.setValue(null);
		padding = null;
		firePropertyChange(oldPattern, null);
	}

}
