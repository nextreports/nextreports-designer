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
package ro.nextreports.designer.ui.wizard.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Displays a list of messages and blocks until close is pressed.
 *
 * @author Decebal Suiu
 */
public class InfoDialog extends JDialog {

    private static final Icon icon = UIManager.getIcon("OptionPane.informationIcon");

    private MultiLineLabel multilineLabel;

    public InfoDialog(JFrame frame) {
        super(frame, "Info", true);
        initComponents();
        setLocationRelativeTo(frame);
    }

    public InfoDialog(JDialog dialog) {
        super(dialog, "Info", true);
        initComponents();
        setLocationRelativeTo(dialog);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        iconPanel.add(new JLabel(icon));

        JScrollPane scrollPane = new JScrollPane(multilineLabel = new MultiLineLabel());
        scrollPane.setBorder(null);

        mainPanel.add(iconPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 10), 0, 0));
        mainPanel.add(scrollPane, new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(20, 10, 0, 10), 0, 0));

        add(mainPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                setVisible(false);
            }

        });

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        /*
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        CompoundBorder innerBorder = new CompoundBorder(new EdgeBorder(
                SwingConstants.NORTH), new EmptyBorder(10, 0, 0, 0));
        buttonsPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10),
                innerBorder));
         */
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));        
        buttonsPanel.add(closeButton);

        add(buttonsPanel, BorderLayout.SOUTH);
        setSize(300, 140);
    }

    /**
     * Show a list of messages and block until close is pressed.
     *
     * @param messages a List of String objects.
     */
    public void showMessages(List<String> messages) {
        String text = "";
        for (String s : messages) {
            text = text + s + "\n";
        }
        multilineLabel.setText(text);
        setModal(true);
        setVisible(true);
    }

}
