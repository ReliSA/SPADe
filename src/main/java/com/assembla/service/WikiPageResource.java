package com.assembla.service;

import com.assembla.WikiPage;
import com.assembla.WikiPageVersion;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;

public interface WikiPageResource {

	PagedIterator<WikiPage> getAll(Paging paging);

	WikiPage get(String id);

	WikiPage create(WikiPage page);

	void update(WikiPage page);

	void deleteAll(String id);

	void delete(String id);

	PagedIterator<WikiPageVersion> getVersions(String id, Paging paging);

	WikiPageVersion getVersion(String id, String versionId);

}
