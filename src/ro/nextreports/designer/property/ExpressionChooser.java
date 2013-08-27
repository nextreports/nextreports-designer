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

import ro.nextreports.engine.band.ExpressionBean;

import javax.swing.*;

import ro.nextreports.designer.ExpressionPanel;
import ro.nextreports.designer.ReportLayoutUtil;
import ro.nextreports.designer.SelectExpressionPanel;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import java.awt.*;
import java.util.List;

/**
 * User: mihai.panaitescu
 * Date: 26-Jul-2010
 * Time: 13:51:06
 */
public class ExpressionChooser {

    public static String showDialog(Component parent, String title,
                                                  String expression, final boolean isBoolean, boolean isStaticBand, boolean isFooterBand, String bandName) {

        final ExpressionPanel expressionPanel = new ExpressionPanel(isStaticBand, isFooterBand, bandName, false);
		expressionPanel.setExpression(expression);
        BaseDialog dialog = new BaseDialog(expressionPanel, title, true) {
            protected boolean ok() {
                if (isBoolean && !ReportLayoutUtil.isValidBooleanExpression(expressionPanel.getExpression())) {
                    Show.error(SwingUtilities.getWindowAncestor(expressionPanel), I18NSupport.getString("expression.error.invalid.boolean"));
                    return false;
                }
                return true;
            }
        };
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        if (dialog.okPressed()) {
            return expressionPanel.getExpression();
		} else {
			return null;
		}
	}
    
	public static String showSelectDialog(Component parent, String title,  List<ExpressionBean> list) {

		final SelectExpressionPanel expressionPanel = new SelectExpressionPanel(list);		
		BaseDialog dialog = new BaseDialog(expressionPanel, title, true);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
		if (dialog.okPressed()) {
			return expressionPanel.getExpression();
		} else {
			return null;
		}
	}
}
