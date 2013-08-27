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
package ro.nextreports.designer.grid;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.util.ObjectCloner;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.undo.LayoutEdit;

/**
 * Generic implementation of <code>GridCellEditor</code> that uses the
 * <code>toString</code> method.
 * 
 * @author Decebal Suiu
 */
public class DefaultGridCellEditor extends DefaultCellEditor implements
		GridCellEditor {

    protected JGrid grid;

    /**
	 * Constructs a <code>DefaultGridCellEditor</code> that uses a text field.
	 * 
	 * @param textField
	 *            a <code>JTextField</code> object
	 */
	public DefaultGridCellEditor(final JTextField textField) {
		super(textField);
	}

	/**
	 * Constructs a <code>DefaultGridCellEditor</code> object that uses a check box.
	 * 
	 * @param checkBox
	 *            a <code>JCheckBox</code> object
	 */
	public DefaultGridCellEditor(final JCheckBox checkBox) {
		super(checkBox);
	}

	/**
	 * Constructs a <code>DefaultGridCellEditor</code> object that uses a combo box.
	 * 
	 * @param comboBox
	 *            a <code>JComboBox</code> object
	 */
	public DefaultGridCellEditor(final JComboBox comboBox) {
		super(comboBox);		
		setClickCountToStart(2);
	}
	
	/**
	 * @see GridCellEditor#getEditorComponent(int, int, Object, boolean, JGrid)
	 */
	public Component getEditorComponent(int row, int column, Object value,
			boolean isSelected, JGrid grid) {
       return getEditorComponent(row, column, value, isSelected, grid, false);
	}
	
	
	// must recreate the editor if we change the number of items,
	// otherwise JCombobox popup will show gray areas
	public Component getEditorComponent(int row, int column, Object value,
			boolean isSelected, JGrid grid, boolean recreate) {
		if (editorComponent instanceof JComboBox) {
			final JComboBox comboBox = new JComboBox();
			editorComponent = comboBox;
			comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
		        delegate = new EditorDelegate() {
		        	public void setValue(Object value) {
		        		comboBox.setSelectedItem(value);
		            }

		        	public Object getCellEditorValue() {
		        		return comboBox.getSelectedItem();
		        	}
		                
		            public boolean shouldSelectCell(EventObject anEvent) { 
		                if (anEvent instanceof MouseEvent) { 
		                    MouseEvent e = (MouseEvent)anEvent;
		                    return e.getID() != MouseEvent.MOUSE_DRAGGED;
		                }
		                return true;
		            }
		            
		            public boolean stopCellEditing() {
		            	if (comboBox.isEditable()) {
		            		// 	Commit edited value.
		            		comboBox.actionPerformed(new ActionEvent(DefaultGridCellEditor.this, 0, ""));
		            	}
		            	return super.stopCellEditing();
		            }
		        };
			comboBox.addActionListener(delegate);
		}
        this.grid = grid;
//		editorComponent.setBorder(new LineBorder(Color.black));
		delegate.setValue(value);
		
		return editorComponent;
	}

    private ReportLayout getLayoutBeforeInsert() {
        return (ReportLayout) grid.getClientProperty("layoutBeforeInsert");
    }

    protected ReportLayout getOldLayout() {
        // get the variable shared with the insert text action
		ReportLayout layoutBeforeInsert = getLayoutBeforeInsert();

		ReportLayout oldLayout;
		if (layoutBeforeInsert == null) {
			oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
		} else {
			oldLayout = layoutBeforeInsert;
		}
        return oldLayout;
    }

    protected void registerUndoRedo(ReportLayout oldLayout, String editPresentationName, String insertPresentationName) {
        ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
        if (getLayoutBeforeInsert() == null) {
        	Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, editPresentationName));
        } else {
        	Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, insertPresentationName));
        }

        // reset the variable shared with the insert text action
        grid.putClientProperty("layoutBeforeInsert", null);
    }

}
