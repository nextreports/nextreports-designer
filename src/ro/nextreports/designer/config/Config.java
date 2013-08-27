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
package ro.nextreports.designer.config;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Configuration object. This class extends <code>Properties</code>, adding
 * convenience methods for storing and retrieving properties as strings,
 * integers, booleans, lists. All values are stored
 * internally as strings, so that persisting the object will produce a
 * human-readable and - modifiable file.
 *
 * @author Decebal Suiu
 */
public class Config extends Properties {

    private static final String DEFAULT_DESCRIPTION = "Configuration Parameters";

    /**
     * The description for this set of configuration parameters.
     */
    protected String description = DEFAULT_DESCRIPTION;

    /**
     * Construct a new <code>Config</code> with a default description.
     */
    public Config() {
        this(DEFAULT_DESCRIPTION);
    }

    /**
     * Construct a new <code>Config</code> object.
     *
     * @param description
     *            The description of the configuration parameters that will be
     *            stored in this object (one line of text).
     */
    public Config(String description) {
        this.description = description;
    }

    /**
     * Get the description for this set of configuration parameters.
     *
     * @return The description.
     * @see #setDescription
     */
    public String getDescription() {
        return (description);
    }

    /**
     * Set the description for this set of configuration parameters.
     *
     * @param description
     *            The new description, or <b>null</b> if a default description
     *            should be used.
     * @see #getDescription
     */
    public void setDescription(String description) {
        this.description = (description == null ? DEFAULT_DESCRIPTION
                : description);
    }

    /**
     * Look up a <code>String</code> property.
     *
     * @param key
     *            The name of the property.
     * @return The property's value, as a <code>String</code>, or <b>null</b>
     *         if a property with the specified name does not exist.
     * @see #putString
     */
    public String getString(String key) {
        Object object = get(key);
        if (object == null) {
            return null;
        }
        return ((String)object );
    }

    /**
     * Look up a <code>String</code> property.
     *
     * @param key
     *            The name of the property.
     * @param defaultValue
     *            The default value to return.
     * @return The property's value, as a <code>String</code>, or
     *         <code>defaultValue</code> if a property with the specified name
     *         does not exist.
     * @see #putString
     */
    public String getString(String key, String defaultValue) {
        String value = (String) get((Object) key);
        if (value == null) {
            value = defaultValue;
        }

        return (value);
    }

    /**
     * Store a <code>String</code> property.
     *
     * @param key
     *            The name of the property.
     * @param value
     *            The value of the property.
     * @return The old value associated with this key, or <b>null</b> if there
     *         was no previous value.
     * @see #getString
     */
    public String putString(String key, String value) {
        String oldValue = getString(key);
        put(key, value);
        return (oldValue);
    }

    /**
     * Look up an integer property.
     *
     * @param key
     *            The name of the property.
     * @return The property's value, as an <code>int</code>, or <b>0</b> if
     *         a property with the specified name does not exist.
     * @see #putInt
     */
    public int getInt(String key) {
        return (getInt(key, 0));
    }

    /**
     * Look up an integer property.
     *
     * @param key
     *            The name of the property.
     * @param defaultValue
     *            The default value to return.
     * @return The property's value, as an <code>String</code>, or
     *         <code>defaultValue</code> if a property with the specified name
     *         does not exist.
     * @see #putInt
     */
    public int getInt(String key, int defaultValue) {
        String value = (String) get(key);
        if (value == null) {
            return (defaultValue);
        }

        return Integer.parseInt(value);
    }

    /**
     * Store an integer property.
     *
     * @param key
     *            The name of the property.
     * @param value
     *            The value of the property.
     * @return The old value associated with this key, or 0 if there was no
     *         previous value.
     * @see #getInt
     */
    public int putInt(String key, int value) {
        int oldValue = getInt(key);
        put(key, String.valueOf(value));
        return (oldValue);
    }

    /**
     * Look up a long property.
     *
     * @param key
     *            The name of the property.
     * @return The property's value, as a <code>long</code>, or <b>0</b> if
     *         a property with the specified name does not exist.
     * @see #putLong
     */
    public long getLong(String key) {
        return getLong(key, 0);
    }

    /**
     * Look up a long property.
     *
     * @param key
     *            The name of the property.
     * @param defaultValue
     *            The default value to return.
     * @return The property's value, as a <code>long</code>, or
     *         <code>defaultValue</code> if a property with the specified name
     *         does not exist.
     * @see #putlong
     */
    public long getLong(String key, long defaultValue) {
        String value = (String) get(key);
        if (value == null) {
            return defaultValue;
        }

        return Long.parseLong(value);
    }

    /**
     * Store a long property.
     *
     * @param key
     *            The name of the property.
     * @param value
     *            The value of the property.
     * @return The old value associated with this key, or 0 if there was no
     *         previous value.
     * @see #getLong
     */
    public long putLong(String key, long value) {
        long old = getLong(key);
        put(key, String.valueOf(value));
        return old;
    }

    /**
     * Look up a float property.
     *
     * @param key
     *            The name of the property.
     * @return The property's value, as a <code>float</code>, or <b>0</b> if
     *         a property with the specified name does not exist.
     * @see #putFloat
     */
    public float getFloat(String key) {
        return (getFloat(key, 0));
    }

    /**
     * Look up a float property.
     *
     * @param key
     *            The name of the property.
     * @param defaultValue
     *            The default value to return.
     * @return The property's value, as a <code>float</code>, or
     *         <code>defaultValue</code> if a property with the specified name
     *         does not exist.
     * @see #putFloat
     */
    public float getFloat(String key, float defaultValue) {
        String value = (String) get(key);
        if (value == null) {
            return defaultValue;
        }

        return Float.parseFloat(value);
    }

    /**
     * Store a float property.
     *
     * @param key
     *            The name of the property.
     * @param value
     *            The value of the property.
     * @return The old value associated with this key, or 0 if there was no
     *         previous value.
     * @see #getFloat
     */
    public float putFloat(String key, float value) {
        float oldValue = getFloat(key);
        put(key, String.valueOf(value));
        return oldValue;
    }

    /**
     * Look up an boolean property.
     *
     * @param key
     *            The name of the property.
     * @return The property's value, as a <code>boolean</code>. Returns
     *         <b>false</b> if a property with the specified name does not
     *         exist.
     * @see #putBoolean
     */
    public boolean getBoolean(String key) {
        return (getBoolean(key, false));
    }

    /**
     * Look up a boolean property.
     *
     * @param key
     *            The name of the property.
     * @param defaultValue
     *            The default value to return.
     * @return The property's value, as a <b>boolean</b>, or
     *         <code>defaultValue</code> if a property with the specified name
     *         does not exist.
     * @see #putBoolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = (String) get(key);
        if (value == null) {
            return (defaultValue);
        }

        return (Boolean.valueOf(value).booleanValue());
    }

    /**
     * Store a boolean property.
     *
     * @param key
     *            The name of the property.
     * @param value
     *            The value of the property.
     * @return The old value associated with this key, or <b>false</b> if there
     *         was no previous value.
     * @see #getBoolean
     */
    public boolean putBoolean(String key, boolean value) {
        boolean oldValue = getBoolean(key);
        put(key, String.valueOf(value));
        return (oldValue);
    }

    public List getList(String key) {
        return getList(key, new ArrayList());
    }

    public List getList(String key, List defaultValue) {
        String value = (String) get(key);
        if (value == null) {
            return (defaultValue);
        }

        List<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(value, ",");
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }

        return list;
    }

    /**
     * Get a list of properties.
     *
     * @return A list of the property names as an <code>Enumeration</code>.
     */
    public Enumeration list() {
        return (keys());
    }

}
