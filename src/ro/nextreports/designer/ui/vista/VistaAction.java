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
package ro.nextreports.designer.ui.vista;


import javax.swing.*;

import ro.nextreports.designer.util.UIActivator;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Feb 20, 2009
 * Time: 10:24:17 AM
 */
/**
 * VistaAction performs the job outside EDT and can have an UI activator for disabling the
 * VistaDialog
 */
public abstract class VistaAction extends AbstractAction {

    private UIActivator activator;
    private JDialog dialog;

    /**
     * Action performed method
     * It is done on a thread outside EDT
     * @param ev action event
     */
    public void actionPerformed(ActionEvent ev) {
        Runnable runnable = new Runnable() {
            public void run() {
                perform();
                if (activator != null) {
                    activator.stop();
                }
                if (dialog != null) {
                    dialog.dispose();
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     * Set UI activator
     * @param activator UI acivator
     */
    public void setActivator(UIActivator activator) {
        this.activator = activator;
    }

    /**
     * Set Vista dialog (it is needed for dispose)
     * @param dialog vista dialog
     */
    protected void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * Perform task
     * executed outside EDT
     */
    public abstract void perform();

}
