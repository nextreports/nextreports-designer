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

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ReporterPreferencesManager;


/**
 * @author Decebal Suiu
 * @author Mihai Dinca-Panaitescu
 */
public class BaseDialog extends JDialog {

    private final String OK = I18NSupport.getString("base.dialog.ok");
    private final char OK_MNEMONIC = 'O';
    private final String CLOSE = I18NSupport.getString("base.dialog.close");
    private final char CLOSE_MNEMONIC = 'C';

    protected JPanel basePanel;
    protected JPanel buttonsPanel;
    protected Action okAction;
    protected Action closeAction;

    private boolean okPressed = false;

    // need an instance of close button for "enter" key pressed action
    private JButton closeButton;

    public BaseDialog(JPanel basePanel, String title) {
        this(basePanel, title, true);
    }

    public BaseDialog(JPanel basePanel, String title, boolean modal) {
        super(Globals.getMainFrame(), title, modal);
        this.basePanel = basePanel;

        // create base criteria
        okAction = new OkButtonAction();
        closeAction = new CloseButtonAction();

        // "enter" pressed
        // if the close button has focus, on enter pressed we will call close()
        // otherwise (no matter where the focus is) we will call ok();
        ActionMap am = getRootPane().getActionMap();
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Object windowEnterKey = new Object();
        KeyStroke windowEnterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        Action windowEnterAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (closeButton.hasFocus()) {
                    closeNow();
                } else {
                    okNow();
                }
            }
        };
        im.put(windowEnterStroke, windowEnterKey);
        am.put(windowEnterKey, windowEnterAction);

        // "escape" pressed : close()
        Object windowCloseKey = new Object();
        KeyStroke windowCloseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Action windowCloseAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                closeNow();
            }
        };
        im.put(windowCloseStroke, windowCloseKey);
        am.put(windowCloseKey, windowCloseAction);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();

        // create the layout
        double[] columns = {
                TableLayoutConstants.FILL,
        };
        double[] rows = {
                TableLayoutConstants.FILL,
                TableLayoutConstants.PREFERRED
        };
        TableLayout layout = new TableLayout(columns, rows);
        layout.setVGap(10);
        mainPanel.setLayout(layout);

        createButtonsPanel();

        mainPanel.add(basePanel, "0, 0");
        mainPanel.add(buttonsPanel, "0, 1");

        mainPanel.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new EmptyBorder(10, 10, 10, 10)));
        getContentPane().add(mainPanel);
        
        pack();      
    }
    
    public void setVisible(boolean visible) {
        pack();
        setLocationRelativeTo(this.getParent());
        // if dialog is not resizable, it means a fixed dimension was set for it
        if ((basePanel != null) && isResizable()) {
            loadPreferences();
        }
        super.setVisible(visible);
    }


    protected void createButtonsPanel() {
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new EqualsLayout(5));
        Action[] actions = getButtonActions();
        if (actions == null) {
            return;
        }

        for (Action action : actions) {
            JButton button = new JButton(action);
            buttonsPanel.add(button);
            if (action.getValue(Action.NAME).equals(CLOSE)) {
               closeButton = button;
            }
        }

        // set border
        CompoundBorder innerBorder = new CompoundBorder(new EdgeBorder(
                SwingConstants.NORTH), new EmptyBorder(10, 0, 0, 0));
        buttonsPanel.setBorder(new CompoundBorder(new EmptyBorder(
                0, 0, 0, 0), innerBorder));
    }

    protected Action[] getButtonActions() {
        Action[] baseActions = new Action[2];
        baseActions[0] = okAction;
        baseActions[1] = closeAction;
        return baseActions;
    }

    class OkButtonAction extends AbstractAction {

        public OkButtonAction() {
            putValue(Action.NAME, OK);
            putValue(Action.MNEMONIC_KEY, new Integer(OK_MNEMONIC));
        }

        public void actionPerformed(ActionEvent e) {
            okNow();
        }

    }

    class CloseButtonAction extends AbstractAction {

        public CloseButtonAction() {
            putValue(Action.NAME, CLOSE);
            putValue(Action.MNEMONIC_KEY, new Integer(CLOSE_MNEMONIC));
        }

        public void actionPerformed(ActionEvent e) {
            closeNow();
        }

    }

    protected boolean ok() {
        return true;
    }

    protected boolean close() {
        return true;
    }


    private void closeNow() {
        if (close()) {
        	savePreferences();
            okPressed = false;
            setVisible(false);
            dispose();
        }
    }

    private void okNow() {
        if (ok()) {
        	savePreferences();
            okPressed = true;
            setVisible(false);
            dispose();
        }
    }

    public boolean okPressed() {
        return okPressed;
    }
    
    private void savePreferences() {
        if (basePanel == null) {
            return;
        }                
        Class cls = basePanel.getClass();
        ReporterPreferencesManager.getInstance().storeBoundsForWindow(cls, getBounds());
    }
    
    private void loadPreferences() {    	
        Class cls = basePanel.getClass();
        Rectangle bounds = ReporterPreferencesManager.getInstance().loadBoundsForWindow(cls);        
        if (bounds != null) {
            setBounds(bounds);
            setPreferredSize(bounds.getSize());
        }
    }
    
    public void setOkText(String text) {
    	okAction.putValue(Action.NAME, text);
    }
    
    public void setCloseText(String text) {
    	closeAction.putValue(Action.NAME, text);
    }


}
