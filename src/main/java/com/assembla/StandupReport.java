package com.assembla;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("standup_report")
public class StandupReport {

	private String whatIWillDo;
	private String spaceId;
	private String roadblocks;
	private ZonedDateTime updatedAt;
	private String userId;
	private String whatIDid;
	private Boolean awayFlag;
	private LocalDate filledFor;
	private ZonedDateTime createdAt;
	private String id;
	private LocalDate from;
	private LocalDate to;

	public String getWhatIWillDo() {
		return whatIWillDo;
	}

	public StandupReport setWhatIWillDo(String whatIWillDo) {
		this.whatIWillDo = whatIWillDo;
		return this;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public StandupReport setSpaceId(String spaceId) {
		this.spaceId = spaceId;
		return this;
	}

	public String getRoadblocks() {
		return roadblocks;
	}

	public StandupReport setRoadblocks(String roadblocks) {
		this.roadblocks = roadblocks;
		return this;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public StandupReport setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public String getUserId() {
		return userId;
	}

	public StandupReport setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getWhatIDid() {
		return whatIDid;
	}

	public StandupReport setWhatIDid(String whatIDid) {
		this.whatIDid = whatIDid;
		return this;
	}

	public Boolean getAwayFlag() {
		return awayFlag;
	}

	public StandupReport setAwayFlag(Boolean awayFlag) {
		this.awayFlag = awayFlag;
		return this;
	}

	public LocalDate getFilledFor() {
		return filledFor;
	}

	public StandupReport setFilledFor(LocalDate filledFor) {
		this.filledFor = filledFor;
		return this;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public StandupReport setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public String getId() {
		return id;
	}

	public StandupReport setId(String id) {
		this.id = id;
		return this;
	}

	public LocalDate getFrom() {
		return from;
	}

	public StandupReport setFrom(LocalDate from) {
		this.from = from;
		return this;
	}

	public LocalDate getTo() {
		return to;
	}

	public StandupReport setTo(LocalDate to) {
		this.to = to;
		return this;
	}

}
