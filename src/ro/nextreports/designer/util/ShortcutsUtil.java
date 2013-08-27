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
import java.io.IOException;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 13-Oct-2009
// Time: 10:54:52

//
public class ShortcutsUtil {

    private static Properties shortcuts;

    static {
        loadShortcuts();
    }

    private static void loadShortcuts() {
        if (shortcuts == null) {
            shortcuts = new Properties();
            try {
                shortcuts.load(ImageUtil.class.getResourceAsStream("/shortcuts.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getShortcut(String key, String defaultKey) {
        return shortcuts.getProperty(key, defaultKey);
    }

    public static Integer getMnemonic(String key, Integer defaultKey) {
        String value = shortcuts.getProperty(key, String.valueOf(defaultKey));
        return new Integer(value.charAt(0));
    }


}
