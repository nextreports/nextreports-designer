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

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.BevelBorder;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Sep 1, 2006
 * Time: 5:31:15 PM
 */
/**
 * UIActivator is an utility class used for showing long task execution.
 * It's purpose is to block all possible user interactions with the parent
 * frame, parent dialog or a parent rootPane and to show a progress bar.
 * If an action is passed in the "start" method a stop button is shown, which
 * allows to regain user interactions on the parent frame.
 * The action is responsible to 'really' stop the task execution!
 * <p/>
 * <p>
 * Usage :
 * UIActivator activator = new UIActivator(frame, "Wait ...", tasks);
 * Thread t = new Thread(new TestRunnable(activator));
 * activator.start(new StopAction(t));
 * t.start();
 * <p/>
 * Inside TestRunnable (updateProgress and stop activator methods must be called from the EDT) :
 * <p/>
 * try {
 * ....
 * for (int i = 0; i < activator.getTasks(); i++) {
 * // the long task must be interruptible
 * try {
 * // long task ....
 * } catch (InterruptedException e) {
 * activator.stop();
 * return;
 * }
 * final String message = (i + 1) + " files ...";
 * SwingUtilities.invokeLater(new Runnable() {
 * public void run() {
 * activator.updateProgress(message);
 * }
 * });
 * }
 * ....
 * } finally {
 * SwingUtilities.invokeLater(new Runnable() {
 * public void run() {
 * activator.stop();
 * }
 * });
 * }
 * </p>
 */
public class UIActivator {

    private Color PB_FOREGROUND = new Color(0, 130, 130);

    private WaitGlassPane glass;
    private String message;
    private JPanel panel;
    private JFrame parentFrame;
    private JDialog parentDialog;
    private JRootPane parentRootPane;
    private boolean indeterminate;
    private JLabel textLabel;
    private JProgressBar progressBar;
    private boolean disableProgressBar;
    private int tasks;

    /**
     * Constructor. Creates an UIActivator with an indeterminate progress bar
     * The glass panel is set on the specified frame.
     *
     * @param parentFrame parent frame
     * @param message     message that appears in a label before the progress bar
     */
    public UIActivator(JFrame parentFrame, String message) {
        this.parentFrame = parentFrame;
        this.message = message;
        this.indeterminate = true;
        this.tasks = 0; // does not matter
    }

    /**
     * Constructor. Creates an UIActivator with a determinate progress bar
     * The glass panel is set on the specified frame.
     *
     * @param parentFrame parent frame
     * @param message     message that appears in a label before the progress bar
     * @param tasks       number of tasks which represents the maximum value for progress bar
     */
    public UIActivator(JFrame parentFrame, String message, int tasks) {
        this.parentFrame = parentFrame;
        this.message = message;
        this.indeterminate = false;
        this.tasks = tasks;

        // cell spacing and length are used only if progress bar has setStringPainted(false)!!!
        //UIDefaults defaults = UIManager.getDefaults();
        //defaults.put("ProgressBar.cellSpacing", 2);
        //defaults.put("ProgressBar.cellLength", 3);
    }

    /**
     * Constructor. Creates an UIActivator with an indeterminate progress bar
     * The glass panel is set on the specified dialog.
     *
     * @param parentDialog parent dialog
     * @param message      message that appears in a label before the progress bar
     */
    public UIActivator(JDialog parentDialog, String message) {
        this.parentDialog = parentDialog;
        this.message = message;
        this.indeterminate = true;
        this.tasks = 0; // does not matter
    }

    /**
     * Constructor. Creates an UIActivator with a determinate progress bar
     * The glass panel is set on the specified dialog.
     *
     * @param parentDialog parent dialog
     * @param message      message that appears in a label before the progress bar
     * @param tasks        number of tasks which represents the maximum value for progress bar
     */
    public UIActivator(JDialog parentDialog, String message, int tasks) {
        this.parentDialog = parentDialog;
        this.message = message;
        this.indeterminate = false;
        this.tasks = tasks;

        // cell spacing and length are used only if progress bar has setStringPainted(false)!!!
        //UIDefaults defaults = UIManager.getDefaults();
        //defaults.put("ProgressBar.cellSpacing", 2);
        //defaults.put("ProgressBar.cellLength", 3);
    }

    /**
     * Constructor. Creates an UIActivator with an indeterminate progress bar
     * The glass panel is set on the specified rootPane.
     * This constructor is useful when we want to block only a component from a window.
     * <p/>
     * Instead of adding the component directly to the layout, we will add it to a rootPane and
     * the rootPane is added to the layout :
     * JRootPane rootPane = new JRootPane();
     * rootPane.setContentPane(panel);
     * layout.add(rootPane);
     *
     * @param parentRootPane parent rootPane
     * @param message        message that appears in a label before the progress bar
     */
    public UIActivator(JRootPane parentRootPane, String message) {
        this.parentRootPane = parentRootPane;
        this.message = message;
        this.indeterminate = true;
        this.tasks = 0; // does not matter
    }

    /**
     * Constructor. Creates an UIActivator with a determinate progress bar
     * The glass panel is set on the specified rootPane.
     * This constructor is useful when we want to block only a component from a window.
     * <p/>
     * Instead of adding the component directly to the layout, we will add it to a rootPane and
     * the rootPane is added to the layout :
     * JRootPane rootPane = new JRootPane();
     * rootPane.setContentPane(panel);
     * layout.add(rootPane);
     *
     * @param parentRootPane parent rootPane
     * @param message        message that appears in a label before the progress bar
     * @param tasks          number of tasks which represents the maximum value for progress bar
     */
    public UIActivator(JRootPane parentRootPane, String message, int tasks) {
        this.parentRootPane = parentRootPane;
        this.message = message;
        this.indeterminate = false;
        this.tasks = tasks;

        // cell spacing and length are used only if progress bar has setStringPainted(false)!!!
        //UIDefaults defaults = UIManager.getDefaults();
        //defaults.put("ProgressBar.cellSpacing", 2);
        //defaults.put("ProgressBar.cellLength", 3);
    }

    /**
     * Start UIActivator
     * no stop button is shown
     */
    public void start() {
        start(null);
    }


    /**
     * Start UIActivator
     *
     * @param action action used to stop long task
     *               if action is null no stop button is shown, otherwise a stop button is shown
     *               and the action is called when button is clicked
     */
    public void start(AbstractAction action) {

        if (parentFrame != null) {
            parentFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        } else if (parentDialog != null) {
            parentDialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        } else if (parentRootPane != null) {
            parentRootPane.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        } else {
            return;
        }

        glass = new WaitGlassPane();
        //glass.setText(message);
        glass.setLayout(new GridBagLayout());

        // Panel shown over glass panel

        String text = decorateText(message);
        textLabel = new JLabel(text);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(indeterminate);
        //progressBar.setForeground(PB_FOREGROUND);
        //progressBar.setUI(new GradientProgressBarUI());
        //progressBar.setBorderPainted(false);
        //progressBar.setOpaque(false);
        progressBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        if (!indeterminate) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(tasks);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
        }

        panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(192, 192, 192, 150));
        panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        panel.add(textLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 10, 0));
        if (!disableProgressBar) {
            panel.add(progressBar, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 5, 5), 0, 0));
        }

        if (action != null) {
            JButton stopButton = new JButton(action);
            stopButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createBevelBorder(BevelBorder.LOWERED),
                            BorderFactory.createBevelBorder(BevelBorder.RAISED))));
            stopButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panel.add(stopButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 5, 5, 5), 0, 0));
        }


        glass.add(new JLabel(""), new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(10, 10, 10, 10), 0, 0));
        if (!disableProgressBar || (action != null)) {
            glass.add(panel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(10, 10, 40, 10), 0, 0));
        }

        if (parentFrame != null) {
            parentFrame.setGlassPane(glass);
        } else if (parentDialog != null) {
            parentDialog.setGlassPane(glass);
        } else {
            parentRootPane.setGlassPane(glass);
        }
        glass.start();
    }

    /**
     * Update text
     *
     * @param message text
     */
    public void updateText(String message) {
        if ((message != null) && (!message.trim().equals(""))) {
            textLabel.setText(decorateText(message));
        }
    }


    /**
     * Update progress bar
     *
     * @param message progress bar message
     *                if message is null percentage is shown, otherwise the message is shown
     */
    public void updateProgress(String message) {
        if ((message != null) && (!message.trim().equals(""))) {
            progressBar.setString(message);
        }
        progressBar.setValue(progressBar.getValue() + 1);
    }

    /**
     * Update progress bar
     *
     * @param value   progress bar value
     * @param message progress bar message
     *                if message is null percentage is shown, otherwise the message is shown
     */
    public void updateProgress(int value, String message) {
        if ((message != null) && (!message.trim().equals(""))) {
            progressBar.setString(message);
        }
        progressBar.setValue(value);
    }

    /**
     * Update progress bar, percentage is shown over the progress bar
     */
    public void updateProgress() {
        updateProgress(null);
    }

    /**
     * Update progress bar
     *
     * @param value progress bar value
     */
    public void updateProgress(int value) {
        updateProgress(value, null);
    }


    /**
     * Stop UIActivator
     * User regains the input interactions over the parent frame
     */
    public void stop() {

        // a small delay after the job is done (just for visualization effects :
        // see full percent for a small amount of time)
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // notihing to do
        }

        if (parentFrame != null) {
            parentFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else if (parentDialog != null) {
            parentDialog.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else if (parentRootPane != null) {
            parentRootPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        if (glass != null) {
            glass.stop();
            // components added to glass panel must be removed
            // otherwise they will appear at every repaint !
            if (panel != null) {
                glass.remove(panel);
            }
            panel = null;
            glass = null;
        }
    }

    /**
     * Get the number of tasks
     *
     * @return number of tasks
     */
    public int getTasks() {
        return tasks;
    }

    /**
     * Disable progress bar
     */
    public void disableProgressBar() {
        this.disableProgressBar = true;
    }

    private String decorateText(String text) {
        return "<html><b><font size=\"3\">" + text + "</font></b></html>";
    }
}
