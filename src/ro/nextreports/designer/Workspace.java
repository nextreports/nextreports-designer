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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.noos.xing.mydoggy.ToolWindowManager;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;
import org.noos.xing.mydoggy.plaf.ui.ResourceManager;

/**
 * @author Decebal Suiu
 */
public class Workspace {

    private String name;
    private MyDoggyToolWindowManager toolWindowManager;

    public Workspace(String name) {
        this.name = name;
        toolWindowManager = new MyDoggyToolWindowManager();
        ResourceManager resourceManager = toolWindowManager.getResourceManager();
        resourceManager.putBoolean("drag.toolwindow.asTab", false);
//        resourceManager.putColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_START, Color.CYAN);
//        resourceManager.putColor(MyDoggyKeySpace.RAB_BACKGROUND_ACTIVE_END, Color.WHITE);
        /*
        ToolWindow[] toolWindows = toolWindowManager.getToolWindows();
        for (ToolWindow toolWindow : toolWindows) {
            DockedTypeDescriptor dockedTypeDescriptor = toolWindow.getTypeDescriptor(DockedTypeDescriptor.class);
        	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.DOCK_ACTION_ID).setVisible(false);
        	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.PIN_ACTION_ID).setVisible(false);
        	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_ACTION_ID).setVisible(false);
        	dockedTypeDescriptor.getToolWindowAction(ToolWindowAction.FLOATING_LIVE_ACTION_ID).setVisible(false);
        }
        */
    }

    public String getName() {
        return name;
    }

    public ToolWindowManager getToolWindowManager() {
        return toolWindowManager;
    }

    public void store(String file) throws IOException {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            toolWindowManager.getPersistenceDelegate().save(output);
            output.close();
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    public void restore(String file) throws IOException {
        InputStream input = null;
        if (new File(file).exists()) {
            input = new FileInputStream(file);
        } else {
            // restore default workspace
            input = Workspace.class.getResourceAsStream("/workspaces/" + file);
        }

        if (input == null) {
            // cannot restore the workspace
            return;
        }

        try {
            toolWindowManager.getPersistenceDelegate().apply(input);
        } catch (Error e) {
            // if a panel is "undocked", docking complains about it, and the following exception
            // is thrown when NextReports starts :
            // java.lang.Error : "Destination component not connected to component tree hierarchy"
            // to avoid this delete the workspace file and restore the docking
            new File(file).delete();
            restoreDefault(file);
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    private void restoreDefault(String file) throws IOException {
        InputStream input = Workspace.class.getResourceAsStream("/workspaces/" + file);
        if (input == null) {
            // cannot restore the workspace
            return;
        }
        toolWindowManager.getPersistenceDelegate().apply(input);
    }

}
