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

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * @author Decebal Suiu
 */
public abstract class MessageWizardPanel extends WizardPanel {

    private String message;
    private JTextPane textPane;

    public MessageWizardPanel() {
    	this("");
    }

    public MessageWizardPanel(String message) {
        super();
        initComponents();
    }

	public String getMessage() {
		return message;
	}

	public void setTextMessage(String message) {
		this.message = message;
		textPane.setText(message);
	}

    public void setHtmlMessage(String message, String type) {
		this.message = message;
		if (!"text/html".equals(textPane.getContentType())) {
			textPane.setContentType("text/html");
		}        
        textPane.setText(message);
	}

    private void initComponents() {
		setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setText(message);
        add(new JScrollPane(textPane), BorderLayout.CENTER);
	}

}
