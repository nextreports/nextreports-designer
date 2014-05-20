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

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.band.ExpressionBandElement;
import ro.nextreports.engine.band.ExpressionBean;
import ro.nextreports.engine.exporter.util.variable.Variable;

import javax.swing.*;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import java.util.EventObject;

/**
 * User: mihai.panaitescu
 * Date: 03-May-2010
 * Time: 13:01:24
 */
public class ExpressionCellEditor extends DefaultGridCellEditor {

    private ExpressionPanel panel;
    private BaseDialog dialog;
    private ExpressionBandElement bandElement;
    private boolean isEdit = false;
    private boolean isStatic = false;
    private boolean isHeaderOrFooter = false;    
    private String bandName = "";

    public ExpressionCellEditor() {
        super(new JTextField()); // not really relevant - sets a text field as the editing default.
    }

    @Override
    public boolean isCellEditable(EventObject event) {
        boolean isEditable = super.isCellEditable(event);
        if (isEditable) {
            // must see if selected cell is in a static band or not (to show or not columns in expression panel)
            if (event.getSource() instanceof ReportGrid) {
                ReportGrid grid = (ReportGrid) event.getSource();
                bandName = grid.getBandName(grid.getSelectionModel().getSelectedCell());                
                isStatic = !(bandName.equals(ReportLayout.DETAIL_BAND_NAME) ||
                        bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX) ||
                        bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX));
                isHeaderOrFooter = bandName.equals(ReportLayout.FOOTER_BAND_NAME) ||                
                        bandName.startsWith(ReportLayout.GROUP_FOOTER_BAND_NAME_PREFIX) ||
                        bandName.equals(ReportLayout.HEADER_BAND_NAME) ||                
                        bandName.startsWith(ReportLayout.GROUP_HEADER_BAND_NAME_PREFIX);
            }
            editorComponent = new JLabel("...", JLabel.HORIZONTAL);
            delegate = new ExpressionDelegate();
        }

        return isEditable;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }
    
    public void setIsHeaderOrFooter(boolean _isHeaderOrFooter) {
    	isHeaderOrFooter = _isHeaderOrFooter;
    }
    
    public void setBandName(String bandName) {
		this.bandName = bandName;
	}

    class ExpressionDelegate extends EditorDelegate {

        ExpressionDelegate() {
            panel = new ExpressionPanel(isStatic, isHeaderOrFooter, bandName, true);
            dialog = new BaseDialog(panel, I18NSupport.getString("expression.insert"), true) {
                protected boolean ok() {
                    String expName = panel.getExpressionName();
                    if (expName.trim().equals("")) {
                        Show.error(dialog, I18NSupport.getString("expression.error.enter.name"));
                        return false;
                    }

                    String exp = panel.getExpression();
                    if (exp.trim().equals("")) {
                        Show.error(dialog, I18NSupport.getString("expression.error.enter.expression"));
                        return false;
                    }
                    
                    if (exp.contains("$V_" + Variable.TOTAL_PAGE_NO_VARIABLE)) {
                    	Show.error(dialog, I18NSupport.getString("expression.error.notallowed"));
                        return false;                    	
                    }

                    for (ExpressionBean bean: ReportUtil.getExpressions(LayoutHelper.getReportLayout())) {
                        if (expName.equals(bean.getBandElement().getExpressionName()) && !isEdit) {
                           Show.error(dialog, I18NSupport.getString("expression.error.name.found"));                           
                           return false; 
                        }
                    }
                    if (!ReportLayoutUtil.isValidExpression(panel.getExpression())) {
                        Show.error(dialog, I18NSupport.getString("expression.error.invalid"));
                        return false;
                    }

                    return true;
                }
            };
            dialog.pack();
            dialog.setLocationRelativeTo(Globals.getMainFrame());
        }

        public void setValue(Object value) {
            bandElement = (ExpressionBandElement) value;
            panel.setExpressionName(bandElement.getExpressionName());
            panel.setExpression(bandElement.getExpression());
            isEdit = !bandElement.getExpression().trim().equals("");            
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dialog.setVisible(true);
                    if (dialog.okPressed()) {
                        stopCellEditing();
                    } else {
                        cancelCellEditing();
                        //delete $E{(?,?)} (when close expression panel)
                        if (bandElement.getExpression().equals("?")) {
                            new ClearCellAction().actionPerformed(null);
                        }

                    }
                }

            });
        }

        public Object getCellEditorValue() {
            ReportLayout oldLayout = getOldLayout();
            bandElement.setExpressionName(panel.getExpressionName());
            bandElement.setExpression(panel.getExpression());            
            registerUndoRedo(oldLayout, I18NSupport.getString("expression.edit"), I18NSupport.getString("expression.add"));
            return bandElement;
        }

    }	
}
