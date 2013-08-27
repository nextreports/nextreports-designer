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
package ro.nextreports.designer.ui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import ro.nextreports.designer.ui.wizard.util.LookAndFeelTweaks;


/**
 * @author Decebal Suiu
 */
public class BannerPanel extends JPanel {

	private JLabel titleLabel;
    private JTextComponent subtitleLabel;
    private JLabel iconLabel;

    public BannerPanel() {
        setBorder(new CompoundBorder(new EtchedBorder(), new EmptyBorder(3, 3, 3, 3)));

        setOpaque(true);
        setBackground(UIManager.getColor("Table.background"));

        titleLabel = new JLabel();
        titleLabel.setOpaque(false);

        subtitleLabel = new JEditorPane("text/html", "<html>");
        subtitleLabel.setFont(titleLabel.getFont());

        LookAndFeelTweaks.makeBold(titleLabel);
        LookAndFeelTweaks.makeMultilineLabel(subtitleLabel);
        LookAndFeelTweaks.htmlize(subtitleLabel);

        iconLabel = new JLabel();
        iconLabel.setMinimumSize(new Dimension(50, 50));

        setLayout(new BorderLayout());

        JPanel nestedPane = new JPanel();
        nestedPane.setLayout(new GridBagLayout());
        nestedPane.setOpaque(false);
        nestedPane.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
        nestedPane.add(new JLabel(""), new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));        
        nestedPane.add(subtitleLabel, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));

        add(BorderLayout.CENTER, nestedPane);
        add(BorderLayout.EAST, iconLabel);
    }

    public void setTitleColor(Color color) {
        titleLabel.setForeground(color);
    }

    public Color getTitleColor() {
        return titleLabel.getForeground();
    }

    public void setSubtitleColor(Color color) {
        subtitleLabel.setForeground(color);
    }

    public Color getSubtitleColor() {
        return subtitleLabel.getForeground();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public String getTitle() {
        return titleLabel.getText();
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public String getSubtitle() {
        return subtitleLabel.getText();
    }

    public void setSubtitleVisible(boolean b) {
        subtitleLabel.setVisible(b);
    }

    public boolean isSubtitleVisible() {
        return subtitleLabel.isVisible();
    }

    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }

    public Icon getIcon() {
        return iconLabel.getIcon();
    }

    public void setIconVisible(boolean b) {
        iconLabel.setVisible(b);
    }

    public boolean isIconVisible() {
        return iconLabel.isVisible();
    }

}
