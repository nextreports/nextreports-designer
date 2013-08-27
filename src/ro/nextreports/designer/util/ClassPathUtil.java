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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import ro.nextreports.designer.util.file.DirectoryFilter;
import ro.nextreports.designer.util.file.JarFilter;


/**
 * @author Decebal Suiu
 */
public class ClassPathUtil {

	private static final Class[] PARAMETERS = new Class[] { URL.class };

	public static void addClasses(String directory) throws IOException {
		addClasses(new File(directory));
	}

	public static void addClasses(File directory) throws IOException {
		addToClassPath(directory.toURL());
	}

	public static void addJar(String file) throws IOException {
		addJar(new File(file));
	}

	public static void addJar(File file) throws IOException {
		addToClassPath(file.toURL());
	}

	public static void addJars(String directory) throws IOException {
	    addJars(new File(directory));
	}

	public static void addJars(File directory) throws IOException {
		File dir = directory.getAbsoluteFile();

		List<String> jars = new ArrayList<String>();
		getJars(jars, dir);

		for (String jar : jars) {
			File jarFile = new File(dir, jar);
			System.out.println("Adding '" + jarFile + "' to the class loader path.");
			addJar(jarFile);
		}
	}

	public static void addToClassPath(URL url) throws IOException {
		URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysClass = URLClassLoader.class;

		try {
			Method method = sysClass.getDeclaredMethod("addURL", PARAMETERS);
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { url });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Could not add '" + url + "' to system classloader");
		}
	}

	private static void getJars(List<String> jars, File file) {
		JarFilter jarFilter = new JarFilter();
		DirectoryFilter directoryFilter = new DirectoryFilter();

		if (file.exists() && file.isDirectory() && file.isAbsolute()) {
			String[] jarList = file.list(jarFilter);
			for (int i = 0; (jarList != null) && (i < jarList.length); ++i) {
				jars.add(jarList[i]);
			}

			String[] directories = file.list(directoryFilter);
			for (int i = 0; (directories != null) && (i < directories.length); ++i) {
				getJars(jars, new File(file, directories[i]));
			}
		}
	}

}
