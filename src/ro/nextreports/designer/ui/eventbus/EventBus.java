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
 * An <code>EventBus</code> brokers events between publishers and subscribers.
 * <p>
 * The event bus provides a centralized place for publishing and receiving
 * events. Object that need to be notified of events must implement the
 * {@link Subscriber} interface and dynamically register with the bus.
 * Subscription is based on event type and inheritance hierarchy,
 * meaning that subscribing to a particular type also subscribes
 * to all of its sub-types. The bus also provides a mechanism to filter
 * events so that subscribers can select events of interest.
 *
 * @author Decebal Suiu
 */
public interface EventBus {

	/**
	 * Registers a <code>Subscriber</code> with the bus
	 * for notification of <code>Event</code>s of the specified type
	 * and all its subtypes. <br>
	 * The bus will use the supplied <code>Filter</code> prior to
	 * notification to verify the interest of the subscriber in the event.
	 *
	 * @param eventType The type of event for which the object is subscribing,
	 *                  which must be a subtype of {@link java.util.EventObject}
	 * @param filter The filter to apply to each event, or null if events should
	 *               not be filtered
	 * @param subscriber The object subscribing to the bus
	 */
	public void subscribe(Class eventType, Filter filter, Subscriber subscriber);

	/**
	 * Publishes the supplied event to the bus. All subscribers that have
	 * indicated their interest in the class of the event and for whom the
	 * event passes their <code>Filter</code> will be <code>eventError</code>ed of
	 * the event.
	 *
	 * @see Filter
	 * @see Subscriber#inform(EventObject)
	 * @param event The event to broadcast
	 */
	public void publish(EventObject event);

	/**
	 * Publish supplied event, but block until event subscribers have
	 * processed the event.
	 *
	 * @see EventBus#publish(EventObject)
	 */
	public void publishAndWait(EventObject event);

	/**
	 * Removes a specific subscription from the bus. <br>
	 * Both the event type and filter are checked as well as the
	 * subscriber, so it is possible to selectively unsubscribe
	 * from any of the individual subscriptions the subscriber
	 * object had already performed. If there is no
	 * event type/filter/subscriber tuple that matches the arguments
	 * to this function, the bus is left unaffected.
	 *
	 * @param eventType The type of event to match
	 * @param filter The filter to match
	 * @param subscriber The subscriber to match
	 */
	public void unsubscribe(Class eventType, Filter filter, Subscriber subscriber);

}

