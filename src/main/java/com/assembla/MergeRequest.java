package com.assembla;

import java.time.ZonedDateTime;

import com.assembla.enums.IntValuedEnum;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName("merge_request")
public class MergeRequest {

	public enum Status implements IntValuedEnum {

		OPEN, CLOSED, IGNORED;

		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	private String processedByUserId;
	private State status;
	// Not documented
	private Object applyStatus;
	private String description;
	private String title;
	private String targetSpaceToolId;
	private String targetSpaceId;
	private ZonedDateTime appliedAt;
	private String targetSymbol;
	private SourceSymbolType sourceSymbolType;
	private String sourceSymbol;
	private String spaceToolId;
	private ZonedDateTime updatedAt;
	private String commit;
	private Integer id;
	private ZonedDateTime createdAt;
	private String userId;
	private String url;

	public String getProcessedByUserId() {
		return processedByUserId;
	}

	public MergeRequest setProcessedByUserId(String processedByUserId) {
		this.processedByUserId = processedByUserId;
		return this;
	}

	public State getStatus() {
		return status;
	}

	public MergeRequest setStatus(State status) {
		this.status = status;
		return this;
	}

	public Object getApplyStatus() {
		return applyStatus;
	}

	public MergeRequest setApplyStatus(Object applyStatus) {
		this.applyStatus = applyStatus;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public MergeRequest setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public MergeRequest setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getTargetSpaceToolId() {
		return targetSpaceToolId;
	}

	public MergeRequest setTargetSpaceToolId(String targetSpaceToolId) {
		this.targetSpaceToolId = targetSpaceToolId;
		return this;
	}

	public String getTargetSpaceId() {
		return targetSpaceId;
	}

	public MergeRequest setTargetSpaceId(String targetSpaceId) {
		this.targetSpaceId = targetSpaceId;
		return this;
	}

	public ZonedDateTime getAppliedAt() {
		return appliedAt;
	}

	public MergeRequest setAppliedAt(ZonedDateTime appliedAt) {
		this.appliedAt = appliedAt;
		return this;
	}

	public String getTargetSymbol() {
		return targetSymbol;
	}

	public MergeRequest setTargetSymbol(String targetSymbol) {
		this.targetSymbol = targetSymbol;
		return this;
	}

	public SourceSymbolType getSourceSymbolType() {
		return sourceSymbolType;
	}

	public MergeRequest setSourceSymbolType(SourceSymbolType sourceSymbolType) {
		this.sourceSymbolType = sourceSymbolType;
		return this;
	}

	public String getSourceSymbol() {
		return sourceSymbol;
	}

	public MergeRequest setSourceSymbol(String sourceSymbol) {
		this.sourceSymbol = sourceSymbol;
		return this;
	}

	public String getSpaceToolId() {
		return spaceToolId;
	}

	public MergeRequest setSpaceToolId(String spaceToolId) {
		this.spaceToolId = spaceToolId;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public MergeRequest setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public String getCommit() {
		return commit;
	}

	public MergeRequest setCommit(String commit) {
		this.commit = commit;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public MergeRequest setId(Integer id) {
		this.id = id;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public MergeRequest setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public MergeRequest setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public MergeRequest setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MergeRequest [");
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
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
