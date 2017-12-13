package com.assembla;

import java.time.ZonedDateTime;

public class MergeRequestVersion {

	private Integer mergeRequestId;
	private String sourceRevision;
	private SourceSymbolType sourceSymbolType;
	private String sourceSymbol;
	private Boolean latest;
	private String targetRevision;
	private Integer version;
	private ZonedDateTime updatedAt;
	private Integer id;
	private ZonedDateTime createdAt;
	private String userId;
	private String url;
	private String processedByUserId;

	public Integer getMergeRequestId() {
		return mergeRequestId;
	}

	public MergeRequestVersion setMergeRequestId(Integer mergeRequestId) {
		this.mergeRequestId = mergeRequestId;
		return this;
	}

	public String getSourceRevision() {
		return sourceRevision;
	}

	public MergeRequestVersion setSourceRevision(String sourceRevision) {
		this.sourceRevision = sourceRevision;
		return this;
	}

	public SourceSymbolType getSourceSymbolType() {
		return sourceSymbolType;
	}

	public MergeRequestVersion setSourceSymbolType(SourceSymbolType sourceSymbolType) {
		this.sourceSymbolType = sourceSymbolType;
		return this;
	}

	public String getSourceSymbol() {
		return sourceSymbol;
	}

	public MergeRequestVersion setSourceSymbol(String sourceSymbol) {
		this.sourceSymbol = sourceSymbol;
		return this;
	}

	public Boolean getLatest() {
		return latest;
	}

	public MergeRequestVersion setLatest(Boolean latest) {
		this.latest = latest;
		return this;
	}

	public String getTargetRevision() {
		return targetRevision;
	}

	public MergeRequestVersion setTargetRevision(String targetRevision) {
		this.targetRevision = targetRevision;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public MergeRequestVersion setVersion(Integer version) {
		this.version = version;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public MergeRequestVersion setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public MergeRequestVersion setId(Integer id) {
		this.id = id;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public MergeRequestVersion setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public MergeRequestVersion setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public MergeRequestVersion setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getProcessedByUserId() {
		return processedByUserId;
	}

	public MergeRequestVersion setProcessedByUserId(String processedByUserId) {
		this.processedByUserId = processedByUserId;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MergeRequestVersion [");
		if (sourceSymbolType != null) {
			builder.append("sourceSymbolType=");
			builder.append(sourceSymbolType);
			builder.append(", ");
		}
		if (latest != null) {
			builder.append("latest=");
			builder.append(latest);
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
		}
		builder.append("]");
		return builder.toString();
	}

}
