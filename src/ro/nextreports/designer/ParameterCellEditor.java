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

import ro.nextreports.engine.band.Padding;
import ro.nextreports.engine.band.ParameterBandElement;
import ro.nextreports.engine.ReportLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;

import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.property.CustomLineBorder;
import ro.nextreports.designer.querybuilder.ParameterManager;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 11, 2008
 * Time: 3:05:37 PM
 */
public class ParameterCellEditor extends DefaultGridCellEditor {

	private ParameterBandElement bandElement;

    public ParameterCellEditor(JComboBox comboBox) {
		super(comboBox);
	}

	@Override
	public Component getEditorComponent(int row, int column, Object value,
			boolean isSelected, JGrid grid) {

		if (value == null) {
			return super.getEditorComponent(row, column, value, isSelected, grid);
		}

		try {
			bandElement = (ParameterBandElement) value;
		} catch (ClassCastException e) {
			return super.getEditorComponent(row, column, value, isSelected, grid);
		}

		JComboBox renderer = (JComboBox) super.getEditorComponent(row, column, bandElement.getText(), isSelected, grid);
		List<String> parameters = ParameterManager.getInstance().getUsedParameterNames(Globals.getMainFrame().getQueryBuilderPanel().getUserSql());
		renderer.setModel(new DefaultComboBoxModel(parameters.toArray()));
		renderer.setSelectedItem(bandElement.getParameter());
		renderer.setFont(bandElement.getFont());
		renderer.setBackground(bandElement.getBackground());
		renderer.setForeground(bandElement.getForeground());
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

		ReportLayout oldLayout = getOldLayout();
        String text = (String) super.getCellEditorValue();
		bandElement.setParameter(text);
        registerUndoRedo(oldLayout, I18NSupport.getString("edit.parameter"), I18NSupport.getString("edit.parameter.insert"));

        return bandElement;
	}

}
