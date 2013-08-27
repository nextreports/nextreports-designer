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
package ro.nextreports.designer.ui.tail;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jun 1, 2006
 * Time: 4:43:28 PM
 */

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

/**
 * A log file tailer is designed to monitor a log file and send notifications
 * when new lines are added to the log file. This class has a notification
 * strategy similar to a SAX parser: implement the LogFileTailerListener interface,
 * create a LogFileTailer to tail your log file, add yourself as a listener, and
 * start the LogFileTailer. It is your job to interpret the results, build meaningful
 * sets of data, etc. This tailer simply fires notifications containing new log file lines,
 * one at a time.
 */
public class LogFileTailer extends Thread {
    /**
     * How frequently to check for file changes; defaults to 5 seconds
     */
    private long sampleInterval = 5000;

    /**
     * The log file to tail
     */
    private File logfile;

    /**
     * Defines whether the log file tailer should include the entire contents
     * of the exising log file or tail from the end of the file when the tailer starts
     */
    private boolean startAtBeginning = false;

    /**
     * Is the tailer currently tailing?
     */
    private boolean tailing = false;

    /**
     * Set of listeners
     */
    private Set<LogFileTailerListener> listeners = new HashSet<LogFileTailerListener>();

    /**
     * Creates a new log file tailer that tails an existing file and checks the file for
     * updates every 5000ms
     */
    public LogFileTailer(File file) {
        setName("NEXT : "  + getClass().getSimpleName());
        this.logfile = file;
    }

    /**
     * Creates a new log file tailer
     *
     * @param file             The file to tail
     * @param sampleInterval   How often to check for updates to the log file (default = 5000ms)
     * @param startAtBeginning Should the tailer simply tail or should it process the entire
     *                         file and continue tailing (true) or simply start tailing from the
     *                         end of the file
     */
    public LogFileTailer(File file, long sampleInterval, boolean startAtBeginning) {
        setName("NEXT : "  + getClass().getSimpleName());
        this.logfile = file;
        this.sampleInterval = sampleInterval;
        this.startAtBeginning = startAtBeginning;
    }

    public void addLogFileTailerListener(LogFileTailerListener l) {
        this.listeners.add(l);
    }

    public void removeLogFileTailerListener(LogFileTailerListener l) {
        this.listeners.remove(l);
    }

    protected void fireNewLogFileLine(String line) {
        for (LogFileTailerListener listener : this.listeners) {            
            listener.newLogFileLine(line);
        }
    }

    public void stopTailing() {
        this.tailing = false;
    }

    public void run() {
        // The file pointer keeps track of where we are in the file
        long filePointer = 0;

        // Determine start point
        if (this.startAtBeginning) {
            filePointer = 0;
        } else {
            filePointer = this.logfile.length();
        }

        try {
            // Start tailing
            this.tailing = true;
            RandomAccessFile file = new RandomAccessFile(logfile, "r");
            while (this.tailing) {
                try {
                    // Compare the length of the file to the file pointer
                    long fileLength = this.logfile.length();
                    if (fileLength < filePointer) {
                        // Log file must have been rotated or deleted;
                        // reopen the file and reset the file pointer
                        file = new RandomAccessFile(logfile, "r");
                        filePointer = 0;
                    }

                    if (fileLength > filePointer) {
                        // There is data to read
                        file.seek(filePointer);
                        String line = file.readLine();
                        while (line != null) {
                            this.fireNewLogFileLine(line);
                            line = file.readLine();
                        }
                        filePointer = file.getFilePointer();
                    }

                    // Sleep for the specified interval
                    sleep(this.sampleInterval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Close the file that we are tailing
            file.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
