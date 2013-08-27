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

import ro.nextreports.designer.grid.event.HeaderModelListener;

/**
 * This interface captures the model for handling a component that can be
 * segmented along the horizontal or vertical axis, with each segment size being
 * separately tunable.
 * 
 * @author Decebal Suiu
 */
public interface HeaderModel {
	
	public void addHeaderModelListener(HeaderModelListener listener);

	public void removeHeaderModelListener(HeaderModelListener listener);

	/**
	 * Returns the size of the specified entry.
	 * 
	 * @param index
	 *            the index corresponding to the entry
	 * @return the size of the entry
	 */
	public int getSize(int index);

	/**
	 * Returns the index of the entry that contains the specified position.
	 * 
	 * @param position
	 *            the position of the entry
	 * @return the index of the entry that occupies the specified position
	 */
	public int getIndex(int position);

	/**
	 * Returns the start position for the specified entry.
	 * 
	 * @param index
	 *            the index of the entry whose position is desired
	 * @return the starting position of the specified entry
	 */
	public int getPosition(int index);

	/**
	 * Returns the number of entries.
	 * 
	 * @return number of entries
	 */
	public int getCount();

	/**
	 * Sets the size of the specified entry.
	 * 
	 * @param index
	 *            the index corresponding to the entry
	 * @param size
	 *            the size of the entry
	 */
	public void setSize(int index, int size);

	/**
	 * Returns the total size of the entries.
	 * 
	 * @return the total size of the entries
	 */
	public int getTotalSize();
	
}
