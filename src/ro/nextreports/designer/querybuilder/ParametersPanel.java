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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.querybuilder.sql.util.CollectionUtil;
import ro.nextreports.engine.queryexec.QueryParameter;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * @author Decebal Suiu
 */
public class ParametersPanel extends JPanel {	

	private JXTable table;
	private ParametersTableModel model;

	public ParametersPanel() {
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());

		JToolBar toolBar = new JToolBar();
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE); // hide buttons borders
        toolBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
        toolBar.setBorderPainted(false);

		toolBar.add(new AbstractAction() {

			public Object getValue(String key) {
				if (AbstractAction.SMALL_ICON.equals(key)){
					return ImageUtil.getImageIcon("add");
				} else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
					return I18NSupport.getString("parameter.add");
				}

				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				add();
			}

		});
		
		toolBar.add(new AbstractAction() {

			public Object getValue(String key) {
				if (AbstractAction.SMALL_ICON.equals(key)){
					return ImageUtil.getImageIcon("add_par");
				} else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
					return I18NSupport.getString("parameter.add.from.report");
				}

				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				addFromReport();
			}

		});
		
		toolBar.add(new AbstractAction() {

			public Object getValue(String key) {
				if (AbstractAction.SMALL_ICON.equals(key)){
					return ImageUtil.getImageIcon("parameter_clone");
				} else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
					return I18NSupport.getString("parameter.add.clone");
				}

				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				cloneParameter();
			}

		});

		toolBar.add(new AbstractAction() {

			public Object getValue(String key) {
				if (AbstractAction.SMALL_ICON.equals(key)){
					return ImageUtil.getImageIcon("edit");
				} else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
					return I18NSupport.getString("parameter.edit");
				}

				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				modify();
			}

		});

		toolBar.add(new AbstractAction() {

			public Object getValue(String key) {
				if (AbstractAction.SMALL_ICON.equals(key)){
					return ImageUtil.getImageIcon("delete");
				} else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
					return I18NSupport.getString("parameter.delete");
				}

				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				delete();
			}

		});

        toolBar.add(new AbstractAction() {

			public Object getValue(String key) {
				if (AbstractAction.SMALL_ICON.equals(key)){
					return ImageUtil.getImageIcon("parameter_up");
				} else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
					return I18NSupport.getString("parameter.up");					
				}

				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				up();
			}

		});

        toolBar.add(new AbstractAction() {

			public Object getValue(String key) {
				if (AbstractAction.SMALL_ICON.equals(key)){
					return ImageUtil.getImageIcon("parameter_down");
				} else if (AbstractAction.SHORT_DESCRIPTION.equals(key)) {
					return I18NSupport.getString("parameter.down");					
				}

				return super.getValue(key);
			}

			public void actionPerformed(ActionEvent e) {
				down();
			}

		});

//        SwingUtil.registerButtonsForFocus(buttonsPanel);

		add(toolBar, BorderLayout.NORTH);

		createTable();
		table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
	            String tmp = (String) value;
	            tmp = tmp.substring(tmp.lastIndexOf('.') + 1);
	            /*
	            if ("Object".equals(tmp)) {
	            	tmp = "Any";
	            }
	            */
	            setText(tmp);
	            
	            return this;
			}
			
		});
        table.addMouseListener(new MouseAdapter() {
        	
        	@Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    modify();
                }
            }

        });
        table.addKeyListener(new KeyAdapter() {
        	
        	@Override
            public void keyPressed(KeyEvent event) {
        		int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.VK_ENTER) {
                    modify();
                    // don't let anyone else handle the event
                    event.consume();
                } else if (keyCode == KeyEvent.VK_INSERT) {
                	add();
                    // don't let anyone else handle the event
                    event.consume();
                } else if (keyCode == KeyEvent.VK_DELETE) {
                	delete();
                    // don't let anyone else handle the event
                    event.consume();
                }
            }

        });
		add(new JScrollPane(table), BorderLayout.CENTER);
		table.setPreferredScrollableViewportSize(new Dimension(250, 200));
	}

	private void createTable() {
		model = new ParametersTableModel();
		table = new JXTable(model);
        table.setSortable(false);
        //table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRolloverEnabled(true);
        table.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED)); 
	}

	public void addAll(List<QueryParameter> parameters) {
		for (QueryParameter param : parameters) {
			ParameterManager.getInstance().addParameter(param);
			model.addObject(param);
		}
	}

	public void set(List<QueryParameter> parameters) {
		ParameterManager.getInstance().clearParameters();
		model.clear();
		addAll(parameters);
	}


	public void addParameter(QueryParameter param) {
		ParameterManager.getInstance().addParameter(param);
		model.addObject(param);
	}

	private void add() {
		if (Globals.getConnection() == null) {
			Show.info(I18NSupport.getString("no.data.source.connected"));
			return;
		}
		ParameterEditPanel panel = new ParameterEditPanel(null);
		ParameterEditDialog dlg = new ParameterEditDialog(panel, I18NSupport.getString("add.parameter.title"), true);
		dlg.pack();
		Show.centrateComponent(Globals.getMainFrame(), dlg);
		dlg.setVisible(true);

		if (dlg.okPressed() && (dlg.getParameter() != null)) {
			ParameterManager.getInstance().addParameter(dlg.getParameter());
			model.addObject(dlg.getParameter());
		}
	}
	
	private void addFromReport() {
		if (Globals.getConnection() == null) {
			Show.info(I18NSupport.getString("no.data.source.connected"));
			return;
		}
		final ReportParametersPanel panel = new ReportParametersPanel();
		panel.setMinimumSize(new Dimension(300, (int)panel.getMinimumSize().getHeight()));
		BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("parameter.add.from.report")) {
			protected boolean ok() {
				List<QueryParameter> parameters = panel.getSelectedParameters();
				if (parameters.size() == 0) {
					return false;
				}
				for (QueryParameter qp : parameters) {
					if (ParameterManager.getInstance().containsParameter(qp.getName())) {
						JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.edit.name.exists.value", qp.getName()));
						return false;
					}
				}					
		        return true;
		    }
		};
	    panel.setParent(dialog);
		dialog.pack();
		Show.centrateComponent(Globals.getMainFrame(), dialog);
		dialog.setVisible(true);
		if (dialog.okPressed()) {
			for (QueryParameter qp : panel.getSelectedParameters()) {
				ParameterManager.getInstance().addParameter(qp);
			}
			model.addObjects(panel.getSelectedParameters());
		}
	}
	
	private void cloneParameter() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length != 1) {
			Show.info(I18NSupport.getString("parameter.select"));
			return;
		}

		QueryParameter oldParam = (QueryParameter) model.getObjectForRow(selectedRows[0]);
		QueryParameter clone = ObjectCloner.silenceDeepCopy(oldParam);	
		clone.setName(clone.getName() + "_c");

		ParameterManager.getInstance().addParameter(clone);
		model.addObject(clone);
	}

	private void modify() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length != 1) {
			Show.info(I18NSupport.getString("parameter.select"));
			return;
		}

		QueryParameter oldParam = (QueryParameter) model.getObjectForRow(selectedRows[0]);

		ParameterEditPanel panel = new ParameterEditPanel(oldParam);
		ParameterEditDialog dlg = new ParameterEditDialog(panel, I18NSupport.getString("modify.parameter.title"), true);
		dlg.pack();
		Show.centrateComponent(Globals.getMainFrame(), dlg);
		dlg.setVisible(true);

		if (!dlg.okPressed()) {
			return;
		}

		QueryParameter newParam = dlg.getParameter();
		ParameterManager.getInstance().modifyParameter(oldParam, newParam);		
		model.updateObject(selectedRows[0], newParam);
	}

	private void delete() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length == 0) {
			Show.info(I18NSupport.getString("parameter.select"));
			return;
		}

		if (JOptionPane.showConfirmDialog(Globals.getMainFrame(), I18NSupport.getString("parameter.askDelete")) != JOptionPane.OK_OPTION) {
			return;
		}

        for (int i = selectedRows.length - 1; i >= 0; i--) {
            QueryParameter param = (QueryParameter) model.getObjectForRow(selectedRows[i]);
            ParameterManager.getInstance().deleteParameter(param);
            model.deleteObject(selectedRows[i]);
        }
    }

    private void up() {
        int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length != 1) {
			Show.info(I18NSupport.getString("parameter.select"));
			return;
		}
        QueryParameter param = (QueryParameter) model.getObjectForRow(selectedRows[0]);
		ParameterManager.getInstance().moveParameter(param.getName(), true);
		model.moveObjectUp(selectedRows[0]);
    }

    private void down() {
        int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length != 1) {
			Show.info(I18NSupport.getString("parameter.select"));
			return;
		}
        QueryParameter param = (QueryParameter) model.getObjectForRow(selectedRows[0]);
		ParameterManager.getInstance().moveParameter(param.getName(), false);
		model.moveObjectDown(selectedRows[0]);
    }

    public class ParametersTableModel extends AbstractTableModel {

		private final String[] columnNames = {
				I18NSupport.getString("parameter.name"),
				I18NSupport.getString("parameter.type")
//                "Description"
		};

		private List elements = ParameterManager.getInstance().getParameters();

		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			// this method is called in the constructor so we must test for null
			if (elements == null) {
				return 0;
			}

			return elements.size();
		}

		@SuppressWarnings("unchecked")
		public void addObject(Object object) {
			elements.add(object);
			fireTableDataChanged();
		}

		@SuppressWarnings("unchecked")
		public void addObjects(List objects) {
			elements.addAll(objects);
			fireTableDataChanged();
		}

		public void deleteObject(int rowIndex) {
			elements.remove(rowIndex);
			fireTableDataChanged();
		}

        public void moveObjectUp(int rowIndex) {
            if (rowIndex > 0) {
                QueryParameter param = (QueryParameter) elements.get(rowIndex);
                CollectionUtil.moveItem(elements, param, rowIndex - 1);
                fireTableDataChanged();
                table.setRowSelectionInterval(rowIndex - 1, rowIndex - 1);
            }
        }

        public void moveObjectDown(int rowIndex) {
            if (rowIndex < elements.size() - 1) {
                QueryParameter param = (QueryParameter) elements.get(rowIndex);
                CollectionUtil.moveItem(elements, param, rowIndex + 1);
                fireTableDataChanged();
                table.setRowSelectionInterval(rowIndex + 1, rowIndex + 1);
            }
        }

        @SuppressWarnings("unchecked")
		public void deleteObjects(List objects) {
			elements.removeAll(objects);
			fireTableDataChanged();
		}

		public void clear() {
			elements.clear();
			fireTableDataChanged();
		}

		public Object getObjectForRow(int rowIndex) {
			return elements.get(rowIndex);
		}

		@SuppressWarnings("unchecked")
		public void updateObject(int row, Object object) {
			row = table.convertRowIndexToModel(row);
			elements.set(row, object);
			fireTableDataChanged();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			QueryParameter row = (QueryParameter) elements.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return row.getName();
				case 1:
					return row.getValueClassName();
//                case 2:
//                    return row.getDescription();
				default:
					return null;
			}
		}

	}

}
