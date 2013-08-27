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
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImagePreviewPanel;
import ro.nextreports.designer.util.file.ImageFilter;


public class ImageChooser {
	
	public static String showDialog(Component parent, String title, String initialImage) {
				
		JFileChooser fc = new JFileChooser();
        ImagePreviewPanel previewPane = new ImagePreviewPanel();
        fc.setAccessory(previewPane);
        fc.addPropertyChangeListener(previewPane);
        fc.setDialogTitle(I18NSupport.getString("image.title"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new ImageFilter());
        
        int returnVal = fc.showOpenDialog(Globals.getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File f = fc.getSelectedFile();
            if (f != null) {
            	 try {
                     FileUtil.copyToDir(f, new File(Globals.getCurrentReportAbsolutePath()).getParentFile(), true);                
                 } catch (IOException e) {
                     e.printStackTrace();  
                 }
            	return f.getName();
            }            
        } 
        return null;        		       
	}

}
