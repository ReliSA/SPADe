package com.assembla.service;

import com.assembla.MergeRequest;
import com.assembla.MergeRequest.Status;
import com.assembla.MergeRequestComment;
import com.assembla.Ticket;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;
import com.assembla.utils.ValidationUtils;

public class MergeRequestService extends AbstractBaseService implements MergeRequestResource {

	private int spaceToolId;

	public MergeRequestService(AssemblaClient assemblaClient, String spaceId, int spaceToolId) {
		super(assemblaClient, spaceId);
		this.spaceToolId = spaceToolId;
	}
	
	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#getSpaceToolId()
	 */
	@Override
	public int getSpaceToolId() {
		return spaceToolId;
	}
	
	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#setSpaceToolId(int)
	 */
	@Override
	public void setSpaceToolId(int spaceToolId) {
		this.spaceToolId = spaceToolId;
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#getAll()
	 */
	@Override
	public PagedIterator<MergeRequest> getAll() {
		String uri = String.format(AssemblaConstants.MERGE_REQUESTS, this.spaceId, this.spaceToolId);
		PagedAssemblaRequest request = new PagedAssemblaRequest(uri, MergeRequest[].class);
		return new PagedIterator<>(request, super.client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#getAll(com.assembla.client.Paging, com.assembla.MergeRequest.Status)
	 */
	@Override
	public PagedIterator<MergeRequest> getAll(Paging paging, Status status) {
		String uri = String.format(AssemblaConstants.MERGE_REQUESTS, this.spaceId, this.spaceToolId);
		PagedAssemblaRequest request = null;
		if (paging == null) {
			request = new PagedAssemblaRequest(uri, MergeRequest[].class);
		} else {
			request = new PagedAssemblaRequest(uri, MergeRequest[].class, paging.page(), paging.size());
		}
		request.addParam("status", status.toString().toLowerCase());
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#create(com.assembla.MergeRequest)
	 */
	@Override
	public MergeRequest create(MergeRequest mergeRequest) {
		ValidationUtils.notNull(mergeRequest, "mergeRequest == null");
		String uri = String.format(AssemblaConstants.MERGE_REQUESTS, this.spaceId, this.spaceToolId);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequest.class);
		request.withBody(mergeRequest);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#get(int)
	 */
	@Override
	public MergeRequest get(int id) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_BY_ID, this.spaceId, this.spaceToolId, id);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequest.class);
		return super.get(request, String.format("Merge Request with id %d does not exist", id));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#getComments(int, com.assembla.client.Paging)
	 */
	@Override
	public PagedIterator<MergeRequestComment> getComments(int mergeRequestId, Paging paging) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_COMMENTS, this.spaceId, this.spaceToolId, mergeRequestId);
		PagedAssemblaRequest request = null;
		if (paging == null) {
			request = new PagedAssemblaRequest(uri, MergeRequestComment[].class);
		} else {
			request = new PagedAssemblaRequest(uri, MergeRequestComment[].class, paging.page(), paging.size());
		}
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#mergeAndClose(int)
	 */
	@Override
	public void mergeAndClose(int id) {
		String uri = String.format(AssemblaConstants.MERGE_AND_CLOSE, this.spaceId, this.spaceToolId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.put(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#ignore(int)
	 */
	@Override
	public void ignore(int id) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_IGNORE, this.spaceId, this.spaceToolId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		super.client.put(request);
		
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestResource#associatedTickets(int, com.assembla.client.Paging)
	 */
	@Override
	public PagedIterator<Ticket> associatedTickets(int id, Paging paging) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_TICKETS, this.spaceId, this.spaceToolId, id);
		PagedAssemblaRequest request = null;
		if (paging == null) {
			request = new PagedAssemblaRequest(uri, Ticket[].class);
		} else {
			request = new PagedAssemblaRequest(uri, Ticket[].class, paging.page(), paging.size());
		}
		return new PagedIterator<>(request, client);
	}

}
