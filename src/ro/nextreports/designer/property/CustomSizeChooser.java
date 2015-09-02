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
import java.awt.Dimension;

import ro.nextreports.designer.ui.BaseDialog;

import ro.nextreports.engine.band.PaperSize;

/**
 * @author Mihai Dinca-Panaitescu
 */
public class CustomSizeChooser {
	
	public static PaperSize showDialog(Component parent, String title, PaperSize initialPaperSize) {

        CustomSizePanel sizePanel = new CustomSizePanel();
		sizePanel.setPaperSize(initialPaperSize);
        BaseDialog dialog = new BaseDialog(sizePanel, title, true);
        dialog.setPreferredSize(new Dimension(250, 200));
        dialog.setResizable(false);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        if (dialog.okPressed()) {
			return sizePanel.getFinalPaperSize();
		} else {
			return null;
		}
	}

}
