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

import ro.nextreports.engine.condition.BandElementCondition;
import ro.nextreports.engine.condition.ConditionalExpression;
import ro.nextreports.engine.condition.ConditionalOperator;

import java.io.Serializable;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 13:57:34
 */
public class BandElementConditionEditDialog extends BaseDialog {

	private BandElementCondition condition;
	private boolean okPressed;

	public BandElementConditionEditDialog(BandElementConditionEditPanel basePanel, String title, boolean modal) {
		super(basePanel, title, modal);
	}

	protected boolean ok() {
		BandElementConditionEditPanel panel = (BandElementConditionEditPanel) basePanel;
		String operator = panel.getOperator();
		Serializable value = panel.getValue();
        Serializable value2 = panel.getValue2();
        int property = panel.getProperty();
        Serializable propertyValue = panel.getPropertyValue();

        if (value == null) {
            Show.info(this, I18NSupport.getString("condition.empty.value", panel.getType()));
			return false;
        }

        if (propertyValue == null) {
            Show.info(this, I18NSupport.getString("condition.empty.property.value"));
			return false;
        }

        if (ConditionalOperator.BETWEEN.equals(operator) && (value2 == null)) {
            Show.info(this, I18NSupport.getString("condition.empty.value2", panel.getType()));
			return false;
        }

        BandElementCondition oldParam = panel.getCondition();

        ConditionalExpression exp = new ConditionalExpression(operator);
        exp.setRightOperand(value);
        exp.setRightOperand2(value2);
        condition = new BandElementCondition(exp, property, propertyValue);

		okPressed = true;

		return true;
	}

	public boolean okPressed() {
		return okPressed;
	}

	public BandElementCondition getCondition() {
		return condition;
	}

}
