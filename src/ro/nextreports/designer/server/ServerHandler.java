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


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 30-Sep-2009
// Time: 14:00:13

import ro.nextreports.designer.Globals;
import ro.nextreports.designer.util.LinkedProperties;

//
public class ServerHandler {

    public static final String CONFIG_FILE = Globals.USER_DATA_DIR + "/config/servers.properties";
    private List<Server> servers;

    public ServerHandler() {
    	servers = new LinkedList<Server>();
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        
	public List<Server> getServers() {        
    	if (!servers.isEmpty()) {
    		return servers;
    	}
        FileInputStream fin = null;
        try {
            LinkedProperties p = new LinkedProperties();
            fin = new FileInputStream(CONFIG_FILE);
            p.load(fin);
            Set<Map.Entry<Object, Object>> set = p.entrySet();
            for (Map.Entry entry : set) {
                String serverName = (String) entry.getKey();
                String url = (String) entry.getValue();
                Server server = new Server(serverName, url);
                servers.add(server);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return servers;
    }

    public List<String> getServerNames(List<Server> servers) {
        List<String> list = new LinkedList<String>();
        for (Server server : servers) {
            list.add(server.getName());
        }
        return list;
    }

    public void saveServers(List<Server> servers) {
        FileOutputStream fos = null;
        try {
            LinkedProperties p = new LinkedProperties();
            for (Server server : servers) {
                p.setProperty(server.getName(), server.getUrl());
            }
            fos = new FileOutputStream(CONFIG_FILE);
            p.store(fos, "Server name = url");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void adjustServers(String serverName) {
        Server found = null;
        for (Iterator<Server> it = servers.iterator(); it.hasNext();) {
            Server server = it.next();            
            if (server.getName().equals(serverName)) {
                found = server;
                it.remove();
                break;
            }
        }
        if (found != null) {            
            servers.add(0, found);
        }
    }

    public boolean serverExists(String serverName) {
        for (Server server : getServers()) {
            if (server.getName().equals(serverName)) {
                return true;
            }
        }
        return false;
    }
}
