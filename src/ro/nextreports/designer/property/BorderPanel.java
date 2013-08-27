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
package ro.nextreports.designer.property;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.band.Border;

/**
 * @author Mihai Dinca-Panaitescu
 * @author Decebal Suiu
 * @author alexandru.parvulescu
 * 
 */
public class BorderPanel extends JPanel {

	private Border border;
	private JButton btnTop;
	private JButton btnLeft;
	private JButton btnRight;
	private JButton btnBottom;
	private JButton btnCenter;
	private JPanel center;
	private JPanel borderStuff;
	private boolean hasTop;
	private boolean hasLeft;
	private boolean hasRight;
	private boolean hasBottom;
	private boolean hasAll;
	private JComboBox cmbTop;
	private JComboBox cmbLeft;
	private JComboBox cmbBottom;
	private JComboBox cmbRight;
	private JButton btnLeftColor;
	private JButton btnRightColor;
	private JButton btnBottomColor;
	private JButton btnTopColor;
	private Dimension dim = new Dimension(20,20);

	private final String THIN = I18NSupport.getString("border.thin");
	private final int THIN_VALUE = 1;
	private final String THICK = I18NSupport.getString("border.thick");
	private final int THICK_VALUE = 3;
	private final String MEDIUM = I18NSupport.getString("border.medium");
	private final int MEDIUM_VALUE = 2;

	public BorderPanel() {
		super();
		initComponents();
		initUI();
	}

	private void initComponents() {		
		
		btnLeftColor = new JButton();
		btnLeftColor.setPreferredSize(dim);
		btnLeftColor.setMinimumSize(dim);
		btnLeftColor.setMaximumSize(dim);
		btnLeftColor.setBackground(Color.BLACK);
		registerColorSelection(btnLeftColor);
		
		btnRightColor = new JButton();
		btnRightColor.setPreferredSize(dim);
		btnRightColor.setMinimumSize(dim);
		btnRightColor.setMaximumSize(dim);
		btnRightColor.setBackground(Color.BLACK);
		registerColorSelection(btnRightColor);
		
		btnTopColor = new JButton();
		btnTopColor.setPreferredSize(dim);
		btnTopColor.setMinimumSize(dim);
		btnTopColor.setMaximumSize(dim);
		btnTopColor.setBackground(Color.BLACK);
		registerColorSelection(btnTopColor);
		
		btnBottomColor = new JButton();
		btnBottomColor.setPreferredSize(dim);
		btnBottomColor.setMinimumSize(dim);
		btnBottomColor.setMaximumSize(dim);
		btnBottomColor.setBackground(Color.BLACK);
		registerColorSelection(btnBottomColor);
		
		cmbTop = new JComboBox(new String[] { THIN, MEDIUM, THICK });		
        cmbTop.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateBorder();
            }
        });
        
        cmbLeft = new JComboBox(new String[] { THIN, MEDIUM, THICK });        
        cmbLeft.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateBorder();
            }
        });
        
        cmbBottom = new JComboBox(new String[] { THIN, MEDIUM, THICK });        
        cmbBottom.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateBorder();
            }
        });
        
        cmbRight = new JComboBox(new String[] { THIN, MEDIUM, THICK });        
        cmbRight.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateBorder();
            }
        });
        
		btnTop = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				hasTop = !hasTop;
				updateBorder();
			}
		});
		btnTop.setPreferredSize(new Dimension(80, 10));
		btnLeft = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				hasLeft = !hasLeft;
				updateBorder();
			}
		});
		btnLeft.setPreferredSize(new Dimension(10, 75));
		btnRight = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				hasRight = !hasRight;
				updateBorder();
			}
		});
		btnRight.setPreferredSize(new Dimension(10, 75));
		btnBottom = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				hasBottom = !hasBottom;
				updateBorder();
			}
		});
		btnBottom.setPreferredSize(new Dimension(80, 10));
		//
		btnCenter = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				hasAll = !hasAll;
				if (hasAll) {
					hasTop = true;
					hasLeft = true;
					hasRight = true;
					hasBottom = true;
				} else {
					hasTop = false;
					hasLeft = false;
					hasRight = false;
					hasBottom = false;
				}
				updateBorder();
			}
		});
		btnCenter.setPreferredSize(new Dimension(30, 28));
		//
		center = new JPanel();
		center.setLayout(new GridBagLayout());
		center.setBorder(new MyBorder());
		center.setPreferredSize(new Dimension(75, 75));
		center.add(btnCenter);
				
	}
	
	private void registerColorSelection(final JButton btn) {
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JDialog parent = (JDialog) SwingUtilities.getWindowAncestor(BorderPanel.this);
				Color color = ExtendedColorChooser.showDialog(parent, 
						I18NSupport.getString("color.dialog.title"), btn.getBackground());
				if (color == null) {
					color = Color.BLACK;
				}				
				btn.setBackground(color);
				updateBorder();
			}			
		});
	}

	private void updateBorder() {
		center.setBorder(new MyBorder());
		updateTooltip();
	}

	private void updateTooltip() {
		if (hasAll) {
			btnCenter.setToolTipText(I18NSupport.getString("border.hide.all"));
		} else {
			btnCenter.setToolTipText(I18NSupport.getString("border.show.all"));
		}
		if (hasTop) {
			btnTop.setToolTipText(I18NSupport.getString("border.hide.top"));
		} else {
			btnTop.setToolTipText(I18NSupport.getString("border.show.top"));
		}
		if (hasLeft) {
			btnLeft.setToolTipText(I18NSupport.getString("border.hide.left"));
		} else {
			btnLeft.setToolTipText(I18NSupport.getString("border.show.left"));
		}
		if (hasRight) {
			btnRight.setToolTipText(I18NSupport.getString("border.hide.right"));
		} else {
			btnRight.setToolTipText(I18NSupport.getString("border.show.right"));
		}
		if (hasBottom) {
			btnBottom.setToolTipText(I18NSupport.getString("border.hide.bottom"));
		} else {
			btnBottom.setToolTipText(I18NSupport.getString("border.show.bottom"));
		}
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		
		borderStuff = new JPanel();
		borderStuff.setLayout(new GridBagLayout());
		borderStuff.setPreferredSize(new Dimension(160, 130));
		borderStuff.setMinimumSize(new Dimension(160, 130));
		borderStuff.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
					
		borderStuff.add(btnTop, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		borderStuff.add(btnLeft, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		borderStuff.add(btnRight, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		borderStuff.add(btnBottom, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		borderStuff.add(center, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
								
		add(new JLabel(I18NSupport.getString("border.choose.borders")), 
				new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		add(borderStuff, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		add(new JLabel(I18NSupport.getString("border.choose.thick")), 
				new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0,0,5,0), 0, 0));		
		add(new JLabel("Top"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(cmbTop, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(btnTopColor, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,0), 0, 0));
		add(new JLabel("Left"), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(cmbLeft, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(btnLeftColor, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,0), 0, 0));
		add(new JLabel("Bottom"), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(cmbBottom, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(btnBottomColor, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,0), 0, 0));
		add(new JLabel("Right"), new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(cmbRight, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		add(btnRightColor, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(5,0,0,0), 0, 0));

	}

	public Border getBorderValue() {
		if (border == null) {
			border = new Border(0, 0, 0, 0);
		}
		return border;
	}

	public int getTopBorderThicknessValue() {
        if (cmbTop.getSelectedItem().toString().equalsIgnoreCase(THIN))
			return THIN_VALUE;
		if (cmbTop.getSelectedItem().toString().equalsIgnoreCase(THICK))
			return THICK_VALUE;
		return MEDIUM_VALUE;
	}
	
	public int getLeftBorderThicknessValue() {
        if (cmbLeft.getSelectedItem().toString().equalsIgnoreCase(THIN))
			return THIN_VALUE;
		if (cmbLeft.getSelectedItem().toString().equalsIgnoreCase(THICK))
			return THICK_VALUE;
		return MEDIUM_VALUE;
	}
	
	public int getBottomBorderThicknessValue() {
        if (cmbBottom.getSelectedItem().toString().equalsIgnoreCase(THIN))
			return THIN_VALUE;
		if (cmbBottom.getSelectedItem().toString().equalsIgnoreCase(THICK))
			return THICK_VALUE;
		return MEDIUM_VALUE;
	}
	
	public int getRightBorderThicknessValue() {
        if (cmbRight.getSelectedItem().toString().equalsIgnoreCase(THIN))
			return THIN_VALUE;
		if (cmbRight.getSelectedItem().toString().equalsIgnoreCase(THICK))
			return THICK_VALUE;
		return MEDIUM_VALUE;
	}

	public Border getFinalBorder() {
		int left = 0;
		int right = 0;
		int top = 0;
		int bottom = 0;
		if (hasTop) {
			top = getTopBorderThicknessValue();
		}
		if (hasBottom) {
			bottom = getBottomBorderThicknessValue();
		}
		if (hasLeft) {
			left = getLeftBorderThicknessValue();
		}
		if (hasRight) {
			right = getRightBorderThicknessValue();
		}
		Border border = new Border(left, right, top, bottom);
		border.setLeftColor(btnLeftColor.getBackground());
		border.setRightColor(btnRightColor.getBackground());
		border.setTopColor(btnTopColor.getBackground());
		border.setBottomColor(btnBottomColor.getBackground());		
		return border;
	}

	public void setBorderValue(Border border) {
		if (border == null) {
			border = new Border(0, 0, 0, 0);
		}
		this.border = border;
		//
		if (border.getTop() > 0) {
			hasTop = true;
		} else {
			hasTop = false;
		}
		if (border.getLeft() > 0) {
			hasLeft = true;
		} else {
			hasLeft = false;
		}
		if (border.getRight() > 0) {
			hasRight = true;
		} else {
			hasRight = false;
		}
		if (border.getBottom() > 0) {
			hasBottom = true;
		} else {
			hasBottom = false;
		}
		if (hasTop && hasBottom && hasLeft && hasRight) {
			hasAll = true;
		} else {
			hasAll = false;
		}
		
		btnLeftColor.setBackground(border.getLeftColor());
        btnRightColor.setBackground(border.getRightColor());
        btnTopColor.setBackground(border.getTopColor());
        btnBottomColor.setBackground(border.getBottomColor());

        if (border.getTop() == MEDIUM_VALUE) {
			cmbTop.setSelectedItem(MEDIUM);
		} else if (border.getTop() == THICK_VALUE) {
			cmbTop.setSelectedItem(THICK);
		} 
        
        if (border.getLeft() == MEDIUM_VALUE) {
			cmbLeft.setSelectedItem(MEDIUM);
		} else if (border.getLeft() == THICK_VALUE) {
			cmbLeft.setSelectedItem(THICK);
		} 
        
        if (border.getBottom() == MEDIUM_VALUE) {
			cmbBottom.setSelectedItem(MEDIUM);
		} else if (border.getBottom() == THICK_VALUE) {
			cmbBottom.setSelectedItem(THICK);
		} 
        
        if (border.getRight() == MEDIUM_VALUE) {
			cmbRight.setSelectedItem(MEDIUM);
		} else if (border.getRight() == THICK_VALUE) {
			cmbRight.setSelectedItem(THICK);
		} 
                        
        updateBorder();
    }

	private class MyBorder extends AbstractBorder {

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {			
			
            int thTop = getTopBorderThicknessValue();
            int thLeft = getLeftBorderThicknessValue();
            int thBottom = getBottomBorderThicknessValue();
            int thRight = getRightBorderThicknessValue();
            
            if (hasLeft) {            	
            	g.setColor(btnLeftColor.getBackground());
                g.drawLine(x + 2, y + 4, x + 2, y - 5 + height);
                if ( (thLeft & MEDIUM_VALUE)  == MEDIUM_VALUE) {
                    g.drawLine(x + 2 + 1, y + 4, x + 2 + 1, y - 5 + height);
                }
                if ( (thLeft & THICK_VALUE) == THICK_VALUE) {
                    g.drawLine(x + 2 + 2, y + 4, x + 2 + 2, y - 5 + height);
                }
            }
			if (hasTop) {
				g.setColor(btnTopColor.getBackground());
				g.drawLine(x + 2, y + 4, x - 2 + width, y + 4);
                if ( (thTop & MEDIUM_VALUE)  == MEDIUM_VALUE) {
                    g.drawLine(x + 2, y + 4 + 1, x - 2 + width, y + 4 + 1);
                }
                if ( (thTop & THICK_VALUE) == THICK_VALUE) {
                    g.drawLine(x + 2, y + 4 + 2, x - 2 + width, y + 4 + 2);
                }
            }
			if (hasRight) {
				g.setColor(btnRightColor.getBackground());
				g.drawLine(x - 2 + width, y + 4, x - 2 + width, y - 5 + height);
                if ( (thRight & MEDIUM_VALUE)  == MEDIUM_VALUE) {
                    g.drawLine(x - 2 + width - 1, y + 4, x - 2 + width - 1, y - 5 + height);
                }
                if ( (thRight & THICK_VALUE) == THICK_VALUE) {
                    g.drawLine(x - 2 + width - 2, y + 4, x - 2 + width - 2, y - 5 + height);
                }
            }
			if (hasBottom) {
				g.setColor(btnBottomColor.getBackground());
				g.drawLine(x + 2, y - 5 + height, x - 2 + width, y - 5 + height);
                if ( (thBottom & MEDIUM_VALUE)  == MEDIUM_VALUE) {
                    g.drawLine(x + 2, y - 5 + height - 1, x - 2 + width, y - 5 + height - 1);
                }
                if ( (thBottom & THICK_VALUE) == THICK_VALUE) {
                    g.drawLine(x + 2, y - 5 + height - 2, x - 2 + width, y - 5 + height - 2);
                }
            }
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(3, 3, 3, 3);
		}

		public boolean isBorderOpaque() {
			return false;
		}
	}

}
