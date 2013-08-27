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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXPanel;

import ro.nextreports.designer.action.report.layout.cell.ClearCellAction;
import ro.nextreports.designer.grid.DefaultGridCellEditor;
import ro.nextreports.designer.ui.BaseDialog;
import ro.nextreports.designer.util.I18NSupport;
import ro.nextreports.designer.util.Show;

import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.band.BarcodeBandElement;

/**
 * 
 * @author Mihai Dinca-Panaitescu
 *
 */
public class BarcodeCellEditor extends DefaultGridCellEditor  {
	
	private BarcodePanel panel;
    private BaseDialog dialog;
	private BarcodeBandElement bandElement;
	
	public BarcodeCellEditor() {
		super(new JTextField()); // not really relevant - sets a text field as the editing default.
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		boolean isEditable = super.isCellEditable(event);
		if (isEditable) {
			editorComponent = new JLabel("...", JLabel.HORIZONTAL);
			delegate = new BarcodeDelegate();
		}

		return isEditable;
	}
	
	class BarcodeDelegate extends EditorDelegate {

		BarcodeDelegate() {
            panel = new BarcodePanel();
            dialog = new BaseDialog(panel, I18NSupport.getString("barcode.title"), true);
            dialog.pack();
            dialog.setLocationRelativeTo(Globals.getMainFrame());
        }

        public void setValue(Object value) {
            bandElement = (BarcodeBandElement) value;
            panel.setType(bandElement.getBarcodeType());
            panel.setValue(bandElement.getValue(), bandElement.isColumn());
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dialog.setVisible(true);
                    if (dialog.okPressed()) {
                        stopCellEditing();
                    } else {
                        cancelCellEditing();
                        //delete $B{(?)} (when close function panel)
                        if (bandElement.getValue().equals("?")) {
                            new ClearCellAction().actionPerformed(null);
                        }
                    }
                }

            });
        }

        public Object getCellEditorValue() {
            ReportLayout oldLayout = getOldLayout();
            bandElement.setBarcodeType(panel.getType());
            bandElement.setValue(panel.getValue());
            bandElement.setColumn(panel.isColumn());
            registerUndoRedo(oldLayout, I18NSupport.getString("edit.barcode"), I18NSupport.getString("edit.barcode.insert"));            
            return bandElement;
        }

    }
	
	class BarcodePanel extends JXPanel {

        private JComboBox typeComboBox;
        private JRadioButton columnRadioButton;
        private JRadioButton valueRadioButton;        
        private JComboBox columnComboBox;
        private JTextField valueText;
        
        private BarcodeType[] types = BarcodeType.getTypes();

        public BarcodePanel() {

            columnRadioButton = new JRadioButton();
            valueRadioButton = new JRadioButton();
            ButtonGroup group = new ButtonGroup();
            group.add(columnRadioButton);
            group.add(valueRadioButton);
            columnRadioButton.setSelected(true);
            columnRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     if (columnRadioButton.isSelected()) {
                         enableButtons(true);
                     }
                }
            });
            valueRadioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     if (valueRadioButton.isSelected()) {
                         enableButtons(false);
                     }
                }
            });

            List<String> columns = new ArrayList<String>();
            try {
                columns = ReportLayoutUtil.getAllColumnNamesForReport(null);
            } catch (Exception e) {
                Show.error(e);
            }            

            JLabel typeLabel = new JLabel(I18NSupport.getString("barcode.type"));
            DefaultComboBoxModel typeComboModel = new DefaultComboBoxModel(types);
            typeComboBox = new JComboBox(typeComboModel);
            typeComboBox.setPrototypeDisplayValue("XXXXXXXXXXXXXXX");

            JLabel columnLabel = new JLabel(I18NSupport.getString("barcode.column"));
            DefaultComboBoxModel functionColumnGroupComboModel = new DefaultComboBoxModel(columns.toArray());
            columnComboBox = new JComboBox(functionColumnGroupComboModel);

            JLabel valueLabel = new JLabel(I18NSupport.getString("barcode.value"));
            valueText = new JTextField();

            setLayout(new GridBagLayout());

            add(typeLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
			add(typeComboBox, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, 
					GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
					new Insets(0, 5, 5, 0), 0, 0));

			add(columnRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
					GridBagConstraints.WEST, GridBagConstraints.NONE,
					new Insets(0, 0, 5, 5), 0, 0));
            add(columnLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
            add(columnComboBox, new GridBagConstraints(2, 1, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 5, 0), 0, 0));
            
            add(valueRadioButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 5), 0, 0));
            add(valueLabel, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE,
                    new Insets(0, 0, 5, 0), 0, 0));
            add(valueText, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 5, 5, 0), 0, 0));
            
        }

        public int getType() {
            return ((BarcodeType) typeComboBox.getSelectedItem()).getType();
        }

        public void setType(int type) {
        	BarcodeType bt = getBarcodeType(type);
            typeComboBox.setSelectedItem(bt);
        }

        public String getValue() {
            if (columnRadioButton.isSelected()) {
                return (String) columnComboBox.getSelectedItem();
            } else {
                return (String) valueText.getText();
            }
        }

        public void setValue(String value, boolean isColumn) {
            if (isColumn) {
                columnComboBox.setSelectedItem(bandElement.getValue());
                columnRadioButton.setSelected(true);
            } else {
                valueText.setText(bandElement.getValue());
                valueRadioButton.setSelected(true);
            }
            enableButtons(isColumn);
        }

        public boolean isColumn() {
            return columnRadioButton.isSelected();
        }

        private void enableButtons(boolean isColumn) {
           columnComboBox.setEnabled(isColumn);
           valueText.setEnabled(!isColumn);
        }
        
        private BarcodeType getBarcodeType(int type) {
        	for (BarcodeType bt : types) {
        		if (bt.getType() == type) {
        			return bt;
        		}
        	}
        	return null;
        }

    }
	
	enum BarcodeType {				
				
		EAN13("EAN.UCC-13", BarcodeBandElement.EAN13),
		EAN8("EAN.UCC-8", BarcodeBandElement.EAN8),
		UPCA("UCC-12 (UPC-A)", BarcodeBandElement.UPCA),
		UPCE("UPC-E", BarcodeBandElement.UPCE),
		SUPP2("SUPP2", BarcodeBandElement.SUPP2),
		SUPP5("SUPP5", BarcodeBandElement.SUPP5),
		CODE128("CODE128", BarcodeBandElement.CODE128),
		CODE128_RAW("CODE128_RAW", BarcodeBandElement.CODE128_RAW),
		INTER25("INTER25", BarcodeBandElement.INTER25),
		CODE39("CODE39", BarcodeBandElement.CODE39),
		CODE39EXT("CODE39EXT", BarcodeBandElement.CODE39EXT),
		CODABAR("CODABAR", BarcodeBandElement.CODABAR),
		PDF417("PDF417", BarcodeBandElement.PDF417),
		DATAMATRIX("DATAMATRIX", BarcodeBandElement.DATAMATRIX),
		QRCODE("QRCODE", BarcodeBandElement.QRCODE);
		
		private final String name;
		private final int type;
		
		private BarcodeType(String name, int type) {
	        this.name = name;
	        this.type = type;
	    }

	    public String toString() {
	        return name;
	    }
	    
	    public int getType() {
	    	return type;
	    }
	    
	    public static BarcodeType[] getTypes() {	    	
	    	return new BarcodeType[] { 
	    			BarcodeType.EAN13, BarcodeType.EAN8, 
	    			BarcodeType.UPCA, BarcodeType.UPCE, 
	    			BarcodeType.SUPP2, BarcodeType.SUPP5,
	    			BarcodeType.CODE128, BarcodeType.CODE128_RAW,
	    			BarcodeType.INTER25, BarcodeType.CODE39, 
	    			BarcodeType.CODE39EXT, BarcodeType.CODABAR,
	    			BarcodeType.PDF417, BarcodeType.DATAMATRIX,
	    			BarcodeType.QRCODE
	    	};
	    }
	}


}
