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

import ro.nextreports.engine.band.Band;

import javax.swing.*;
import javax.swing.tree.TreePath;

import ro.nextreports.designer.action.report.layout.InsertAfterRowAction;
import ro.nextreports.designer.action.report.layout.InsertBeforeRowAction;
import ro.nextreports.designer.action.report.layout.RemoveRowAction;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.grid.JGridHeader;
import ro.nextreports.designer.util.TreeUtil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

import java.util.List;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 23-Sep-2009
// Time: 11:46:56

//
public class ReportRowGridHeader extends JGridHeader {
	
	private SelectionListener selectionListener;

    public ReportRowGridHeader(JGrid viewport) {
        super(viewport, SwingConstants.VERTICAL, false, true);   
        addMouseListener(selectionListener = new SelectionListener());
        addMouseListener(new PopupListener());        
    }      
    
    class SelectionListener extends MouseAdapter {
    	
		@Override
		public void mousePressed(MouseEvent event) {
			if (!event.isPopupTrigger()) {
				List<Cell> cells = getSelectionModel().getSelectedCells();				
				JTree tree = Globals.getReportDesignerPanel().getStructurePanel().getStructureTree();
				TreePath[] paths = new TreePath[cells.size()];
				for (int i = 0, size = cells.size(); i < size; i++) {
					paths[i] = TreeUtil.getTreePath(cells.get(i).getRow());
					Globals.getReportLayoutPanel().getReportGridPanel().getRowHeader().getSelectionModel().addSelectionCell(cells.get(i));
				}
				tree.setSelectionPaths(paths);
			}
		}
	}

    class PopupListener extends MouseAdapter {

        private JPopupMenu popup;

        public PopupListener() {
            popup = new JPopupMenu();
        }

        @Override
        public void mousePressed(MouseEvent event) {
            maybeShowPopup(event);            
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }

        private void maybeShowPopup(MouseEvent event) {
            List<Cell> list = ReportRowGridHeader.this.getSelectionModel().getSelectedCells();

            if (event.isPopupTrigger() && (list.size() > 0)) {
                popup.removeAll();
                updatePopupMenu(popup, event.getPoint());
                popup.show(event.getComponent(), event.getX(), event.getY());                
            }                                   
        }

        private void updatePopupMenu(JPopupMenu popup, final Point point) {
            final List<Cell> list = ReportRowGridHeader.this.getSelectionModel().getSelectedCells();
            if (list.size() == 1) {            	
            	Band band = BandUtil.getBand(LayoutHelper.getReportLayout(), list.get(0).getRow());            	
                popup.add(new InsertBeforeRowAction(list.get(0).getRow()));
                popup.add(new InsertAfterRowAction(list.get(0).getRow()));
            }
            if (list.size() > 0) {
                int selectedRows[] = new int[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    selectedRows[i] = list.get(i).getRow();
                }
                popup.add(new RemoveRowAction(selectedRows));
            }
        }

    }		
    
}
