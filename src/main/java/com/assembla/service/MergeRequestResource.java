package com.assembla.service;

import com.assembla.MergeRequest;
import com.assembla.MergeRequestComment;
import com.assembla.Ticket;
import com.assembla.MergeRequest.Status;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;
import com.assembla.exception.AssemblaAPIException;

public interface MergeRequestResource {
	/**
	 * Current space tool id
	 * 
	 * @return space tool id of this resource
	 */
	int getSpaceToolId();

	/**
	 * Update current space tool id
	 * 
	 * @param spaceToolId
	 *            id of space tool to use
	 */
	void setSpaceToolId(int spaceToolId);

	/**
	 * Get all merge requests
	 * 
	 * @return {@link PagedIterator} of all merge requests for current space and
	 *         space tool
	 */
	PagedIterator<MergeRequest> getAll();

	/**
	 * Get all merge requests, specify paging and status
	 * 
	 * @param paging
	 *            paging object to use, null will use defaults
	 * @param status
	 *            staus of merge requests, not null
	 * @return list of merge requests with {@code status} initial page and page
	 *         size set by {@code paging}
	 */
	PagedIterator<MergeRequest> getAll(Paging paging, Status status);

	/**
	 * Create a new merge request
	 * 
	 * @param mergeRequest
	 *            merge request to create, not null
	 * @return newly created merge request
	 */
	MergeRequest create(MergeRequest mergeRequest);

	/**
	 * Get merge request by id
	 * 
	 * @param id
	 *            id of the merge request
	 * @return merge request with id {@code id}
	 * @throws AssemblaAPIException
	 *             if no merge request exists with id {@code id}
	 */
	MergeRequest get(int id);

	/**
	 * Get merge request comments for a merge request
	 * 
	 * @param mergeRequestId
	 *            id of merge request to get merge request comments for
	 * @param paging
	 *            paging to use, null will use defaults
	 * @return {@link PagedIterator} of merge request comments for merge request
	 *         with id {@code id}, initial page and page size set by
	 *         {@code paging}
	 */
	PagedIterator<MergeRequestComment> getComments(int mergeRequestId, Paging paging);

	/**
	 * Merge and close a merge request
	 * 
	 * @param id
	 *            merge request id
	 */
	void mergeAndClose(int id);

	/**
	 * Ignore a merge request
	 * 
	 * @param id
	 *            merge request id
	 */
	void ignore(int id);

	/**
	 * Get tickets association with a merge request
	 * 
	 * @param id
	 *            id of merge request
	 * @param paging
	 *            paging to use, null will use defaults
	 * @return {@link PagedIterator} of {@link Ticket} for merge request with id
	 *         {@code id} initial page and page size set by {@code paging}
	 */
	PagedIterator<Ticket> associatedTickets(int id, Paging paging);

}
