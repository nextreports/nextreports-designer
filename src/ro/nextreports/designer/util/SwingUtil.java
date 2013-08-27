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

import javax.swing.*;

import ro.nextreports.designer.ui.CloseDialog;


import java.util.ArrayList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Decebal Suiu
 */
public class SwingUtil {

    public static CloseDialog createCloseDialog(JPanel panel, String title, boolean modal) {
        CloseDialog dlg = new CloseDialog(panel, title, modal);
        dlg.pack();
        Show.centrateComponent(dlg);
        return dlg;
    }

    public static CloseDialog createCloseDialog(JPanel panel, String title) {
        return createCloseDialog(panel, title, true);
    }

    public static void showCloseDialog(JPanel panel, String title, boolean modal) {
        CloseDialog dlg = createCloseDialog(panel, title, modal);
        dlg.setVisible(true);
    }

    public static void showCloseDialog(JPanel panel, String title) {
        showCloseDialog(panel, title, true);
    }

    /**
     * Sets the JButtons inside a JPanelto be the same size.
     * This is done dynamically by setting each button's preferred and maximum
     * sizes after the buttons are created. This way, the layout automatically
     * adjusts to the locale-specific strings.
     *
     * @param jPanelButtons JPanel containing buttons
     */
    public static void equalizeButtonSizes(JPanel jPanelButtons) {
        ArrayList<JButton> lbuttons = new ArrayList<JButton>();
        for (int i = 0; i < jPanelButtons.getComponentCount(); i++) {
            Component c = jPanelButtons.getComponent(i);
            if (c instanceof JButton) {
                lbuttons.add((JButton)c);
            }
        }

        // Get the largest width and height
        Dimension maxSize = new Dimension(0, 0);
        for (JButton lbutton : lbuttons) {
            Dimension d = lbutton.getPreferredSize();
            maxSize.width = Math.max(maxSize.width, d.width);
            maxSize.height = Math.max(maxSize.height, d.height);
        }

        for (JButton btn : lbuttons) {
            btn.setPreferredSize(maxSize);
            btn.setMinimumSize(maxSize);
            btn.setMaximumSize(maxSize);
        }
    }

    /**
     * Register a component for requesting focus
     * The component will request focus every time a "mouse enter" event occurs
     * @param component component which requests focus
     */
    /*
    public static void registerForFocus(final JComponent component) {
        component.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                // Request the focus (if don't already have it)
                if (!component.hasFocus()) {
                    // ensure requestFocus is enabled
                    if (!component.isRequestFocusEnabled()) {
                        component.setRequestFocusEnabled(true);
                    }
                    component.grabFocus();
                }
            }
        });
    }
    */

    /**
     * Register all buttons inside a panel to request focus
     * every time a "mouse enter" event occurs
     * The panel can contain other panels with buttons.
     * @param jPanelButtons a panel with buttons
     */
    /*
    public static void registerButtonsForFocus(JPanel jPanelButtons) {
        for (int i = 0; i < jPanelButtons.getComponentCount(); i++) {
            Component c = jPanelButtons.getComponent(i);
            if (c instanceof JButton) {
                registerForFocus((JButton) c);
            } else if (c instanceof JPanel) {
                registerButtonsForFocus((JPanel)c);
            }
        }
    }
    */

    public static void addCustomSeparator(JToolBar toolBar) {
        JLabel label = new JLabel(ImageUtil.getImageIcon("separator"));
        toolBar.addSeparator();
        toolBar.add(label);
        toolBar.addSeparator();
    }
    
}
