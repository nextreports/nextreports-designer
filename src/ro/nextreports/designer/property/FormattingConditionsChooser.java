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
package ro.nextreports.designer.property;

import ro.nextreports.engine.condition.FormattingConditions;

import java.awt.*;

import ro.nextreports.designer.ui.BaseDialog;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 12:10:39
 */
public class FormattingConditionsChooser {

    public static FormattingConditions showDialog(Component parent, String title,
                                                  FormattingConditions initial, String type) {

        FormattingConditionsPanel condPanel = new FormattingConditionsPanel(type);
		condPanel.setRenderConditions(initial);
        BaseDialog dialog = new BaseDialog(condPanel, title, true);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        if (dialog.okPressed()) {
            return condPanel.getFinalRenderConditions();
		} else {
			return null;
		}
	}
}
