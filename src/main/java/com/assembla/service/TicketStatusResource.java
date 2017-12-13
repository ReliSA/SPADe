package com.assembla.service;

import java.util.List;

import com.assembla.TicketStatus;
import com.assembla.exception.AssemblaAPIException;

public interface TicketStatusResource {
	/**
	 * Get a list of all ticket statuses available for current space
	 * 
	 * @return List of all ticket statuses
	 */
	List<TicketStatus> getAll();

	/**
	 * Get ticket status by id
	 * 
	 * @param id
	 *            id of ticket status
	 * @return ticket status with id {@code id}
	 * @throws AssemblaAPIException
	 *             when no ticket status exists with id {@code id}
	 */
	TicketStatus get(int id);

	/**
	 * Create a new ticket status
	 * 
	 * @param ticketStatus
	 *            ticket status to create, not null
	 * @return newly created ticket status
	 */
	TicketStatus create(TicketStatus ticketStatus);

	/**
	 * Update existing ticket status
	 * 
	 * @param ticketStatus
	 *            ticket status to update, not null, must have id
	 */
	void update(TicketStatus ticketStatus);

	/**
	 * Delete an existing ticket status
	 * 
	 * @param ticketStatus
	 *            ticket status to delete, not null, must have id
	 */
	void delete(TicketStatus ticketStatus);

}
