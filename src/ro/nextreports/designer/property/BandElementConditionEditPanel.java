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

import ro.nextreports.engine.condition.BandElementCondition;
import ro.nextreports.engine.condition.ConditionalOperator;
import ro.nextreports.engine.condition.BandElementConditionProperty;
import ro.nextreports.engine.condition.ConditionalExpression;
import ro.nextreports.engine.band.Border;
import com.l2fprod.common.swing.JFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.jdesktop.swingx.JXDatePicker;

import ro.nextreports.designer.util.I18NSupport;

/**
 * User: mihai.panaitescu
 * Date: 23-Apr-2010
 * Time: 13:31:23
 */
public class BandElementConditionEditPanel extends JPanel {

    private Dimension btnDim = new Dimension(20, 20);
    private Dimension txtDim = new Dimension(200, 20);

    private static String[] PROPERTIES = {
            getProperty(BandElementConditionProperty.BACKGROUND_PROPERTY),
            getProperty(BandElementConditionProperty.FOREGROUND_PROPERTY),
            getProperty(BandElementConditionProperty.FONT_PROPERTY),
            getProperty(BandElementConditionProperty.BORDER_PROPERTY)};

    private BandElementCondition condition;

    private JComboBox operatorComboBox;
    private JTextField operandText;
    private JLabel label2;
    private JTextField operandText2;
    private JComboBox propertyComboBox;
    private JTextField propertyValueText;
    private JButton propertyValueButton;
    private JXDatePicker datePicker;
    private JXDatePicker datePicker2;

    private Serializable propertyValue;
    private String type;
    private boolean lockBackground = false;

    public BandElementConditionEditPanel(BandElementCondition condition, String type) {
        this(condition, type, false);
    }
    
    public BandElementConditionEditPanel(BandElementCondition condition, String type, boolean lockBackground) {
        this.condition = condition;
        this.lockBackground = lockBackground;
        if (type == null) {
            type = "java.lang.Double";
        }
        this.type =type;
        initUI(type);
    }

    public BandElementCondition getCondition() {
        return condition;
    }

    private void initUI(String type) {
        this.setLayout(new GridBagLayout());

        if ("java.lang.String".equals(type) || "java.lang.Boolean".equals(type)) {
            operatorComboBox = new JComboBox(new String[]{ConditionalOperator.EQUAL, ConditionalOperator.NOT_EQUAL});
        } else {
            operatorComboBox = new JComboBox(ConditionalOperator.operators);
        }
        operandText = new JTextField();
        operandText2 = new JTextField();
        datePicker = new JXDatePicker();
        datePicker.setFormats(ConditionalExpression.DATE_FORMAT);
        datePicker2 = new JXDatePicker();
        datePicker2.setFormats(ConditionalExpression.DATE_FORMAT);
        propertyComboBox = new JComboBox(PROPERTIES);
        if (lockBackground) {
        	propertyComboBox.setSelectedIndex(0);
        	propertyComboBox.setEnabled(false);
        }
        propertyComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                propertyValue = null;
                propertyValueText.setText("");
            }
        });
        propertyValueText = new JTextField();
        propertyValueText.setEditable(false);
        propertyValueText.setPreferredSize(txtDim);
        propertyValueButton = new JButton("...");
        propertyValueButton.setMinimumSize(btnDim);
        propertyValueButton.setMaximumSize(btnDim);
        propertyValueButton.setPreferredSize(btnDim);

        if (condition != null) {
            String operator = condition.getExpression().getOperator();
            operatorComboBox.setSelectedItem(operator);
            String operand = condition.getExpression().getRightOperand().toString();
            if ("java.util.Date".equals(type)) {
               operand = ConditionalExpression.DATE_FORMAT.format(condition.getExpression().getRightOperand());
               datePicker.setDate((Date)condition.getExpression().getRightOperand());
               if (ConditionalOperator.BETWEEN.equals(operator))  {
                   datePicker2.setDate((Date)condition.getExpression().getRightOperand2());
               }
            } else {
               operandText.setText(operand);
               if (ConditionalOperator.BETWEEN.equals(operator))  {
                   operandText2.setText( condition.getExpression().getRightOperand2().toString());
               }
            }
            propertyComboBox.setSelectedIndex(condition.getProperty());
            propertyValue = condition.getPropertyValue();
            if (propertyValue != null) {
                propertyValueText.setText(getPropertyValueAsString(propertyValue));
            }
        }

        propertyValueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int property = getProperty();
                JDialog parent = (JDialog) SwingUtilities.getWindowAncestor(BandElementConditionEditPanel.this);
                if ((property == 0) || (property == 1)) {
                    Color initialColor = null;
                    if (propertyValue instanceof Color) {
                        initialColor = (Color) propertyValue;
                    }                    
//                    JColorChooser chooser;
//                    if (initialColor != null) {
//                        chooser = new JColorChooser(initialColor);
//                    } else {
//                        chooser = new JColorChooser();
//                    }
//                    chooser.addChooserPanel(new ExcelColorChooserPanel());
//                    ColorTracker ok = new ColorTracker(chooser);
//                    JDialog dialog = JColorChooser.createDialog(parent, I18NSupport.getString("color.dialog.title"), true, chooser, ok, null);
//                    dialog.setVisible(true);
//                    Color color = ok.getColor();
                    Color color = ExtendedColorChooser.showDialog(parent, I18NSupport.getString("color.dialog.title"), initialColor);
                    propertyValue = color;
                } else if (property == 2) {
                    Font initialFont = null;
                    if (propertyValue instanceof Font) {
                        initialFont = (Font) propertyValue;
                    }
                    Font font = JFontChooser.showDialog(parent, I18NSupport.getString("font.dialog.title"), initialFont);
                    propertyValue = font;
                } else if (property == 3) {
                    Border initialBorder = null;
                    if (propertyValue instanceof Border) {
                        initialBorder = (Border) propertyValue;
                    }
                    Border border = BorderChooser.showDialog(parent, I18NSupport.getString("border.dialog.title"), initialBorder);
                    propertyValue = border;
                }
                if (propertyValue != null) {
                    propertyValueText.setText(getPropertyValueAsString(propertyValue));
                } else {
                    propertyValueText.setText("");
                }
            }
        });

        add(new JLabel(I18NSupport.getString("condition.operator")),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        add(operatorComboBox,
                new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
        add(new JLabel(I18NSupport.getString("condition.value")),
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        if ("java.util.Date".equals(type)) {
           add(datePicker,
                new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        } else {
            add(operandText,
                new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        }
        add(label2 =  new JLabel(I18NSupport.getString("condition.value")),
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        if ("java.util.Date".equals(type)) {
           add(datePicker2,
                new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        } else {
            add(operandText2,
                new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        }

        add(new JLabel(I18NSupport.getString("condition.property")),
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        add(propertyComboBox,
                new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        add(new JLabel(I18NSupport.getString("condition.property.value")),
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
        add(propertyValueText,
                new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        add(propertyValueButton,
                new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                        GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

        operatorComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableOperand2(ConditionalOperator.BETWEEN.equals(getOperator()));
                JDialog dialog = (JDialog)SwingUtilities.getWindowAncestor(BandElementConditionEditPanel.this);
                dialog.pack();
            }
        });
        if (condition == null) {
            enableOperand2(false);
        } else {
            String operator = condition.getExpression().getOperator();
            enableOperand2(ConditionalOperator.BETWEEN.equals(operator));      
        }

    }

    public static String getProperty(int property) {
        switch (property) {
            case 0:
                return I18NSupport.getString("property.background");
            case 1:
                return I18NSupport.getString("property.foreground");
            case 2:
                return I18NSupport.getString("property.font");
            case 3:
                return I18NSupport.getString("property.border");
            default:
                return null;
        }
    }

    public static String getPropertyValueAsString(Serializable propertyValue) {
            if (propertyValue instanceof Color) {
                Color color = (Color)propertyValue;
                return "R:" + color.getRed() + " G:" + color.getGreen() + " B:"+color.getBlue();
            } else if (propertyValue instanceof Font) {
                Font font = (Font)propertyValue;
                return font.getFamily() + "," + font.getStyle() + "," + font.getSize();
            } else {
                return propertyValue.toString();
            }
        }


    public String getOperator() {
        return (String) operatorComboBox.getSelectedItem();
    }

    public Serializable getValue() {
        if ("java.util.Date".equals(type)) {
            return datePicker.getDate();
        } else {
            String text = operandText.getText();
            return getObjectFromString(text, type);
        }
    }

    public Serializable getValue2() {
    	if (!ConditionalOperator.BETWEEN.equals(operatorComboBox.getSelectedItem())) {
    		return null;
    	}
        if ("java.util.Date".equals(type)) {
            return datePicker2.getDate();
        } else {
            String text = operandText2.getText();
            return getObjectFromString(text, type);
        }
    }


    public int getProperty() {
        return propertyComboBox.getSelectedIndex();
    }

    public Serializable getPropertyValue() {
        return propertyValue;
    }

    public Serializable getObjectFromString(String text, String className) {
        Serializable value = null;
        try {
            if ("java.lang.String".equals(className)) {
                value = text;
            } else if ("java.lang.Integer".equals(className) ||
                       "java.lang.Byte".equals(className) ||
                       "java.lang.Short".equals(className) ||
                       "java.lang.Long".equals(className) ||
                       "java.lang.Float".equals(className) ||
                       "java.lang.Double".equals(className) ||
                       "java.math.BigDecimal".equals(className) )  {
                value = Double.parseDouble(text);
            } else if ("java.lang.Boolean".equals(className)) {
                value = Boolean.parseBoolean(text);
            } else if ("java.util.Date".equals(className)) {
                value = ConditionalExpression.DATE_FORMAT.parse(text);
            } else {
                throw new IllegalArgumentException("Invalid class : " + className);
            }
        } catch (NumberFormatException nfe) {
            return null;
        } catch (ParseException ex) {
            return null;
        }
        return value;
    }

    public String getType() {
        return type;
    }

    private void enableOperand2(boolean enable) {
        label2.setVisible(enable);
        datePicker2.setVisible(enable);
        operandText2.setVisible(enable);
    }
}
