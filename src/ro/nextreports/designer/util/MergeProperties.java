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
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 16-Jun-2009
// Time: 10:05:40

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * MergeProperties provides functionality to merge two separate .properties
 * configuration files into one while preserving user comments.
 */
public class MergeProperties {

    /** source .properties file */
    private File mergeFile;

    /** property override file  */
    private File importFile;

    /** file where final properties are saved */
    private File destFile;

    /**
     * Configures the source input file to read the source properties.
     *
     * @param file A File object representing the source .properties file to read.
     */
    public void setFile(final File file) {
        mergeFile = file;
    }

    /**
     * Configures the destination file to overwrite the properties provided
     * in the source file.
     *
     * @param file A File object representing the destination file to merge the
     *             combined properties into.
     */
    public void setImportFile(final File file) {
        importFile = file;
    }

    /**
     * Configures the destination file to write the combined properties.
     *
     * @param file A File object representing the destination file to merge the
     *             combined properties into.
     */
    public void setDestinationFile(final File file) {
        destFile = file;
    }

    /**
     * Method invoked by the ant framework to execute the action associated
     * with this task.
     *
     * @throws Exception if cannot read files
     */
    public void execute() throws Exception {
        // validate provided parameters
        validate();

        // read source .properties
        List<FileContents> newFile = new ArrayList<FileContents>();
        List source = loadFile(mergeFile, newFile);
        List merge = loadFile(importFile, newFile);

        // iterate through source, and write to file with updated properties
        writeFile(newFile);
    }

    public void execute(Map<String, String> props) throws Exception {
        // validate provided parameters
        validate();

        // read source .properties
        List<FileContents> newFile = new ArrayList<FileContents>();
        List source = loadFile(mergeFile, newFile);
        List merge = loadProps(props, newFile);

        // iterate through source, and write to file with updated properties
        writeFile(newFile);
    }

    /**
     * Validate that the task parameters are valid.
     *
     * @throws Exception if parameters are invalid
     */
    private void validate() throws Exception {
        if (importFile != null) {
            if (!importFile.canRead()) {
                final String message = "Unable to read from " + importFile + ".";
                throw new Exception(message);
            }
        }
        if (!mergeFile.canRead()) {
            final String message = "Unable to read from " + mergeFile + ".";
            throw new Exception(message);
        }
        if (!destFile.canWrite()) {
            try {
                destFile.createNewFile();
            }
            catch (IOException e) {
                throw new Exception("Unable to write to " + destFile + ".");
            }
        }
    }

    /**
     * Reads the contents of the selected file and returns them in a List that
     * contains String objects that represent each line of the file in the
     * order that they were read.
     *
     * @param file         The file to load the contents into a List.
     * @param fileContents list of file contents
     * @return a List of the contents of the file where each line of the file
     *         is stored as an individual String object in the List in the same
     *         physical order it appears in the file.
     * @throws Exception An exception can occur if the version file is corrupted or the
     *                   process is in someway interrupted
     */
    private List loadFile(File file, List<FileContents> fileContents) throws Exception {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String curLine;
            String property;
            String comment = "";
            try {
                while ((curLine = in.readLine()) != null) {
                    curLine = curLine.trim();
                    if (curLine.startsWith("#")) {
                        comment += curLine + "\r\n";
                    } else if (curLine.indexOf("=") > 0) {
                        while (curLine.endsWith("\\")) {
                            curLine += "\r\n" + in.readLine().trim();
                        }
                        FileContents fc = new FileContents();
                        fc.name = curLine.substring(0, curLine.indexOf("="));
                        fc.value = curLine;
                        fc.comment = comment;
                        comment = "";
                        if (fileContents.contains(fc)) {
                            FileContents existing = getExistingElement(fileContents, fc.name);
                            if (existing != null) {
                                existing.value = fc.value;
                            } else {
                                fileContents.add(fc);
                            }
                        } else {
                            fileContents.add(fc);
                        }
                    }
                }
            } catch (Exception e) {
                throw new Exception("Could not read file:" + file, e);
            } finally {
                in.close();
            }
        } catch (IOException IOe) {
            // had an exception trying to open the file
            throw new Exception("Could not read file:" + file, IOe);
        }
        return fileContents;
    }

    private List loadProps(Map<String, String> props, List<FileContents> fileContents) throws Exception {

        String curLine;
        String property;
        String comment = "";

        for (String key : props.keySet()) {
            FileContents fc = new FileContents();
            fc.name = key;
            fc.value = key + "=" + props.get(key);
            fc.comment = comment;
            if (fileContents.contains(fc)) {
                FileContents existing = getExistingElement(fileContents, fc.name);
                if (existing != null) {
                    existing.value = fc.value;
                } else {
                    fileContents.add(fc);
                }
            } else {
                fileContents.add(fc);
            }
        }
        return fileContents;
    }

    private FileContents getExistingElement(List<FileContents> list, String name) {
        for (FileContents fc : list) {
            if (fc.getName().equals(name)) {
                return fc;
            }
        }
        return null;
    }

    /**
     * Writes the merged properties to a single file while preserving any
     * comments.
     *
     * @param fileContents list of file contents
     * @throws Exception if the destination file can't be created
     */
    private void writeFile(List fileContents) throws Exception {
        Iterator iterate = fileContents.iterator();
        try {
            FileOutputStream out = new FileOutputStream(destFile);
            PrintStream p = new PrintStream(out);
            try {
                // write original file with updated values
                while (iterate.hasNext()) {
                    FileContents fc = (FileContents) iterate.next();
                    if (fc.comment != null && !fc.comment.equals("")) {
                        p.println();
                        p.print(fc.comment);
                    }
                    p.println(fc.value);
                }
            } catch (Exception e) {
                throw new Exception("Could not write file: " + destFile, e);
            } finally {
                out.close();
            }
        } catch (IOException IOe) {
            throw new Exception("Could not write file: " + destFile, IOe);
        }
    }


    protected class FileContents {
        public String name;
        public String comment;
        public int order;
        public String value;

        public String getName() {
            return name;
        }

        public boolean equals(Object obj) {
            if (obj instanceof FileContents) {
                FileContents fc = (FileContents) obj;
                if (fc.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }    

}

