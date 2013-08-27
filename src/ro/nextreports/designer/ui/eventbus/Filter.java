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
 * An interface for event filters.
 * <p>
 * A <code>Filter</code> is used by {@link Subscriber}s when
 * registering with the {@link EventBus} to restrict event
 * notification to events of interest.
 *
 * @see EventBus#subscribe(java.lang.Class, Filter, Subscriber)
 * @author Decebal Suiu
 */
public interface Filter {

	/**
	 * Specifies if a given <code>Event</code> is of interest to the
	 * <code>Subscriber</code> that has registered with the
	 * <code>EventBus</code> using this filter.
	 * <p>
	 * If the event should be handled by the subscriber,
	 * this method returns <code>true</code>
	 * otherwise it returns <code>false</code>.
	 *
	 * @param event The event to filter
	 * @return <code>true</code> if the event should be broadcast to
	 * the subscriber, <code>false</code> otherwise
	 */
	public boolean apply(EventObject event);

}

