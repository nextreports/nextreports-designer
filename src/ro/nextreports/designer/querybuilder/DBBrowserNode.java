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
package ro.nextreports.designer.querybuilder;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Decebal Suiu
 */
public class DBBrowserNode extends DefaultMutableTreeNode {

    private final DBObject dbObject;
    private boolean nodeAllowsChildren = true;

    public DBBrowserNode(DBObject dbObject) {
        super(dbObject.getName() + dbObject.getInfo(), true);
        this.dbObject = dbObject;
    }

    public DBBrowserNode(DBObject dbObject, boolean useCatalog) {
        super();
        if (useCatalog) {
            String catalog = dbObject.getCatalog();
            if (catalog != null) {
                this.userObject = catalog + "." + dbObject.getName() + dbObject.getInfo();
            } else {
                this.userObject = dbObject.getName() + dbObject.getInfo();
            }
        } else {
            this.userObject = dbObject.getName() + dbObject.getInfo();
        }
        parent = null;
	    this.allowsChildren = true;
        this.dbObject = dbObject;
    }

    public DBObject getDBObject() {
        return dbObject;
    }

    public boolean getAllowsChildren() {
        return nodeAllowsChildren;
    }

    public boolean isLeaf() {
        return !nodeAllowsChildren;
    }

    public void setName(String name) {
        dbObject.setName(name + dbObject.getInfo());
        this.setUserObject(name);
    }

    public void setAllowsChildren(boolean value) {
        super.setAllowsChildren(value);
        nodeAllowsChildren = value;
    }

}
