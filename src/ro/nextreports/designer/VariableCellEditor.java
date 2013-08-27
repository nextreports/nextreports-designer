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
package ro.nextreports.designer;

import ro.nextreports.engine.band.VariableBandElement;
import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.exporter.util.variable.VariableFactory;
import ro.nextreports.engine.ReportLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.property.CustomLineBorder;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 2, 2008
 * Time: 3:24:58 PM
 */
public class VariableCellEditor extends DefaultGridCellEditor {

	private VariableBandElement bandElement;

    public VariableCellEditor(JComboBox comboBox) {
		super(comboBox);
	}

	@Override
	public Component getEditorComponent(int row, int column, Object value,
			boolean isSelected, JGrid grid) {
		// TODO
		if (value == null) {
			return super.getEditorComponent(row, column, value, isSelected, grid);
		}

		try {
			bandElement = (VariableBandElement) value;
		} catch (ClassCastException e) {
			return super.getEditorComponent(row, column, value, isSelected, grid);
		}
//		System.out.println("bandElement = " + bandElement);

		JComboBox renderer = (JComboBox) super.getEditorComponent(row, column, bandElement.getText(), isSelected, grid);
		// TODO cache
		List<String> variables = VariableFactory.getVariableNames();
		renderer.setModel(new DefaultComboBoxModel(variables.toArray()));
		renderer.setSelectedItem(bandElement.getVariable());
		renderer.setFont(bandElement.getFont());
		renderer.setBackground(bandElement.getBackground());
		renderer.setForeground(bandElement.getForeground());
//		renderer.setHorizontalAlignment(bandElement.getHorizontalAlign());
		Border outer = new CustomLineBorder(bandElement.getBorder());
		Border inner = null;
		if (bandElement.getPadding() != null) {
			Padding padding = bandElement.getPadding();
			inner = new EmptyBorder(padding.getTop(), padding.getLeft(), padding.getBottom(), padding
					.getRight());
		} else {
			inner = new EmptyBorder(0, 0, 0, 0);
		}
		CompoundBorder border = new CompoundBorder(outer, inner);
		renderer.setBorder(border);

        this.grid = grid;
        return renderer;
	}

	@Override
	public Object getCellEditorValue() {	
		if (bandElement == null) {
			return super.getCellEditorValue();
		}

		ReportLayout oldLayout  = getOldLayout();
        String text = (String) super.getCellEditorValue();
		bandElement.setVariable(text);
        registerUndoRedo(oldLayout, I18NSupport.getString("edit.variable"), I18NSupport.getString("edit.variable.insert"));

        return bandElement;
	}

}
