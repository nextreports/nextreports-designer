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

import javax.swing.*;
import java.util.prefs.Preferences;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 11, 2006
 * Time: 3:12:26 PM
 */
public class ReporterPreferencesManager {

    private static final String NO_MAIN_CLASS_MSG = "main class cannot be null";
    private static final String NO_USERNAME_MSG = "username cannot be null";
    private static final String LAF_KEY = "L&F";
    private static final String LAF_THEME_KEY = "L&F theme";
    private static final String BOUNDS_KEY = "bounds";
    public static final String JAR_PATH_KEY = "JAR_PATH";
    public static final String TEMPLATE_PATH_KEY = "TEMPLATE_PATH";
    public static final String IMAGE_PATH_KEY = "IMAGE_PATH";
    public static final String SHOW_AT_STARTUP = "SHOW_STARTUP";
    public static final String LAST_SERVER = "LAST_SERVER";
    public static final String SERVER_AUTH = "SERVER_AUTH";
    public static final String REMEMBER_AUTH = "REMEMBER_AUTH";
    public static final String NEXT_REPORT_SPATH = "NEXT_REPORT_SPATH";
    public static final String NEXT_REPORT_EXPORT_PATH = "NEXT_REPORT_EXPORT_PATH";
    public static final String NEXT_REPORT_IMPORT_PATH = "NEXT_REPORT_IMPORT_PATH";
    public static final String INIT_DATE = "INIT_DATE";
    public static final String SURVEY_DAY = "SURVEY_DAY";
    private Class mainClass;
    private String userName;
    private String defaultLaf;
    private String defaultLafTheme;
    private static ReporterPreferencesManager instance;

    public static synchronized ReporterPreferencesManager getInstance() {
        if (instance == null) {
            instance = new ReporterPreferencesManager();
        }
        return instance;
    }

    private ReporterPreferencesManager() {
    }

    public Class getMainClass() {
        return mainClass;
    }

    public void setMainClass(Class mainClass) {
        this.mainClass = mainClass;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDefaultLookAndFeel() {
        return (defaultLaf != null) ? defaultLaf : UIManager.getSystemLookAndFeelClassName();
    }

    public void setDefaultLookAndFeel(String defaultLaf) {
        this.defaultLaf = defaultLaf;
    }

    public String getDefaultLookAndFeelTheme() {
        return defaultLafTheme;
    }

    public void setDefaultLookAndFeelTheme(String defaultLafTheme) {
        this.defaultLafTheme = defaultLafTheme;
    }

    public String loadParameter(String key) {
        if (mainClass == null) {
            throw new IllegalStateException(NO_MAIN_CLASS_MSG);
        }
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(mainClass).node(userName);
        return prefs.get(key, null);
    }

    public void storeParameter(String key, String path) {
        if (mainClass == null) {
            throw new IllegalStateException(NO_MAIN_CLASS_MSG);
        }
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(mainClass).node(userName);
        prefs.put(key, path);
    }

    public String loadLookAndFeelClassName() {
        if (mainClass == null) {
            throw new IllegalStateException(NO_MAIN_CLASS_MSG);
        }
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(mainClass).node(userName);
        return prefs.get(LAF_KEY, getDefaultLookAndFeel());
    }

    public void storeLookAndFeelClassName(String laf) {
        if (mainClass == null) {
            throw new IllegalStateException(NO_MAIN_CLASS_MSG);
        }
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(mainClass).node(userName);
        prefs.put(LAF_KEY, laf);
    }

    public String loadLookAndFeelThemeClassName() {
        if (mainClass == null) {
            throw new IllegalStateException(NO_MAIN_CLASS_MSG);
        }
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(mainClass).node(userName);
        return prefs.get(LAF_THEME_KEY, getDefaultLookAndFeelTheme());
    }

    public void storeLookAndFeelThemeClassName(String theme) {
        if (mainClass == null) {
            throw new IllegalStateException(NO_MAIN_CLASS_MSG);
        }
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(mainClass).node(userName);
        prefs.put(LAF_THEME_KEY, theme);
    }

    public Rectangle loadBoundsForWindow(Class cls) {
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(cls).node(userName);
        return (Rectangle) bytes2Object(prefs.getByteArray(getKey(cls, BOUNDS_KEY), null));
    }

    public void storeBoundsForWindow(Class cls, Rectangle rect) {
        if (userName == null) {
            throw new IllegalStateException(NO_USERNAME_MSG);
        }
        Preferences prefs = Preferences.userNodeForPackage(cls).node(userName);
        prefs.putByteArray(getKey(cls, BOUNDS_KEY), object2Bytes(rect));
    }

    private String getKey(Class cls, String append) {
        int dotIndex = cls.getName().lastIndexOf('.');
        String shortClsName = cls.getName().substring(dotIndex + 1);
        return shortClsName + " " + append;
    }

    private static byte[] object2Bytes(Object o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            return baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static Object bytes2Object(byte raw[]) {
        if (raw == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(raw);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
