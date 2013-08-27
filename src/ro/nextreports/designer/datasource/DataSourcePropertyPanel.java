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
package ro.nextreports.designer.datasource;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;

import ro.nextreports.designer.util.I18NSupport;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;

public class DataSourcePropertyPanel extends PropertySheetPanel {
	
	private String SEPARATOR_NAME = "separator";
    private String SEPARATOR_PARAM_NAME = I18NSupport.getString("property.datasource.separator");
    private String FILE_EXTENSION_NAME = "fileExtension";
    private String FILE_EXTENSION_PARAM_NAME = I18NSupport.getString("property.datasource.fileExtension");
    private String SUPPRESS_HEADERS_NAME = "suppressHeaders";
    private String SUPPRESS_HEADERS_PARAM_NAME = I18NSupport.getString("property.datasource.suppressHeaders");
    private String HEADERLINE_NAME = "headerline";
    private String HEADERLINE_PARAM_NAME = I18NSupport.getString("property.datasource.headerline");
    private String COLUMN_TYPES_NAME = "columnTypes";
    private String COLUMN_TYPES_PARAM_NAME = I18NSupport.getString("property.datasource.columnTypes");
    
    private PropertyEditorRegistry editorRegistry;
    private boolean ignoreEvent;
    private boolean isInit;
    private Properties properties;
    private Properties localProperties;
    
    public DataSourcePropertyPanel(Properties properties) {
        super();
        setDescriptionVisible(false);
        setToolBarVisible(false);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        editorRegistry = (PropertyEditorRegistry) getEditorFactory();
        this.properties = properties;
        this.localProperties = properties;
        
        List<Property> props = getDataSourceProperties();
        setProperties(props.toArray(new Property[props.size()]));
    }        
    
    @Override
    public void propertyChange(PropertyChangeEvent event) {
		if (ignoreEvent) {
			return;
		}

        if (isInit) {
        	return;
        }
        
        Property prop = (Property) event.getSource();
        String propName = prop.getName();
        Object propValue = prop.getValue();
        localProperties.put(propName, propValue);
    }    
    
    private List<Property> getDataSourceProperties() {
        List<Property> props = new ArrayList<Property>();
        props.add(getFileExtensionProperty());
        props.add(getSeparatorProperty());        
        props.add(getSuppressHeadersProperty());
        props.add(getHeaderlineProperty());
        props.add(getColumnTypesProperty());
        return props;
    }
    
    private Property getSeparatorProperty() {
        DefaultProperty prop = new DefaultProperty();
        prop.setName(SEPARATOR_NAME);
        prop.setDisplayName(SEPARATOR_PARAM_NAME);
        prop.setType(String.class);
        String s = (String)properties.get(SEPARATOR_NAME);
        if (s == null) {
        	s = ",";
        }
        prop.setValue(s);               
        return prop;
    }
    
    private Property getFileExtensionProperty() {
        DefaultProperty prop = new DefaultProperty();
        prop.setName(FILE_EXTENSION_NAME);
        prop.setDisplayName(FILE_EXTENSION_PARAM_NAME);
        prop.setType(String.class);
        String s = (String)properties.get(FILE_EXTENSION_NAME);
        if (s == null) {
        	s = ".csv";
        }
        prop.setValue(s);               
        return prop;
    }
    
    private Property getSuppressHeadersProperty() {
        DefaultProperty prop = new DefaultProperty();
        prop.setName(SUPPRESS_HEADERS_NAME);
        prop.setDisplayName(SUPPRESS_HEADERS_PARAM_NAME);
        prop.setType(Boolean.class);
        Object value = properties.get(SUPPRESS_HEADERS_NAME);
        Boolean s;
        if (value instanceof String) {
        	s = Boolean.parseBoolean((String)value);
        } else {
        	s = (Boolean)value;
        }	
        if (s == null) {
        	s = false;
        }
        prop.setValue(s);               
        return prop;
    }
    
    private Property getHeaderlineProperty() {
        DefaultProperty prop = new DefaultProperty();
        prop.setName(HEADERLINE_NAME);
        prop.setDisplayName(HEADERLINE_PARAM_NAME);
        prop.setType(String.class);
        String s = (String)properties.get(HEADERLINE_NAME);
        if (s == null) {
        	s = "";
        }
        prop.setValue(s);               
        return prop;
    }
    
    private Property getColumnTypesProperty() {
        DefaultProperty prop = new DefaultProperty();
        prop.setName(COLUMN_TYPES_NAME);
        prop.setDisplayName(COLUMN_TYPES_PARAM_NAME);
        prop.setType(String.class);
        String s = (String)properties.get(COLUMN_TYPES_NAME);
        if (s == null) {
        	s = "";
        }
        prop.setValue(s);               
        return prop;
    }

	public Properties getLocalProperties() {
		return localProperties;
	}        
      
}
