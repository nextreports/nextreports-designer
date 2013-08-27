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

import java.awt.Font;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;

/**
 * @author Decebal Suiu
 */
public class LookAndFeelTweaks {

    public static void tweak() {
        Object listFont = UIManager.get("List.font");
        UIManager.put("Table.font", listFont);
        UIManager.put("ToolTip.font", listFont);
        UIManager.put("TextField.font", listFont);
        UIManager.put("FormattedTextField.font", listFont);
        UIManager.put("Viewport.background", "Table.background");
    }

    public static void makeBold(JComponent component) {
        component.setFont(component.getFont().deriveFont(Font.BOLD));
    }

    public static void makeMultilineLabel(JTextComponent area) {
        area.setFont(UIManager.getFont("Label.font"));
        area.setEditable(false);
        area.setOpaque(false);
        if (area instanceof JTextArea) {
            ((JTextArea) area).setWrapStyleWord(true);
            ((JTextArea) area).setLineWrap(true);
        }
    }

    public static void htmlize(JComponent component) {
        Font defaultFont = UIManager.getFont("Button.font");

        String stylesheet = "body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: "
                + defaultFont.getName()
                + "; font-size: "
                + defaultFont.getSize()
                + "pt;  }"
                + "a, p, li { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: "
                + defaultFont.getName()
                + "; font-size: "
                + defaultFont.getSize() + "pt;  }";

        try {
            HTMLDocument doc = null;
            if (component instanceof JEditorPane) {
                if (((JEditorPane) component).getDocument() instanceof HTMLDocument) {
                    doc = (HTMLDocument) ((JEditorPane) component).getDocument();
                }
            } else {
                View v = (View) component.getClientProperty(BasicHTML.propertyKey);
                if (v != null && v.getDocument() instanceof HTMLDocument) {
                    doc = (HTMLDocument) v.getDocument();
                }
            }
            if (doc != null) {
                doc.getStyleSheet().loadRules(new StringReader(stylesheet), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
