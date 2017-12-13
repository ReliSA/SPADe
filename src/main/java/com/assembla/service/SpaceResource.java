package com.assembla.service;

import java.util.List;

import com.assembla.Space;
import com.assembla.exception.AssemblaAPIException;

public interface SpaceResource {
	/**
	 * Get list spaces user is participating in.
	 * 
	 * @return List of spaces
	 */
	List<Space> getAll();

	/**
	 * Get space by id
	 * 
	 * @param id
	 *            id of space to return
	 * @return Space with id {@code id}
	 * @throws AssemblaAPIException
	 *             if no space exists with id {@code id}
	 */
	Space get(String id);

	/**
	 * Create a new space
	 * 
	 * @param space
	 *            Space to create, not null
	 * @return New Space
	 */
	Space create(Space space);

	/**
	 * Update a space
	 * 
	 * @param space
	 *            Space to update, not null, must have an id
	 */
	void update(Space space);

	/**
	 * Delete a space
	 * 
	 * @param space
	 *            Space to delete, not null
	 */
	void delete(Space space);

	/**
	 * Create a new space by copying an existing space
	 * 
	 * @param oldSpace
	 *            Space to copy, not null
	 * @param name
	 *            Name of the new space, not null
	 * @param wikiName
	 *            Name of wiki format on space creation, not null
	 * @return New Space which has been created
	 */
	Space copy(Space oldSpace, String name, String wikiName);

}
