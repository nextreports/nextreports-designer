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
package ro.nextreports.designer.ui.eventbus;

import java.util.EventObject;

/**
 * An interface for objects that handle events.
 * <p>
 * <code>Subscriber</code>s register with the {@link EventBus} to receive
 * notification when events of specific types are published to the bus. The bus
 * will only notify a subscriber if the event passes the filter provided at
 * subscription.
 * </p>
 *
 * @see EventBus#subscribe(Class, Filter, Subscriber)
 * @author Decebal Suiu
 */
public interface Subscriber {

	/**
	 * Notifies this subscriber that an event has occurred.
	 *
	 * @param event
	 *            The event to handle
	 */
	public void inform(EventObject event);

}
