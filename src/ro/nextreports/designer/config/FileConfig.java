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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Persistent configuration object. This class extends <code>Config</code>,
 * adding convenience methods for saving a property list to a file, and reading
 * a property list from a file.
 *
 * @author Decebal Suiu
 */
public class FileConfig extends Config {

    private File file;

    /**
     * Construct a new <code>FileConfig</code>. Note that the object has to
     * be initialized by explicitly loading the properties via a call to
     * <code>load()</code>; the constructor does not preload the file.
     *
     * @param file
     *            The <code>File</code> object for this configuration file.
     * @param comment
     *            The top-of-file comment (one line).
     */
    public FileConfig(File file, String comment) {
        super(comment);
        this.file = file;
    }

    /**
     * Construct a new <code>FileConfig</code> with a default comment. Note
     * that the object has to be initialized by explicitly loading the
     * properties via a call to <code>load()</code>; the constructor does not
     * preload the file.
     *
     * @param file
     *            The <code>File</code> object for this configuration file.
     */
    public FileConfig(File file) {
        this(file, null);
    }

    /**
     * Load the configuration parameters from the file. Also fires a
     * <code>ChangeEvent</code> to notify listeners that the object
     * (potentially) changed.
     *
     * @exception java.io.FileNotFoundException
     *                If the associated file does not exist.
     * @exception java.io.IOException
     *                If the file could not be read.
     * @see #store
     */
    public void load() throws IOException {
        FileInputStream fin = new FileInputStream(file);
        super.load(fin);
        fin.close();
    }

    /**
     * Save the configuration parameters to the file.
     *
     * @exception java.io.IOException
     *                If the file could not be written.
     * @see #load
     */
    public void store() throws IOException {
        FileOutputStream fout = new FileOutputStream(file);
        super.store(fout, description);
        fout.close();
    }

    /**
     * Get the absolute path of this configuration file.
     */
    public String getPath() {
        return (file.getAbsolutePath());
    }

}
