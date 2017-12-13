package com.assembla;

import java.time.ZonedDateTime;

public class WikiPageVersion {

	private String changeComment;
	private String contents;
	private String wikiPageId;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
	private String userId;
	private Integer id;
	private Integer version;

	public String getChangeComment() {
		return changeComment;
	}

	public WikiPageVersion setChangeComment(String changeComment) {
		this.changeComment = changeComment;
		return this;
	}

	public String getContents() {
		return contents;
	}

	public WikiPageVersion setContents(String contents) {
		this.contents = contents;
		return this;
	}

	public String getWikiPageId() {
		return wikiPageId;
	}

	public WikiPageVersion setWikiPageId(String wikiPageId) {
		this.wikiPageId = wikiPageId;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public WikiPageVersion setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public WikiPageVersion setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public WikiPageVersion setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public WikiPageVersion setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public WikiPageVersion setVersion(Integer version) {
		this.version = version;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WikiPageVersion [");
		if (changeComment != null) {
			builder.append("changeComment=");
			builder.append(changeComment);
			builder.append(", ");
		}
		if (userId != null) {
			builder.append("userId=");
			builder.append(userId);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (version != null) {
			builder.append("version=");
			builder.append(version);
		}
		builder.append("]");
		return builder.toString();
	}

}
