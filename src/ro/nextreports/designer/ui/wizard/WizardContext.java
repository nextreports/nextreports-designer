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
package ro.nextreports.designer.ui.wizard;

import java.util.HashMap;
import java.util.Map;

/**
 * An instance of this class is maintained by the Wizard.
 * WizardPanels can store information here for use
 * in other WizardPanels, or for the overal purpose of the wizard.
 *
 * @author Decebal Suiu
 */
public class WizardContext {

    private Map<String, Object> attributes;

    public WizardContext() {
    	attributes = new HashMap<String, Object>();
    }

    /**
     * Sets an attribute.
     *
     * @param key an String that is the key for this attribute
     * @param value an Object this is the value of this attribute
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Gets an attribute.
     *
     * @param key an String used to retrieve information from this context
     * @return an Object
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return WizardContext.class.getName() + attributes.toString();
    }

}
