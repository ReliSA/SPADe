package com.assembla;

import java.time.ZonedDateTime;

public class MergeRequestVote {

	private Integer mergeRequestId;
	private Integer mergeRequestVersionId;
	private ZonedDateTime updatedAt;
	private Integer id;
	private ZonedDateTime createdAt;
	private Integer vote;
	private String userId;

	public Integer getMergeRequestId() {
		return mergeRequestId;
	}

	public MergeRequestVote setMergeRequestId(Integer mergeRequestId) {
		this.mergeRequestId = mergeRequestId;
		return this;
	}

	public Integer getMergeRequestVersionId() {
		return mergeRequestVersionId;
	}

	public MergeRequestVote setMergeRequestVersionId(Integer mergeRequestVersionId) {
		this.mergeRequestVersionId = mergeRequestVersionId;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public MergeRequestVote setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public MergeRequestVote setId(Integer id) {
		this.id = id;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public MergeRequestVote setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Integer getVote() {
		return vote;
	}

	public MergeRequestVote setVote(Integer vote) {
		this.vote = vote;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public MergeRequestVote setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MergeRequestVote [");
		if (mergeRequestId != null) {
			builder.append("mergeRequestId=");
			builder.append(mergeRequestId);
			builder.append(", ");
		}
		if (mergeRequestVersionId != null) {
			builder.append("mergeRequestVersionId=");
			builder.append(mergeRequestVersionId);
			builder.append(", ");
		}
		if (updatedAt != null) {
			builder.append("updatedAt=");
			builder.append(updatedAt);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (createdAt != null) {
			builder.append("createdAt=");
			builder.append(createdAt);
			builder.append(", ");
		}
		if (vote != null) {
			builder.append("vote=");
			builder.append(vote);
			builder.append(", ");
		}
		if (userId != null) {
			builder.append("userId=");
			builder.append(userId);
		}
		builder.append("]");
		return builder.toString();
	}

}
