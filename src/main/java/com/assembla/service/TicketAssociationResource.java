package com.assembla.service;

import java.util.List;

import com.assembla.Ticket;
import com.assembla.TicketAssociation;
import com.assembla.exception.AssemblaAPIException;

public interface TicketAssociationResource {
	/**
	 * Get ticket associations for a ticket
	 * 
	 * @param ticket
	 *            ticket to get ticket associations for, not null, must have an
	 *            number
	 * @return
	 */
	List<TicketAssociation> get(Ticket ticket);

	/**
	 * Get a ticket association by id and ticket
	 * 
	 * @param ticket
	 *            ticket to get ticket association with {@code id} , not null,
	 *            must have an number
	 * @param id
	 *            id of ticket association for {@code ticket} , not null
	 * @return ticket association for {@code ticket} with id {@code id}
	 * @throws AssemblaAPIException
	 *             if no association exists for {@code ticket} with id
	 *             {@code id}
	 */
	TicketAssociation getById(Ticket ticket, int id);

	/**
	 * Create a new ticket association for a ticket
	 * 
	 * @param ticket
	 *            ticket to create association for, not null, must have an
	 *            number
	 * @param association
	 *            ticket association to create for {@code ticket} , not null,
	 *            must have non null {@code TicketAssociation#ticket2Id}
	 * @return newly created ticket association
	 */
	TicketAssociation create(Ticket ticket, TicketAssociation association);

	/**
	 * Update an existing ticket association
	 * 
	 * @param ticket
	 *            ticket which owns association, not null, must have an number
	 * @param association
	 *            association to update,not null, must have an id
	 */
	void update(Ticket ticket, TicketAssociation association);

	/**
	 * Delete an existing ticket association
	 * 
	 * @param ticket
	 *            ticket which owns association, not null
	 * @param association
	 *            association to delete, not null, must have an id
	 */
	void delete(Ticket ticket, TicketAssociation association);

}
