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
package ro.nextreports.designer.datasource;

import java.util.List;

public class TagsDTO {
	
	private List<String> tags; 
	private List<String> tagsValues;
	private String defaultPort;
	private boolean tns; 
	private boolean server;
	private String user;
	private String password; 
	
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public List<String> getTagsValues() {
		return tagsValues;
	}
	public void setTagsValues(List<String> tagsValues) {
		this.tagsValues = tagsValues;
	}
	public String getDefaultPort() {
		return defaultPort;
	}
	public void setDefaultPort(String defaultPort) {
		this.defaultPort = defaultPort;
	}
	public boolean isTns() {
		return tns;
	}
	public void setTns(boolean tns) {
		this.tns = tns;
	}
	public boolean isServer() {
		return server;
	}
	public void setServer(boolean server) {
		this.server = server;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
