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
package ro.nextreports.designer.grid;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * @author Decebal Suiu
 */
public class GridTransferable implements Transferable {

    public static final DataFlavor GRID_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + 
    		"; class=com.asf.swing.grid.GridTransferable", "Grid Data Flavor");

	private DataFlavor flavors[] =  { GRID_FLAVOR };
	private String data;
	
	public GridTransferable(String data) {
		this.data = data;
	}
	
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (isDataFlavorSupported(flavor)) {
			return data;
		}
		
		return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (DataFlavor tmp : flavors) {
			if (tmp.equals(flavor)) {
				return true;
			}
		}
		
		return false;
	}

}
