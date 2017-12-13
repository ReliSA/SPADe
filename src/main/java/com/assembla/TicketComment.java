package com.assembla;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "ticket_comment")
public class TicketComment {

	private String ticketChanges;
	private Integer id;
	private ZonedDateTime createdOn;
	private Integer ticketId;
	private String comment;
	private ZonedDateTime updatedAt;

	private String userId;

	public String getTicketChanges() {
		return ticketChanges;
	}

	public TicketComment setTicketChanges(String ticketChanges) {
		this.ticketChanges = ticketChanges;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public TicketComment setId(Integer id) {
		this.id = id;
		return this;
	}

	public ZonedDateTime getCreatedOn() {
		return createdOn;
	}

	public TicketComment setCreatedOn(ZonedDateTime createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public Integer getTicketId() {
		return ticketId;
	}

	public TicketComment setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public TicketComment setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public TicketComment setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public TicketComment setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TicketComment [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (ticketId != null) {
			builder.append("ticketId=");
			builder.append(ticketId);
			builder.append(", ");
		}
		if (comment != null) {
			builder.append("comment=");
			builder.append(comment);
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
