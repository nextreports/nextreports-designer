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
package ro.nextreports.designer.wizrep;


import javax.swing.*;

import org.jdesktop.swingx.JXList;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.dbviewer.DefaultDBViewer;
import ro.nextreports.designer.dbviewer.common.DBColumn;
import ro.nextreports.designer.dbviewer.common.NextSqlException;
import ro.nextreports.designer.querybuilder.SelectionColumnPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 26, 2009
 * Time: 1:47:05 PM
 */
public class EasySelectColumnsPanel extends JPanel {

    private Dimension btnDim = new Dimension(20, 20);
    private Dimension scrDim = new Dimension(200, 150);
    private SelectionColumnPanel columnPanel;
    private JScrollPane scrDest = new JScrollPane();
    private JPanel pp = new JPanel();
    private JXList lstDest = new JXList();
    private JButton btnAddAll = new JButton();
    private JButton btnAdd = new JButton();
	private JButton btnRem = new JButton();
    private JButton btnRemAll = new JButton();
    private JButton btnUp = new JButton();
	private JButton btnDown = new JButton();
	private DefaultListModel lstModelDest = new DefaultListModel();

    public EasySelectColumnsPanel() {
        jbInit();
    }

    private void jbInit() {
		this.setLayout(new GridBagLayout());

        String schema = null;
        List<String> schemas = new ArrayList<String>();
        try {
            schema = Globals.getDBViewer().getUserSchema();
            schemas = Globals.getDBViewer().getSchemas();
            boolean noschema = true;
            for (String s : schemas) {
                if (s.equals(schema)) {
                    noschema = false;
                }
            }
            if (noschema) {
                schema = DefaultDBViewer.NO_SCHEMA_NAME;
            }
        } catch (NextSqlException e1) {
            e1.printStackTrace();
        }        
        columnPanel = new SelectionColumnPanel(schema, false, false) {
            protected void onDoubleClick() {
                btnAdd_actionPerformed();
            }
        };

        lstDest.setCellRenderer(new DBColumnCellRenderer());
        lstDest.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					btnRem_actionPerformed();
				}
			}
		});


		scrDest.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrDest.setMinimumSize(scrDim);
		scrDest.setPreferredSize(scrDim);
		scrDest.getViewport().add(lstDest, null);
		lstDest.setModel(lstModelDest);

        btnAddAll.setIcon(ImageUtil.getImageIcon("right.all"));
		btnAddAll.setPreferredSize(btnDim);
		btnAddAll.setMinimumSize(btnDim);
		btnAddAll.setMaximumSize(btnDim);
		btnAddAll.setToolTipText(I18NSupport.getString("listselectionpanel.addall"));
		btnAddAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAddAll_actionPerformed();
			}
		});

        btnAdd.setIcon(ImageUtil.getImageIcon(ImageUtil.RIGHT_ICON_NAME));
		btnAdd.setPreferredSize(btnDim);
		btnAdd.setMinimumSize(btnDim);
		btnAdd.setMaximumSize(btnDim);
		btnAdd.setToolTipText(I18NSupport.getString("listselectionpanel.add"));
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAdd_actionPerformed();
			}
		});

		//btnRem.setText("<");
		btnRem.setIcon(ImageUtil.getImageIcon(ImageUtil.LEFT_ICON_NAME));
		btnRem.setPreferredSize(btnDim);
		btnRem.setMinimumSize(btnDim);
		btnRem.setMaximumSize(btnDim);
		btnRem.setToolTipText(I18NSupport.getString("listselectionpanel.remove"));
		btnRem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRem_actionPerformed();
			}
		});

        btnRemAll.setIcon(ImageUtil.getImageIcon("left.all"));
		btnRemAll.setPreferredSize(btnDim);
		btnRemAll.setMinimumSize(btnDim);
		btnRemAll.setMaximumSize(btnDim);
		btnRemAll.setToolTipText(I18NSupport.getString("listselectionpanel.removeall"));
		btnRemAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetSelectedColumns();
			}
		});

        //btnUp.setText("/\\");
		btnUp.setIcon(ImageUtil.getImageIcon(ImageUtil.UP_ICON_NAME));
		btnUp.setPreferredSize(btnDim);
		btnUp.setMinimumSize(btnDim);
		btnUp.setMaximumSize(btnDim);
		btnUp.setMargin(new Insets(2, 2, 2, 2));
		btnUp.setToolTipText(I18NSupport.getString("listselectionpanel.up"));
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				upCrit();
			}
		});

		//btnDown.setText("\\/");
		btnDown.setPreferredSize(btnDim);
		btnDown.setIcon(ImageUtil.getImageIcon(ImageUtil.DOWN_ICON_NAME));
		btnDown.setMinimumSize(btnDim);
		btnDown.setMaximumSize(btnDim);
		btnDown.setMargin(new Insets(2, 2, 2, 2));
		btnDown.setToolTipText(I18NSupport.getString("listselectionpanel.down"));
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downCrit();
			}
		});

		btnSelection();

		add(columnPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(1, 5, 1, 5), 0, 0));
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
		btnPanel.add(Box.createGlue());
        btnPanel.add(btnAddAll);
		btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        btnPanel.add(btnAdd);
		btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		btnPanel.add(btnRem);
        btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		btnPanel.add(btnRemAll);
        btnPanel.add(Box.createGlue());
		add(btnPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));

		pp.setLayout(new GridBagLayout());
		pp.add(scrDest, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(1, 5, 1, 2), 0, 0));


        JPanel btnPriPanel = new JPanel();
        btnPriPanel.setLayout(new BoxLayout(btnPriPanel, BoxLayout.Y_AXIS));
        btnPriPanel.add(btnUp);
        btnPriPanel.add(Box.createGlue());
        btnPriPanel.add(btnDown);
        pp.add(btnPriPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(1, 0, 2, 0), 0, 0));
        add(pp, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
                        GridBagConstraints.EAST, GridBagConstraints.BOTH,
                        new Insets(36, 5, 2, 2), 0, 0));
        
    }

    private void btnSelection() {
		if (lstModelDest.size() == 0) {
			btnRem.setEnabled(false);
            btnRemAll.setEnabled(false);
        } else {
			btnRem.setEnabled(true);
            btnRemAll.setEnabled(true);
        }
	}

    private void btnAdd_actionPerformed() {
        List<DBColumn> list  = columnPanel.getSelectedColumns();
        for (DBColumn col : list) {
            if (!find(col)) {
                lstModelDest.addElement(col);
            }    
        }
        btnSelection();
    }

    private void btnAddAll_actionPerformed() {
        List<DBColumn> list  = columnPanel.getAllColumns();
        for (DBColumn col : list) {
            if (!find(col)) {
                lstModelDest.addElement(col);
            }
        }
        btnSelection();
    }

    private boolean find(DBColumn col) {
        for (int i=0; i< lstModelDest.size(); i++) {
            if (col.equals(lstModelDest.elementAt(i))) {
                return true;
            }
        }
        return false;
    }

    private void btnRem_actionPerformed() {
        Object[] values = lstDest.getSelectedValues();
        if (values.length > 0) {
            for (Object value : values) {
                lstModelDest.removeElement(value);
            }
            btnSelection();
        }
    }


    public int getCriteriiRow() {
		int i = -1;
		boolean selectat;
		for (int j = 0; j < lstModelDest.getSize(); j++) {
			selectat = lstDest.isSelectedIndex(j);
			if (selectat) {
				i = j;
			}
		}
		return i;
	}

	private void downCrit() {
		if (-1 < getCriteriiRow() && getCriteriiRow() < lstModelDest.getSize() - 1) {
			setRowDown(getCriteriiRow());
		}
	}

	private void upCrit() {
		if (0 < getCriteriiRow() && getCriteriiRow() <= lstModelDest.getSize() - 1) {
			setRowUp(getCriteriiRow());
		}
	}

	public void setRowDown(int row) {
		Object obj1 = lstModelDest.get(row);
		Object obj2 = lstModelDest.get(row + 1);
		lstModelDest.set(row, obj2);
		lstModelDest.set(row + 1, obj1);
		lstDest.setModel(lstModelDest);
		lstDest.setSelectedIndex(row + 1);
	}

	public void setRowUp(int row) {
		Object obj1 = lstModelDest.get(row);
		Object obj2 = lstModelDest.get(row - 1);
		lstModelDest.set(row, obj2);
		lstModelDest.set(row - 1, obj1);
		lstDest.setModel(lstModelDest);
		lstDest.setSelectedIndex(row - 1);
	}

    public List<DBColumn> getSelectedColumns() {
        List<DBColumn> result = new ArrayList<DBColumn>();
        for (int i=0; i < lstModelDest.size(); i++) {
            result.add((DBColumn)lstModelDest.elementAt(i));
        }
        return result;
    }

    public void resetSelectedColumns() {
        lstModelDest.removeAllElements();
        btnSelection();
    }

    class DBColumnCellRenderer extends DefaultListCellRenderer {

        private final Icon primaryKeyIcon = ImageUtil.getImageIcon("keycolumn");
        private final Icon foreignKeyIcon = ImageUtil.getImageIcon("fkeycolumn");
        private final Icon indexKeyIcon = ImageUtil.getImageIcon("icolumn");
        private final Icon columnIcon = ImageUtil.getImageIcon("column");

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                DBColumn column = (DBColumn) value;
                if (column.isPrimaryKey()) {
                    comp.setIcon(primaryKeyIcon);
                } else if (column.isForeignKey()) {
                    comp.setIcon(foreignKeyIcon);
                } else if (column.isIndex()) {
                    comp.setIcon(indexKeyIcon);    
                } else {
                    comp.setIcon(columnIcon);
                }
                value = column.getTable() + "." + column.getName();
                comp.setText(value.toString());
                list.setToolTipText(value.toString());
            }
            return comp;
        }

    }

    public String getSchemaName() {
        return columnPanel.getSchema();
    }

}
