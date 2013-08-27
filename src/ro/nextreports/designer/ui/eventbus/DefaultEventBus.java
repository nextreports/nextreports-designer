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

import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

/**
 * A default implementation of an <code>EventBus</code>. This implementation
 * is threadsafe so only one event bus is needed in the system.
 * <code>Event</code>s published to the bus will be broadcasted to
 * <code>Subscriber</code>s in the reverse order of the subscription.
 *
 * @author Decebal Suiu
 */
public final class DefaultEventBus implements EventBus {

	/**
	 * Current subscriptions to the bus.
	 */
	protected final Collection<Subscription> subscriptions;

	/**
	 * Cached copy of subscriptions so we can avoid copying during event firing.
	 */
	protected Subscription[] subscriptionsArray;

	/**
	 * The error listener for the eventbus
	 */
	protected BusListener busListener;

	private boolean async;

	/**
	 * Constructs the <code>EventBus</code>.
	 */
	public DefaultEventBus() {
		this(true);
	}

	public DefaultEventBus(final boolean asynchronous) {
		async = asynchronous;
		subscriptions = new LinkedList<Subscription>();
	}

	public void setErrorListener(BusListener listener) {
		busListener = listener;
	}

	/**
	 * Publishes the supplied event to the bus. All subscribers that have
	 * indicated their interest in the class of the event and for whom the event
	 * passes their <code>Filter</code> will be <code>eventError</code>ed
	 * of the event. <p/> This implementation notifies subscribers in the
	 * reverse order of the subscription. The last subscriber registered will be
	 * the first notified of the event.
	 *
	 * @param event
	 *            The event to broadcast
	 *
	 * @see Filter
	 * @see Subscriber#inform(EventObject)
	 */
	public void publish(final EventObject event) {
		fireEventPublished(event);
		final EventDispatcher dispatcher = new EventDispatcher(event);

		if (async) {
			SwingUtilities.invokeLater(dispatcher);
		} else {
			try {
				SwingUtilities.invokeAndWait(dispatcher);
			} catch (Exception t) {
				fireEventError(event, t);
			}
		}
	}

	/**
	 * Publish supplied event, but block until event subscribers have processed
	 * the event.
	 *
	 * @see EventBus#publish(EventObject)
	 */
	public void publishAndWait(EventObject event) {
		fireEventPublished(event);
		final EventDispatcher dispatcher = new EventDispatcher(event);

		if (!SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(dispatcher);
			} catch (Exception t) {
				fireEventError(event, t);
			}
		} else {
			dispatcher.run();
		}
	}

	private void fireEventPublished(final EventObject event) {
		if (null != busListener) {
			busListener.eventPublished(event);
		}
	}

	private void fireEventError(EventObject event, Exception e) {
		if (busListener != null) {
			busListener.eventError(event, e);
		}
	}

	/**
	 * Registers a <code>Subscriber</code> with the bus for notification of
	 * <code>Event</code>s of the specified type and all its subtypes. <br>
	 * The bus will use the supplied <code>Filter</code> prior to notification
	 * to verify the interest of the subscriber in the event.
	 *
	 * @param eventType
	 *            The type of event for which the object is subscribing, which
	 *            must be a subtype of {@link java.util.EventObject}*
	 * @param filter
	 *            The filter to apply to each event, or null if events should
	 *            not be filtered
	 * @param subscriber
	 *            The object subscribing to the bus
	 *
	 * @throws IllegalArgumentException
	 *             if eventType is not a subtype of <code>EventObject</code>
	 */
	public void subscribe(final Class eventType, final Filter filter,
			final Subscriber subscriber) {
		checkSubscriberInfo(eventType, subscriber);

		final Subscription subscription = new Subscription(eventType, filter, subscriber);

		synchronized (this) {
			subscriptions.add(subscription);
			subscriptionsArray = null;
		}
	}

	private void checkSubscriberInfo(final Class eventType,
			final Subscriber subscriber) {
		if (!EventObject.class.isAssignableFrom(eventType)) {
			throw new IllegalArgumentException("Invalid event class: "
					+ eventType.getName());
		}

		if (null == subscriber) {
			throw new IllegalArgumentException("'subscriber' cannot be empty.");
		}
	}

	/**
	 * Removes a specific subscription from the bus. <br>
	 * Both the event type and filter are checked as well as the subscriber, so
	 * it is possible to selectively unsubscribe from any of the individual
	 * subscriptions the subscriber object had already performed. If there is no
	 * event type/filter/subscriber tuple that matches the arguments to this
	 * function, the bus is left unaffected.
	 *
	 * @param eventType
	 *            The type of event to match
	 * @param filter
	 *            The filter to match
	 * @param subscriber
	 *            The subscriber to match
	 *
	 * @throws IllegalArgumentException
	 *             if eventType is not a subtype of <code>EventObject</code>
	 */
	public void unsubscribe(final Class eventType, final Filter filter,
			final Subscriber subscriber) {
		checkSubscriberInfo(eventType, subscriber);

		final Subscription subscription = new Subscription(eventType, filter, subscriber);

		synchronized (this) {
			subscriptions.remove(subscription);
			subscriptionsArray = null;
		}
	}

	/**
	 * Encapsulate the event and dispatch it when we can.
	 *
	 * @author Decebal Suiu
	 */
	class EventDispatcher implements Runnable {

		private final EventObject event;

		/**
		 * Create an EventDispatcher.
		 *
		 * @param event
		 *            The event we are going to dispatch
		 */
		public EventDispatcher(final EventObject event) {
			this.event = event;
		}

		/**
		 * Iterate through the subscriptions and send the event to the proper
		 * destinations.
		 */
		public void run() {
			// Ensure we are not steping on each others toes
			synchronized (DefaultEventBus.this) {
				if (subscriptionsArray == null) {
					subscriptionsArray = (Subscription[]) subscriptions
							.toArray(new Subscription[0]);

				}

				// Iterate subscriptions in reverse order
				for (int i = 0; i < subscriptionsArray.length; i++) {
					final Subscription subscription = subscriptionsArray[i];
					if ((subscription.getEventType().isAssignableFrom(event.getClass()))
							&& (subscription.getFilter() == null || subscription
									.getFilter().apply(event))) {
						try {
							subscription.getSubscriber().inform(event);
						} catch (Exception e) {
							fireEventError(event, e);
						}
					}
				}
			}
		}

	}

	/**
	 * Represents a subscription to the bus.
	 *
	 * @author Decebal Suiu
	 */
	static final class Subscription {

		private final Class eventType;
		private final Filter filter;
		private final Subscriber subscriber;

		/**
		 * Create the Subscription object.
		 *
		 * @param eventType
		 *            The type of event to listen for
		 * @param filter
		 *            The filter used to check
		 * @param subscriber
		 *            The subscriber that receives notification
		 */
		public Subscription(final Class eventType, final Filter filter,
				final Subscriber subscriber) {
			this.eventType = eventType;
			this.filter = filter;
			this.subscriber = subscriber;
		}

		/**
		 * Get the EventType.
		 *
		 * @return The event type
		 */
		public Class getEventType() {
			return eventType;
		}

		/**
		 * Get the filter used with this subscription.
		 *
		 * @return The filter
		 */
		public Filter getFilter() {
			return filter;
		}

		/**
		 * Get the subscriber.
		 *
		 * @return the subscriber
		 */
		public Subscriber getSubscriber() {
			return subscriber;
		}

		/**
		 * Compare two Subscriptions to each other.
		 *
		 * @param o
		 *            The other object
		 *
		 * @return <code>true</code> if the two Subscription objects are the
		 *         same
		 */
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Subscription)) {
				return false;
			}

			final Subscription subscription = (Subscription) o;

			if (!eventType.equals(subscription.eventType)) {
				return false;
			}
			if (filter != null ? !filter.equals(subscription.filter)
					: subscription.filter != null) {
				return false;
			}
			if (!subscriber.equals(subscription.subscriber)) {
				return false;
			}

			return true;
		}

		/**
		 * Get the hashcode (used in HashMaps). <p/> hashCode = 37 * (37 * (629 +
		 * event.hashCode()) + filter.hashCode()) + subscriber.hashCode()
		 *
		 * @return the hashCode value
		 */
		public int hashCode() {
			int result = 17;
			result = 37 * result + eventType.hashCode();
			result = 37 * result + (filter != null ? filter.hashCode() : 0);
			result = 37 * result + subscriber.hashCode();
			return result;
		}

	}

}
