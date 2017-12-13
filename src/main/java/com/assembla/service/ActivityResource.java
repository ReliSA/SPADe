package com.assembla.service;

import java.time.LocalDateTime;

import com.assembla.Event;
import com.assembla.client.PagedIterator;

public interface ActivityResource {
	/**
	 * Get events
	 * 
	 * @return {@code PagedIterator} of events
	 */
	PagedIterator<Event> get();

	/**
	 * Get events between two dates
	 * 
	 * @param start
	 *            start date, not null
	 * @param end
	 *            end date, not null
	 * @return {@code PagedIterator} of events between {@code start} and
	 *         {@code end}
	 */
	PagedIterator<Event> get(LocalDateTime start, LocalDateTime end);

}
