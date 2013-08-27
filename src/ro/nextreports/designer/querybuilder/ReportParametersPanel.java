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
package ro.nextreports.designer.querybuilder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import ro.nextreports.designer.FormLoader;
import ro.nextreports.designer.Globals;
import ro.nextreports.designer.chart.ChartUtil;
import ro.nextreports.designer.persistence.ReportPersistence;
import ro.nextreports.designer.persistence.ReportPersistenceFactory;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.Report;
import ro.nextreports.engine.queryexec.QueryParameter;

public class ReportParametersPanel extends JPanel {
		
	private DBBrowserTree dbBrowserTree;
	private JXTable table;
	private ReportParametersTableModel model;
	private Component parent;
	
	private Dimension scrDim = new Dimension(200, 200);
	
	public ReportParametersPanel() {		
		
		JScrollPane scroll = createBrowserTree();        
        createTable();
        
        setLayout(new GridBagLayout());
                        
        add(scroll, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        add(new JScrollPane(table), new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 5, 0, 0), 0, 0));                
	}
	
	private JScrollPane createBrowserTree() {
		// ignore double click listener for tree (which opens the query)
        // and create our own listener (which just selects the path)
        dbBrowserTree = new DBBrowserTree(DBObject.DATABASE, false);
        dbBrowserTree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                job(e, true);
            }                       
           
            private void job(MouseEvent e, boolean pressed) {
                final TreePath selPath = dbBrowserTree.getPathForLocation(e.getX(), e.getY());
                if (selPath == null) {
                    return;
                }

                dbBrowserTree.setSelectionPath(selPath);
                final DBBrowserNode selectedNode = (DBBrowserNode) selPath.getLastPathComponent();   
                Report report = null;
                if (selectedNode.getDBObject().getType() == DBObject.QUERIES) {
                	ReportPersistence repPersist = ReportPersistenceFactory.createReportPersistence(
                            Globals.getReportPersistenceType());
                	report = repPersist.loadReport(selectedNode.getDBObject().getAbsolutePath());
                } else if (selectedNode.getDBObject().getType() == DBObject.REPORTS) {
                	report = FormLoader.getInstance().load(selectedNode.getDBObject().getAbsolutePath(), false);
                } else if(selectedNode.getDBObject().getType() == DBObject.CHARTS) {
                	report = ChartUtil.loadChart(selectedNode.getDBObject().getAbsolutePath()).getReport();
                } else {
                	report = null;
                }
                model.clear();
                if (report != null) {
                	model.addObjects(report.getParameters());
                } 
            }
        });
        JScrollPane scroll = new JScrollPane(dbBrowserTree);
        scroll.setPreferredSize(scrDim);
        return scroll;
	}
	
	private void createTable() {
		model = new ReportParametersTableModel();
		table = new JXTable(model);
        table.setSortable(false);
        table.setRolloverEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED)); 
        
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
	            String tmp = (String) value;
	            tmp = tmp.substring(tmp.lastIndexOf('.') + 1);	            
	            setText(tmp);	            
	            return this;
			}
			
		});    
        
        table.addMouseListener(new MouseAdapter() {
        	 public void mouseClicked(MouseEvent ev) {
                 if (ev.getClickCount() == 2) {
                	QueryParameter qp = model.getObjectForRow(table.getSelectedRow());
                 	BaseDialog dialog = new BaseDialog(new ParameterPanelInfo(qp), qp.getName());
                 	dialog.pack();
                 	if (parent == null) {
                 		parent = Globals.getMainFrame();
                 	}
            		Show.centrateComponent(parent, dialog);
            		dialog.setVisible(true);
                 }
             }    
        });
		
		table.setPreferredScrollableViewportSize(new Dimension(250, 200));
	}
	
	public class ReportParametersTableModel extends AbstractTableModel {

		private final String[] columnNames = {
				I18NSupport.getString("parameter.name"),
				I18NSupport.getString("parameter.type")
		};

		private List<QueryParameter> elements = new ArrayList<QueryParameter>();

		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {			
			if (elements == null) {
				return 0;
			}
			return elements.size();
		}
		
		public void addObject(QueryParameter object) {
			elements.add(object);
			fireTableDataChanged();
		}
		
		public void addObjects(List<QueryParameter> objects) {
			elements.addAll(objects);
			fireTableDataChanged();
		}

		public void deleteObject(int rowIndex) {
			elements.remove(rowIndex);
			fireTableDataChanged();
		}
               
		public void deleteObjects(List<QueryParameter> objects) {
			elements.removeAll(objects);
			fireTableDataChanged();
		}

		public void clear() {
			elements.clear();
			fireTableDataChanged();
		}

		public QueryParameter getObjectForRow(int rowIndex) {
			return elements.get(rowIndex);
		}
		
		public Object getValueAt(int rowIndex, int columnIndex) {
			QueryParameter row = (QueryParameter) elements.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return row.getName();
				case 1:
					return row.getValueClassName();
				default:
					return null;
			}
		}

	}
	
	public List<QueryParameter> getSelectedParameters() {
		List<QueryParameter> result = new ArrayList<QueryParameter>(); 
		for (int i : table.getSelectedRows()) {
			result.add(model.getObjectForRow(i));
		}
		return result;
	}

	public void setParent(Component parent) {
		this.parent = parent;
	}

	

}
