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
 * Event Filter that minimizes your chances of having event feedback loops.
 *
 * @author Decebal Suiu
 */
public final class CircularEventFilter implements Filter {

	private final Object source;

	/**
	 * Initialize the filter with the event source you want to make sure does
	 * <strong>not</strong> receive events. Usually instantiated like this:
	 *
	 * <pre>
	 * new CircularEventFilter(this);
	 * </pre>
	 *
	 * @param source
	 *            The source we want to filter out.
	 */
	public CircularEventFilter(final Object source) {
		if (source == null) {
			throw new IllegalArgumentException("'source' cannot be null");
		}
		this.source = source;
	}

	/**
	 * Filter out all events that have a source matching the one in this filter.
	 * This prevents event feedback loops where an object publishes an event,
	 * and the object also happens to be a subscriber. In that case the object
	 * would respond to the event, and possibly reissue the event.
	 *
	 * @param event
	 *            The event to check
	 *
	 * @return <code>true</code> if the event is allowed
	 */
	public boolean apply(final EventObject event) {
		return !source.equals(event.getSource());
	}

}
