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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import ro.nextreports.designer.ui.EdgeBorder;
import ro.nextreports.designer.ui.EqualsLayout;
import ro.nextreports.designer.ui.MagicButton;
import ro.nextreports.designer.ui.wizard.util.GuiUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;
import ro.nextreports.designer.util.UIActivator;
import ro.nextreports.designer.wizrep.WizardConstants;


/**
 * This class controls a wizard.
 * <p/>
 * Add it to a frame or any other container then call start with your initial
 * wizard panel.
 * <p/>
 * Listeners can also be added to trap when the wizard finishes and when the
 * wizard is cancelled.
 *
 * @author Decebal Suiu
 */
public class Wizard extends JPanel {

    private JButton backButton;
    private JButton nextButton;
    private JButton finishButton;
    private JButton cancelButton;
    private JButton helpButton;

    private Stack<WizardPanelWithBanner> previous;
    private WizardPanelWithBanner current;
    private WizardContext context;
    private List<WizardListener> listeners;

    public Wizard(WizardPanel startPanel) {
        listeners = new ArrayList<WizardListener>();
        initComponents();
        start(startPanel);
    }

    /**
     * Add a listener to this wizard.
     *
     * @param listener a WizardListener object
     */
    public void addWizardListener(WizardListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from this wizard.
     *
     * @param listener a WizardListener object
     */
    public void removeWizardListener(WizardListener listener) {
        listeners.remove(listener);
    }

    public void onBack() {
        WizardPanel panel = previous.pop().getPanel();
        setPanel(panel);
        updateButtons();
    }

    public void onNext() {
        final String waitingMessage = current.getPanel().getWaitingMessage();
        if (waitingMessage == null) {
            next();
        } else {
            Thread executorThread = new Thread(new Runnable() {

                public void run() {
                    JDialog dialog = (JDialog)context.getAttribute(WizardConstants.MAIN_FRAME);
                    UIActivator activator = new UIActivator(dialog, waitingMessage);
                    activator.start();
                    try {
                        next();
                    } finally {
                        if (activator != null) {
                            activator.stop();
                        }
                    }
                }
            }, "NEXT : " + getClass().getSimpleName());
            executorThread.start();
        }
    }


    // executes on EDT or not
    private void next() {
        final List<String> messages = new ArrayList<String>();
        if (current.getPanel().validateNext(messages)) {
            previous.push(current);
            final WizardPanel panel = current.getPanel().getNextPanel();
            if (panel != null) {
                panel.setContext(context);
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setPanel(panel);
                    updateButtons();
                }
            });

        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GuiUtil.showMessages(Wizard.this, messages);
                }
            });

        }
    }

    public void onFinish() {
        List<String> messages = new ArrayList<String>();
        if (current.getPanel().validateFinish(messages)) {
            current.getPanel().onFinish();
            for (WizardListener listener : listeners) {
                listener.wizardFinished(this);
            }
        } else {
            GuiUtil.showMessages(this, messages);
        }
    }

    public void onCancel() {
        for (WizardListener listener : listeners) {
            listener.wizardCancelled(this);
        }
    }

    public void onHelp() {
        current.getPanel().onHelp();
    }

    public WizardContext getContext() {
        return context;
    }

    private void initComponents() {
        createButtons();
        disableButtons();

        setLayout(new BorderLayout(0, 0));

        JPanel navigationButtons = new JPanel();
        navigationButtons.setLayout(new EqualsLayout(5));
        navigationButtons.add(backButton);
        navigationButtons.add(nextButton);
        navigationButtons.add(finishButton);
        navigationButtons.add(cancelButton);

        JPanel helpButtons = new JPanel();
        helpButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        helpButtons.add(helpButton);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BorderLayout(0, 0));
        buttonsPanel.add(navigationButtons, BorderLayout.EAST);
        buttonsPanel.add(helpButtons, BorderLayout.WEST);

        CompoundBorder innerBorder = new CompoundBorder(new EdgeBorder(
                SwingConstants.NORTH), new EmptyBorder(10, 0, 0, 0));
        buttonsPanel.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10),
                innerBorder));

        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void createButtons() {
        backButton = new JButton(new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                onBack();
            }

        });
        backButton.setText(I18NSupport.getString("wizard.panel.back"));
        nextButton = new JButton(new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                onNext();
            }

        });
        nextButton.setText(I18NSupport.getString("wizard.panel.next"));
        finishButton = new JButton(new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                onFinish();
            }

        });
        finishButton.setText(I18NSupport.getString("wizard.panel.finish"));
        cancelButton = new JButton(new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                onCancel();
            }

        });
        cancelButton.setText(I18NSupport.getString("wizard.panel.cancel"));
        helpButton = new MagicButton(new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                onHelp();
            }

        });
        helpButton.setText("");
        helpButton.setIcon(ImageUtil.getImageIcon("wizard_help"));
    }

    private void disableButtons() {
        nextButton.setEnabled(false);
        backButton.setEnabled(false);
        finishButton.setEnabled(false);
        cancelButton.setEnabled(false);
        helpButton.setEnabled(false);
    }

    /**
     * Start this wizard with this panel.
     */
    private void start(WizardPanel wizardPanel) {
        previous = new Stack<WizardPanelWithBanner>();
        context = new WizardContext();
        wizardPanel.setContext(context);
        setPanel(wizardPanel);
        updateButtons();
    }

    private void setPanel(WizardPanel wizardPanel) {
        if (current != null) {
            remove(current);
        }

        wizardPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        current = new WizardPanelWithBanner(wizardPanel);
        if (current == null) {
            current = new WizardPanelWithBanner(new WizardPanelAdapter());
        }
        add(current, BorderLayout.CENTER);

        for (WizardListener listener : listeners) {
            listener.wizardPanelChanged(this);
        }

        setVisible(true);
        revalidate();
        updateUI();
        current.getPanel().onDisplay();
    }

    private void updateButtons() {
        cancelButton.setEnabled(true);
//        helpButton.setEnabled(current.getPanel().hasHelp());
        helpButton.setVisible(current.getPanel().hasHelp());
        backButton.setEnabled(previous.size() > 0);
        nextButton.setEnabled(current.getPanel().hasNext());
        finishButton.setEnabled(current.getPanel().canFinish());
    }

    private class WizardPanelWithBanner extends JPanel {

        private WizardPanel panel;

        public WizardPanelWithBanner(WizardPanel panel) {
            super();
            this.panel = panel;
            initComponents();
        }

        public WizardPanel getPanel() {
            return panel;
        }

        private void initComponents() {
            setLayout(new BorderLayout());
            if (panel.getBanner().isVisible()) {
                add(panel.getBanner(), BorderLayout.NORTH);
            }
            add(panel, BorderLayout.CENTER);
        }

    }

}
