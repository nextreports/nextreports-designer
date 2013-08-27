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
package ro.nextreports.designer;

import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.ReportLayout;

import javax.swing.*;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.util.FileUtil;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImagePreviewPanel;
import ro.nextreports.designer.util.ReporterPreferencesManager;
import ro.nextreports.designer.util.file.ImageFilter;

import java.io.File;
import java.io.IOException;
import java.util.EventObject;

/**
 * User: mihai.panaitescu
 * Date: 02-Dec-2009
 * Time: 13:10:39
 */
public class ImageCellEditor extends DefaultGridCellEditor {

    private JFileChooser fc;
    private ImageBandElement bandElement;

    public ImageCellEditor() {
        super(new JTextField()); // not really relevant - sets a text field as the editing default.
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        boolean isEditable = super.isCellEditable(event);
        if (isEditable) {
            editorComponent = new JLabel("...", JLabel.HORIZONTAL);
            delegate = new ImageDelegate();
        }

        return isEditable;
    }

    class ImageDelegate extends EditorDelegate {

        ImageDelegate() {
            fc = new JFileChooser();
            ImagePreviewPanel previewPane = new ImagePreviewPanel();
            fc.setAccessory(previewPane);
            fc.addPropertyChangeListener(previewPane);
            fc.setDialogTitle(I18NSupport.getString("image.title"));
            fc.setAcceptAllFileFilterUsed(false);
            fc.addChoosableFileFilter(new ImageFilter());

        }

        public void setValue(Object value) {
            bandElement = (ImageBandElement) value;
            String path =  ReporterPreferencesManager.getInstance().loadParameter(ReporterPreferencesManager.IMAGE_PATH_KEY);
            if (path != null) {
                fc.setSelectedFile(new File(path));
            }

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    int returnVal = fc.showOpenDialog(Globals.getMainFrame());
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        final File f = fc.getSelectedFile();
                        if (f != null) {

                        }
                        stopCellEditing();
                    } else {
                        cancelCellEditing();
                        if (bandElement.getImage().equals("?")) {
                            new ClearCellAction().actionPerformed(null);
                        }
                    }
                }

            });
        }

        public Object getCellEditorValue() {
            ReportLayout oldLayout = getOldLayout();
            bandElement.setImage(fc.getSelectedFile().getName());
            ReporterPreferencesManager.getInstance().storeParameter(ReporterPreferencesManager.IMAGE_PATH_KEY, fc.getSelectedFile().getAbsolutePath());
            try {
                FileUtil.copyToDir(fc.getSelectedFile(), new File(Globals.getCurrentReportAbsolutePath()).getParentFile(), true);                
            } catch (IOException e) {
                e.printStackTrace();  
            }
            registerUndoRedo(oldLayout, I18NSupport.getString("edit.image"), I18NSupport.getString("edit.image.insert"));
            return bandElement;
        }

    }

}
