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
package ro.nextreports.designer.grid.plaf;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import ro.nextreports.designer.grid.JGrid;


/**
 * GridUI for use with row and column headers.
 * 
 * @author Decebal Suiu
 */
public class BasicGridHeaderUI extends BasicGridUI {
	
	private static boolean installedHeader;
	
//	protected JGrid gridHeader;

	public BasicGridHeaderUI() {
	}

	public static ComponentUI createUI(JComponent c) {
		return new BasicGridHeaderUI();
	}

	@Override
	public void installUI(JComponent component) {
		grid = (JGrid) component;
		rendererPane = new CellRendererPane();
		grid.add(rendererPane);
//		gridHeader = (JGrid) component;
		component.setOpaque(false);
		LookAndFeel.installColorsAndFont(
			component,
			"TableHeader.background",
			"TableHeader.foreground",
			"TableHeader.font");
		installDefaults();
		installListeners();
		installKeyboardActions();
	}

	@Override
	protected void installDefaults() {
		Color defaultGridColor = UIManager.getColor("Table.gridColor");
//		Color defaultForegroundColor = UIManager.getColor("TableHeader.foreground");
//		Color defaultBackgroundColor = UIManager.getColor("TableHeader.background");
//		Font defaultGridFont = UIManager.getFont("Table.font");
//		Border defaultGridBorder = UIManager.getBorder("TableHeader.border");
//		Color defaultSelectionForegroundColor = defaultForegroundColor.brighter();
//		Color defaultSelectionBackgroundColor = defaultBackgroundColor;
//		Color defaultFocusForegroundColor = defaultForegroundColor.brighter();
//		Color defaultFocusBackgroundColor = defaultBackgroundColor.brighter();
//		if (!installedHeader) {
//			UIManager.getDefaults().put("GridHeader.gridColor", defaultGridColor);
//			UIManager.getDefaults().put("GridHeader.foreground", defaultForegroundColor);
//			UIManager.getDefaults().put("GridHeader.background", defaultBackgroundColor);
//			UIManager.getDefaults().put(
//				"GridHeader.selectionForegroundColor",
//				defaultSelectionForegroundColor);
//			UIManager.getDefaults().put(
//				"GridHeader.selectionBackgroundColor",
//				defaultSelectionBackgroundColor);
//			UIManager.getDefaults().put(
//				"GridHeader.focusForegroundColor",
//				defaultFocusForegroundColor);
//			UIManager.getDefaults().put(
//				"GridHeader.focusBackgroundColor",
//				defaultFocusBackgroundColor);
//			UIManager.getDefaults().put("GridHeader.border", defaultGridBorder);
//			UIManager.getDefaults().put("GridHeader.font", defaultGridFont);
//		}
//		Color foregroundColor = gridHeader.getForeground();
//		Color backgroundColor = gridHeader.getBackground();
//		Font gridFont = gridHeader.getFont();
//		Border gridBorder = gridHeader.getBorder();
//		Color gridColor = gridHeader.getGridColor();
		Color gridColor = grid.getGridColor();
//		if (foregroundColor == null || foregroundColor instanceof UIResource) {
//			gridHeader.setForeground(defaultForegroundColor);
//		}
//		if (backgroundColor == null || backgroundColor instanceof UIResource) {
//			gridHeader.setBackground(defaultBackgroundColor);
//		}
//		if (gridColor == null || gridColor instanceof UIResource) {
			grid.setGridColor(defaultGridColor);
//		}
//		if (gridFont == null || gridFont instanceof UIResource) {
//			gridHeader.setFont(defaultGridFont);
//		}
//		if (gridBorder == null || gridBorder instanceof UIResource) {
//			gridHeader.setBorder(defaultGridBorder);
//		}
	}

	@Override
	public void paintEditor(Graphics g) {
	}

	@Override
	public void paintSelection(Graphics g, int rowMin, int rowMax, int colMin,
			int colMax) {
	}

	/*
	@Override
	protected DropTargetListener createDropTargetListener() {
//		return super.createDropTargetListener();
		return null;
	}
	*/

//	@Override
//	protected MouseInputListener createMouseInputListener() {
//		// TODO Auto-generated method stub
////		return new MouseInputListener();
//		return null;
//	}
	
}
