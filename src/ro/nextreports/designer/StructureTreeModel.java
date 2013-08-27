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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Jan 28, 2009
 * Time: 2:22:33 PM
 */
public class StructureTreeModel extends DefaultTreeModel {

    protected boolean filterIsActive;

    public StructureTreeModel(TreeNode root) {
        this(root, false);
    }

    public StructureTreeModel(TreeNode root, boolean asksAllowsChildren) {
        this(root, false, false);
    }

    public StructureTreeModel(TreeNode root, boolean asksAllowsChildren
            , boolean filterIsActive) {
        super(root, asksAllowsChildren);
        this.filterIsActive = filterIsActive;
    }

    public void activateFilter(boolean newValue) {
        filterIsActive = newValue;
    }

    public boolean isActivatedFilter() {
        return filterIsActive;
    }

    public Object getChild(Object parent, int index) {
        if (filterIsActive) {
            if (parent instanceof StructureTreeNode) {
                return ((StructureTreeNode) parent).getChildAt(index, filterIsActive);
            }
        }
        return ((TreeNode) parent).getChildAt(index);
    }

    public int getChildCount(Object parent) {
        if (filterIsActive) {
            if (parent instanceof StructureTreeNode) {
                return ((StructureTreeNode) parent).getChildCount(filterIsActive);
            }
        }
        return ((TreeNode) parent).getChildCount();
    }

    public void removeNodeFromParent(MutableTreeNode node) {
        if (!filterIsActive) {
            super.removeNodeFromParent(node);
        } else {
            MutableTreeNode parent = (MutableTreeNode) node.getParent();
            if (parent == null)
                throw new IllegalArgumentException("node does not have a parent.");

            int[] childIndex = new int[1];
            Object[] removedArray = new Object[1];

            childIndex[0] = ((StructureTreeNode)parent).getIndex(node, filterIsActive);            
            if ((childIndex[0] != -1) && ((StructureTreeNode)node).isVisible()) {
                node.removeFromParent();
                removedArray[0] = node;
                nodesWereRemoved(parent, childIndex, removedArray);
            }
        }
    }
}
