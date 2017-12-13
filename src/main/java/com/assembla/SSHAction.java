package com.assembla;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName("ssh_action")
public class SSHAction {

	public enum SSHActionStatus implements IntValuedEnum {
		IDLE, QUEUED, RUNNING;

		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	public enum SSHActionFrequency implements IntValuedEnum {
		MANUAL(0), HOURLY(1), DAILY(2), COMMIT(10), MERGE_REQUEST_CREATE(11), MERGE_REQUEST_MERGE(12);

		private int value;

		SSHActionFrequency(int value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public Integer getValue() {
			return this.value;
		}

		@JsonCreator
		public static SSHActionFrequency parse(int value) {
			return ValuedEnum.parse(value, SSHActionFrequency.values());
		}

	}

	private Integer id;
	private String command;
	private String description;
	private SSHActionFrequency frequency;
	private String name;
	private String repoToolId;
	private String username;
	private SSHActionStatus status;
	private String spaceToolId;
	private Integer position;
	private Integer sshToolServerId;

	public Integer getId() {
		return id;
	}

	public SSHAction setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getCommand() {
		return command;
	}

	public SSHAction setCommand(String command) {
		this.command = command;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public SSHAction setDescription(String description) {
		this.description = description;
		return this;
	}

	public SSHActionFrequency getFrequency() {
		return frequency;
	}

	public SSHAction setFrequency(SSHActionFrequency frequency) {
		this.frequency = frequency;
		return this;
	}

	public String getName() {
		return name;
	}

	public SSHAction setName(String name) {
		this.name = name;
		return this;
	}

	public String getRepoToolId() {
		return repoToolId;
	}

	public SSHAction setRepoToolId(String repoToolId) {
		this.repoToolId = repoToolId;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public SSHAction setUsername(String username) {
		this.username = username;
		return this;
	}

	public SSHActionStatus getStatus() {
		return status;
	}

	public SSHAction setStatus(SSHActionStatus status) {
		this.status = status;
		return this;
	}

	public String getSpaceToolId() {
		return spaceToolId;
	}

	public SSHAction setSpaceToolId(String spaceToolId) {
		this.spaceToolId = spaceToolId;
		return this;
	}

	public Integer getPosition() {
		return position;
	}

	public SSHAction setPosition(Integer position) {
		this.position = position;
		return this;
	}

	public Integer getSshToolServerId() {
		return sshToolServerId;
	}

	public SSHAction setSshToolServerId(Integer sshToolServerId) {
		this.sshToolServerId = sshToolServerId;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SSHAction [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (command != null) {
			builder.append("command=");
			builder.append(command);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
		}
		builder.append("]");
		return builder.toString();
	}

}
