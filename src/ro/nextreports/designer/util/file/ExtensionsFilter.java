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
package ro.nextreports.designer.util.file;

import javax.swing.filechooser.FileFilter;
import java.io.FilenameFilter;
import java.io.File;

/**
 * User: mihai.panaitescu
 * Date: 12-Jan-2010
 * Time: 11:26:48
 */
public class ExtensionsFilter extends FileFilter implements FilenameFilter {

	////////////////////
	// variables

    private String[] extensions;

	////////////////////
	// constructors

    public ExtensionsFilter(String[] extensions) {
        this.extensions = extensions;
    }

	////////////////////
	// business

    /**
     * Accepts any file ending in any of the extensions. The case of the filename is ignored.
     */
    public boolean accept(File file) {
    	if (file.isDirectory()) {
    		return true;
    	}

        // perform a case insensitive check.
        for (String extension : extensions) {
            if (file.getName().toUpperCase().endsWith(extension.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }

	@Override
	public String getDescription() {
		return "extensions filter";
	}

}
