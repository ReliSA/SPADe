package com.assembla.service;

import java.util.List;

import com.assembla.SpaceTool;
import com.assembla.exception.AssemblaAPIException;

public interface SpaceToolResource {
	/**
	 * Get list of space tools
	 * 
	 * @return List of all space tools installed in space
	 */
	List<SpaceTool> getAll();

	/**
	 * Get list of repositories
	 * 
	 * @return List of space tools which are also repositories and are installed
	 *         in space
	 */
	List<SpaceTool> getRepositories();

	/**
	 * Get space tool by id or name
	 * 
	 * @param idOrName
	 *            The space tool identifier or name, not null
	 * @return Space tool with identifier or name {@code idOrName}
	 * @throws AssemblaAPIException
	 *             if no space tool exists with id or name {@code idOrName}
	 */
	SpaceTool get(String idOrName);

	/**
	 * Create and add a new space tool
	 * 
	 * @param tool
	 *            Space tool to add, not null, tool id must be provided, not
	 *            null
	 * @return Newly created space tool
	 */
	SpaceTool create(SpaceTool tool);

	/**
	 * Update an existing space tool
	 * 
	 * @param tool
	 *            Space tool to update, not null
	 */
	void update(SpaceTool tool);

	/**
	 * Delete a space tool which is already installed on space
	 * 
	 * @param tool
	 *            Space tool to delete, not null
	 */
	void delete(SpaceTool tool);

}
