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

import java.awt.*;

import ro.nextreports.designer.ui.BaseDialog;

/**
 * User: mihai.panaitescu
 * Date: 01-Mar-2010
 * Time: 12:35:37
 */
public class HyperlinkChooser {

    public static Hyperlink showDialog(Component parent, String title, Hyperlink initialHyperlink) {

            final HyperlinkPanel hyperlinkPanel = new HyperlinkPanel();
            hyperlinkPanel.setHyperLink(initialHyperlink);
            BaseDialog dialog = new BaseDialog(hyperlinkPanel, title, true) {
                protected boolean ok() {
                    Hyperlink hyperlink = hyperlinkPanel.getHyperLink();
                    if ("".equals(hyperlink.getText().trim()) || "".equals(hyperlink.getUrl().trim())) {
                        return false;
                    }
                    return true;
                }
            };
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
            if (dialog.okPressed()) {
                return hyperlinkPanel.getHyperLink();
            } else {
                return null;
            }
        }

}
