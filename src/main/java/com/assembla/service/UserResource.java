package com.assembla.service;

import java.util.List;

import com.assembla.User;
import com.assembla.exception.AssemblaAPIException;

public interface UserResource {
	/**
	 * Get currently authenticated user
	 * 
	 * @return Currently authenticated user
	 */
	User get();

	/**
	 * Get user by id or login name
	 * 
	 * @param idOrLogin
	 *            id or login name of user to get, not null
	 * @return User with id or login matching {@code idOrLogin}
	 * @throws AssemblaAPIException
	 *             if no user exists with id or login name matching
	 *             {@code idOrLogin}
	 */
	User get(String idOrLogin);

	/**
	 * List of users for current space
	 * 
	 * @return List of users for current space
	 */
	List<User> getForSpace();

}
