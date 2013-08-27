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
package ro.nextreports.designer.action.favorites;

import java.io.Serializable;
import java.util.List;

import ro.nextreports.engine.util.xstream.XStreamable;

public class Favorites implements XStreamable, Serializable  {
		
	private static final long serialVersionUID = -5172665155925240812L;
	
	private List<FavoriteEntry> entries;
	
	public Favorites() {		
	}

	public List<FavoriteEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<FavoriteEntry> entries) {
		this.entries = entries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Favorites other = (Favorites) obj;
		if (entries == null) {
			if (other.entries != null) return false;
		} else if (!entries.equals(other.entries)) return false;
		return true;
	}		

}
