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
package ro.nextreports.designer;

import ro.nextreports.engine.band.HyperlinkBandElement;
import ro.nextreports.engine.ReportLayout;

import javax.swing.*;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.property.HyperlinkPanel;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;

import java.util.EventObject;

/**
 * User: mihai.panaitescu
 * Date: 01-Mar-2010
 * Time: 13:56:21
 */
public class HyperlinkCellEditor extends DefaultGridCellEditor {

    private HyperlinkPanel panel;
    private BaseDialog dialog;
    private HyperlinkBandElement bandElement;

    public HyperlinkCellEditor() {
        super(new JTextField()); // not really relevant - sets a text field as the editing default.
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        boolean isEditable = super.isCellEditable(event);
        if (isEditable) {
            editorComponent = new JLabel("...", JLabel.HORIZONTAL);
            delegate = new HyperlinkDelegate();
        }

        return isEditable;
    }

    class HyperlinkDelegate extends EditorDelegate {

        HyperlinkDelegate() {
            panel = new HyperlinkPanel();
            dialog = new BaseDialog(panel, I18NSupport.getString("url.dialog.title"), true);
            dialog.pack();
            dialog.setLocationRelativeTo(Globals.getMainFrame());
        }

        public void setValue(Object value) {
            bandElement = (HyperlinkBandElement) value;
            panel.setHyperLink(bandElement.getHyperlink());
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dialog.setVisible(true);
                    if (dialog.okPressed()) {
                        stopCellEditing();
                    } else {
                        cancelCellEditing();
                        //delete $F{(?,?)} (when close function panel)
                        if ("?".equals(bandElement.getHyperlink().getText()) ||
                            "?".equals(bandElement.getHyperlink().getUrl())) {
                            new ClearCellAction().actionPerformed(null);
                        }

                    }
                }

            });
        }

        public Object getCellEditorValue() {
            ReportLayout oldLayout = getOldLayout();
            bandElement.setHyperlink(panel.getHyperLink());
            registerUndoRedo(oldLayout, I18NSupport.getString("url.dialog.edit.title"), I18NSupport.getString("url.dialog.title"));
            return bandElement;
        }

    }
}
