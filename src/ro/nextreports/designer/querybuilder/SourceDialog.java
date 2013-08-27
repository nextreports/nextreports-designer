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
package ro.nextreports.designer.querybuilder;

import ro.nextreports.engine.queryexec.QueryParameter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.ui.sqleditor.EditorPanel;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.Show;
import ro.nextreports.designer.util.SwingUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 11, 2008
 * Time: 5:01:17 PM
 */
public class SourceDialog extends JDialog {

    private Dimension dim = new Dimension(400, 200);
    private Dimension buttonDim  = new Dimension(20,20);

    private EditorPanel editorPanel;

    private JButton btnOk = new JButton();
    private JButton btnCancel = new JButton();
    private boolean okPressed = false;
    private List<String> types;
    private byte orderBy;

    public SourceDialog(String select, byte orderBy, boolean showTemplate) throws HeadlessException {
        super(Globals.getMainFrame(), "", true);

        this.orderBy = orderBy;
        this.setTitle(I18NSupport.getString("source.dialog.select"));
        this.setLayout(new GridBagLayout());
        editorPanel = new EditorPanel();
        JScrollPane scr = new JScrollPane(editorPanel);
        scr.setPreferredSize(dim);

        final JComboBox orderByCombo = new JComboBox();
        createOrderByModel(orderByCombo);
        orderByCombo.setSelectedIndex(orderBy);
        orderByCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SourceDialog.this.orderBy = (byte) orderByCombo.getSelectedIndex();
            }
        });


        btnOk.setText(I18NSupport.getString("source.dialog.ok"));
        btnCancel.setText(I18NSupport.getString("source.dialog.cancel"));

        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });

        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.X_AXIS));
        buttonPanel2.add(Box.createHorizontalGlue());
        buttonPanel2.add(btnOk);
        buttonPanel2.add(Box.createRigidArea(new Dimension(5, 5)));
        buttonPanel2.add(btnCancel);
        buttonPanel2.add(Box.createHorizontalGlue());
        SwingUtil.equalizeButtonSizes(buttonPanel2);

        JButton dateButton = new JButton(ImageUtil.getImageIcon("calendar"));
        dateButton.setToolTipText(I18NSupport.getString("source.dialog.date"));
        dateButton.setPreferredSize(buttonDim);
        dateButton.setMinimumSize(buttonDim);
        dateButton.setMaximumSize(buttonDim);
        dateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentDate();
            }
        });

        if (showTemplate) {
            this.add(dateButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 0, 10), 0, 0));
        }
        this.add(scr, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
                , GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 5, 10), 0, 0));
        if (QueryParameter.NO_ORDER != orderBy) {
            this.add(new JLabel(I18NSupport.getString("parameter.orderby")), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 10, 0), 0, 0));
            this.add(orderByCombo, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                    , GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 10, 10), 0, 0));
        }
        this.add(buttonPanel2, new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0
                , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));

        if (select != null) {
            editorPanel.setText(select);
        }

        pack();

    }

    // the order is done by QueryParameter ORDER_BY_SELECT , ORDER_BY_NAME, ORDER_BY_ID values
    private void createOrderByModel(JComboBox orderByCombo) {
        orderByCombo.addItem(I18NSupport.getString("parameter.orderby.none"));
        orderByCombo.addItem(I18NSupport.getString("parameter.orderby.name"));
        orderByCombo.addItem(I18NSupport.getString("parameter.orderby.id"));
    }

    public String getSource() {
        return editorPanel.getText();
    }

    public byte getOrderBy() {
        return orderBy;
    }

    private void ok() {
        if (editorPanel.getText().trim().equals("")) {
            Show.info(I18NSupport.getString("source.dialog.enter.select"));
        } else {
//            if (!testSelect(editorPanel.getText())) {
//                String m = I18NSupport.getString("source.dialog.valid");
//               Show.info(m + " : \"select <exp1> , <exp2> from ...\"");
//            } else {

            try {
                types = ReportLayoutUtil.getAllColumnTypesForReport(editorPanel.getText());
                if (types.size() > 2) {
                    String m = I18NSupport.getString("source.dialog.valid");
                    Show.info(m + " : \"select <exp1> , <exp2> from ...\"");
                } else {
                    okPressed = true;
                    setVisible(false);
                }
            } catch (Exception e) {

                JXErrorPane.showDialog(this, new ErrorInfo(I18NSupport.getString("source.dialog.execute"),
                        I18NSupport.getString("source.dialog.execute"),
                        null, null, e, null, null));
                okPressed = false;
            }
//            }
        }
    }

    private void cancel() {
        okPressed = false;
        setVisible(false);
    }

    private void currentDate() {
        try {
            editorPanel.setText(Globals.getDialect().getCurrentDateSelect());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean okPressed() {
        return okPressed;
    }

    public List<String> getTypes() {
        return types;
    }

    // sql must be of form : "select <a> , <b> from ..."
    // where <a> is the expression for jasper parameter value
    // and <b> is the expression for rendered parameter value
    private boolean testSelect(String sql) {
        try {
            // text between "select " and " from "
            // can throw IndexOutOfBoundsException
            String fields = sql.substring(7, sql.toLowerCase().indexOf(" from ")).trim();

            //must have at least one comma
            //this condition is not enough for a valid sql
            //(may contain the comma inside a function)
//            if (fields.indexOf(',') == -1) {
//                return false;
//            }

        } catch (IndexOutOfBoundsException ex) {
            // sql is not of the form ("select ... from ....")
            return false;
        }

        return true;
    }
}
