package com.assembla.service;

import java.io.File;

import com.assembla.Document;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;
import com.assembla.exception.AssemblaAPIException;

public interface DocumentResource {
	/**
	 * Get all documents for current space
	 * 
	 * @param paging
	 *            {@link Paging} object to use to page results, use defaults if
	 *            null
	 * @return {@link PagedIterator} of all documents for current space, initial
	 *         page and page size set by {@code paging}
	 */
	PagedIterator<Document> getAll(Paging paging);

	/**
	 * Get a document by id
	 * 
	 * @param id
	 *            document id, not null
	 * @return document with id {@code id}
	 * @throws AssemblaAPIException
	 *             if no document exists with {@code id }
	 */
	Document get(String id);

	/**
	 * Create a new document
	 * 
	 * @param file
	 *            File representation of document to be created, not null, must
	 *            exist
	 * @param location
	 *            Initial location to upload document to, can be null
	 * @return newly created document
	 */
	Document create(File file, String location);

	/**
	 * Update an existing document
	 * 
	 * @param document
	 *            document to update, not null, must have an id
	 */
	void update(Document document);

	/**
	 * Update file represented by an existing document
	 * 
	 * @param id
	 *            id of document, not null
	 * @param file
	 *            new file to upload to replace existing file for document with
	 *            id {@code id}, not null, must exist
	 * @param location
	 *            location to upload document, can be null
	 * @return newly updated document
	 */
	Document update(String id, File file, String location);

	/**
	 * Delete an existing document
	 * 
	 * @param id
	 *            id of document to delete , not null
	 */
	void delete(String id);

}
