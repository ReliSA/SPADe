package com.assembla;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("status")
public class TicketStatus {

	public Integer id;
	public String spaceToolId;
	public String name;
	public State state;
	public Integer listOrder;
	public ZonedDateTime createdAt;
	public ZonedDateTime updatedAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSpaceToolId() {
		return spaceToolId;
	}

	public void setSpaceToolId(String spaceToolId) {
		this.spaceToolId = spaceToolId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Integer getListOrder() {
		return listOrder;
	}

	public void setListOrder(Integer listOrder) {
		this.listOrder = listOrder;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TicketStatus [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (spaceToolId != null) {
			builder.append("spaceToolId=");
			builder.append(spaceToolId);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
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
