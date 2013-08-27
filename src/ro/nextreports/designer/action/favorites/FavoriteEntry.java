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

import ro.nextreports.engine.util.xstream.XStreamable;

public class FavoriteEntry implements XStreamable, Serializable {
	
	private static final long serialVersionUID = 7852106754854458878L;

	private String type; // report, chart
	private String dataSource;
	private String path;
	private String name;

	public FavoriteEntry() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FavoriteEntry fav = (FavoriteEntry) o;

		if (type != null ? !type.equals(fav.type) : fav.type != null) return false;
		if (dataSource != null ? !dataSource.equals(fav.dataSource) : fav.dataSource != null) return false;
		if (path != null ? !path.equals(fav.path) : fav.path != null) return false;
		if (name != null ? !name.equals(fav.name) : fav.name != null) return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "FavoriteEntry [type=" + type + ", dataSource=" + dataSource + ", path=" + path + ", name=" + name + "]";
	}		

}
