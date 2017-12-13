package com.assembla;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("private-ssh-key")
public class PrivateSSHKey {

	private Integer id;
	private String privateContent;
	private String spaceToolId;

	public Integer getId() {
		return id;
	}

	public PrivateSSHKey setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getPrivateContent() {
		return privateContent;
	}

	public PrivateSSHKey setPrivateContent(String privateContent) {
		this.privateContent = privateContent;
		return this;
	}

	public String getSpaceToolId() {
		return spaceToolId;
	}

	public PrivateSSHKey setSpaceToolId(String spaceToolId) {
		this.spaceToolId = spaceToolId;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PrivateSSHKey [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (privateContent != null) {
			builder.append("privateContent=");
			builder.append(privateContent);
			builder.append(", ");
		}
		if (spaceToolId != null) {
			builder.append("spaceToolId=");
			builder.append(spaceToolId);
		}
		builder.append("]");
		return builder.toString();
	}

}
