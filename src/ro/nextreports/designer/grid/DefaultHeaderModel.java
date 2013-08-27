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

import javax.swing.SizeSequence;
import javax.swing.SwingConstants;

/**
 * Default implementation of <code>HeaderModel</code>. Uses
 * <code>javax.swing.SizeSequence</code> as underlying data structure.
 * 
 * @author Decebal Suiu
 */
public class DefaultHeaderModel extends AbstractHeaderModel implements
		ResizableGrid {
	
	private SizeSequence delegate;
	private int count;
	private int defaultSize;
	private int orientation;

	public DefaultHeaderModel(int numEntries, int defaultSize, int orientation) {
		delegate = new SizeSequence(numEntries, defaultSize);
		count = numEntries;
		this.defaultSize = defaultSize;
		this.orientation = orientation;
	}

	public int getSize(int index) {
		return delegate.getSize(index);
	}

    // for an index outside the model return the last index
    public int getIndex(int position) {
        int index = delegate.getIndex(position);
        if (index >= count) {
            index = count - 1;
        }
        
        return index;
	}

	public int getPosition(int index) {
		return delegate.getPosition(index);
	}

	public int getCount() {
		return count;
	}

	public void setSize(int index, int size) {
		delegate.setSize(index, size);
		fireIndexChanged(index);
	}

	public int getTotalSize() {
		int totalSize = 0;
		for (int i = 0; i < count; i++) {
			totalSize += delegate.getSize(i);
		}
		
		return totalSize;
	}

	public void insertRows(int row, int rowCount) {
		if (orientation == SwingConstants.VERTICAL) {
			delegate.insertEntries(row, rowCount, defaultSize);
			count += rowCount;
		}
	}

	public void removeRows(int row, int rowCount) {
		if (orientation == SwingConstants.VERTICAL) {
			delegate.removeEntries(row, rowCount);
			count -= rowCount;
		}
	}

	public void insertColumns(int column, int columnCount) {
		if (orientation == SwingConstants.HORIZONTAL) {
			delegate.insertEntries(column, columnCount, defaultSize);
			count += columnCount;
		}
	}

	public void removeColumns(int column, int columnCount) {
		if (orientation == SwingConstants.HORIZONTAL) {
			delegate.removeEntries(column, columnCount);
			count -= columnCount;
		}
	}
	
}
