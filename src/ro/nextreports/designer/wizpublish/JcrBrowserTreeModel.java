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
package ro.nextreports.designer.wizpublish;

import ro.nextreports.server.api.client.WebServiceClient;

import javax.swing.tree.DefaultTreeModel;

import ro.nextreports.designer.querybuilder.DBBrowserNode;
import ro.nextreports.designer.querybuilder.DBObject;
import ro.nextreports.designer.querybuilder.tree.NodeExpander;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
//
// Created by IntelliJ IDEA.
// User: mihai.panaitescu
// Date: 29-Sep-2009
// Time: 18:00:16

//
public class JcrBrowserTreeModel extends DefaultTreeModel {

    /**
     * Collection of <TT>NodeExpander</TT> objects. Each entry is a <TT>List</TT>
     * of <TT>NodeExpander</TT> objects. The key to the list is the node type.
     */
    private Map<Byte, List<NodeExpander>> expanders = new HashMap<Byte,List<NodeExpander>>();

    public JcrBrowserTreeModel(DBBrowserNode root, WebServiceClient client) {
        super(root);

        // Standard expanders.
        NodeExpander expander = new JcrNodeExpander(client);
        addExpander(DBObject.DATABASE, expander);
        addExpander(DBObject.REPORTS_GROUP, expander);
        addExpander(DBObject.CHARTS_GROUP, expander);
        addExpander(DBObject.FOLDER_REPORT, expander);
    }

    /**
     * Add an expander for the specified database object type in the
     * object tree.
     */
    public synchronized void addExpander(byte type, NodeExpander expander) {
        if (expander == null) {
            throw new IllegalArgumentException("Null NodeExpander passed");
        }
        getExpandersList(type).add(expander);
    }

    /**
     * Return an array of the node expanders for the passed database object type.
     */
    public synchronized NodeExpander[] getExpanders(byte type) {
        List<NodeExpander> list = getExpandersList(type);
        return list.toArray(new NodeExpander[list.size()]);
    }

    /**
     * Get the collection of expanders for the passed node type. If one
     * doesn't exist then create an empty one.
     */
    private List<NodeExpander> getExpandersList(byte type) {
        List<NodeExpander> list = expanders.get(type);
        if (list == null) {
            list = new ArrayList<NodeExpander>();
            expanders.put(type, list);
        }

        return list;
    }

}

