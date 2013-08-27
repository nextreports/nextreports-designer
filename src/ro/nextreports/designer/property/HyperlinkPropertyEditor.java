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

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import ro.nextreports.engine.band.Hyperlink;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 01-Mar-2010
 * Time: 12:09:22
 */
public class HyperlinkPropertyEditor extends AbstractPropertyEditor {

    private DefaultCellRenderer label;
    private JButton button;
    private Hyperlink hyperlink;

    public HyperlinkPropertyEditor() {

        JPanel urlEditor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
		editor = urlEditor;
		urlEditor.add("*", label = new DefaultCellRenderer());
		label.setOpaque(false);
		urlEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectHyperlink();
			}

		});
		urlEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
		button.setText("X");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectNull();
			}

		});
		urlEditor.setOpaque(false);
	}

	public Object getValue() {
		return hyperlink;
	}

	public void setValue(Object value) {
		hyperlink = (Hyperlink) value;
		label.setValue(hyperlink.getText());
    }
    protected void selectHyperlink() {
		Hyperlink selectedHyperlink = HyperlinkChooser.showDialog(editor, I18NSupport.getString("url.dialog.title"), hyperlink);
		if (selectedHyperlink != null) {
			Hyperlink oldHyperlink = hyperlink;
			Hyperlink newHyperlink = selectedHyperlink;
			label.setValue(newHyperlink.getText());
			hyperlink = newHyperlink;
			firePropertyChange(oldHyperlink, newHyperlink);
		}
	}

	protected void selectNull() {
		Object oldHyperlink = hyperlink;
		label.setValue(null);
		hyperlink = null;
		firePropertyChange(oldHyperlink, null);
	}


}
