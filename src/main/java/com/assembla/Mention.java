package com.assembla;

import java.time.ZonedDateTime;

public class Mention {

	private Integer id;
	private String authorId;
	private String link;
	private String message;
	private Boolean read;
	private ZonedDateTime createdAt;

	public Integer getId() {
		return id;
	}

	public Mention setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getAuthorId() {
		return authorId;
	}

	public Mention setAuthorId(String authorId) {
		this.authorId = authorId;
		return this;
	}

	public String getLink() {
		return link;
	}

	public Mention setLink(String link) {
		this.link = link;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Mention setMessage(String message) {
		this.message = message;
		return this;
	}

	public Boolean getRead() {
		return read;
	}

	public Mention setRead(Boolean read) {
		this.read = read;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public Mention setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Mention [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (authorId != null) {
			builder.append("authorId=");
			builder.append(authorId);
			builder.append(", ");
		}
		if (link != null) {
			builder.append("link=");
			builder.append(link);
			builder.append(", ");
		}
		if (message != null) {
			builder.append("message=");
			builder.append(message);
			builder.append(", ");
		}
		if (read != null) {
			builder.append("read=");
			builder.append(read);
		}
		builder.append("]");
		return builder.toString();
	}

}
