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
package ro.nextreports.designer.action.undo;

import java.util.List;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ro.nextreports.designer.BandUtil;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.band.BandElement;

/**
 * @author Decebal Suiu
 */
public class ModifyElementsEdit extends AbstractUndoableEdit {
	
	private List<BandElement> elements;
	private List<BandElement> oldElements;
	private List<Integer> rows;
	private List<Integer> columns;
	
	public ModifyElementsEdit(List<BandElement> elements, List<BandElement> oldElements, 
			List<Integer> rows, List<Integer> columns) {
		this.elements = elements;
		this.oldElements = oldElements;
		this.rows = rows;
		this.columns = columns;
	}
	
	@Override
	public String getPresentationName() {
		return I18NSupport.getString("edit.modify.element");
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		
		BandUtil.insertElements(oldElements, rows, columns);
        Globals.getReportDesignerPanel().getPropertiesPanel().refresh();
	}
	
	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		
		BandUtil.insertElements(elements, rows, columns);
        Globals.getReportDesignerPanel().getPropertiesPanel().refresh();
	}
		
}
