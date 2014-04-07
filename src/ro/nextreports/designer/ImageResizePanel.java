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

import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;

import ro.nextreports.designer.ui.IntegerTextField;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

/**
 * User: mihai.panaitescu
 * Date: 23-Mar-2010
 * Time: 11:12:06
 */
public class ImageResizePanel  extends JXPanel {

    private IntegerTextField widthTextField;
    private IntegerTextField heightTextField;
    private JCheckBox checkBox;
    private JButton percentageButton;
    private JTextField percentageTextField;

    private Integer actualWidth;
    private Integer actualHeight;
    private Integer realWidth;
    private Integer realHeight;
    private double ratio;

    private Dimension dim = new Dimension(60, 20);
    private Dimension percentDim = new Dimension(30, 20);
    private Dimension buttonDim = new Dimension(20, 20);

    public ImageResizePanel(int[] realSize, BandElement be) {
        setLayout(new GridBagLayout());

        if (be instanceof ImageBandElement) {
        	ImageBandElement ibe = (ImageBandElement)be;
        	this.actualWidth = ibe.getWidth();
        	this.actualHeight = ibe.getHeight();
        } else {
        	ImageColumnBandElement icbe = (ImageColumnBandElement)be;
        	this.actualWidth = icbe.getWidth();
        	this.actualHeight = icbe.getHeight();
        }
        this.realWidth = realSize[0];
        this.realHeight = realSize[1];

        ratio = (double)realWidth / realHeight;
        
        widthTextField = new IntegerTextField();
        if (actualWidth != null) {
            widthTextField.setText(actualWidth.toString());
        }
        widthTextField.setPreferredSize(dim);
        widthTextField.setMinimumSize(dim);
        widthTextField.setMaximumSize(dim);
        widthTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (checkBox.isSelected()) {
                    int width = getImageWidth();
                    int height = (int) (width / ratio);
                    heightTextField.setText(String.valueOf(height));
                }
            }
        });

        heightTextField = new IntegerTextField();
        if (actualHeight != null) {
            heightTextField.setText(actualHeight.toString());
        }
        heightTextField.setPreferredSize(dim);
        heightTextField.setMinimumSize(dim);
        heightTextField.setMaximumSize(dim);
        heightTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (checkBox.isSelected()) {
                    int height = getImageHeight();
                    int width = (int) (height * ratio);
                    widthTextField.setText(String.valueOf(width));
                }
            }
        });

        percentageButton = new JButton(ImageUtil.getImageIcon("select_down"));
        percentageButton.setPreferredSize(buttonDim);
        percentageButton.setMinimumSize(buttonDim);
        percentageButton.setMaximumSize(buttonDim);

        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenuItem[] m = new JMenuItem[5];
        final int[] percents = {10, 25, 50, 75, 100};
        for (int i = 0; i < 5; i++) {
            m[i] = new JMenuItem(percents[i] + "%");
            final int j = i;
            m[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    percentageTextField.setText(String.valueOf(percents[j]));
                    updateDimensions(getPercent());
                }
            });
            popupMenu.add(m[i]);
        }
        percentageButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
				if(popupMenu.isVisible()) {
					popupMenu.setVisible(false);
				} else {
                    if (popupMenu.getComponentCount() > 0) {
					    popupMenu.show(percentageButton, 0, percentageButton.getHeight());
                    }
				}
			}		
        });

        percentageTextField = new JTextField();
        percentageTextField.setPreferredSize(percentDim);
        percentageTextField.setMinimumSize(percentDim);
        percentageTextField.setMaximumSize(percentDim);
        percentageTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updateDimensions(getPercent());
            }
        });


        checkBox = new JCheckBox(I18NSupport.getString("size.image.action.ratio"));
        checkBox.setSelected(true);

        JLabel info = new JLabel(I18NSupport.getString("size.image.action.info"));

        add(info, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JXTitledSeparator(""),
                new GridBagConstraints(0, 1, 3, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
        add(checkBox, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        JPanel pPanel = new JPanel();
        pPanel.setLayout(new BoxLayout(pPanel, BoxLayout.X_AXIS));
        pPanel.add(percentageTextField);
        pPanel.add(Box.createHorizontalStrut(1));
        pPanel.add(percentageButton);
        pPanel.add(Box.createHorizontalStrut(2));
        pPanel.add(new JLabel("%"));
        add(pPanel, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel(I18NSupport.getString("size.image.action.width")), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        add(widthTextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel("[ " + realWidth + " px ]"), new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        add(new JLabel(I18NSupport.getString("size.image.action.height")), new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));
        add(heightTextField, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        add(new JLabel("[ " + realHeight + " px ]"), new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 0), 0, 0));

    }

    public int getImageWidth()  {
        if ("".equals(widthTextField.getText().trim())) {
            return 0;
        }
        return Integer.parseInt(widthTextField.getText());
    }

    public int getImageHeight()  {
        if ("".equals(heightTextField.getText().trim())) {
            return 0;
        }
        return Integer.parseInt(heightTextField.getText());
    }

    private int getPercent() {
        String text = percentageTextField.getText();
        if ("".equals(text.trim())) {
           return 100;
        }
        try {
            return Integer.parseInt(text);            
        } catch (NumberFormatException nfe) {
            return 100;
        }
    }

    private void updateDimensions(int percent) {
        widthTextField.setText(String.valueOf(realWidth * percent / 100));
        heightTextField.setText(String.valueOf(realHeight * percent / 100));
    }
}
