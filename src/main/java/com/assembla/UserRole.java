package com.assembla;

import java.time.ZonedDateTime;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName("user_role")
public class UserRole {
	
	public enum UserRoleStatus implements IntValuedEnum {
		INVITED(1), ACCEPTED(2), REMOVED(3);

		private int value;

		UserRoleStatus(int value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public Integer getValue() {
			return this.value;
		}

		@JsonCreator
		public static UserRoleStatus parse(int value) {
			return ValuedEnum.parse(value, UserRoleStatus.values());
		}

	}

	public String invitedById;
	public ZonedDateTime agreedTime;
	public UserRoleStatus status;
	public String spaceId;
	public String userId;
	public ZonedDateTime invitedTime;
	public Integer id;
	public String title;
	public String role;

	public UserRole() {

	}

	public String getInvitedById() {
		return invitedById;
	}

	public void setInvitedById(String invitedById) {
		this.invitedById = invitedById;
	}

	public ZonedDateTime getAgreedTime() {
		return agreedTime;
	}

	public void setAgreedTime(ZonedDateTime agreedTime) {
		this.agreedTime = agreedTime;
	}

	public UserRoleStatus getStatus() {
		return status;
	}

	public void setStatus(UserRoleStatus status) {
		this.status = status;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ZonedDateTime getInvitedTime() {
		return invitedTime;
	}

	public void setInvitedTime(ZonedDateTime invitedTime) {
		this.invitedTime = invitedTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserRole [");
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (spaceId != null) {
			builder.append("spaceId=");
			builder.append(spaceId);
			builder.append(", ");
		}
		if (userId != null) {
			builder.append("userId=");
			builder.append(userId);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (role != null) {
			builder.append("role=");
			builder.append(role);
		}
		builder.append("]");
		return builder.toString();
	}

}
