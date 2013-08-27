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
 * Date: 02-Dec-2009
 * Time: 13:00:29
 */
public class ImageFilter extends FileFilter implements FilenameFilter {

    private String BMP = ".bmp";
    private String JPG = ".jpg";
    private String PNG = ".png";
    private String GIF = ".gif";
    private String TIFF  = ".tiff";

    public boolean accept(File file) {
    	if (file.isDirectory()) {
    		return true;
    	}

        // perform a case insensitive check.
        String s = file.getName().toLowerCase();
        return (s.endsWith(BMP) || s.endsWith(JPG) || s.endsWith(PNG) ||
            s.endsWith(GIF) || s.endsWith(TIFF) );
                    
    }

    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }

	@Override
	public String getDescription() {
		return "Image filter";
	}
}
