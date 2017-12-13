package com.assembla;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("ssh_server")
public class SSHServer {

	private Integer id;
	private String remoteHost;
	private Integer remotePort;
	private String name;
	private String description;
	private String spaceToolId;

	public Integer getId() {
		return id;
	}

	public SSHServer setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public SSHServer setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
		return this;
	}

	public Integer getRemotePort() {
		return remotePort;
	}

	public SSHServer setRemotePort(Integer remotePort) {
		this.remotePort = remotePort;
		return this;
	}

	public String getName() {
		return name;
	}

	public SSHServer setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public SSHServer setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getSpaceToolId() {
		return spaceToolId;
	}

	public SSHServer setSpaceToolId(String spaceToolId) {
		this.spaceToolId = spaceToolId;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SSHServer [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (remoteHost != null) {
			builder.append("remoteHost=");
			builder.append(remoteHost);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (description != null) {
			builder.append("description=");
			builder.append(description);
		}
		builder.append("]");
		return builder.toString();
	}

}
