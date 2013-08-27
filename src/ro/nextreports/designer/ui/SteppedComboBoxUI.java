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
package ro.nextreports.designer.ui;

import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.AffineTransform;

import org.jdesktop.jxlayer.JXLayer;
import org.pbjar.jxlayer.demo.TransformUtils;
import org.pbjar.jxlayer.demo.QualityHints;
import org.pbjar.jxlayer.plaf.ext.transform.DefaultTransformModel;
import org.pbjar.jxlayer.plaf.ext.TransformUI;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 16-Oct-2009
// Time: 13:42:32

//
/**
 * This UI for Combo Boxes inside ReportGrid does the following  :
 *
 * 1. popup width will be less than the combobox width : inside report grid a combo box can be very large and
 *    we do not want the popup to be so large, so we will compute a maximum width from the existing items and we
 *    will add a padding
 * 2. popup size and position will be computed regarding the transformation of its parent container (report grid
 *    has a zoom functionality)
 */
public class SteppedComboBoxUI extends MetalComboBoxUI  {

    private int padding = 10;

    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(comboBox) {

            @SuppressWarnings("unchecked")
            public void show() {

                // get the tranform model and the transform of the combobox container
                Container comboContainer = comboBox.getParent();
                while (!(comboContainer instanceof JXLayer)) {
                    if (comboContainer == null) {
                        return;
                    }
                    comboContainer = comboContainer.getParent();
                }
                JXLayer parentLayer = (JXLayer) comboContainer;
                DefaultTransformModel parentModel = (DefaultTransformModel) ((TransformUI) parentLayer.getUI()).getModel();
                AffineTransform parentTransform = parentModel.getTransform(parentLayer);

                //compute width of text
                int widest = getWidestItemWidth();

                //Get the box's size
                Dimension popupSize = comboBox.getSize();

                //Set the size of the popup
                popupSize.setSize(widest + (2 * padding), getPopupHeightForRowCount(comboBox.getMaximumRowCount()));

                //Compute the complete bounds , take care if there is a border
                Border border = comboBox.getBorder();
                int left = 0;
                int bottom = 0;
                if (border != null) {
                    Insets insets = border.getBorderInsets(comboBox);
                    left = insets.left;
                    bottom = insets.bottom;
                }
                Rectangle popupBounds = computePopupBounds(left, comboBox.getBounds().height-bottom, popupSize.width, popupSize.height);

                //Set the size of the scroll pane
                Dimension dim = parentTransform.createTransformedShape(popupBounds).getBounds().getSize();           
                list.setPreferredSize(new Dimension(dim.width, list.getPreferredSize().height));
                scroller.setMaximumSize(dim);
                scroller.setPreferredSize(dim);
                scroller.setMinimumSize(dim);

                //Cause it to re-layout
                list.invalidate();

                //Handle selection of proper item
                int selectedIndex = comboBox.getSelectedIndex();
                if (selectedIndex == -1) {
                    list.clearSelection();
                } else {
                    list.setSelectedIndex(selectedIndex);
                }

                //Make sure the selected item is visible
                list.ensureIndexIsVisible(list.getSelectedIndex());

                //Use lightweight if asked for
                setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

                // use the same transform model for the base JXLayer and the popup JXLayer
                JXLayer layer = TransformUtils.createTransformJXLayer(list, parentModel, new QualityHints());
                scroller.setViewportView(layer);

                // apply transform for popup top left point
                Point point = new Point(left, comboBox.getHeight()-bottom);
                point = SwingUtilities.convertPoint(comboBox, point, parentLayer);
                parentTransform.transform(point, point);
                                                               
                //Show the popup relative to JXLayer
                show(parentLayer, point.x, point.y);

            }
        };
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }



    public int getWidestItemWidth() {
        int numItems = comboBox.getItemCount();
        Font font = comboBox.getFont();
        FontMetrics metrics = comboBox.getFontMetrics(font);

        //The widest width
        int widest = 0;
        for (int i = 0; i < numItems; i++) {
            Object item = comboBox.getItemAt(i);
            int lineWidth = metrics.stringWidth(item.toString());
            widest = Math.max(widest, lineWidth);
        }

        return widest;
    }

}
