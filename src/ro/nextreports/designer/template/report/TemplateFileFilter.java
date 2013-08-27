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


import javax.swing.filechooser.FileFilter;

import ro.nextreports.designer.util.I18NSupport;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 22, 2007
 * Time: 6:09:11 PM
 */
public class TemplateFileFilter extends FileFilter {

    public static String TEMPLATE_FILE_EXT = ".ntempl";

    public boolean accept(File pathname) {

        if (pathname.isDirectory()) {
            return true;
        }

        if (pathname.getName().endsWith(TEMPLATE_FILE_EXT)) {
            return true;
        } else {
            return false;
        }
    }

    public String getDescription() {
        return I18NSupport.getString("template.file.filter");
    }
}
