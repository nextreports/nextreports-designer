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
package ro.nextreports.designer.template.report;

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.FieldBandElement;
import ro.nextreports.engine.band.Padding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.*;
import java.util.List;
import java.util.*;

import ro.nextreports.designer.property.BorderPropertyEditor;
import ro.nextreports.designer.property.ExtendedColorPropertyEditor;
import ro.nextreports.designer.property.PaddingPropertyEditor;
import ro.nextreports.designer.util.I18NSupport;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Dec 8, 2008
 * Time: 5:18:27 PM
 */
public class TemplatePropertyPanel extends PropertySheetPanel implements
        PropertyChangeListener {


    private String TEXT_PARAM_NAME = I18NSupport.getString("property.text");
    private String FONT_PARAM_NAME = I18NSupport.getString("property.font");
    private String BACKGROUND_PARAM_NAME = I18NSupport.getString("property.background");
    private String FOREGROUND_PARAM_NAME = I18NSupport.getString("property.foreground");
    private String ALIGNMENT_PARAM_NAME = I18NSupport.getString("property.allignment");
    private String V_ALIGNMENT_PARAM_NAME = I18NSupport.getString("property.vertical.allignment");
    private String PATTERN_PARAM_NAME = I18NSupport.getString("property.pattern");
    private String PADDING_PARAM_NAME = I18NSupport.getString("property.padding");
    private String BORDER_PARAM_NAME = I18NSupport.getString("property.border");
    private String CENTER = I18NSupport.getString("property.allignment.center");
    private String LEFT = I18NSupport.getString("property.allignment.left");
    private String RIGHT = I18NSupport.getString("property.allignment.right");
    private String MIDDLE = I18NSupport.getString("property.vertical.allignment.middle");
    private String TOP = I18NSupport.getString("property.vertical.allignment.top");
    private String BOTTOM = I18NSupport.getString("property.vertical.allignment.bottom");

    private BandElement bandElement;
    private PropertyEditorRegistry editorRegistry;

    public TemplatePropertyPanel(BandElement bandElement) {
        super();
        setDescriptionVisible(false);
        setToolBarVisible(false);
        setSortingCategories(true);
        addPropertySheetChangeListener(this);
        this.bandElement = bandElement;
        editorRegistry = (PropertyEditorRegistry) getEditorFactory();
        List<Property> props = getFilteredProperties();
        setProperties(props.toArray(new Property[props.size()]));
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {

        Property prop = (Property) event.getSource();
        String propName = prop.getName();

        if (TEXT_PARAM_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();
            bandElement.setText(propValue);
        } else if (FONT_PARAM_NAME.equals(propName)) {
            Font propValue = (Font) prop.getValue();
            bandElement.setFont(propValue);
        } else if (BACKGROUND_PARAM_NAME.equals(propName)) {
            Color propValue = (Color) prop.getValue();
            bandElement.setBackground(propValue);
        } else if (FOREGROUND_PARAM_NAME.equals(propName)) {
            Color propValue = (Color) prop.getValue();
            bandElement.setForeground(propValue);
        } else if (ALIGNMENT_PARAM_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();
            if (CENTER.equals(propValue)) {
                bandElement.setHorizontalAlign(BandElement.CENTER);
            } else if (RIGHT.equals(propValue)) {
                bandElement.setHorizontalAlign(BandElement.RIGHT);
            } else {
                bandElement.setHorizontalAlign(BandElement.LEFT);
            }
        } else if (V_ALIGNMENT_PARAM_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();
            if (MIDDLE.equals(propValue)) {
                bandElement.setVerticalAlign(BandElement.MIDDLE);
            } else if (TOP.equals(propValue)) {
                bandElement.setVerticalAlign(BandElement.TOP);
            } else {
                bandElement.setVerticalAlign(BandElement.BOTTOM);
            }
        } else if (PATTERN_PARAM_NAME.equals(propName)) {
            String propValue = (String) prop.getValue();
            ((FieldBandElement) bandElement).setPattern(propValue);
        } else if (PADDING_PARAM_NAME.equals(propName)) {
            Padding propValue = (Padding) prop.getValue();
            bandElement.setPadding(propValue);
        } else if (BORDER_PARAM_NAME.equals(propName)) {
            ro.nextreports.engine.band.Border propValue = (ro.nextreports.engine.band.Border) prop
                    .getValue();
            bandElement.setBorder(propValue);
        }
        propertySelection(bandElement);
    }

    protected void propertySelection(BandElement bandElement) {        
    }

    public BandElement getBandElement() {
        return bandElement;
    }

    private List<Property> getFilteredProperties() {
        List<Property> props = new ArrayList<Property>();

        // font
        props.add(getFontProperty());

        // background
        props.add(getBackgroundProperty());

        // foreground
        props.add(getForegroundProperty());

        // alignment
        props.add(getAlignmentProperty());

        // vertical alignment
        props.add(getVerticalAlignmentProperty());

        // padding
        props.add(getPaddingProperty());

        // border
        props.add(getBorderProperty());

        return props;
    }


    private Property getFontProperty() {
        DefaultProperty fontProp = new DefaultProperty();
        fontProp.setName(FONT_PARAM_NAME);
        fontProp.setDisplayName(FONT_PARAM_NAME);
        fontProp.setType(Font.class);
        fontProp.setValue(bandElement.getFont());

        return fontProp;
    }

    private Property getBackgroundProperty() {
        DefaultProperty backgroundProp = new DefaultProperty();
        backgroundProp.setName(BACKGROUND_PARAM_NAME);
        backgroundProp.setDisplayName(BACKGROUND_PARAM_NAME);
        backgroundProp.setType(Color.class);
        backgroundProp.setValue(bandElement.getBackground());
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(backgroundProp, colorEditor);
        return backgroundProp;
    }

    private Property getForegroundProperty() {
        DefaultProperty foregroundProp = new DefaultProperty();
        foregroundProp.setName(FOREGROUND_PARAM_NAME);
        foregroundProp.setDisplayName(FOREGROUND_PARAM_NAME);
        foregroundProp.setType(Color.class);
        foregroundProp.setValue(bandElement.getForeground());
        ExtendedColorPropertyEditor colorEditor = new ExtendedColorPropertyEditor();
        editorRegistry.registerEditor(foregroundProp, colorEditor);
        return foregroundProp;
    }

    private Property getAlignmentProperty() {
        DefaultProperty alignmentProp = new DefaultProperty();
        alignmentProp.setName(ALIGNMENT_PARAM_NAME);
        alignmentProp.setDisplayName(ALIGNMENT_PARAM_NAME);
        alignmentProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[]{LEFT, CENTER, RIGHT});
        int alignment = bandElement.getHorizontalAlign();
        switch (alignment) {
            case BandElement.CENTER:
                alignmentProp.setValue(CENTER);
                break;
            case BandElement.RIGHT:
                alignmentProp.setValue(RIGHT);
                break;
            case BandElement.LEFT:
                alignmentProp.setValue(LEFT);
                break;
            default:
                alignmentProp.setValue(null);
        }
        editorRegistry.registerEditor(alignmentProp, alignmentEditor);

        return alignmentProp;
    }

    private Property getVerticalAlignmentProperty() {
        DefaultProperty alignmentProp = new DefaultProperty();
        alignmentProp.setName(V_ALIGNMENT_PARAM_NAME);
        alignmentProp.setDisplayName(V_ALIGNMENT_PARAM_NAME);
        alignmentProp.setType(String.class);
        ComboBoxPropertyEditor alignmentEditor = new ComboBoxPropertyEditor();
        alignmentEditor.setAvailableValues(new String[]{TOP, MIDDLE, BOTTOM});
        int alignment = bandElement.getVerticalAlign();
        switch (alignment) {
            case BandElement.TOP:
                alignmentProp.setValue(TOP);
                break;
            case BandElement.MIDDLE:
                alignmentProp.setValue(MIDDLE);
                break;
            case BandElement.BOTTOM:
                alignmentProp.setValue(BOTTOM);
                break;
            default:
                alignmentProp.setValue(null);
        }
        editorRegistry.registerEditor(alignmentProp, alignmentEditor);

        return alignmentProp;
    }

    private Property getPaddingProperty() {
        DefaultProperty paddingProp = new DefaultProperty();
        paddingProp.setName(PADDING_PARAM_NAME);
        paddingProp.setDisplayName(PADDING_PARAM_NAME);
        paddingProp.setType(Padding.class);
        paddingProp.setValue(bandElement.getPadding());
        PaddingPropertyEditor paddingEditor = new PaddingPropertyEditor();
        editorRegistry.registerEditor(paddingProp, paddingEditor);

        return paddingProp;
    }

    private Property getBorderProperty() {
        DefaultProperty borderProp = new DefaultProperty();
        borderProp.setName(BORDER_PARAM_NAME);
        borderProp.setDisplayName(BORDER_PARAM_NAME);
        borderProp.setType(ro.nextreports.engine.band.Border.class);
        borderProp.setValue(bandElement.getBorder());
        BorderPropertyEditor borderEditor = new BorderPropertyEditor();
        editorRegistry.registerEditor(borderProp, borderEditor);

        return borderProp;
    }

    public void setBandElement(BandElement bandElement) {
        this.bandElement = bandElement;
        List<Property> props = getFilteredProperties();
        setProperties(props.toArray(new Property[props.size()]));
    }


}
