package com.assembla;

import java.time.ZonedDateTime;

public class MergeRequestComment {

	private Integer mergeRequestId;
	private String content;
	private ZonedDateTime updatedAt;
	private String spaceId;
	private Integer id;
	private ZonedDateTime createdAt;
	private String userId;

	public Integer getMergeRequestId() {
		return mergeRequestId;
	}

	public MergeRequestComment setMergeRequestId(Integer mergeRequestId) {
		this.mergeRequestId = mergeRequestId;
		return this;
	}

	public String getContent() {
		return content;
	}

	public MergeRequestComment setContent(String content) {
		this.content = content;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public MergeRequestComment setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public MergeRequestComment setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public MergeRequestComment setId(Integer id) {
		this.id = id;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public MergeRequestComment setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public MergeRequestComment setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MergeRequestComment [");
		if (content != null) {
			builder.append("content=");
			builder.append(content);
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
		if (userId != null) {
			builder.append("userId=");
			builder.append(userId);
		}
		builder.append("]");
		return builder.toString();
	}

}
