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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import ro.nextreports.designer.action.report.layout.InsertAfterColumnAction;
import ro.nextreports.designer.action.report.layout.InsertBeforeColumnAction;
import ro.nextreports.designer.action.report.layout.RemoveColumnAction;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.JGrid;
import ro.nextreports.designer.grid.JGridHeader;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.exporter.ResultExporter;
import ro.nextreports.engine.exporter.TxtExporter;

/**
 * @author Decebal Suiu
 */
public class ReportColumnGridHeader extends JGridHeader {

    public ReportColumnGridHeader(JGrid viewport) {
        super(viewport, SwingConstants.HORIZONTAL, true, true);
        addMouseListener(new PopupListener());
    }

    private void updatePopupMenu(JPopupMenu popup, final Point point) {

        final List<Cell> list = ReportColumnGridHeader.this.getSelectionModel().getSelectedCells();

        if (list.size() == 1) {
            popup.add(new InsertBeforeColumnAction(list.get(0).getColumn()));
            popup.add(new InsertAfterColumnAction(list.get(0).getColumn()));
        }
        if (list.size() > 0) {
            int selectedColumns[] = new int[list.size()];
            for (int i=0; i<list.size(); i++) {
                selectedColumns[i]=list.get(i).getColumn();
            }
            popup.add(new RemoveColumnAction(selectedColumns));
        }

        if (LayoutHelper.getReportLayout().isUseSize()) {
        	popup.add(new SizeAction(I18NSupport.getString("width.action.size.name"), list, MINIMUM_COLUMN_SIZE, MAXIMUM_COLUMN_SIZE, false));
        	popup.add(new SizeAction(I18NSupport.getString("width.action.size.characters.name"), list, 1, 120, true));
        }
    }
    
    class SizeAction extends AbstractAction {
    	
    	private List<Cell> list;
    	private int min;
    	private int max;
    	private boolean chars;
    	
    	public SizeAction(String name, List<Cell> list, int min, int max, boolean chars) {
            putValue(Action.NAME, name);
            this.list = list;
            this.min = min;
            this.max = max;
            this.chars = chars;
    	}

		public void actionPerformed(ActionEvent e) {
			int column;// = columnAtPoint(point);
            Integer currentSize;// = getColumnWidth(column);
            String cols = "";
            if (list.size() == 0) {
                return;
            } else if (list.size() == 1) {
                column = list.get(0).getColumn();
                currentSize = getColumnWidth(column);
                cols = String.valueOf(column);
            } else {
                currentSize = null;
                for (int i = 0, size = list.size(); i < size; i++) {
                    cols = cols + list.get(i).getColumn();
                    if (i < size - 1) {
                        cols = cols + ",";
                    }
                }
            }

            if (chars && (currentSize !=  null)) {
            	currentSize = Math.round(currentSize / TxtExporter.PIXELS_PER_CHAR);
            }
            String text = I18NSupport.getString("width.action.size", cols);
            if (chars) {
            	text = I18NSupport.getString("width.action.size.characters", cols);
            }
            String size = JOptionPane.showInputDialog(text, currentSize);
            if (size != null) {
                int s;
                try {
                    s = Integer.parseInt(size);
                } catch (NumberFormatException ex) {
                    Show.error(I18NSupport.getString("width.action.size.invalid"));
                    return;
                }                
                if ((s < min) || (s > max)) {
                	if (chars) {
                		Show.error(I18NSupport.getString("width.action.size.characters.range", min, max));
                	} else {
                		Show.error(I18NSupport.getString("width.action.size.range", min, max));
                	}
                    return;
                }
                if (chars) {
                	s = Math.round(s * TxtExporter.PIXELS_PER_CHAR);
                }

                ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

                for (Cell cell : list) {
                    setColumnWidth(cell.getColumn(), s);
                }

                int total = 0;
                List<Integer> columnsWidth = new ArrayList<Integer>();
                for (int i = 0, n = getColumnCount(); i < n; i++) {
                    total += getColumnWidth(i);
                    columnsWidth.add(getColumnWidth(i));
                }
                LayoutHelper.getReportLayout().setColumnsWidth(columnsWidth);

                // repaint headers
                Globals.getReportLayoutPanel().getReportGridPanel().repaintHeaders();

                ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
                Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("width.action.size")));

                if (Globals.getA4Warning()) {
                    if (ResultExporter.A4_LANDSCAPE_PIXELS < total) {
                        Show.info(I18NSupport.getString("width.action.exceed.landscape"));
                    } else if (ResultExporter.A4_PORTRAIT_PIXELS < total) {
                        Show.info(I18NSupport.getString("width.action.exceed.portrait"));
                    }
                }
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
            List<Cell> list = ReportColumnGridHeader.this.getSelectionModel().getSelectedCells();

            if (event.isPopupTrigger() && (list.size() > 0)) {
                popup.removeAll();
                updatePopupMenu(popup, event.getPoint());
                popup.show(event.getComponent(), event.getX(), event.getY());
            }

        }

    }

}
