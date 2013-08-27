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
package ro.nextreports.designer.ui;


import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: mihai.panaitescu
 * Date: 14-May-2010
 * Time: 12:40:15
 */
public class ExcelColorChooserPanel extends AbstractColorChooserPanel {

    private Dimension btnDim = new Dimension(20, 20);

    // http://support.softartisans.com/kbview_1205.aspx
    private Color[] standardColors = new Color[]{
            new Color(0, 0, 0), new Color(153, 51, 0), new Color(51, 51, 0), new Color(0, 51, 0),
            new Color(0, 51, 102), new Color(0, 0, 128), new Color(51, 51, 153), new Color(51, 51, 51),
            new Color(128, 0, 0), new Color(255, 102, 0), new Color(128, 128, 0), new Color(0, 128, 0),
            new Color(0, 128, 128), new Color(0, 0, 255), new Color(102, 102, 153), new Color(128, 128, 128),
            new Color(255, 0, 0), new Color(255, 153, 0), new Color(153, 204, 0), new Color(51, 153, 102),
            new Color(51, 204, 204), new Color(51, 102, 255), new Color(128, 0, 128), new Color(150, 150, 150),
            new Color(255, 0, 255), new Color(255, 204, 0), new Color(255, 255, 0), new Color(0, 255, 0),
            new Color(0, 255, 255), new Color(0, 204, 255), new Color(153, 51, 102), new Color(192, 192, 192),
            new Color(255, 153, 204), new Color(255, 204, 153), new Color(255, 255, 153), new Color(204, 255, 204),
            new Color(204, 255, 255), new Color(153, 204, 255), new Color(204, 153, 255), new Color(255, 255, 255)
    };

    private Color[] chartFillColors = new Color[]{
            new Color(153, 153, 255), new Color(153, 51, 102), new Color(255, 255, 204), new Color(204, 255, 255),
            new Color(102, 0, 102), new Color(255, 128, 128), new Color(0, 102, 204), new Color(204, 204, 255)
    };

    private Color[] chartLineColors = new Color[]{
            new Color(0, 0, 128), new Color(255, 0, 255), new Color(255, 255, 0), new Color(0, 255, 255),
            new Color(128, 0, 128), new Color(128, 0, 0), new Color(0, 128, 128), new Color(0, 0, 255)
    };

    public void buildChooser() {
        setLayout(new GridBagLayout());

        add(new JLabel(I18NSupport.getString("colorchooser.excel.standard")),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));

        JPanel standardPanel = new JPanel();
        standardPanel.setLayout(new GridLayout(5, 8));
        for (Color color : standardColors) {
            standardPanel.add(makeAddButton("", color));
        }
        add(standardPanel,
                new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));

        add(new JLabel(I18NSupport.getString("colorchooser.excel.fills")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        JPanel fillPanel = new JPanel();
        fillPanel.setLayout(new GridLayout(1, 8));
        for (Color color : chartFillColors) {
            fillPanel.add(makeAddButton("", color));
        }
        add(fillPanel,
                new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));

        add(new JLabel(I18NSupport.getString("colorchooser.excel.lines")),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        JPanel linePanel = new JPanel();
        linePanel.setLayout(new GridLayout(1, 8));
        for (Color color : chartLineColors) {
            linePanel.add(makeAddButton("", color));
        }
        add(linePanel,
                new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST,
                        GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0));
    }

    public void updateChooser() {
    }

    public String getDisplayName() {
        return I18NSupport.getString("colorchooser.excel.name");
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }

    private JButton makeAddButton(String name, Color color) {
        JButton button = new JButton(name);
        button.setPreferredSize(btnDim);
        button.setMinimumSize(btnDim);
        button.setMaximumSize(btnDim);
        button.setBackground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                BorderFactory.createRaisedBevelBorder()));
        button.setAction(setColorAction);
        button.setToolTipText(color.getRed() + "," + color.getGreen() + "," + color.getBlue());
        return button;
    }

    Action setColorAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton) evt.getSource();
            getColorSelectionModel().setSelectedColor(button.getBackground());
        }
    };
}
