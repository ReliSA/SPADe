package com.assembla;

import java.time.ZonedDateTime;
import java.util.List;

import com.assembla.enums.ValuedEnum;
import com.assembla.serialization.NewLineToListDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonRootName(value = "custom_field")
public class CustomField {

	public enum FieldType implements ValuedEnum<String> {
		TEXT("text"), NUMERIC("numeric"), TEAM_LIST("team list"), LIST("list"), DATE("date");

		private String value;

		FieldType(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String getValue() {
			return value;
		}

		@JsonCreator
		public static FieldType parse(String value) {
			return ValuedEnum.parse(value.toLowerCase(), FieldType.values());
		}

	}

	private Integer id;
	private String spaceToolId;
	private FieldType type;
	private String title;
	private Integer order;
	private Boolean required;
	private Boolean hide;
	private String defaultValue;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
	@JsonDeserialize(using = NewLineToListDeserializer.class)
	private List<String> listOptions;

	public CustomField() {
	}

	public Integer getId() {
		return id;
	}

	public CustomField setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getSpaceToolId() {
		return spaceToolId;
	}

	public CustomField setSpaceToolId(String spaceToolId) {
		this.spaceToolId = spaceToolId;
		return this;
	}

	public FieldType getType() {
		return type;
	}

	public CustomField setType(FieldType type) {
		this.type = type;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public CustomField setTitle(String title) {
		this.title = title;
		return this;
	}

	public Integer getOrder() {
		return order;
	}

	public CustomField setOrder(Integer order) {
		this.order = order;
		return this;
	}

	public Boolean getRequired() {
		return required;
	}

	public CustomField setRequired(Boolean required) {
		this.required = required;
		return this;
	}

	public Boolean getHide() {
		return hide;
	}

	public CustomField setHide(Boolean hide) {
		this.hide = hide;
		return this;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public CustomField setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public CustomField setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public CustomField setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomField [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (type != null) {
			builder.append("type=");
			builder.append(type);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
		}
		builder.append("]");
		return builder.toString();
	}

}
