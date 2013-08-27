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
package ro.nextreports.designer.querybuilder;

import javax.swing.JOptionPane;

import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.queryexec.QueryParameter;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Decebal Suiu
 */
public class ParameterEditDialog extends BaseDialog {

	private QueryParameter parameter;
	private boolean okPressed;

	public ParameterEditDialog(ParameterEditPanel basePanel, String title, boolean modal) {
		super(basePanel, title, modal);
	}

	public ParameterEditDialog(ParameterEditPanel basePanel, String title) {
		super(basePanel, title);
	}

	protected boolean ok() {
		ParameterEditPanel panel = (ParameterEditPanel) basePanel;
		String name = panel.getNameTextField().getText();
		String runtimeName = panel.getRuntimeNameTextField().getText();
		String description = panel.getDescriptionTextArea().getText();
		String type = (String) panel.getTypeComboBox().getSelectedItem();
		String source = panel.getSource();
		String selection = panel.getSelection();
		boolean mandatory = panel.getMandatory();
		boolean manualSource = panel.getManualSource();
        String schema = panel.getSchema();
        boolean isProcedureParameter = panel.getProcedureParameter();
        String previewValue = panel.getPreviewValue();
        ArrayList<Serializable> defaultValues = panel.getDefaultValues();
        String defaultSource = panel.getDefaultSource();
        boolean hidden = panel.getHidden();

        if (name.equals("")) {
			Show.info(I18NSupport.getString("parameter.edit.ask.name"));
			return false;
		}

		if (name.indexOf(" ") != -1) {
			Show.info(I18NSupport.getString("parameter.edit.name.space"));
			return false;
		}

		QueryParameter oldParam = panel.getParameter();
		ParameterManager paramManger = ParameterManager.getInstance();
		if (oldParam == null) {
			if (paramManger.containsParameter(name)) {
				JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.edit.name.exists"));
				return false;
			}
		} else if ((!oldParam.getName().equals(name)) && paramManger.containsParameter(name)) {
			JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.edit.name.exists"));
			return false;
		}
        
        if (panel.getTypeComboBox().getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.notype"));
            return false;
        }

        if (QueryParameter.SINGLE_SELECTION.equals(selection) && (defaultValues.size() > 1) ) {
            JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.default.too.many"));
            return false;
        }

        if (isProcedureParameter && (previewValue == null)) {
            JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.procedure.preview.invalid"));
            return false;
        }
        
        parameter = new QueryParameter(name, description, type);
		parameter.setRuntimeName(runtimeName);
		parameter.setSource(source);
		parameter.setSelection(selection);
		parameter.setMandatory(mandatory);
		parameter.setManualSource(manualSource);
        parameter.setSchema(schema);
        parameter.setProcedureParameter(isProcedureParameter);
        parameter.setPreviewValue(previewValue);        
        parameter.setOrderBy(panel.getOrderBy());
        parameter.setDefaultValues(defaultValues);
        parameter.setDefaultSource(defaultSource);
        parameter.setHidden(hidden);

        if (parameter.isHidden()) {
            if (parameter.isDependent()) {
                JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.hidden.dependent.parent"));
                return false;
            }
            // It is possible to use hidden parameters and parameters which depends on that hidden parameter! (so comment this for now)            
//            if (ParameterManager.getInstance().getChildDependentParameters(parameter).size() > 0) {
//                JOptionPane.showMessageDialog(this, I18NSupport.getString("parameter.hidden.dependent.child"));
//                return false;
//            }
        }

        RuntimeParametersPanel.resetParametersValues();
		okPressed = true;

		return true;
	}

	public boolean okPressed() {
		return okPressed;
	}

	public QueryParameter getParameter() {
		return parameter;
	}

}
