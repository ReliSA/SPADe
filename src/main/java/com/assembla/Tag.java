package com.assembla;

import java.time.ZonedDateTime;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName(value = "tag")
public class Tag {

	public enum TagStatus implements IntValuedEnum {
		PROPOSED(1), ACTIVE(2), HIDDEN(4);

		private int value;

		TagStatus(int value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public Integer getValue() {
			return this.value;
		}

		@JsonCreator
		public static TagStatus parse(int value) {
			return ValuedEnum.parse(value, TagStatus.values());
		}

	}

	private Integer id;
	private String name;
	private String spaceId;
	private TagStatus state;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	public Integer getId() {
		return id;
	}

	public Tag setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Tag setName(String name) {
		this.name = name;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public Tag setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public TagStatus getState() {
		return state;
	}

	public Tag setState(TagStatus state) {
		this.state = state;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public Tag setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Tag setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tag [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (spaceId != null) {
			builder.append("spaceId=");
			builder.append(spaceId);
			builder.append(", ");
		}
		if (state != null) {
			builder.append("state=");
			builder.append(state);
		}
		builder.append("]");
		return builder.toString();
	}

}
