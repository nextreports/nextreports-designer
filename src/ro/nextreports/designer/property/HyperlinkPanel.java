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

import ro.nextreports.engine.band.Hyperlink;

import javax.swing.*;

import ro.nextreports.designer.util.I18NSupport;

import java.awt.*;

/**
 * User: mihai.panaitescu
 * Date: 01-Mar-2010
 * Time: 12:28:46
 */
public class HyperlinkPanel extends JPanel {

    private Hyperlink hyperlink;
    private JTextField nameTextField;
    private JTextField urlTextField;

    public HyperlinkPanel() {
        setLayout(new GridBagLayout());
        nameTextField = new JTextField(30);
        urlTextField = new JTextField(30);
        add(new JLabel(I18NSupport.getString("url.text")),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(nameTextField,
                new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
        add(new JLabel(I18NSupport.getString("url.link")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        add(urlTextField,
                new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
    }

    public Hyperlink getHyperLink()  {
        return new Hyperlink(nameTextField.getText(), urlTextField.getText());
    }

    public void setHyperLink(Hyperlink hyperlink) {
        nameTextField.setText(hyperlink.getText());
        urlTextField.setText(hyperlink.getUrl());
    }
}
