package com.assembla.service;

import java.util.List;

import com.assembla.UserRole;
import com.assembla.exception.AssemblaAPIException;

public interface UserRoleResource {
	/**
	 * Get a list of all user roles for space
	 * 
	 * @return List of all user roles for space
	 */
	List<UserRole> getAll();

	/**
	 * Get a user role by id
	 * 
	 * @param id
	 *            Id of user role to get
	 * @return User role with id {@code id}
	 * @throws AssemblaAPIException
	 *             if no user role exists with id {@code id}
	 */
	UserRole get(long id);

	/**
	 * Create a new user role
	 * 
	 * @param role
	 *            User role to create, not null
	 * @return Newly created user role
	 */
	UserRole create(UserRole role);

	/**
	 * Update existing user role
	 * 
	 * @param role
	 *            User role to update, not null
	 */
	void update(UserRole role);

	/**
	 * Delete an existing user role
	 * 
	 * @param role
	 *            User role to update, not null
	 */
	void delete(UserRole role);

}
