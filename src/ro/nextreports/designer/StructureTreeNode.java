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
package ro.nextreports.designer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 28, 2009
 * Time: 2:21:10 PM
 */
public class StructureTreeNode extends DefaultMutableTreeNode {

    protected boolean isVisible;

    public StructureTreeNode() {
        this(null);
    }

    public StructureTreeNode(Object userObject) {
        this(userObject, true, true);
    }

    public StructureTreeNode(Object userObject, boolean allowsChildren
            , boolean isVisible) {
        super(userObject, allowsChildren);
        this.isVisible = isVisible;
    }

    public TreeNode getChildAt(int index, boolean filterIsActive) {
        if (!filterIsActive) {
            return super.getChildAt(index);
        }
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }

        int realIndex = -1;
        int visibleIndex = -1;
        Enumeration menum = children.elements();
        while (menum.hasMoreElements()) {
            StructureTreeNode node = (StructureTreeNode) menum.nextElement();
            if (node.isVisible()) {
                visibleIndex++;
            }
            realIndex++;
            if (visibleIndex == index) {
                return (TreeNode) children.elementAt(realIndex);
            }
        }

        throw new ArrayIndexOutOfBoundsException("index unmatched");

    }

    public int getChildCount(boolean filterIsActive) {
        if (!filterIsActive) {
            return super.getChildCount();
        }
        if (children == null) {
            return 0;
        }

        int count = 0;
        Enumeration menum = children.elements();
        while (menum.hasMoreElements()) {
            StructureTreeNode node = (StructureTreeNode) menum.nextElement();
            if (node.isVisible()) {
                count++;
            }
        }

        return count;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public int getIndex(TreeNode aChild, boolean filterIsActive) {
        if (!filterIsActive) {
            return super.getIndex(aChild);
        } else {
            if (aChild == null) {
                throw new IllegalArgumentException("argument is null");
            }
            if (!isNodeChild(aChild)) {
                return -1;
            }
            StructureTreeNode sn = (StructureTreeNode)aChild;
            if (sn.isVisible()) {
                int visibleIndex = -1;
                int realIndex = -1;
                int index = children.indexOf(aChild);
                Enumeration menum = children.elements();
                while (menum.hasMoreElements()) {
                    StructureTreeNode node = (StructureTreeNode) menum.nextElement();
                    if (node.isVisible()) {
                        visibleIndex++;
                    }
                    realIndex++;
                    if (index == realIndex) {
                        return visibleIndex;
                    }
                }

            } else {
                return -1;
            }
        }
        return -1;
    }

    
}
