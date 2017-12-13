package com.assembla;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonRootName;
@JsonRootName("user_task")
public class Task {

	private Boolean billed;
	private ZonedDateTime createdAt;
	private Integer ticketNumber;
	private String spaceId;
	private ZonedDateTime beginAt;
	private String url;
	private String description;
	private ZonedDateTime updatedAt;
	private Integer jobAgreementId;
	private String userId;
	private Integer ticketId;
	private ZonedDateTime endAt;
	private Double hours;
	private Integer id;

	public Boolean getBilled() {
		return billed;
	}

	public Task setBilled(Boolean billed) {
		this.billed = billed;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public Task setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Integer getTicketNumber() {
		return ticketNumber;
	}

	public Task setTicketNumber(Integer ticketNumber) {
		this.ticketNumber = ticketNumber;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public Task setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public ZonedDateTime getBeginAt() {
		return beginAt;
	}

	public Task setBeginAt(ZonedDateTime beginAt) {
		this.beginAt = beginAt;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public Task setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Task setDescription(String description) {
		this.description = description;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Task setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Integer getJobAgreementId() {
		return jobAgreementId;
	}

	public Task setJobAgreementId(Integer jobAgreementId) {
		this.jobAgreementId = jobAgreementId;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public Task setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public Integer getTicketId() {
		return ticketId;
	}

	public Task setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
		return this;
	}

	public ZonedDateTime getEndAt() {
		return endAt;
	}

	public Task setEndAt(ZonedDateTime endAt) {
		this.endAt = endAt;
		return this;
	}

	public Double getHours() {
		return hours;
	}

	public Task setHours(Double hours) {
		this.hours = hours;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public Task setId(Integer id) {
		this.id = id;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Task [");
		if (spaceId != null) {
			builder.append("spaceId=");
			builder.append(spaceId);
			builder.append(", ");
		}
		if (url != null) {
			builder.append("url=");
			builder.append(url);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (updatedAt != null) {
			builder.append("updatedAt=");
			builder.append(updatedAt);
			builder.append(", ");
		}
		if (userId != null) {
			builder.append("userId=");
			builder.append(userId);
			builder.append(", ");
		}
		if (ticketId != null) {
			builder.append("ticketId=");
			builder.append(ticketId);
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
