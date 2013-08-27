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
package ro.nextreports.designer.server;

import java.io.Serializable;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 30-Sep-2009
// Time: 14:00:56

//
public class Server implements Serializable {

    private static final long serialVersionUID = -5712345608947514617L;

    private String name;
    private String url;

    public Server(String name, String url) {
        if (url == null) {
            throw new IllegalArgumentException("Url cannot be null!");
        }
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        String fullName;
        if (name == null) {
            fullName = url;
        } else {
            fullName = name + " (" + url + ")";
        }

        return fullName;
    }

    public String getUrl() {
        return url;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Server server = (Server) o;

        if (name != null ? !name.equals(server.name) : server.name != null) return false;
        if (url != null ? !url.equals(server.url) : server.url != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Server{" +
                "name='" + getName() + '\'' +
                ", url='" + getUrl() + '\'' +                
                '}';
    }


}

