package com.assembla.service;

import com.assembla.Milestone;
import com.assembla.client.PagedIterator;
import com.assembla.exception.AssemblaAPIException;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public interface MilestoneResource {
	/**
	 * Get upcoming milestones for current space
	 * 
	 * @return {@link PagedIterator} of upcoming milestones
	 */
	PagedIterator<Milestone> getUpcoming();

	/**
	 * Get all milestones for current space
	 * 
	 * @return {@link PagedIterator} of all milestones for current space
	 */
	PagedIterator<Milestone> getAll();

	/**
	 * Get all completed milestones for current space
	 * 
	 * @return {@link PagedIterator} of all completed milestones for current
	 *         space
	 */
	PagedIterator<Milestone> getCompleted();

	/**
	 * Get all released milestones for current space
	 * 
	 * @return {@link PagedIterator} of all completed milestones for current
	 *         space
	 */
	PagedIterator<Milestone> getReleases();

	/**
	 * Get milestone by id
	 * 
	 * @param id
	 *            if of existing milestone, not null
	 * @return milestone with id {@code milestoneId}
	 * @throws AssemblaAPIException
	 *             if no milestone exists with {@code milestoneId} for current
	 *             space
	 */
	Milestone get(String id);

	/**
	 * Create a new milestone for current space
	 * 
	 * @param milestone
	 *            milestone to create, not null
	 * @return newly created milestone
	 */
	Milestone create(Milestone milestone);

	/**
	 * Update an existing milestone
	 * 
	 * @param milestone
	 *            milestone to update, not null, must have an id
	 */
	void update(Milestone milestone);

	/**
	 * Delete an existing milestone
	 * 
	 * @param milestone
	 *            milestone to delete, not null, must have an id
	 */
	void delete(Milestone milestone);

}
