package com.assembla.service;

import java.io.File;

import com.assembla.Document;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;
import com.assembla.client.UploadAssemblaRequest;
import com.assembla.client.UploadableDocument;
import com.assembla.utils.ValidationUtils;

public class DocumentService extends AbstractBaseService implements DocumentResource {

	public DocumentService(AssemblaClient assemblaClient, String spaceId) {
		super(assemblaClient, spaceId);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.DocumentResource#getAll(com.assembla.client.Paging)
	 */
	@Override
	public PagedIterator<Document> getAll(Paging paging) {
		String uri = String.format(AssemblaConstants.DOCUMENTS_ALL, this.spaceId);
		PagedAssemblaRequest request = null;
		if (paging == null) {
			request = new PagedAssemblaRequest(uri, Document[].class);
		} else {
			request = new PagedAssemblaRequest(uri, Document[].class, paging.page(), paging.size());
		}
		return new PagedIterator<>(request, super.client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.DocumentResource#get(java.lang.String)
	 */
	@Override
	public Document get(String id) {
		ValidationUtils.notNull(id, "id == null");
		String uri = String.format(AssemblaConstants.DOCUMENT, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri, Document.class);
		return super.get(request, String.format("Document with id %s does not exist", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.DocumentResource#create(java.io.File, java.lang.String)
	 */
	@Override
	public Document create(File file, String location) {
		String uri = String.format(AssemblaConstants.DOCUMENTS_ALL_NO_EXTENSION, this.spaceId);
		UploadableDocument uploadWrapper = new UploadableDocument(file, location);
		UploadAssemblaRequest request = new UploadAssemblaRequest(uri, Document.class, uploadWrapper);
		return client.post(request).<Document> getValue().orElse(null);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.DocumentResource#update(com.assembla.Document)
	 */
	@Override
	public void update(Document document) {
		ValidationUtils.notNull(document, "document == null");
		String uri = String.format(AssemblaConstants.DOCUMENT, this.spaceId, document.getId());
		AssemblaRequest request = new AssemblaRequest(uri);
		request.withBody(document);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.DocumentResource#update(java.lang.String, java.io.File, java.lang.String)
	 */
	@Override
	public Document update(String id, File file, String location) {
		String uri = String.format(AssemblaConstants.DOCUMENT_NO_EXTENSION, this.spaceId, id);
		UploadableDocument uploadWrapper = new UploadableDocument(file, location);
		UploadAssemblaRequest request = new UploadAssemblaRequest(uri, Document.class, uploadWrapper);
		return client.put(request).<Document> getValue().orElse(null);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.DocumentResource#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		ValidationUtils.notNull(id, "id == null");	
		String uri = String.format(AssemblaConstants.DOCUMENT, this.spaceId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.delete(request);
	}
}
