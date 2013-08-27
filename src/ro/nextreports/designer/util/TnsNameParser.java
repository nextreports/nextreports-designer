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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.text.Collator;

/**
 * User: mihai.panaitescu
 * Date: 22-Feb-2010
 * Time: 16:41:02
 */
public class TnsNameParser {

    private static final Log LOG = LogFactory.getLog(TnsNameParser.class);

    public static List<String> getTnsDataSources(String path) {
        List<String> result = new ArrayList<String>();
        if (path == null) {
            return result;
        }
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(path, "tnsnames.ora")));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) != -1) {
                fileData.append(buf, 0, numRead);
            }

            Pattern pattern = Pattern.compile("[\\n][\\s]*([^\\(][\\w$#]+)[\\s]*=[\\s]*\\(");
            Matcher matcher = pattern.matcher(fileData.toString());
            while (matcher.find()) {
                result.add(matcher.group(1));                
            }
            Collections.sort(result,  new Comparator<String>() {
            public int compare(String s1, String s2) {
                return Collator.getInstance().compare(s1, s2);
            }
        });
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
   
}
