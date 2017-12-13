package com.assembla.service;

import java.util.List;

import com.assembla.CustomReport;
import com.assembla.Document;
import com.assembla.Tag;
import com.assembla.Ticket;
import com.assembla.client.PagedIterator;
import com.assembla.exception.AssemblaAPIException;

public interface TicketResource {
	/**
	 * Get ticket by id
	 * 
	 * @param id
	 *            Id of ticket, not null
	 * @return ticket with {@code id}
	 * @throws AssemblaAPIException
	 *             if no ticket is found with {@code id} exists
	 */
	Ticket getById(String id);

	/**
	 * Get ticket by number
	 * 
	 * @param ticketNumber
	 *            Number of ticket
	 * @return ticket with {@code number}
	 * @throws AssemblaAPIException
	 *             if no ticket with {@code number} exists
	 */
	Ticket getByNumber(int ticketNumber);

	/**
	 * Get tickets with no milestone
	 * 
	 * @return {@link PagedIterator} of tickets with no milestone
	 */
	PagedIterator<Ticket> getNoMilestone();

	/**
	 * Get all tickets
	 * 
	 * @return {@link PagedIterator} of all tickets
	 */
	PagedIterator<Ticket> getAll();

	/**
	 * Get all followed tickets
	 * 
	 * @return {@link PagedIterator} of all followed tickets
	 */
	PagedIterator<Ticket> getFollowed();

	/**
	 * Get all active tickets
	 * 
	 * @return list of all active tickets
	 */
	List<Ticket> getActive();

	/**
	 * Get tags for a ticket
	 * 
	 * @param ticketNumber
	 *            Number of ticket
	 * @return list of tags for ticket with number {@code ticketNumber}
	 */
	List<Tag> getTags(int ticketNumber);

	/**
	 * Get attachments for ticket
	 * 
	 * @param ticketNumber
	 *            Number of ticket
	 * @return List of attachments for ticket with number {@code ticketNumber}
	 */
	List<Document> getAttachments(int ticketNumber);

	/**
	 * Update a ticket
	 * 
	 * @param ticket
	 *            Ticket to update
	 */
	void update(Ticket ticket);

	/**
	 * Get custom report of user and team reports
	 * 
	 * @return Custom Report containing user and team reports
	 */
	CustomReport getCustomReports();

	/**
	 * Delete a ticket
	 * 
	 * @param ticket
	 *            Ticket to delete, not null, must have an Id
	 */
	void delete(Ticket ticket);

	/**
	 * Create a new ticket
	 * 
	 * @param ticket
	 *            Ticket to create, not null
	 * @return New ticket
	 */
	Ticket create(Ticket ticket);

	/**
	 * Get tickets matching {@link TicketRequest} {@code request}
	 * 
	 * @param request
	 *            {@link TicketRequest} for tickets to return, not null
	 * @return {@link PagedIterator} of tickets matching {@code request}
	 */
	PagedIterator<Ticket> get(TicketRequest request);

	/**
	 * Get tickets for a milestone and matching {@link TicketRequest}
	 * {@code request}
	 * 
	 * @param milestoneId
	 *            Id of milestone to return tickets for, not null
	 * @param request
	 *            {@link TicketRequest} for tickets to return, not null
	 * @return {@link PagedIterator} of tickets in milestone {@code milestoneId}
	 *         and matching {@link TicketRequest} {@code request}
	 */
	PagedIterator<Ticket> getByMilestone(String milestoneId, TicketRequest request);

}
