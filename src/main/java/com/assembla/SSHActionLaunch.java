package com.assembla;

import java.time.ZonedDateTime;

public class SSHActionLaunch {

	private Integer id;
	private Boolean success;
	private String command;
	private ZonedDateTime startedAt;
	private ZonedDateTime endedAt;
	private Integer actionId;
	private Integer serverId;
	private String output;

	public Integer getId() {
		return id;
	}

	public SSHActionLaunch setId(Integer id) {
		this.id = id;
		return this;
	}

	public Boolean getSuccess() {
		return success;
	}

	public SSHActionLaunch setSuccess(Boolean success) {
		this.success = success;
		return this;
	}

	public String getCommand() {
		return command;
	}

	public SSHActionLaunch setCommand(String command) {
		this.command = command;
		return this;
	}

	public ZonedDateTime getStartedAt() {
		return startedAt;
	}

	public SSHActionLaunch setStartedAt(ZonedDateTime startedAt) {
		this.startedAt = startedAt;
		return this;
	}

	public ZonedDateTime getEndedAt() {
		return endedAt;
	}

	public SSHActionLaunch setEndedAt(ZonedDateTime endedAt) {
		this.endedAt = endedAt;
		return this;
	}

	public Integer getActionId() {
		return actionId;
	}

	public SSHActionLaunch setActionId(Integer actionId) {
		this.actionId = actionId;
		return this;
	}

	public Integer getServerId() {
		return serverId;
	}

	public SSHActionLaunch setServerId(Integer serverId) {
		this.serverId = serverId;
		return this;
	}

	public String getOutput() {
		return output;
	}

	public SSHActionLaunch setOutput(String output) {
		this.output = output;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SSHActionLaunch [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (success != null) {
			builder.append("success=");
			builder.append(success);
			builder.append(", ");
		}
		if (command != null) {
			builder.append("command=");
			builder.append(command);
			builder.append(", ");
		}
		if (startedAt != null) {
			builder.append("startedAt=");
			builder.append(startedAt);
			builder.append(", ");
		}
		if (endedAt != null) {
			builder.append("endedAt=");
			builder.append(endedAt);
			builder.append(", ");
		}
		if (actionId != null) {
			builder.append("actionId=");
			builder.append(actionId);
			builder.append(", ");
		}
		if (serverId != null) {
			builder.append("serverId=");
			builder.append(serverId);
			builder.append(", ");
		}
		if (output != null) {
			builder.append("output=");
			builder.append(output);
		}
		builder.append("]");
		return builder.toString();
	}

}
