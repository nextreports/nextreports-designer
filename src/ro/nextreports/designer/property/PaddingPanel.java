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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.band.Padding;

/**
 * @author Decebal Suiu
 */
/**
 * @author alexandru.parvulescu
 * 
 */
public class PaddingPanel extends JPanel {

	private Padding padding;
	private JRadioButton shared;
	private JLabel sharedPadding;
	private JTextField sharedPaddingValue;
	private JRadioButton individual;
	private JLabel leftPadding;
	private JTextField leftPaddingValue;
	private JLabel rightPadding;
	private JTextField rightPaddingValue;
	private JLabel topPadding;
	private JTextField topPaddingValue;
	private JLabel bottomPadding;
	private JTextField bottomPaddingValue;
	ButtonGroup group;

	public PaddingPanel() {
		super();
		initComponents();
		initUI();
	}

	private void initComponents() {
		shared = new JRadioButton(I18NSupport.getString("padding.settings.shared"));
		individual = new JRadioButton(I18NSupport.getString("padding.settings.individual"));
		sharedPadding = new JLabel(I18NSupport.getString("padding.name"));
		sharedPaddingValue = new JTextField(3);
		leftPadding = new JLabel(I18NSupport.getString("padding.left.name"));
		leftPaddingValue = new JTextField(3);
		rightPadding = new JLabel(I18NSupport.getString("padding.right.name"));
		rightPaddingValue = new JTextField(3);
		topPadding = new JLabel(I18NSupport.getString("padding.top.name"));
		topPaddingValue = new JTextField(3);
		bottomPadding = new JLabel(I18NSupport.getString("padding.bottom.name"));
		bottomPaddingValue = new JTextField(3);

		shared.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectShared();
			}
		});
		individual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectIndividual();
			}
		});

		group = new ButtonGroup();
		group.add(shared);
		group.add(individual);

		updatePadding();
	}

	private void selectShared() {
		this.sharedPaddingValue.setEnabled(true);
		//
		this.leftPaddingValue.setEnabled(false);
		this.leftPaddingValue.setText("0");
		this.rightPaddingValue.setEnabled(false);
		this.rightPaddingValue.setText("0");
		this.topPaddingValue.setEnabled(false);
		this.topPaddingValue.setText("0");
		this.bottomPaddingValue.setEnabled(false);
		this.bottomPaddingValue.setText("0");
	}

	private void selectIndividual() {
		this.sharedPaddingValue.setEnabled(false);
		this.sharedPaddingValue.setText("0");
		//
		this.leftPaddingValue.setEnabled(true);
		this.rightPaddingValue.setEnabled(true);
		this.topPaddingValue.setEnabled(true);
		this.bottomPaddingValue.setEnabled(true);
	}

	private void initUI() {
		setLayout(new GridBagLayout());
		String pxPaddingValue = "px";

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		add(shared, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		add(sharedPadding, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 1);
		add(sharedPaddingValue, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		add(new JLabel(pxPaddingValue), gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 3;
		add(individual, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 3);
		add(leftPadding, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 1);
		add(leftPaddingValue, gbc);

		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel(pxPaddingValue), gbc);

		gbc.gridx = 3;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 3);
		gbc.fill = GridBagConstraints.NONE;
		add(rightPadding, gbc);

		gbc.gridx = 4;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 1);
		add(rightPaddingValue, gbc);

		gbc.gridx = 5;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(new JLabel(pxPaddingValue), gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 3);
		add(topPadding, gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 1);
		add(topPaddingValue, gbc);

		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 5);
		add(new JLabel(pxPaddingValue), gbc);

		gbc.gridx = 3;
		gbc.gridy = 4;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 3);
		add(bottomPadding, gbc);

		gbc.gridx = 4;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, 1);
		add(bottomPaddingValue, gbc);

		gbc.gridx = 5;
		gbc.gridy = 4;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(new JLabel(pxPaddingValue), gbc);

	}

	public Padding getPadding() {
		if (padding == null) {
			padding = new Padding(0, 0, 0, 0);
		}
		return padding;
	}

	public Padding getFinalPadding() {
		int left = 0;
		int right = 0;
		int top = 0;
		int bottom = 0;
		if (this.individual.isSelected()) {
			left = Integer.parseInt(leftPaddingValue.getText());
			right = Integer.parseInt(rightPaddingValue.getText());
			top = Integer.parseInt(topPaddingValue.getText());
			bottom = Integer.parseInt(bottomPaddingValue.getText());
		} else {
			left = Integer.parseInt(sharedPaddingValue.getText());
			right = Integer.parseInt(sharedPaddingValue.getText());
			top = Integer.parseInt(sharedPaddingValue.getText());
			bottom = Integer.parseInt(sharedPaddingValue.getText());
		}
		return new Padding(left, right, top, bottom);
	}

	public void setPadding(Padding padding) {
		this.padding = padding;
		updatePadding();
	}

    private void updatePadding() {
        if (getPadding().getLeft() == getPadding().getRight() && (getPadding().getTop() == getPadding().getBottom())
				&& (getPadding().getLeft() == getPadding().getTop())) {
			selectShared();
			sharedPaddingValue.setText("" + getPadding().getTop());
			shared.setSelected(true);
		} else {
			selectIndividual();
			individual.setSelected(true);
			leftPaddingValue.setText("" + getPadding().getLeft());
			rightPaddingValue.setText("" + getPadding().getRight());
			topPaddingValue.setText("" + getPadding().getTop());
			bottomPaddingValue.setText("" + getPadding().getBottom());
		}
    }

}
