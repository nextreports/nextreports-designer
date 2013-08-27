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
package ro.nextreports.designer.ui.sqleditor;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ro.nextreports.designer.ui.JLine;
import ro.nextreports.designer.util.I18NSupport;


/**
 * A generic status bar containing a status message.<p>
 *
 * @author Decebal Suiu
 */

class StatusBar extends JPanel {

	public static final String DEFAULT_STATUS_MESSAGE = "";

	private GridBagLayout gridBag;
    private JLabel statusMessage;
    private JLabel rowAndColumnIndicator;
    private JLabel overwriteModeIndicator;
    private JLabel capsLockIndicator;

    private int row;
    private int column;
    private String message;

    private JPanel messagePanel;
    private JPanel overwritePanel;
    private JPanel capsLockPanel;
    private JPanel rowAndColumnPanel;

	/**
	 * Creates the status bar with a default status message and a size grip.
	 */
	public StatusBar() {
		this(DEFAULT_STATUS_MESSAGE, true, 1, 1, true);
	}

    /**
     * Creates the status bar.
     *
     * @param message The default status message for this status bar.
     * @param showRowColumn If true, the row/column of the caret are displayed.
     * @param row The initial value of the row that is displayed.
     * @param column The initial value fo the column that is displayed.
     * @param overwriteModeEnabled If <code>true</code>, overwrite mode
     *        indicator ("OVR") is enabled.
     */
    public StatusBar(String message, boolean showRowColumn, int row, int column, boolean overwriteModeEnabled) {

        super();

        // initialize private variables.
        this.message = message;
        this.row = row;
        this.column = column; // DON'T call setRowAndColumn() yet!

        // make the layout such that different items can be different sizes.
        gridBag = new GridBagLayout();
        setLayout(gridBag);
        GridBagConstraints c = new GridBagConstraints();

        // create and add a panel containing the "status message."
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.insets = new Insets(3, 3, 3, 3);

        messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusMessage = new JLabel(this.message, SwingConstants.LEFT);
        messagePanel.add(statusMessage);

        add(messagePanel, c);

        // add separator
        c.gridx += 1;
        c.weightx = 0.0;
        add(new JLine(), c);

        // create a Caps lock indicator.
        c.gridx += 1;
        capsLockIndicator = createLabel("sqleditor.statusbar.capsLockIndicator");

        // on Mac OS X at least, the OS doesn't support getLockingKeyState().
        try {
            capsLockIndicator.setEnabled(Toolkit.getDefaultToolkit().
                    getLockingKeyState(KeyEvent.VK_CAPS_LOCK));

            capsLockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            capsLockPanel.add(capsLockIndicator);
            add(capsLockPanel, c);

            // add separator
            c.gridx += 1;
            add(new JLine(), c);

        } catch (UnsupportedOperationException e) {
            // nothing to do
        }

        // create and add a panel containing the overwrite/insert message.
        c.gridx += 1;
        overwritePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        overwriteModeIndicator = createLabel("sqleditor.statusbar.overwriteModeIndicator");
        overwritePanel.add(overwriteModeIndicator);

        add(overwritePanel, c);

        // add separator
        c.gridx += 1;
        add(new JLine(), c);

        // create and add a panel containing the row and column.
        c.gridx += 1;
        rowAndColumnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rowAndColumnIndicator = new JLabel();
        updateRowColumnDisplay();
        rowAndColumnPanel.add(rowAndColumnIndicator);

        add(rowAndColumnPanel, c);
    }

	/**
	 * Returns the message in the status bar.
	 *
	 * @return The message in the status bar.
	 * @see #setStatusMessage
	 */
	public String getStatusMessage() {
		return statusMessage.getText();
	}

	/**
	 * Setter function for message in the status bar.
	 *
	 * @param message The new message to display.
	 * @see #getStatusMessage
	 */
	public void setStatusMessage(String message) {
		statusMessage.setText(message);
		statusMessage.paintImmediately(statusMessage.getBounds());
	}

    private static JLabel createLabel(String key) {
        JLabel label = new JLabel(I18NSupport.getString(key));
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    /**
     * Decrements the value of column in row/column indicator.
     */
    public void decrementColumn() {
        setRowAndColumn(row, Math.max(column - 1, 0));
    }

    /**
     * Decrements the value of row in row/column indicator.
     */
    public void decrementRow() {
        setRowAndColumn(Math.max(row - 1, 0), column);
    }

    /**
     * Increments the value of column in row/column indicator.
     */
    public void incremetColumn() {
        setRowAndColumn(row, column + 1);
    }

    /**
     * Increments the value of row in row/column indicator.
     */
    public void incrementRow() {
        setRowAndColumn(row + 1, column);
    }

    /**
     * Setter function for the column in row/column indicator.
     *
     * @param column The column value to display for the caret.
     */
    public void setColumn(int column) {
        setRowAndColumn(row, column);
    }

    /**
     * Setter function for the row in row/column indicator.
     *
     * @param row The row value to display for the caret.
     */
    public void setRow(int row) {
        setRowAndColumn(row, column);
    }

    /**
     * Setter function for row/column part of status bar.
     *
     * @param row The row value to display for the caret.
     * @param column The column value to display for the caret.
     */
    public void setRowAndColumn(int row, int column) {
        this.row = row;
        this.column = column;
        updateRowColumnDisplay();
    }

    /**
     * Changes whether the caps lock indicator is enabled or disabled.  This
     * should be called whenever the user presses CAPS LOCK, perhaps through a
     * <code>KeyListener</code>.
     *
     * @param enabled If <code>true</code>, the caps indicator ("OVR") is
     *                enabled; if <code>false</code>, it is disabled.
     */
    public void setCapsLockIndicatorEnabled(boolean enabled) {
        capsLockIndicator.setEnabled(enabled);
    }

    /**
     * Changes whether the overwrite indicator is enabled or disabled.
     *
     * @param enabled If <code>true</code>, the overwrite indicator ("OVR") is
     *                enabled; if <code>false</code>, it is disabled.
     */
    public void setOverwriteModeIndicatorEnabled(boolean enabled) {
        overwriteModeIndicator.setEnabled(enabled);
    }

    /**
     * Updates the row/column indicator to reflect the current caret
     * location, if it is enabled.
     */
    private void updateRowColumnDisplay() {
        rowAndColumnIndicator.setText(row + ":" + column);
    }

}

