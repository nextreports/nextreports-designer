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
package ro.nextreports.designer.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImagePreviewPanel  extends JPanel implements PropertyChangeListener {
	private JLabel label;
	private int maxImgWidth;
	public ImagePreviewPanel() {
		setLayout(new BorderLayout(5,5));
		setBorder(BorderFactory.createEmptyBorder(1,5,0,0));
		add(new JLabel(I18NSupport.getString("preview")), BorderLayout.NORTH);
		label = new JLabel("", JLabel.CENTER);
		label.setBackground(Color.WHITE);
		label.setOpaque(true);
		label.setPreferredSize(new Dimension(200, 200));
		maxImgWidth = 195;
		label.setBorder(BorderFactory.createEtchedBorder());
		add(label, BorderLayout.CENTER);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		Icon icon = null;
		if(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
			File newFile = (File) evt.getNewValue();
			if(newFile != null) {
				String path = newFile.getAbsolutePath();
				if(canPreview(path)) {
					try {
						BufferedImage img = ImageIO.read(newFile);
						float width = img.getWidth();
						float height = img.getHeight();
						float scale = height / width;
						if ( height > maxImgWidth) {
							height = maxImgWidth;
							width = height / scale;
						}	
						if (width > maxImgWidth) {
							width = maxImgWidth;
							height = width * scale;
						} 						
						icon = new ImageIcon(img.getScaledInstance(Math.max(1, (int)width),
                                Math.max(1, (int)height), Image.SCALE_SMOOTH));
					}
					catch(IOException e) {
						// couldn't read image.
					}
				}
			}

			label.setIcon(icon);
			this.repaint();

		}
	}

    private boolean canPreview(String path) {
        String ignoreCasePath = path.toLowerCase();
        return (ignoreCasePath.endsWith(".gif") || ignoreCasePath.endsWith(".jpg") ||
                ignoreCasePath.endsWith(".png") || ignoreCasePath.endsWith(".bmp"));
    }

}

