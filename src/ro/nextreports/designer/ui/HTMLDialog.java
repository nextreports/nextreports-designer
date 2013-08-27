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


import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: 17-Mar-2009
 * Time: 15:00:29
 */
public class HTMLDialog extends JDialog {

    private JEditorPane editor;
    private JScrollPane scr;

    public HTMLDialog(Frame owner, String title, String htmlText) {
        super(owner);
        setTitle(title);
        editor = new JEditorPane("text/html", htmlText);
        scr = new JScrollPane(editor);

        JButton closeButton = new JButton(I18NSupport.getString("base.dialog.close"));
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(Box.createHorizontalGlue());
        buttonsPanel.add(closeButton);

        setLayout(new GridBagLayout());
        add(scr, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(10, 10, 0, 10), 0, 0));
        add(buttonsPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(10, 10, 10, 10), 0, 0));
        
        setSize(new Dimension(400, 300));
    }

}
