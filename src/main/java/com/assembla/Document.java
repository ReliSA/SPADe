package com.assembla;

import java.net.URL;
import java.time.ZonedDateTime;

import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName("document")
public class Document {
	
	public enum AttachableType implements ValuedEnum<String> {
		TICKET("Ticket"), FLOW("Flow"), MILESTONE("Milestone");

		private String value;

		private AttachableType(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String getValue() {
			return value;
		}

		@JsonCreator
		public static AttachableType parse(String value) {
			return ValuedEnum.parse(value, AttachableType.values());
		}

	}

	private String name;
	private String filename;
	private String contentType;
	private String createdBy;
	private String id;
	private Integer version;
	private Integer filesize;
	private String updatedBy;
	private String description;
	private String cachedTagList;
	private Integer position;
	private URL url;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
	private Integer ticketId;
	private AttachableType attachableType;
	private Boolean hasThumbnail;
	private String spaceId;
	private Integer attachableId;
	private String attachableGuid;

	public String getName() {
		return name;
	}

	public Document setName(String name) {
		this.name = name;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public Document setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public Document setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Document setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public String getId() {
		return id;
	}

	public Document setId(String id) {
		this.id = id;
		return this;
	}

	public Integer getVersion() {
		return version;
	}

	public Document setVersion(Integer version) {
		this.version = version;
		return this;
	}

	public Integer getFilesize() {
		return filesize;
	}

	public Document setFilesize(Integer filesize) {
		this.filesize = filesize;
		return this;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public Document setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Document setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getCachedTagList() {
		return cachedTagList;
	}

	public Document setCachedTagList(String cachedTagList) {
		this.cachedTagList = cachedTagList;
		return this;
	}

	public Integer getPosition() {
		return position;
	}

	public Document setPosition(Integer position) {
		this.position = position;
		return this;
	}

	public URL getUrl() {
		return url;
	}

	public Document setUrl(URL url) {
		this.url = url;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public Document setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Document setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Integer getTicketId() {
		return ticketId;
	}

	public Document setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
		return this;
	}

	public AttachableType getAttachableType() {
		return attachableType;
	}

	public Document setAttachableType(AttachableType attachableType) {
		this.attachableType = attachableType;
		return this;
	}

	public Boolean getHasThumbnail() {
		return hasThumbnail;
	}

	public Document setHasThumbnail(Boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public Document setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public Integer getAttachableId() {
		return attachableId;
	}

	public Document setAttachableId(Integer attachableId) {
		this.attachableId = attachableId;
		return this;
	}

	public String getAttachableGuid() {
		return attachableGuid;
	}

	public Document setAttachableGuid(String attachableGuid) {
		this.attachableGuid = attachableGuid;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Document [");
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (filename != null) {
			builder.append("filename=");
			builder.append(filename);
			builder.append(", ");
		}
		if (contentType != null) {
			builder.append("contentType=");
			builder.append(contentType);
			builder.append(", ");
		}
		if (createdBy != null) {
			builder.append("createdBy=");
			builder.append(createdBy);
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
			builder.append(", ");
		}
		if (filesize != null) {
			builder.append("filesize=");
			builder.append(filesize);
			builder.append(", ");
		}
		if (updatedBy != null) {
			builder.append("updatedBy=");
			builder.append(updatedBy);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (cachedTagList != null) {
			builder.append("cachedTagList=");
			builder.append(cachedTagList);
			builder.append(", ");
		}
		if (position != null) {
			builder.append("position=");
			builder.append(position);
			builder.append(", ");
		}
		if (url != null) {
			builder.append("url=");
			builder.append(url);
			builder.append(", ");
		}
		if (createdAt != null) {
			builder.append("createdAt=");
			builder.append(createdAt);
			builder.append(", ");
		}
		if (updatedAt != null) {
			builder.append("updatedAt=");
			builder.append(updatedAt);
			builder.append(", ");
		}
		if (ticketId != null) {
			builder.append("ticketId=");
			builder.append(ticketId);
			builder.append(", ");
		}
		if (attachableType != null) {
			builder.append("attachableType=");
			builder.append(attachableType);
			builder.append(", ");
		}
		if (hasThumbnail != null) {
			builder.append("hasThumbnail=");
			builder.append(hasThumbnail);
			builder.append(", ");
		}
		if (spaceId != null) {
			builder.append("spaceId=");
			builder.append(spaceId);
			builder.append(", ");
		}
		if (attachableId != null) {
			builder.append("attachableId=");
			builder.append(attachableId);
			builder.append(", ");
		}
		if (attachableGuid != null) {
			builder.append("attachableGuid=");
			builder.append(attachableGuid);
		}
		builder.append("]");
		return builder.toString();
	}


}
