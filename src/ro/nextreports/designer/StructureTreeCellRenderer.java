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

import java.awt.*;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.ImageUtil;

import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.ColumnBandElement;
import ro.nextreports.engine.band.FunctionBandElement;
import ro.nextreports.engine.band.ImageBandElement;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.ReportBandElement;
import ro.nextreports.engine.ReportLayout;

/**
 * @author Decebal Suiu
 */
public class StructureTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (node.isRoot()) {
            setText(I18NSupport.getString("report.perspective"));
            setIcon(ImageUtil.getImageIcon("report_perspective"));           
        } else {
            if (userObject instanceof Band) {
                String bandName = ((Band) userObject).getName();
                if (bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX)) {
                    setText(I18NSupport.getString("band.group.header.name") + bandName.substring(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX.length()));
                } else if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX)) {
                    setText(I18NSupport.getString("band.group.footer.name") + bandName.substring(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX.length()));
                } else {
                    setText(I18NSupport.getString("band." + bandName.toLowerCase() + ".name"));
                }
                setIcon(BandUtil.getIcon(bandName));
            } else if (userObject instanceof ReportGridCell) {
                BandElement element = ((ReportGridCell) userObject).getValue();
                if (element instanceof ColumnBandElement) {
                    setIcon(ImageUtil.getImageIcon("column"));
                } else if (element instanceof FunctionBandElement) {
                    setIcon(ImageUtil.getImageIcon("sum"));
                } else if (element instanceof ExpressionBandElement) {
                    setIcon(ImageUtil.getImageIcon("expression"));
                } else if (element instanceof ImageBandElement) {
                    setIcon(ImageUtil.getImageIcon("image"));
                } else if (element instanceof ReportBandElement) {
                    setIcon(ImageUtil.getImageIcon("report"));
                } else {
                    setIcon(ImageUtil.getImageIcon("textfield"));
                }

                if (element != null) {
                    setText(element.getText());
                } else {
                    setText("< >");
                }
            } else if (userObject instanceof Integer) {
                Integer bandRow = (Integer) userObject;
                String bandName = ((Band) (((DefaultMutableTreeNode) node.getParent()).getUserObject())).getName();
                if (bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX)) {
                    setText(I18NSupport.getString("band.group.header.name").substring(0, 1).toUpperCase() + bandRow + bandName.substring(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX.length()));
                } else if (bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX)) {
                    setText(I18NSupport.getString("band.group.footer.name").substring(0, 1).toUpperCase() + bandRow + bandName.substring(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX.length()));
                } else {
                    setText(String.valueOf(I18NSupport.getString("band." + bandName.toLowerCase() + ".name").
                            substring(0, 1).toUpperCase() + bandRow));
                }
                setIcon(ImageUtil.getImageIcon("row"));
            }
        }

        return this;
    }

}
