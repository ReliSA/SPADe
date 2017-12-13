package com.assembla;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.assembla.serialization.CommaToListDeserializer;
import com.assembla.serialization.CustomFieldSerializer;
import com.assembla.serialization.ListToStringSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonRootName(value = "ticket")
public class Ticket {

	public enum Priority implements IntValuedEnum {
		HIGHEST(1), HIGH(2), NORMAL(3), LOW(4), LOWEST(5);

		private int value;

		Priority(int value) {
			this.value = value;
		}

		@JsonValue
		@Override
		public Integer getValue() {
			return value;
		}

		@JsonCreator
		public static Priority parse(int priority) {
			return ValuedEnum.parse(priority, Priority.values());
		}
	}

	public enum PermissionType implements IntValuedEnum{
		DEVELOPMENT, SUPPORT_PRIVATE, SUPPORT_PUBLIC;

		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	public enum HeirarchyType implements IntValuedEnum {
		NO_PLAN, SUB_TASK, STORY, EPIC;

		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	private Integer id;
	private Integer number;
	private String summary;
	private String description;
	private Priority priority;
	private ZonedDateTime completedDate;
	private Integer componentId;
	private ZonedDateTime createdOn;
	private PermissionType permissionType;
	private Double importance;
	private Boolean isStory;
	private Integer milestoneId;
	@JsonDeserialize(using = CommaToListDeserializer.class)
	@JsonSerialize(using = ListToStringSerializer.class)
	private List<String> notificationList;
	private String spaceId;
	private State state;
	private String status;
	private Integer storyImportance;
	private ZonedDateTime updatedAt;
	private Double workingHours;
	private Double estimate;
	private Double totalEstimate;
	private Double totalInvestedHours;
	private Double totalWorkingHours;
	private String assignedToId;
	private String reporterId;
//	@JsonSerialize(keyUsing = CustomFieldSerializer.class)
	private Map<String, String> customFields;
	private HeirarchyType hierarchyType;
	@JsonProperty("tags")
	private List<String> tags;
	private Boolean isSupport;
	private List<String> followers;

	public Ticket() {
	}

	public Ticket(int id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public Ticket setNumber(int number) {
		this.number = number;
		return this;
	}

	public Ticket setStatus(String status) {
		this.status = status;
		return this;
	}

	public List<String> getTags() {
		return tags;
	}

	public Ticket setTags(Collection<String> tags) {
		this.tags = new ArrayList<>(tags);
		return this;
	}

	public Integer getId() {
		return id;
	}

	public Ticket setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getSummary() {
		return summary;
	}

	public Ticket setSummary(String summary) {
		this.summary = summary;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Ticket setDescription(String description) {
		this.description = description;
		return this;
	}

	public Priority getPriority() {
		return priority;
	}

	public Ticket setPriority(Priority priority) {
		this.priority = priority;
		return this;
	}

	public ZonedDateTime getCompletedDate() {
		return completedDate;
	}

	public Ticket setCompletedDate(ZonedDateTime completedDate) {
		this.completedDate = completedDate;
		return this;
	}

	public Integer getComponentId() {
		return componentId;
	}

	public Ticket setComponentId(Integer componentId) {
		this.componentId = componentId;
		return this;
	}

	public ZonedDateTime getCreatedOn() {
		return createdOn;
	}

	public Ticket setCreatedOn(ZonedDateTime createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public PermissionType getPermissionType() {
		return permissionType;
	}

	public Ticket setPermissionType(PermissionType permissionType) {
		this.permissionType = permissionType;
		return this;
	}

	public Double getImportance() {
		return importance;
	}

	public Ticket setImportance(Double importance) {
		this.importance = importance;
		return this;
	}

	public Boolean getIsStory() {
		return isStory;
	}

	public Ticket setIsStory(Boolean isStory) {
		this.isStory = isStory;
		return this;
	}

	public Integer getMilestoneId() {
		return milestoneId;
	}

	public Ticket setMilestoneId(Integer milestoneId) {
		this.milestoneId = milestoneId;
		return this;
	}

	public List<String> getNotificationList() {
		return notificationList;
	}

	public Ticket setNotificationList(List<String> notificationList) {
		this.notificationList = notificationList;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public Ticket setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public State getState() {
		return state;
	}

	public Ticket setState(State state) {
		this.state = state;
		return this;
	}

	public Integer getStoryImportance() {
		return storyImportance;
	}

	public Ticket setStoryImportance(Integer storyImportance) {
		this.storyImportance = storyImportance;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Ticket setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public Double getWorkingHours() {
		return workingHours;
	}

	public Ticket setWorkingHours(Double workingHours) {
		this.workingHours = workingHours;
		return this;
	}

	public Double getEstimate() {
		return estimate;
	}

	public Ticket setEstimate(Double estimate) {
		this.estimate = estimate;
		return this;
	}

	public Double getTotalEstimate() {
		return totalEstimate;
	}

	public Ticket setTotalEstimate(Double totalEstimate) {
		this.totalEstimate = totalEstimate;
		return this;
	}

	public Double getTotalInvestedHours() {
		return totalInvestedHours;
	}

	public Ticket setTotalInvestedHours(Double totalInvestedHours) {
		this.totalInvestedHours = totalInvestedHours;
		return this;
	}

	public Double getTotalWorkingHours() {
		return totalWorkingHours;
	}

	public Ticket setTotalWorkingHours(Double totalWorkingHours) {
		this.totalWorkingHours = totalWorkingHours;
		return this;
	}

	public String getAssignedToId() {
		return assignedToId;
	}

	public Ticket setAssignedToId(String assignedToId) {
		this.assignedToId = assignedToId;
		return this;
	}

	public String getReporterId() {
		return reporterId;
	}

	public Ticket setReporterId(String reporterId) {
		this.reporterId = reporterId;
		return this;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}

	public Ticket setCustomFields(Map<String, String> customFields) {
		this.customFields = customFields;
		return this;
	}

	public Ticket addCustomField(String name, String value) {
		if (this.customFields == null) {
			this.customFields = new HashMap<>();
		}
		customFields.put(name, value);
		return this;
	}

	public HeirarchyType getHierarchyType() {
		return hierarchyType;
	}

	public Ticket setHierarchyType(HeirarchyType hierarchyType) {
		this.hierarchyType = hierarchyType;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public Ticket setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public Boolean isSupport() {
		return isSupport;
	}

	public Ticket setIsSupport(Boolean isSupport) {
		this.isSupport = isSupport;
		return this;
	}

	public List<String> getFollowers() {
		return followers;
	}

	public Ticket setFollowers(List<String> followers) {
		this.followers = followers;
		return this;
	}

	public Ticket addFollower(String follower) {
		if (this.followers == null) {
			this.followers = new ArrayList<>();
		}
		followers.add(follower);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Ticket [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (number != null) {
			builder.append("number=");
			builder.append(number);
			builder.append(", ");
		}
		if (summary != null) {
			builder.append("summary=");
			builder.append(summary);
			builder.append(", ");
		}
		if (priority != null) {
			builder.append("priority=");
			builder.append(priority);
			builder.append(", ");
		}
		if (createdOn != null) {
			builder.append("createdOn=");
			builder.append(createdOn);
			builder.append(", ");
		}
		if (status != null) {
			builder.append("status=");
			builder.append(status);
			builder.append(", ");
		}
		if (assignedToId != null) {
			builder.append("assignedToId=");
			builder.append(assignedToId);
		}
		builder.append("]");
		return builder.toString();
	}

}
