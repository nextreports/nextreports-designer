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
package ro.nextreports.designer.util;

/**
 * Created by IntelliJ IDEA.
 * User: mihai.panaitescu
 * Date: Apr 3, 2006
 * Time: 3:39:07 P
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.KeyEventPostProcessor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Action;

/**
 * This class handles the default key strokes the users type
 * in a panel or component by executing the registered actions.
 * If no actions are registered, then nothing will happen. Any key
 * stroke can be mapped to an action, however, the two main
 * key strokes mapped in this object are the \<enter\> and
 * \<escape\> keys. The key is a KeyEvent.VK_<key> static
 * value and the action is what will be executed if
 * the keystroke is typed by the user.
 *
 */
public class DefaultKeyHandler
        extends DefaultKeyboardFocusManager
        implements KeyListener, KeyEventDispatcher, KeyEventPostProcessor {
    /**
     * The table that will hold the actions to execute
     */
    private HashMap<Integer, Action> actionMap = new HashMap<Integer, Action>();

    /**
     * Default Constructor
     */
    public DefaultKeyHandler() {
        super();
    }

    /**
     * @see java.awt.KeyEventDispatcher#dispatchKeyEvent(java.awt.event.KeyEvent)
     */
    public boolean dispatchKeyEvent(KeyEvent e) {
/** Add custom code here if you need to... */
        return false;
    }

    /**
     * If a KeyEvent matches the key in the actionMap object,
     * the associated action will be fired.
     *
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        Set keys = actionMap.keySet();
        if (keys.contains(keyCode)) {
            final Action a = actionMap.get(keyCode);
            try {
                a.actionPerformed(
                        new ActionEvent(
                                this,
                                1,
                                (String) a.getValue(Action.ACTION_COMMAND_KEY)));
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
    }

    /**
     * This method will map an action to the enter key event
     *
     * @param action  action
     */
    public void mapEnterKeyAction(Action action) {
        actionMap.put(KeyEvent.VK_ENTER, action);
    }

    /**
     * This method will map an action to the escape key event
     *
     * @param action  action
     */
    public void mapEscapeKeyAction(Action action) {
        actionMap.put(KeyEvent.VK_ESCAPE, action);
    }

    /**
     * This method will map an action to the escape key event
     *
     * @param keyEventId  id of the key event
     * @param action action
     */
    public void mapKeyAction(int keyEventId, Action action) {
        actionMap.put(keyEventId, action);
    }

    /**
     * @see java.awt.KeyEventPostProcessor#postProcessKeyEvent(java.awt.event.KeyEvent)
     */
    public boolean postProcessKeyEvent(KeyEvent e) {
        /** Add custom code here if you need to... */
        return false;
    }

    /**
     * @see java.awt.KeyEventPostProcessor#postProcessKeyEvent(java.awt.event.KeyEvent)
     */
    public void processKeyEvent(Component component, KeyEvent e) {
        /** Add custom code here if you need to... */
    }

    /**
     * This method registers a component for the mapped actions.
     *
     * @param component component
     */
    public void registerComponent(Component component) {
        component.addKeyListener(this);
        if (component instanceof Container) {
            Component[] comps = ((Container) component).getComponents();
            for (Component comp : comps) {
                registerComponent((comp));
            }
        }
    }
}
