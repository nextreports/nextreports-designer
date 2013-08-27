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

import java.util.List;

import javax.swing.JPanel;

import ro.nextreports.designer.util.ImageUtil;


/**
 * The base class used for implementing a panel that is
 * displayed in a Wizard.
 *
 * @author Decebal Suiu
 */
public abstract class WizardPanel extends JPanel {

    /* The context of the wizard process. */
    protected WizardContext context;
    protected BannerPanel banner;

    public WizardPanel() {
    	banner = new BannerPanel();
        banner.setIcon(ImageUtil.getImageIcon("wizard_banner"));
    }

    /**
     * Called when the panel is set.
     */
    public abstract void onDisplay();

    /**
     * Is there be a next panel?
     *
     * @return true if there is a panel to move to next
     */
    public abstract boolean hasNext();

    /**
     * Called to validate the panel before moving to next panel.
     *
     * @param messages a List of messages to be displayed.
     * @return true if the panel is valid,
     */
    public abstract boolean validateNext(List<String> messages);

    /**
     * Get the next panel to go to.
     */
    public abstract WizardPanel getNextPanel();

    /**
     * Can this panel finish the wizard?
     *
     * @return true if this panel can finish the wizard.
     */
    public abstract boolean canFinish();

    /**
     * Called to validate the panel before finishing the wizard. Should
     * return false if canFinish returns false.
     *
     * @param messages a List of messages to be displayed.
     * @return true if it is valid for this wizard to finish.
     */
    public abstract boolean validateFinish(List<String> messages);

    /**
     * Handle finishing the wizard.
     */
    public abstract void onFinish();

    /**
     * Has this panel got help? Defaults to false, override to change.
     *
     * @return false if there is no help for this panel.
     */
    public boolean hasHelp() {
        return false;
    }

    /**
     * Override this method to provide help.
     */
    public void onHelp() {
    }

    /**
     * Get the wizard context.
     *
     * @return a WizardContext object
     */
    public final WizardContext getContext() {
        return context;
    }

    /**
     * Gets the BannerPanel displayed in this dialog. The BannerPanel can be made
     * invisible by calling <code>getBanner().setVisible(false);</code> if it
     * is not needed.
     *
     * @return
     * @see BannerPanel
     */
    public final BannerPanel getBanner() {
        return banner;
    }

    /**
     * Sets the context this wizard should use.
     */
    protected final void setContext(WizardContext context) {
        this.context = context;
    }

    /**
     * If waiting message is not null onNext() is executed on other thread than EDT
     * @return waiting message
     */
    public String getWaitingMessage(){
        return null;
    }

}
