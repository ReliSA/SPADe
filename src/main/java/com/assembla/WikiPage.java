package com.assembla;

import java.time.ZonedDateTime;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName("wiki_page")
public class WikiPage {

	public enum WikiStatus implements IntValuedEnum{
		PRIVATE, ACTIVE, ARCHIVED;

		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	public enum WikiFormat implements IntValuedEnum {
		TEXT_FILE(2), HTML(3);

		int value;

		WikiFormat(int value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public Integer getValue() {
			return this.value;
		}

		@JsonCreator
		public static WikiFormat parse(int value) {
			return ValuedEnum.parse(value, WikiFormat.values());
		}

	}

	private String changeComment;
	private WikiStatus status;
	private String contents;
	private ZonedDateTime createdAt;
	private WikiFormat wikiFormat;
	private String pageName;
	private String parentId;
	private ZonedDateTime updatedAt;
	private Integer position;
	private String userId;
	private String spaceId;
	private String id;
	private Integer version;

	public String getChangeComment() {
		return changeComment;
	}

	public WikiPage setChangeComment(String changeComment) {
		this.changeComment = changeComment;
		return this;
	}

	public WikiStatus getStatus() {
		return status;
	}

	public WikiPage setStatus(WikiStatus status) {
		this.status = status;
		return this;
	}

	public String getContents() {
		return contents;
	}

	public WikiPage setContents(String contents) {
		this.contents = contents;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public WikiPage setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public WikiFormat getWikiFormat() {
		return wikiFormat;
	}

	public WikiPage setWikiFormat(WikiFormat wikiFormat) {
		this.wikiFormat = wikiFormat;
		return this;
	}

	public String getPageName() {
		return pageName;
	}

	public WikiPage setPageName(String pageName) {
		this.pageName = pageName;
		return this;
	}

	public String getParentId() {
		return parentId;
	}

	public WikiPage setParentId(String parentId) {
		this.parentId = parentId;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public WikiPage setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Integer getPosition() {
		return position;
	}

	public WikiPage setPosition(Integer position) {
		this.position = position;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public WikiPage setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public WikiPage setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public String getId() {
		return id;
	}

	public WikiPage setId(String id) {
		this.id = id;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public WikiPage setVersion(Integer version) {
		this.version = version;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WikiPage [");
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (pageName != null) {
			builder.append("pageName=");
			builder.append(pageName);
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
		}
		builder.append("]");
		return builder.toString();
	}

}
