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
package ro.nextreports.designer.action.report.layout.cell;

import ro.nextreports.engine.util.ObjectCloner;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ImageColumnBandElement;
import ro.nextreports.engine.ReportLayout;

import javax.swing.*;
import javax.imageio.ImageIO;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.ImageResizePanel;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.ReportGrid;
import ro.nextreports.designer.action.undo.LayoutEdit;
import ro.nextreports.designer.grid.Cell;
import ro.nextreports.designer.grid.SelectionModel;
import ro.nextreports.designer.grid.event.SelectionModelEvent;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mihai.panaitescu
 * Date: 23-Mar-2010
 * Time: 11:07:35
 */
public class ImageSizeAction extends AbstractAction {

    public ImageSizeAction() {
        super();
        putValue(Action.NAME, I18NSupport.getString("size.image.action.name"));
    }

    public void actionPerformed(final ActionEvent event) {

        final ReportGrid grid = Globals.getReportGrid();
		SelectionModel selectionModel = grid.getSelectionModel();
		
		List<Cell> cells = selectionModel.getSelectedCells();
		final List<BandElement> olds = new ArrayList<BandElement>();		
		for (Cell cell : cells) {		
			BandElement be = grid.getBandElement(cell.getRow(), cell.getColumn());
			if ((be instanceof ImageBandElement) || (be instanceof ImageColumnBandElement)) {			
				olds.add(be);
			}
		}
        final ReportLayout oldLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());

        Thread executorThread = new Thread(new Runnable() {

            public void run() {
            	int[] size =  new int[] {0,0};
            	for (int i=0, len = olds.size(); i<len; i++) {
            		BandElement oldElement = olds.get(i);
            		int[] size2;
            		if (oldElement instanceof ImageBandElement) {
            			size2 = getRealImageSize(((ImageBandElement)oldElement).getImage());
            		} else {
            			// for ImageColumnBandElement we put a static actual size (we do not go to database to compute it)
            			size2 = new int[] {50,50};
            		}
            		if (i == 0) {
            			size = size2;
            		} else {
            			if ((size[0] != size2[0]) || (size[1] != size2[1])) {
            				size[0] = size[1] = 0;
            				break;
            			}
            		}
            	}	
            	final int[] s = size;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        final ImageResizePanel panel = new ImageResizePanel(s, olds.get(0));
                        final BaseDialog dialog = new BaseDialog(panel, I18NSupport.getString("size.image.action.name"));
                        dialog.pack();
                        dialog.setLocationRelativeTo(Globals.getMainFrame());
                        dialog.setVisible(true);
                        if (!dialog.okPressed()) {
                            return;
                        }

						for (int i = 0, len = olds.size(); i < len; i++) {
							BandElement be = olds.get(i);
							if (be instanceof ImageBandElement) {
								ImageBandElement ibe = (ImageBandElement)be;
								ibe.setWidth(panel.getImageWidth());
								ibe.setHeight(panel.getImageHeight());
							} else {
								ImageColumnBandElement icbe = (ImageColumnBandElement)be;
								icbe.setWidth(panel.getImageWidth());
								icbe.setHeight(panel.getImageHeight());
							}
						}

                        SelectionModelEvent selectionEvent = new SelectionModelEvent(Globals.getReportGrid().getSelectionModel(), false);
                        Globals.getReportDesignerPanel().getPropertiesPanel().selectionChanged(selectionEvent);

                         ReportLayout newLayout = ObjectCloner.silenceDeepCopy(LayoutHelper.getReportLayout());
                         Globals.getReportUndoManager().addEdit(new LayoutEdit(oldLayout, newLayout, I18NSupport.getString("size.image.action.name")));
                    }
                });
            }
        }, "NEXT : " + getClass().getSimpleName());
        executorThread.start();

    }

    private int[] getRealImageSize(String image) {
        InputStream is = getClass().getResourceAsStream("/" + image);
        int[] size = new int[2];
        size[0] = 0;
        size[1] = 0;
        try {
            BufferedImage img = ImageIO.read(is);
            if (img != null) {
            	size[0] = img.getWidth();
            	size[1] = img.getHeight();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return size;
    }
       
}
