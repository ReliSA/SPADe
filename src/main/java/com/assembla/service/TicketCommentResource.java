package com.assembla.service;

import com.assembla.TicketComment;
import com.assembla.client.PagedIterator;

public interface TicketCommentResource {
	/**
	 * Get all ticket comments for a ticket
	 * 
	 * @param number
	 *            number of ticket to get ticket comments for
	 * @return {@link PagedIterator} of ticket comments for ticket with number
	 *         {@code number}
	 */
	PagedIterator<TicketComment> getAll(int number);

	/**
	 * Get a ticket comment for a ticket by id
	 * 
	 * @param number
	 *            ticket number
	 * @param commentId
	 *            ticket comment id
	 * @return ticket comment for ticket with number {@code number} with id
	 *         {@code commentId}
	 */
	TicketComment getById(int number, int commentId);

	/**
	 * Create a new ticket comment for a ticket
	 * 
	 * @param number
	 *            ticket number
	 * @param newTicketComment
	 *            ticket comment to create, not null
	 * @return newly created ticket comment for ticket with number
	 *         {@code number}
	 */
	TicketComment create(int number, TicketComment newTicketComment);

	/**
	 * Update existing ticket comment
	 * 
	 * @param number
	 *            ticket number
	 * @param ticketComment
	 *            ticket comment to update, not null, must have an id
	 */
	void update(int number, TicketComment ticketComment);

}
