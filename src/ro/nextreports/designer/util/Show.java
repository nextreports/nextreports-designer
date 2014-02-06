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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Level;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.*;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import ro.nextreports.designer.Globals;

/**
 * @author Decebal Suiu
 */
public class Show {

    private static JDialog lastInfoDialog;
    private static volatile boolean disposed = false;

    public static void error(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(Globals.getMainFrame(), message,
                        I18NSupport.getString("error"), JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void error(final Exception e) {
        Show.error(Globals.getMainFrame(), "An error occured. See details for more information.", e);
    }

    public static void error(final String message, final Exception e) {
        Show.error(Globals.getMainFrame(), message, e);
    }

    public static void error(final Component parent, final String message) {
        Show.error(parent, message, null);
    }

    public static void error(final Component parent, final String message, final Exception e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JXErrorPane.showDialog(parent, new ErrorInfo(null, message,
                                null, null, e, Level.SEVERE, null));
            }
        });
    }

    public static void info(final String message) {
        Show.info(Globals.getMainFrame(), message);
    }

    public static void info(final Component parent, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parent, message);
            }
        });
    }
    
    public static void warning(final String message) {
        Show.warning(Globals.getMainFrame(), message);
    }

    public static void warning(final Component parent, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(parent, message, "", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
    
    public static void warningScroll(String title, String message, int rows, int cols) {
        Show.warningScroll(Globals.getMainFrame(), title, message, rows, cols);
    }
    
    public static void warningScroll(String title, String message, int rows, int cols, List<Action> actions) {
        Show.warningScroll(Globals.getMainFrame(), title, message, rows, cols, actions);
    }
    
    public static void warningScroll(final Component parent, final String title, final String message, final int rows, final int cols) {
    	warningScroll(parent, title, message, rows, cols, null);
    }

    public static void warningScroll(final Component parent, final String title, final String message, final int rows, final int cols, final List<Action> actions) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {                
                JTextArea mytext = new JTextArea();
                mytext.setText(message);
                mytext.setRows(rows);
                mytext.setColumns(cols);
                mytext.setEditable(false);
                mytext.setLineWrap(true);
                JScrollPane mypane = new JScrollPane(mytext);

                Object[] objarr = { mypane };
                JOptionPane optpane;
                JButton[] buttons = null;
                if (actions.size() == 0) {
                	optpane = new JOptionPane(objarr, JOptionPane.WARNING_MESSAGE);
                } else {
                	buttons = new JButton[actions.size()+1];                	
                	for (int i=0, size=actions.size(); i<size; i++) {
                		buttons[i] = new JButton(actions.get(i));
                	}
                	buttons[actions.size()] = new JButton(I18NSupport.getString("base.dialog.close"));
                	optpane = new JOptionPane(objarr, JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, buttons, buttons[actions.size()] );
                }
                final JDialog dialog = optpane.createDialog(parent, title);
                if (buttons != null) {
                	buttons[actions.size()].addActionListener(new ActionListener() {						
						@Override
						public void actionPerformed(ActionEvent e) {						
							dialog.setVisible(false);
						}
					});
                }
                dialog.setResizable(true);
                dialog.setVisible(true);
            }
        });
    }

    public static void disposableInfo(final String message) {
        disposableInfo(Globals.getMainFrame(), message);
    }

    public static void disposableInfo(final Component parent, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
                lastInfoDialog = new JDialog((JFrame) parent,
                        UIManager.getString("OptionPane.messageDialogTitle"), true);
                lastInfoDialog.setContentPane(pane);
                lastInfoDialog.pack();
                centrateComponent(parent, lastInfoDialog);
                pane.addPropertyChangeListener(
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent e) {
                                String prop = e.getPropertyName();
                                if (lastInfoDialog.isVisible()
                                        && (e.getSource() == pane)
                                        && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
                                        prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                                    //If you were going to check something
                                    //before closing the window, you'd do it here.
                                    System.out.println("**** dispose listener");
                                    lastInfoDialog.dispose();
                                }
                            }
                        });
                // disposableInfo & dispose methods are called from different threads
                // their order is undetermined : so we must test if dispose() was not already
                // called when showing option panel
                if (!disposed) {
                    lastInfoDialog.setVisible(true);
                } else {
                    disposed = false;
                }

            }
        });
    }

    public static void dispose() {
        if (lastInfoDialog != null) {
            disposed = true;
            lastInfoDialog.dispose();
        }
    }

    public static void centrateComponent(Component c) {
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dimComp = c.getSize();
        c.setLocation((dimScreen.width - dimComp.width) / 2,
                (dimScreen.height - dimComp.height) / 2);
    }

    public static void centrateComponent(Component parent, Component c) {
        if (parent == null) {
            centrateComponent(c);
        } else {
            Dimension dimParent = parent.getSize();
            Point p = parent.getLocation();
            Dimension dimComp = c.getSize();
            c.setLocation(p.x + ((dimParent.width - dimComp.width) / 2),
                    p.y + ((dimParent.height - dimComp.height) / 2));
        }
    }

    public static void pack
            (Window
                    window) {
        Dimension dim = window.getPreferredSize();
        int prefw = dim.width;
        int w = window.getWidth();
        if (w < prefw) {
            w = prefw;
        }
        int prefh = dim.height;
        int h = window.getHeight();
        if (h < prefh) {
            h = prefh;
        }
        window.setSize(w, h);
    }

}
