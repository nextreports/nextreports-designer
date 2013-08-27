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

import java.io.Serializable;
import java.util.Properties;

import ro.nextreports.engine.util.xstream.XStreamable;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: May 15, 2006
 * Time: 11:44:46 AM
 */
public class DataSource implements XStreamable, Serializable {

    private static final long serialVersionUID = -3754661311272916759L;

    private String name;
    private String type;
    private String driver;
    private String url;
    private String user;
    private String password;
    private Properties properties;
    private transient byte status;
    
    public DataSource() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
                
    public Properties getProperties() {
		return properties;
	}
    
    public Properties getUsedProperties() {
		Properties used = new Properties();
		for (Object key : properties.keySet()) {
			Object value = properties.get(key);
			if (!"".equals(value)) {
				used.put(key, value);
			}
		}
		return used;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	// without status
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DataSource that = (DataSource) o;

        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 29 * result + (type != null ? type.hashCode() : 0);
        result = 29 * result + (driver != null ? driver.hashCode() : 0);
        result = 29 * result + (url != null ? url.hashCode() : 0);
        result = 29 * result + (user != null ? user.hashCode() : 0);
        result = 29 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name=").append(name).append(" Type=").append(type).append(" Driver=").append(driver).
                append(" Url=").append(url).append(" User=").append(user).append(" Password=").append(password).
                append(" Status=").append(status);
        return sb.toString();
    }


}
