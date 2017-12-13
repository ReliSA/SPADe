package com.assembla;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("space_tool")
public class SpaceTool {

	public String type;
	public Integer toolId;
	public Boolean active;
	public ZonedDateTime createdAt;
	public String url;
	public String spaceId;
	public Integer number;
	public String parentId;
	public String name;
	public String menuName;
	public String id;
	public Settings settings;
	public TeamPermission teamPermissions;
	public WatcherPermission watcherPermissions;
	public PublicPermission publicPermissions;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getToolId() {
		return toolId;
	}

	public void setToolId(Integer toolId) {
		this.toolId = toolId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public TeamPermission getTeamPermissions() {
		return teamPermissions;
	}

	public void setTeamPermissions(TeamPermission teamPermissions) {
		this.teamPermissions = teamPermissions;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public PublicPermission getPublicPermissions() {
		return publicPermissions;
	}

	public void setPublicPermissions(PublicPermission publicPermissions) {
		this.publicPermissions = publicPermissions;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public WatcherPermission getWatcherPermissions() {
		return watcherPermissions;
	}

	public void setWatcherPermissions(WatcherPermission watcherPermissions) {
		this.watcherPermissions = watcherPermissions;
	}

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpaceTool [");
		if (active != null) {
			builder.append("active=");
			builder.append(active);
			builder.append(", ");
		}
		if (url != null) {
			builder.append("url=");
			builder.append(url);
			builder.append(", ");
		}
		if (number != null) {
			builder.append("number=");
			builder.append(number);
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
