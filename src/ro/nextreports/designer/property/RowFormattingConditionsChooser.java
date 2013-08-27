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

import java.awt.Component;
import java.util.List;

import javax.swing.SwingUtilities;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.condition.FormattingConditions;
import ro.nextreports.engine.condition.RowFormattingConditions;

public class RowFormattingConditionsChooser {

	public static RowFormattingConditions showDialog(Component parent, String title, RowFormattingConditions initial,  List<Integer> rows) {

		final RowFormattingConditionsPanel condPanel = new RowFormattingConditionsPanel(initial, rows);		
		BaseDialog dialog = new BaseDialog(condPanel, title, true) {
			protected boolean ok() {
				if ("".equals(condPanel.getExpressionText().trim())) {
					Show.error(SwingUtilities.getWindowAncestor(condPanel), I18NSupport.getString("expression.error.enter.expression"));
                    return false;
				}
				FormattingConditions fc = condPanel.getCondPanel().getFinalRenderConditions();
				if ( (fc == null) || (fc.getConditions() == null) || (fc.getConditions().size() == 0)) {
					Show.error(SwingUtilities.getWindowAncestor(condPanel), I18NSupport.getString("expression.error.enter.condition"));
                    return false;
				}
				return true;
			}
		};
		condPanel.setParent(dialog);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		if (dialog.okPressed()) {
			return condPanel.getConditions();
		} else {
			return null;
		}
	}
}
