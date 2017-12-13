package com.assembla;

import java.time.ZonedDateTime;

public class Event {

	private String object;
	private String objectId;
	private String url;
	private String spaceId;
	private ZonedDateTime date;
	private String authorId;
	private String operation;
	private String title;
	private String spaceName;
	private String authorName;
	private String whatchanged;
	private Ticket ticket;
	private String commentOrDescription;
	private String ticketOperation;

	public String getObject() {
		return object;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getUrl() {
		return url;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public ZonedDateTime getDate() {
		return date;
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getOperation() {
		return operation;
	}

	public String getTitle() {
		return title;
	}

	public String getSpaceName() {
		return spaceName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getWhatchanged() {
		return whatchanged;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public String getCommentOrDescription() {
		return commentOrDescription;
	}
	
	public String getTicketOperation() {
		return ticketOperation;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Event [");
		if (object != null) {
			builder.append("object=");
			builder.append(object);
			builder.append(", ");
		}
		if (date != null) {
			builder.append("date=");
			builder.append(date);
			builder.append(", ");
		}
		if (authorId != null) {
			builder.append("authorId=");
			builder.append(authorId);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (spaceName != null) {
			builder.append("spaceName=");
			builder.append(spaceName);
			builder.append(", ");
		}
		if (authorName != null) {
			builder.append("authorName=");
			builder.append(authorName);
		}
		builder.append("]");
		return builder.toString();
	}
}
