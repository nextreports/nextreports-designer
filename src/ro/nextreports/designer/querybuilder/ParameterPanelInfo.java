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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ro.nextreports.designer.util.I18NSupport;

import ro.nextreports.engine.queryexec.QueryParameter;

public class ParameterPanelInfo extends JPanel {
	
	public ParameterPanelInfo(QueryParameter qp) {
		setLayout(new GridBagLayout());
        
        add(new JLabel(bold(I18NSupport.getString("parameter.name"))), 
        		new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));       
        add(new JLabel(qp.getName()), 
        		new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 0, 0), 0, 0));        
        add(new JLabel(bold(I18NSupport.getString("parameter.source"))), 
        		new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(new JLabel(multiline(qp.getSource(), 50)), 
        		new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));
        add(new JLabel(bold(I18NSupport.getString("parameter.type"))), 
        		new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(new JLabel(qp.getValueClassName()), 
        		new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));
        add(new JLabel(bold(I18NSupport.getString("parameter.selection"))), 
        		new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(new JLabel(qp.getSelection()), 
        		new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));
        add(new JLabel(bold(I18NSupport.getString("parameter.mandatory"))), 
        		new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(new JLabel(String.valueOf(qp.isMandatory())), 
        		new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));
        add(new JLabel(bold(I18NSupport.getString("parameter.hidden"))), 
        		new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(new JLabel(String.valueOf(qp.isHidden())), 
        		new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));
        add(new JLabel(bold(I18NSupport.getString("parameter.procedure.use"))), 
        		new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(new JLabel(String.valueOf(qp.isProcedureParameter())), 
        		new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));
        add(new JLabel(bold(I18NSupport.getString("parameter.procedure.preview"))), 
        		new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 0, 0), 0, 0));
        add(new JLabel(qp.getPreviewValue()), 
        		new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 0, 0), 0, 0));
		
	}
	
	private String multiline(String s, int chars) {
		if ((s == null) || (s.length() <= chars)) {
			return s;
		}
		StringBuilder sb = new StringBuilder("<html>");
		for (int i=0; i<s.length(); i=i+chars) {
			if (i+chars >= s.length()) {
				sb.append(s.substring(i));
			} else {
				sb.append(s.substring(i, i+chars));
			}	
			sb.append("<br>");
		}	
		sb.append("</html>");
		return sb.toString();
	}
	
	private String bold(String s) {
		return "<html><b>" + s + "</b></html>";
	}

}
