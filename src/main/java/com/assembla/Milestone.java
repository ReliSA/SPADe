package com.assembla;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName(value = "milestone")
public class Milestone {

	public enum PlannerType implements IntValuedEnum {
		NONE, BACKLOG, CURRENT;

		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	public enum ReleaseLevel implements IntValuedEnum {
		ALPHA(1), BETA(2), STABLE(3);

		private int value;

		ReleaseLevel(int value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public Integer getValue() {
			return this.value;
		}

		@JsonCreator
		public static ReleaseLevel parse(int value) {
			return ValuedEnum.parse(value, ReleaseLevel.values());
		}

	}

	private PlannerType plannerType;
	private String prettyReleaseLevel;
	private String releaseNotes;
	private String description;
	private ReleaseLevel releaseLevel;
	private LocalDate completedDate;
	private String createdBy;
	private LocalDate dueDate;
	private Boolean isCompleted;
	private String updatedBy;
	private ZonedDateTime updatedAt;
	private ZonedDateTime createdAt;
	private String title;
	private String userId;
	private String id;
	private String spaceId;
	private Double budget;

	public PlannerType getPlannerType() {
		return plannerType;
	}

	public Milestone setPlannerType(PlannerType plannerType) {
		this.plannerType = plannerType;
		return this;
	}

	public String getPrettyReleseLevel() {
		return prettyReleaseLevel;
	}

	public Milestone setPrettyReleseLevel(String prettyReleseLevel) {
		this.prettyReleaseLevel = prettyReleseLevel;
		return this;
	}

	public String getReleaseNotes() {
		return releaseNotes;
	}

	public Milestone setReleaseNotes(String releaseNotes) {
		this.releaseNotes = releaseNotes;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Milestone setDescription(String description) {
		this.description = description;
		return this;
	}

	public ReleaseLevel getReleaseLevel() {
		return releaseLevel;
	}

	public Milestone setReleaseLevel(ReleaseLevel releaseLevel) {
		this.releaseLevel = releaseLevel;
		return this;
	}

	public LocalDate getCompletedDate() {
		return completedDate;
	}

	public Milestone setCompletedDate(LocalDate completedDate) {
		this.completedDate = completedDate;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Milestone setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public Milestone setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public Milestone setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
		return this;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public Milestone setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Milestone setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public Milestone setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Milestone setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public Milestone setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getId() {
		return id;
	}

	public Milestone setId(String id) {
		this.id = id;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public Milestone setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public Double getBudget() {
		return budget;
	}

	public Milestone setBudget(Double budget) {
		this.budget = budget;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Milestone [");
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (spaceId != null) {
			builder.append("spaceId=");
			builder.append(spaceId);
		}
		builder.append("]");
		return builder.toString();
	}

}
