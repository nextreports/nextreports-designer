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
package ro.nextreports.designer.util;

import java.util.Properties;
import java.util.LinkedHashMap;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 30-Sep-2009
// Time: 13:56:44

//
public class LinkedProperties extends Properties {

    private final LinkedHashMap map = new LinkedHashMap();

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Object put(Object key, Object value) {
        return map.put(key, value);
    }

    @Override
    public synchronized Object get(Object key) {
        return map.get(key);
    }

    @Override
    public synchronized void clear() {
        map.clear();
    }

    @Override
    public synchronized Object clone() {
        return super.clone();
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public synchronized boolean contains(Object value) {
        return containsValue(value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Enumeration elements() {
        return new IteratorEnumeration(map.values().iterator());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<Map.Entry<Object, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof LinkedProperties)) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public synchronized boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized Enumeration keys() {
        return new IteratorEnumeration(map.keySet().iterator());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set keySet() {
        return map.keySet();
    }

    @Override
    public Enumeration propertyNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void putAll(Map t) {
        map.putAll(t);
    }

    @Override
    public synchronized Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public synchronized int size() {
        return map.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProperty(String key) {
        final Object oval = get(key);
        final String sval = (oval instanceof String) ? (String) oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}
