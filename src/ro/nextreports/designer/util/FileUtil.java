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

import ro.nextreports.engine.Report;
import ro.nextreports.engine.ReportLayout;
import ro.nextreports.engine.util.ReportUtil;
import ro.nextreports.engine.band.Band;
import ro.nextreports.engine.band.BandElement;
import ro.nextreports.engine.band.BarcodeBandElement;
import ro.nextreports.engine.band.ChartBandElement;
import ro.nextreports.engine.band.ImageBandElement;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jdesktop.jdic.desktop.Desktop;

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.LayoutHelper;
import ro.nextreports.designer.action.report.layout.export.ExportAction;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 18, 2006
 * Time: 11:43:06 AM
 */
public class FileUtil {
		     
    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir) {        
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String file : children) {
                boolean success = deleteDir(new File(dir, file));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
        Collection<File> files = listFiles(directory, filter, recurse);
        File[] arr = new File[files.size()];
        return files.toArray(arr);
    }

    public static List<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
        // List of files / directories
        List<File> files = new ArrayList<File>();
        // Get files / directories in the directory
        File[] entries = directory.listFiles();
        if (entries == null) {
            return files;
        }
        // Go over entries
        for (File entry : entries) {
            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }
            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }
        
        // Return collection of files
        return files;
    }

    public static void zip(List<String> fileNames, String outFileName) {
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFileName));

            // Compress the files
            for (String fileName : fileNames) {
                FileInputStream in = new FileInputStream(fileName);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(fileName));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public static void zip(List<String> fileNames, String outFileName, String withoutBase) {
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];

        try {
            // Create the ZIP file
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFileName));

            // Compress the files
            for (String fileName : fileNames) {
                FileInputStream in = new FileInputStream(fileName);

                // Add ZIP entry to output stream.
                if (withoutBase != null) {
                	fileName = fileName.substring(withoutBase.length() + 1);
                }
                out.putNextEntry(new ZipEntry(fileName));

                // Transfer bytes from the file to the ZIP file
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Complete the entry
                out.closeEntry();
                in.close();
            }

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void unzip(String filename, String destination) {
        ZipInputStream zipinputstream;
		try {
			zipinputstream = new ZipInputStream(new FileInputStream(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
        unzip(zipinputstream, destination);
    }
    
    public static void unzip(ZipInputStream zipinputstream, String destination) {
        try {
            byte[] buf = new byte[1024];
            ZipEntry zipentry = zipinputstream.getNextEntry();
            while (zipentry != null) {
                String entryName = zipentry.getName();                
                File newFile = new File(entryName);

                String path = destination + File.separator + entryName;

                // take care to create the directories
                String dirs = path.substring(0, path.lastIndexOf(File.separator));
                new File(dirs).mkdirs();

				if (!zipentry.isDirectory()) {
					FileOutputStream fileoutputstream = new FileOutputStream(path);

					int n;
					while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
						fileoutputstream.write(buf, 0, n);
					}

					fileoutputstream.close();
					zipinputstream.closeEntry();
				} else {
					new File(path).mkdirs();
				}
                zipentry = zipinputstream.getNextEntry();
            }
            zipinputstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copy(File source, File dest) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {        	
            in = new FileInputStream(source);
            out = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static void copyToDir(File source, File dest) throws IOException {
        copyToDir(source, dest, false);
    }

    public static void copyToDir(File source, File dest, boolean overwrite) throws IOException {
        File destFile = new File(dest.getAbsolutePath() + File.separator + source.getName());
        if (!overwrite && destFile.exists()) {
            throw new IOException("File '" + destFile.getName() + "' already exists!");
        }
        copy(source, destFile);
    }

    // copy all files recursively from directory source to directory dest
    // dest directory must be already created!
    public static void copyDirToDir(File source, File dest) throws IOException {
        if (!source.isDirectory() || !dest.isDirectory()) {
            return;
        }
        
        List<File> files = listFiles(source, null ,true);
        String sourcePath = source.getAbsolutePath();
        String destPath = dest.getAbsolutePath();
        for (File file : files) {
            String filePath =file.getAbsolutePath();
            if (sourcePath.equals(filePath)) {
                continue;
            }
            String newPath = destPath + File.separator + filePath.substring(sourcePath.length());
            File destFile = new File(newPath);
            if (file.isDirectory()) {
                destFile.mkdirs();
            } else {
                copy(file, destFile);
            }
        }
    }
   
    public static ArrayList<String> tail(String fileName, int lineCount) {
        return tail(fileName, lineCount, 2000);
    }

    /**
     * Given a byte array this method:
     * a. creates a String out of it
     * b. reverses the string
     * c. extracts the lines
     * d. characters in extracted line will be in reverse order,
     * so it reverses the line just before storing in Vector.
     * <p/>
     * On extracting required numer of lines, this method returns TRUE,
     * Else it returns FALSE.
     *
     * @param bytearray  byte array
     * @param lineCount  number of lines
     * @param lastNlines array of lines
     * @return true if extracted required number of lines
     */
    private static boolean parseLinesFromLast(byte[] bytearray, int lineCount, ArrayList<String> lastNlines) {
        String lastNChars = new String(bytearray);
        StringBuffer sb = new StringBuffer(lastNChars);
        lastNChars = sb.reverse().toString();
        StringTokenizer tokens = new StringTokenizer(lastNChars, "\n");
        while (tokens.hasMoreTokens()) {
            StringBuffer sbLine = new StringBuffer(tokens.nextToken());
            lastNlines.add(sbLine.reverse().toString());
            if (lastNlines.size() == lineCount) {
                return true;//indicates we got 'lineCount' lines
            }
        }
        
        return false; //indicates didn't read 'lineCount' lines
    }

    /**
     * Reads last N lines from the given file. File reading is done in chunks.
     * <p/>
     * Constraints:
     * 1 Minimize the number of file reads -- Avoid reading the complete file
     * to get last few lines.
     * 2 Minimize the JVM in-memory usage -- Avoid storing the complete file
     * info in in-memory.
     * <p/>
     * Approach: Read a chunk of characters from end of file. One chunk should
     * contain multiple lines. Reverse this chunk and extract the lines.
     * Repeat this until you get required number of last N lines. In this way
     * we read and store only the required part of the file.
     * <p/>
     * 1 Create a RandomAccessFile.
     * 2 Get the position of last character using (i.e length-1). Let this be curPos.
     * 3 Move the cursor to fromPos = (curPos - chunkSize). Use seek().
     * 4 If fromPos is less than or equal to ZERO then go to step-5. Else go to step-6
     * 5 Read characters from beginning of file to curPos. Go to step-9.
     * 6 Read 'chunksize' characters from fromPos.
     * 7 Extract the lines. On reading required N lines go to step-9.
     * 8 Repeat step 3 to 7 until
     * a. N lines are read.
     * OR
     * b. All lines are read when num of lines in file is less than N.
     * Last line may be a incomplete, so discard it. Modify curPos appropriately.
     * 9 Exit. Got N lines or less than that.
     *
     * @param fileName  file name
     * @param lineCount number of last lines
     * @param chunkSize chunk size
     * @return array of lines
     */
    public static ArrayList<String> tail(String fileName, int lineCount, int chunkSize) {
        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "r");
            ArrayList<String> lastNlines = new ArrayList<String>();
            int delta = 0;
            long curPos = raf.length() - 1;
            if (curPos < 0){
                return lastNlines;
            }
            
            long fromPos;
            byte[] bytearray;
            while (true) {
                fromPos = curPos - chunkSize;
                //System.out.println(curPos);
                //System.out.println(fromPos);
                if (fromPos <= 0) {
                    raf.seek(0);
                    bytearray = new byte[(int) curPos];
                    raf.readFully(bytearray);
                    parseLinesFromLast(bytearray, lineCount, lastNlines);
                    break;
                } else {
                    raf.seek(fromPos);
                    bytearray = new byte[chunkSize];
                    raf.readFully(bytearray);
                    if (parseLinesFromLast(bytearray, lineCount, lastNlines)) {
                        break;
                    }
                    delta = lastNlines.get(lastNlines.size() - 1).length();
                    lastNlines.remove(lastNlines.size()-1);
					curPos = fromPos + delta;
				}
			}
            
            return lastNlines;
		} catch(Exception e) {
			e.printStackTrace();
            return null;
        }
	}

    public static String getEscapedPath(String filePath, String separator) {
        if ("".equals(filePath.trim())) {
            return filePath;
        }
        
        String[] array = filePath.split("\\"+separator);
        StringBuilder sb = new StringBuilder();
        for (int i=0, size=array.length; i<size; i++) {
            sb.append(array[i]);
            if (i < size-1) {
                sb.append("\\");
                sb.append(File.separator);
            }
        }
        
        return sb.toString();
    }
    
    public static void copyTemplateToClasspath(Report report) throws IOException {
        copyTemplate(report, new File(ExportAction.REPORTS_DIR));
    }
    
    public static void copyTemplate(Report report, File directory) throws IOException {
        ReportLayout layout = LayoutHelper.getReportLayout();
        if (report != null) {
            // run report from tree (without open)
            layout = report.getLayout();
        }
        String templateName = layout.getTemplateName();
		if ((templateName != null) && !"".equals(templateName.trim())) {
			String fromPath = Globals.getCurrentReportAbsolutePath();
			if (fromPath == null) {
				fromPath = Globals.getTreeReportAbsolutePath();
			}						
			FileUtil.copyToDir(new File(new File(fromPath).getParentFile().getAbsolutePath() + File.separator + templateName),
						directory, true);			
		}       
    }

    public static void copyImagesToClasspath(Report report) throws IOException {
        copyImages(report, new File(ExportAction.REPORTS_DIR));
    }

    public static void copyImages(Report report, File directory) throws IOException {
        ReportLayout layout = LayoutHelper.getReportLayout();
        if (report != null) {
            // run report from tree (without open)
            layout = report.getLayout();
        }
        List<Band> bands = layout.getBands();
        for (Band band : bands) {
            for (int i=0, rows = band.getRowCount(); i<rows; i++) {
                List<BandElement> list = band.getRow(i);
                for (BandElement be : list) {
                    if ((be instanceof ImageBandElement) && !(be instanceof ChartBandElement) && !(be instanceof BarcodeBandElement)) {
                        String image = ((ImageBandElement)be).getImage();
                        if ((image == null) || "".equals(image.trim())) {
                        	continue;
                        }
                        String fromPath = Globals.getCurrentReportAbsolutePath();
                        if (fromPath == null) {
                            fromPath = Globals.getTreeReportAbsolutePath();
                        }
                        // catch exception here to allow other found images to be copied 
                        try {
                        	FileUtil.copyToDir(
                                new File(new File(fromPath).getParentFile().getAbsolutePath() + File.separator + image ),
                                directory, true);
                        } catch (IOException ex) {
                        	ex.printStackTrace();
                        }
                    }
                }
            }
        }
        if (layout.getBackgroundImage() != null) {
        	String fromPath = Globals.getCurrentReportAbsolutePath();
            if (fromPath == null) {
                fromPath = Globals.getTreeReportAbsolutePath();
            }
        	try {
            	FileUtil.copyToDir(
                    new File(new File(fromPath).getParentFile().getAbsolutePath() + File.separator + layout.getBackgroundImage() ),
                    directory, true);
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
    }

    public static void copyImages(File reportFile, File directory) {
        try {
            Report report = ReportUtil.loadReport(new FileInputStream(reportFile));
            List<String> images = ReportUtil.getStaticImages(report);
            String prefix = reportFile.getParentFile().getAbsolutePath();
            for (String image : images) {
                File f = new File(prefix + File.separator +  image);
                if  (f.exists()) {
                    FileUtil.copyToDir(f, directory);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteImages(Report report) {
        ReportLayout layout = LayoutHelper.getReportLayout();
        if (report != null) {
            // run report from tree (without open)
            layout = report.getLayout();
        }
        List<Band> bands = layout.getBands();
        for (Band band : bands) {
            for (int i=0, rows = band.getRowCount(); i<rows; i++) {
                List<BandElement> list = band.getRow(i);
                for (BandElement be : list) {
                    if (be instanceof ImageBandElement) {
                        String image = ((ImageBandElement)be).getImage();
                        deleteImage(image);
                    }
                }
            }
        }
        if (layout.getBackgroundImage() != null) {
        	deleteImage(layout.getBackgroundImage());
        }
    }

    public static void deleteImage(String name) {
        String fromPath = Globals.getCurrentReportAbsolutePath();
        if (fromPath == null) {
            fromPath = Globals.getTreeReportAbsolutePath();
        }
        //System.out.println("*** path="+new File(fromPath).getParentFile().getAbsolutePath() + File.separator + name);
        new File(new File(fromPath).getParentFile().getAbsolutePath() + File.separator + name).delete();
    }

    public static void createFile(String filePath, byte[] content) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(content);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
    
    public static void openFile(final String fileName, Class classFile) {
        Runnable openFileRunnable = new Runnable() {
            public void run() {
            	String name = "";
                try {
                	name = new File(fileName).getCanonicalPath();
                    Desktop.open(new File(name));
                } catch (Throwable t) {
                	// jdic is built for 32 bit platforms and an error will rise for 64 bit platforms                	
                	// try to open with java Desktop                	
                	try {
						java.awt.Desktop.getDesktop().open(new File(name));
					} catch (Throwable t1) {							
	                    Show.error(I18NSupport.getString("file.open.error", name)+ "\n" + t1.getMessage());
					}                	
                }
            }
        };
        new Thread(openFileRunnable, "NEXT : " + classFile.getSimpleName()).start();
    }
    
    public static void openUrl(final String url,  Class classFile) {
        Runnable openFileRunnable = new Runnable() {

            public void run() {
                try {
                    Desktop.browse(new URL(url));
                } catch (Throwable t) {
                	// jdic is built for 32 bit platforms and an error will rise for 64 bit platforms                	
                	// try to open with java Desktop                	
                	try {
						java.awt.Desktop.getDesktop().browse(new URI(new URL(url).toString()));
					} catch (Throwable t1) {									
						Show.error(I18NSupport.getString("url.open.error", url)+ "\n" + t.getMessage());
					}        
                }
            }

        };
        new Thread(openFileRunnable, "NEXT : " + classFile.getSimpleName()).start();
    }

    public static String convertPathToSystemSeparators(String p) {
    	if (p == null) {
    		return null;
    	}
    	p = p.replaceAll("\\\\", "/");
    	String[] array = p.split("/");;    	
    	StringBuilder sb = new StringBuilder();
    	for (int i=0, size=array.length; i<size; i++) {
    		if ("".equals(array[i])) {
    			continue;
    		}
    		sb.append(array[i]);
    		if (i < size-1) {
    			sb.append(File.separator);
    		}
    	}    	
    	return sb.toString();
    }    
	
	public static String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			return fileData.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}		
	}
	
	public static String getBaseFileName(String path) {
		File file = new File(path);
		String name = file.getName();
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return name;
		} else {
			return name.substring(0,index);
		}
            
	}
    
}




