package com.assembla.service;

import java.util.List;

import com.assembla.MergeRequestComment;
import com.assembla.MergeRequestCommentMessage;
import com.assembla.MergeRequestVersion;
import com.assembla.MergeRequestVote;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.AssemblaRequest;
import com.assembla.client.AssemblaClient;
import com.assembla.client.PagedAssemblaRequest;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;

public class MergeRequestVersionService extends AbstractBaseService implements MergeRequestVersionResource {

	private int spaceToolId;

	public MergeRequestVersionService(AssemblaClient assemblaClient, String testSpaceId, int spaceToolId) {
		super(assemblaClient, testSpaceId);
		this.spaceToolId = spaceToolId;
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#getAll(int)
	 */
	@Override
	public List<MergeRequestVersion> getAll(int id) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VERSIONS, this.spaceId, this.spaceToolId, id);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequestVersion[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#get(int, int)
	 */
	@Override
	public MergeRequestVersion get(int id, int version) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VERSION, this.spaceId, this.spaceToolId, id, version);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequestVersion.class);
		return super.get(request, String.format("No Merge Request Version for id %d at version %d", id, version));
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#create(int)
	 */
	@Override
	public MergeRequestVersion create(int id) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VERSIONS, this.spaceId, this.spaceToolId, id);
		AssemblaRequest request = new AssemblaRequest(uri);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#getComments(int, int, com.assembla.client.Paging)
	 */
	@Override
	public PagedIterator<MergeRequestComment> getComments(int id, int version, Paging paging) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VERSION_COMMENTS, this.spaceId, this.spaceToolId, id,
				version);
		PagedAssemblaRequest request = null;
		if (paging == null) {
			request = new PagedAssemblaRequest(uri, MergeRequestComment[].class);
		} else {
			request = new PagedAssemblaRequest(uri, MergeRequestComment[].class, paging.page(), paging.size());
		}
		return new PagedIterator<>(request, client);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#createComment(int, int, java.lang.String)
	 */
	@Override
	public MergeRequestComment createComment(int id, int version, String message) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VERSION_COMMENTS, this.spaceId, this.spaceToolId, id,
				version);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequestComment.class);
		request.withBody(new MergeRequestCommentMessage(message));
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#getVotes(int, int)
	 */
	@Override
	public List<MergeRequestVote> getVotes(int id, int version) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VOTES, this.spaceId, this.spaceToolId, id, version);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequestVote[].class);
		return super.getList(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#upvote(int, int)
	 */
	@Override
	public MergeRequestVote upvote(int id, int version) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VOTE_UP, this.spaceId, this.spaceToolId, id, version);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequestVote.class);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#downvote(int, int)
	 */
	@Override
	public MergeRequestVote downvote(int id, int version) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VOTE_DOWN, this.spaceId, this.spaceToolId, id, version);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequestVote.class);
		return super.post(request);
	}

	/* (non-Javadoc)
	 * @see com.assembla.service.MergeRequestVersionResource#delete(int, int)
	 */
	@Override
	public MergeRequestVote delete(int id, int version) {
		String uri = String.format(AssemblaConstants.MERGE_REQUEST_VOTE_DELETE, this.spaceId, this.spaceToolId, id, version);
		AssemblaRequest request = new AssemblaRequest(uri, MergeRequestVote.class);
		return super.post(request);
	}

}
