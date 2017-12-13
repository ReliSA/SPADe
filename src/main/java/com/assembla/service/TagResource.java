package com.assembla.service;

import com.assembla.Tag;
import com.assembla.Ticket;
import com.assembla.client.PagedIterator;

public interface TagResource {
	/**
	 * Get all tags for current space
	 * 
	 * @return {@link PagedIterator} of all tags for current space
	 */
	PagedIterator<Tag> getAll();

	/**
	 * Get all active tags for current space
	 * 
	 * @return {@link PagedIterator} of all active tags for current space
	 */
	PagedIterator<Tag> getActive();

	/**
	 * Get all proposed tags for current space
	 * 
	 * @return {@link PagedIterator} of all proposed tags for current space
	 */
	PagedIterator<Tag> getProposed();

	/**
	 * Get all hidden tags for current space
	 * 
	 * @return {@link PagedIterator} of all hidden tags for current space
	 */
	PagedIterator<Tag> getHidden();

	/**
	 * Get all tickets with a tag
	 * 
	 * @param tag
	 *            an existing tag, not null
	 * @return {@link PagedIterator} of all {@link Ticket} which have tag
	 *         {@code tag}
	 */
	PagedIterator<Ticket> getTickets(Tag tag);

	/**
	 * Get a tag by id
	 * 
	 * @param id
	 *            tag id
	 * @return tag which has id {@code id}
	 */
	Tag get(int id);

	/**
	 * Create a new tag for current space
	 * 
	 * @param tag
	 *            tag to create, not null
	 * @return newly created tag
	 */
	Tag create(Tag tag);

	/**
	 * Update an existing tag
	 * 
	 * @param tag
	 *            tag to update, not null, must have an id
	 */
	void update(Tag tag);

	/**
	 * Delete an existing tag
	 * 
	 * @param tag
	 *            tag to delete, not null, must have an id
	 */
	void delete(Tag tag);

}
