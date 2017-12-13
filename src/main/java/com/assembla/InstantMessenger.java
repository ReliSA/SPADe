package com.assembla;

public class InstantMessenger {

	public String type;
	public String id;

	public InstantMessenger() {
	}

	public String getType() {
		return type;
	}

	public InstantMessenger setType(String type) {
		this.type = type;
		return this;
	}

	public String getId() {
		return id;
	}

	public InstantMessenger setId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InstantMessenger [");
		if (type != null) {
			builder.append("type=");
			builder.append(type);
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
