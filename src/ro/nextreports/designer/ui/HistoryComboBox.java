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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author Decebal Suiu
 */
public class HistoryComboBox extends JComboBox {

	public static final int MAX_HISTORY_LENGTH = 30;

	public HistoryComboBox() {
		super();
		setEditable(true);
	}

	public void add(String item) {
		removeItem(item);
		insertItemAt(item, 0);
		setSelectedItem(item);
		if (getItemCount() > MAX_HISTORY_LENGTH) {
			removeItemAt(getItemCount() - 1);
		}
	}
	
	public void load(String fileName) {
		// for now I deserialize the combo model from a file
		try {
			if (getItemCount() > 0) {
				removeAllItems();
			}
			
			File file = new File(fileName);
			if (!file.exists()) {
				return;
			}
			
			FileInputStream fileStream = new FileInputStream(file);
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			Object object = objectStream.readObject();
			if (object instanceof ComboBoxModel) {
				setModel((ComboBoxModel) object);
			}

			objectStream.close();
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save(String fileName) {
		// for now I serialize the combo model from a file
		try {
			FileOutputStream fileStream = new FileOutputStream(fileName);
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(getModel());
			objectStream.flush();
			objectStream.close();
			fileStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
