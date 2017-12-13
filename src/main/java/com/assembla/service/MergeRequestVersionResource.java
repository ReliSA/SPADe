package com.assembla.service;

import java.util.List;

import com.assembla.MergeRequestComment;
import com.assembla.MergeRequestVersion;
import com.assembla.MergeRequestVote;
import com.assembla.client.PagedIterator;
import com.assembla.client.Paging;

public interface MergeRequestVersionResource {

	List<MergeRequestVersion> getAll(int id);

	MergeRequestVersion get(int id, int version);

	MergeRequestVersion create(int id);

	PagedIterator<MergeRequestComment> getComments(int id, int version, Paging paging);

	MergeRequestComment createComment(int id, int version, String message);

	List<MergeRequestVote> getVotes(int id, int version);

	MergeRequestVote upvote(int id, int version);

	MergeRequestVote downvote(int id, int version);

	MergeRequestVote delete(int id, int version);

}
