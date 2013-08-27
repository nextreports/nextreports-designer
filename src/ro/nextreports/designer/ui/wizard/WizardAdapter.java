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

/**
 * This class provides means of abreviating work when using the
 * WizardListener allowing the developer to implement only the
 * needed methods.
 *
 * @author Decebal Suiu
 */
public class WizardAdapter implements WizardListener {

    public WizardAdapter() {
    }

    /**
     * Called when the wizard is cancelled.
     *
     * @param wizard the wizard that was cancelled.
     */
    public void wizardCancelled(Wizard wizard) {
    }

    /**
     * Called when the wizard finishes.
     *
     * @param wizard the wizard that finished.
     */
    public void wizardFinished(Wizard wizard) {
    }

    /**
     * Called when a new panel has been displayed in the wizard.
     *
     * @param wizard the wizard that was updated
     */
    public void wizardPanelChanged(Wizard wizard) {
    }

}
