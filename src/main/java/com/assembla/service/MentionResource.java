package com.assembla.service;

import java.util.List;

import com.assembla.Mention;
import com.assembla.exception.AssemblaAPIException;

public interface MentionResource {
	/**
	 * List of all mentions for currently authenticated user
	 * 
	 * @return List of mentions
	 */
	List<Mention> getAll();

	/**
	 * List of all read mentions for currently authenticated user
	 * 
	 * @return List of mentions
	 */
	List<Mention> getRead();

	/**
	 * List of all unread mentions for currently authenticated user
	 * 
	 * @return List of mentions
	 */
	List<Mention> getUnread();

	/**
	 * Get a mention by id
	 * 
	 * @param id
	 *            id of mention to return
	 * @return Mention with {@code id}
	 * @throws AssemblaAPIException
	 *             if no mention exists with id {@code id}
	 */
	Mention get(int id);

	/**
	 * Mark a mention as read by id
	 * 
	 * @param id
	 *            id of the mention to mark as read
	 */
	void markAsRead(int id);

}
