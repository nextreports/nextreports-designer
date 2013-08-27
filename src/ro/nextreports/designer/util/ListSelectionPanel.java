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
package ro.nextreports.designer.util;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingx.JXList;

/**
 * Created by IntelliJ IDEA.
 * <p/>
 * Author: Mihai Dinca-Panaitescu
 * <p/>
 * User: mihai.panaitescu
 * <p/>
 * Date: Jun 10, 2005 Time: 3:25:26 PM
 */
public class ListSelectionPanel extends JPanel {

	private Dimension scrDim = new Dimension(200, 150);
	private Dimension btnDim = new Dimension(20, 20);
	private JScrollPane scrSrc = new JScrollPane();
	private JScrollPane scrDest = new JScrollPane();
	private JPanel pp = new JPanel();
	private JXList lstDest = new JXList();
	private JXList lstSrc = new JXList();
	private JButton btnAdd = new JButton();
	private JButton btnRem = new JButton();
	private JButton btnUp = new JButton();
	private JButton btnDown = new JButton();
	private DefaultListModel lstModelDest = new DefaultListModel();
	private DefaultListModel lstModelSrc = new DefaultListModel();
	private Comparator comp;

	private List leftElements;
	private List rightElements;
	private String leftTitle;
	private String rightTitle;

	private boolean sort = false;
	private boolean showUpDown = true;    

    public ListSelectionPanel(String leftTitle, String rightTitle, boolean sort, boolean showUpDown) {
		try {
			this.leftTitle = leftTitle;
			this.rightTitle = rightTitle;
			this.sort = sort;
			this.showUpDown = showUpDown;
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ListSelectionPanel(List leftElements, List rightElements,
							  String leftTitle, String rightTitle, boolean sort, boolean showUpDown) {
		this.leftElements = leftElements;
		this.rightElements = rightElements;
		this.leftTitle = leftTitle;
		this.rightTitle = rightTitle;
		this.sort = sort;
		this.showUpDown = showUpDown;

		for (Object leftElement : leftElements) {
			lstModelSrc.addElement(leftElement);
		}
		for (Object rightElement : rightElements) {
			lstModelDest.addElement(rightElement);
		}
		try {
			jbInit();
			if  (lstModelDest.size() > 0) {
				onSetRight();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized void setLists(List leftElements, List rightElements) {
		this.leftElements = leftElements;
		this.rightElements = rightElements;

		lstModelSrc.removeAllElements();
		lstModelDest.removeAllElements();
		for (Object leftElement : leftElements) {
			lstModelSrc.addElement(leftElement);
		}
		for (Object rightElement : rightElements) {
			lstModelDest.addElement(rightElement);
		}
		btnSelection();
		if  (lstModelDest.size() > 0) {
			onSetRight();
		}		
	}

	private void jbInit() {
		this.setLayout(new GridBagLayout());

		//lstSrc.putClientProperty(StringConvertor.class, new DBTableStringConverter());
		lstSrc.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					btnAdd_actionPerformed();
				}
			}
		});

		lstDest.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					btnRem_actionPerformed();
				}
			}
		});

		scrSrc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrSrc.getViewport().add(lstSrc, null);
		scrSrc.setMinimumSize(scrDim);
		scrSrc.setPreferredSize(scrDim);
		scrSrc.setBorder(new TitledBorder(leftTitle));
		lstSrc.setModel(lstModelSrc);
		scrDest.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrDest.setMinimumSize(scrDim);
		scrDest.setPreferredSize(scrDim);
		scrDest.getViewport().add(lstDest, null);
		//scrDest.setBorder(new TitledBorder(rightTitle));
		pp.setBorder(new TitledBorder(rightTitle));
		lstDest.setModel(lstModelDest);

		//btnAdd.setText(">");
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

		add(scrSrc, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(1, 5, 1, 5), 0, 0));
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
		btnPanel.add(Box.createGlue());
		btnPanel.add(btnAdd);
		btnPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		btnPanel.add(btnRem);
		btnPanel.add(Box.createGlue());
		add(btnPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE,
				new Insets(1, 1, 1, 1), 0, 0));

		pp.setLayout(new GridBagLayout());
		pp.add(scrDest, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(1, 5, 1, 2), 0, 0));

		if (showUpDown) {
			JPanel btnPriPanel = new JPanel();
			btnPriPanel.setLayout(new BoxLayout(btnPriPanel, BoxLayout.Y_AXIS));
			btnPriPanel.add(btnUp);
			btnPriPanel.add(Box.createGlue());
			btnPriPanel.add(btnDown);
			pp.add(btnPriPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
					new Insets(1, 0, 2, 0), 0, 0));
		}

		add(pp, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH,
				new Insets(1, 5, 1, 2), 0, 0));


	}

	private void btnSelection() {
		if (lstModelSrc.size() == 0) {
			btnAdd.setEnabled(false);
		} else {
			btnAdd.setEnabled(true);
		}
		if (lstModelDest.size() == 0) {
			btnRem.setEnabled(false);
		} else {
			btnRem.setEnabled(true);
		}
	}

	private void btnAdd_actionPerformed() {
		Object values[] = lstSrc.getSelectedValues();
		if (values.length > 0) {
			for (Object value : values) {
				lstModelSrc.removeElement(value);
				lstModelDest.addElement(value);
			}
			if (sort) {
				sort(lstModelSrc);
				sort(lstModelDest);
			}
			btnSelection();
			onAdd();
		}
	}

	private void btnRem_actionPerformed() {
		Object[] values = lstDest.getSelectedValues();
        if (allowRemove()) {
            if (values.length > 0) {
                for (Object value : values) {
                    lstModelDest.removeElement(value);
                    lstModelSrc.addElement(value);
                }
                if (sort) {
                    sort(lstModelSrc);
                    sort(lstModelDest);
                }
                btnSelection();
                onRemove();
            }
        }
    }

	protected void onAdd() {
	}

    protected boolean allowRemove() {
        return true;
    }

    protected void onRemove() {
	}

	protected void onSetRight() {
	}

	@SuppressWarnings("unchecked")
	private void sort(DefaultListModel model) {
		// we need a List for sorting
		int size = model.getSize();
		ArrayList list = new ArrayList();
		for (int x = 0; x < size; ++x) {
			Object o = model.get(x);
			list.add(o);
		}

		if (comp != null) {
		   Collections.sort(list, comp);
		} else {
		   Collections.sort(list);
		}
		// update the model with a sorted List
		for (int x = 0; x < size; ++x) {
			Object obj = list.get(x);			
			if ((model.getElementAt(x) != null) && !model.getElementAt(x).equals(obj)) {
				model.set(x, obj);
			}
		}
	}

	public List getSourceElements() {
		return Collections.list(lstModelSrc.elements());
	}

	public List getDestinationElements() {
		return Collections.list(lstModelDest.elements());
	}

	public void setRenderer(DefaultListCellRenderer renderer, Comparator comp) {
		lstSrc.setCellRenderer(renderer);
		lstDest.setCellRenderer(renderer);
		this.comp = comp;
	}

	public void setListSize(Dimension dim) {
		scrSrc.setPreferredSize(dim);
		scrSrc.setMinimumSize(dim);
		scrDest.setPreferredSize(dim);
		scrDest.setMinimumSize(dim);
	}

	public void setEnabled(boolean enable) {
		lstSrc.setEnabled(enable);
		lstDest.setEnabled(enable);
		btnAdd.setEnabled(enable);
		btnRem.setEnabled(enable);
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

    public Object[] getDestSelectedValues() {
        return lstDest.getSelectedValues();
    }

}
