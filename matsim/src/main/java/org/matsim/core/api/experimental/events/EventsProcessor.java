package org.matsim.core.api.experimental.events;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.EventArray;

/**
 * The idea here is that one can pass the far less powerful {@link EventsProcessor} to, say agents.  Such agents then can push {@link Event}s into the
 * events channel, but they cannot subscribe/register to the events stream.  Which, in general, they should not because it is far too expensive if
 * every agent would subscribe to all events.  kai, jul'22
 */
public interface EventsProcessor{
	void processEvent( Event event );
	/**
	 * Submit multiple events for processing at once.
	 */
	default void processEvents( EventArray events ) {
		for (int i = 0; i < events.size(); i++) {
			processEvent(events.get(i));
		}
	}
}
