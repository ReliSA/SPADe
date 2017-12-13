package com.assembla.service;

import java.util.List;

import com.assembla.CustomField;

public interface CustomFieldResource {
	/**
	 * Get all ticket custom fields for current space
	 * 
	 * @return List of ticket custom fields associated with current space
	 */
	List<CustomField> getAll();

	/**
	 * Get a ticket custom field by id
	 * 
	 * @param id
	 *            id of ticket custom field
	 * @return ticket custom field with id {@code id}
	 */
	CustomField get(int id);

	/**
	 * Create a new ticket custom field
	 * 
	 * @param customField
	 *            custom field to create, not null
	 * @return newly created ticket custom field
	 */
	CustomField create(CustomField customField);

	/**
	 * Update an existing ticket custom field
	 * 
	 * @param customField
	 *            ticket custom field to update, not null, must have an id
	 */
	void update(CustomField customField);

	/**
	 * Delete an existing ticket custom field
	 * 
	 * @param customField
	 *            ticket custom field to delete, not null, must have an id
	 */
	void delete(CustomField customField);

}
