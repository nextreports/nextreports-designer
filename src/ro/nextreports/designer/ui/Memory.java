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
package ro.nextreports.designer.ui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ro.nextreports.designer.util.I18NSupport;


/**
 * @author Decebal Suiu
 */
public class Memory {

    public static void showMemoryDialog(JFrame parent) {
		Runtime rt = Runtime.getRuntime();
		int before = (int) (rt.freeMemory() / 1024);
		System.gc();
		int after = (int) (rt.freeMemory() / 1024);
		int total = (int) (rt.totalMemory() / 1024);

		Object[] message = new Object[3];
		message[0] = I18NSupport.getString("garbage.collection.released", new Integer(after - before));
		message[1] = I18NSupport.getString("memory.used", new Integer(total - after));
		message[2] = I18NSupport.getString("memory.total", new Integer(total));

		JOptionPane.showMessageDialog(parent, message,
                I18NSupport.getString("memory.status"),
			JOptionPane.INFORMATION_MESSAGE);
	}

}
