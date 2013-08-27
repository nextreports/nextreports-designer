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
package ro.nextreports.designer.ui.vista;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 20, 2009
 * Time: 10:18:03 AM
 */
public class VistaButton extends JButton {

    private final static String iconName = "arrow.png";
    private Action buttonAction;
    private JLabel textLabel;
    private JLabel iconLabel;

    public VistaButton(String text) {
        this(text, null);
    }

    public VistaButton(String text, String description) {
        this(new ImageIcon(VistaButton.class.getResource(iconName)), text, description);
    }

    public VistaButton(Action buttonAction, String description) {
        this.buttonAction = buttonAction;
        Icon icon = (Icon) this.buttonAction.getValue(Action.SMALL_ICON);
        String text = (String) this.buttonAction.getValue(Action.NAME);
        Integer mnemonic = (Integer) this.buttonAction.getValue(Action.MNEMONIC_KEY);
        KeyStroke ks = (KeyStroke) this.buttonAction.getValue(Action.ACCELERATOR_KEY);

        if (icon == null) {
            icon = new ImageIcon(VistaButton.class.getResource(iconName));
        }

        init(icon, text, description, mnemonic);
    }

    public VistaButton(Icon icon, String text, String description) {
        init(icon, text, description, null);
    }

    private void init(Icon icon, String text, String description, Integer mnemonic) {
        setLayout(new GridBagLayout());

        iconLabel = new JLabel(icon);
        textLabel = new JLabel(getHtmlText(text, mnemonic));
        JLabel descLabel = new JLabel(getHtmlDescription(description));

        add(iconLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 2, 0), 0, 0));
        add(textLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 2, 5), 0, 0));
        if (description != null) {
            add(descLabel, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));
        }

        Insets margins = getMargin();
        margins.left = 2;
        margins.right = 2;
        setMargin(margins);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    @Override
    public Action getAction() {
        return buttonAction;
    }

    @Override
    public void setAction(Action action) {
        buttonAction = action;
    }

    public Icon getActionIcon() {
        if (buttonAction != null) {
            return (Icon) this.buttonAction.getValue(Action.SMALL_ICON);
        }
        return null;
    }

    public void setIcon(Icon icon) {
        if (buttonAction != null) {
            iconLabel.setIcon(icon);
            repaint();
        }
    }

    private void buttonSelection() {
        setBorderPainted(true);
        setContentAreaFilled(true);
        textLabel.setForeground(VistaUtil.TEXT_FOREGROUND);
    }

    private void buttonDeselection() {
        setBorderPainted(false);
        setContentAreaFilled(false);
        textLabel.setForeground(getForeground());
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            buttonSelection();
        } else {
            buttonDeselection();
        }
    }

    @Override
    public boolean isSelected() {
        return isBorderPainted();
    }

    private String getHtmlText(String text, Integer mnemonic) {
        if (VistaUtil.isHtml(text)) {
            return text;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<p><b>");
        sb.append("<font size=\"3\">");
        if (mnemonic == null) {
            sb.append(text);
        } else {
            boolean underline = false;
            for (char c : text.toCharArray()) {
                if (!underline && mnemonic.equals(new Integer(c))) {
                    sb.append("<u>");
                    sb.append(c);
                    sb.append("</u>");
                    underline = true;
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append("</font>");
        sb.append("</b></p>");
        sb.append("</html>");
        return sb.toString();
    }

    private String getHtmlDescription(String description) {
        if (VistaUtil.isHtml(description)) {
            return description;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<p>");
        sb.append("<font size=\"3\">");
        sb.append(description);
        sb.append("</font>");
        sb.append("</p>");
        sb.append("</html>");
        return sb.toString();
    }

    // paint button gradient
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isSelected()) {
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, 30, VistaUtil.BACKGROUND);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            setSelected(false);
        }
    }

    // helpers
    public KeyStroke getKeyStroke() {
        if (buttonAction == null) {
            return null;
        }
        return (KeyStroke) this.buttonAction.getValue(Action.ACCELERATOR_KEY);
    }

    public String getActionName() {
        if (buttonAction == null) {
            return null;
        }
        return (String) this.buttonAction.getValue(Action.NAME);
    }


}
