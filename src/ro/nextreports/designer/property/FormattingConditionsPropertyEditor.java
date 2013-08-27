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
import com.l2fprod.common.swing.renderer.DefaultCellRenderer;
import com.l2fprod.common.swing.PercentLayout;
import com.l2fprod.common.swing.ComponentFactory;
import ro.nextreports.engine.condition.FormattingConditions;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 11:57:00
 */
public class FormattingConditionsPropertyEditor extends AbstractPropertyEditor {

    private DefaultCellRenderer label;

    private JButton button;

    private FormattingConditions conditions;
    private String type;

    public FormattingConditionsPropertyEditor(String type) {
        this.type = type;
        JPanel conditionEditor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0));
        editor = conditionEditor;
        conditionEditor.add("*", label = new DefaultCellRenderer());
        label.setOpaque(false);
        conditionEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectConditions();
            }
        });
        conditionEditor.add(button = ComponentFactory.Helper.getFactory().createMiniButton());
        button.setText("X");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectNull();
            }
        });
        conditionEditor.setOpaque(false);
    }

    public Object getValue() {
        return conditions;
    }

    public void setValue(Object value) {
        conditions = (FormattingConditions) value;
        label.setValue(value);
    }
    
    public void setType(String type) {
    	this.type = type;
    }

    protected void selectConditions() {
        FormattingConditions selected = FormattingConditionsChooser.showDialog(editor, I18NSupport.getString("condition.dialog.title"), conditions, type);
        if (selected != null) {
            FormattingConditions oldC = conditions;
            FormattingConditions newC = selected;
            label.setValue(newC);
            conditions = newC;
            firePropertyChange(oldC, newC);
        }
    }

    protected void selectNull() {
        Object oldPattern = conditions;
        label.setValue(null);
        conditions = null;
        firePropertyChange(oldPattern, null);
    }

}
