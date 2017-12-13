package com.assembla.service;

import com.assembla.WikiPage;
import com.assembla.WikiPageVersion;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;
import com.assembla.utils.ValidationUtils;

public class WikiService extends AbstractBaseService implements WikiPageResource {

	public WikiService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#getAll(com.assembla.client.Paging)
	 */
	@Override
	public PagedIterator<WikiPage> getAll(Paging paging) {
		String uri = String.format(AssemblaConstants.WIKI_PAGES, this.spaceId);
		PagedAssemblaRequest request = null;
		if (paging == null) {
			request = new PagedAssemblaRequest(uri, WikiPage[].class);
		} else {
			request = new PagedAssemblaRequest(uri, WikiPage[].class, paging.page(), paging.size());
		}
		return new PagedIterator<>(request, this.client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#get(java.lang.String)
	 */
	@Override
	public WikiPage get(String id) {
		ValidationUtils.notNull(id, "id == null");
		String uri = String.format(AssemblaConstants.WIKI_PAGE, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, WikiPage.class);
		return super.get(request, String.format("No Wiki Page with id %s", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#create(com.assembla.WikiPage)
	 */
	@Override
	public WikiPage create(WikiPage page) {
		ValidationUtils.notNull(page, "page == null");
		String uri = String.format(AssemblaConstants.WIKI_PAGES, this.spaceId);
		AssemblaRequest request = new AssemblaRequest(uri, WikiPage.class);
		request.withBody(page);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#update(com.assembla.WikiPage)
	 */
	@Override
	public void update(WikiPage page) {
		ValidationUtils.notNull(page, "page == null");
		ValidationUtils.notNull(page.getId(), "Wiki Page being updated requires an ID");
		String uri = String.format(AssemblaConstants.WIKI_PAGE, this.spaceId, page.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(page);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#deleteAll(java.lang.String)
	 */
	@Override
	public void deleteAll(String id) {
		ValidationUtils.notNull(id, "id == null");
		String uri = String.format(AssemblaConstants.WIKI_PAGE_ALL, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		ValidationUtils.notNull(id, "id == null");
		String uri = String.format(AssemblaConstants.WIKI_CONTAINER, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#getVersions(java.lang.String, com.assembla.client.Paging)
	 */
	@Override
	public PagedIterator<WikiPageVersion> getVersions(String id, Paging paging) {
		String uri = String.format(AssemblaConstants.WIKI_PAGE_VERSIONS, this.spaceId, id);
		PagedAssemblaRequest request = null;
		if (paging == null) {
			request = new PagedAssemblaRequest(uri, WikiPageVersion[].class);
		} else {
			request = new PagedAssemblaRequest(uri, WikiPageVersion[].class, paging.page(), paging.size());
		}
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.WikiPageResource#getVersion(java.lang.String, java.lang.String)
	 */
	@Override
	public WikiPageVersion getVersion(String id, String versionId) {
		ValidationUtils.notNull(id, "id == null");
		ValidationUtils.notNull(versionId, "versionId == null");
		String uri = String.format(AssemblaConstants.WIKI_PAGE_VERSION, this.spaceId, id, versionId);
		AssemblaRequest request = new AssemblaRequest(uri, WikiPageVersion.class);
		return super.get(request, String.format("No Wiki Page Version for id %s and version id %s", id, versionId));
	}

}
