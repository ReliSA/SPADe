package com.assembla;

public class Report {

	private String id;
	private String name;

	public String getId() {
		return id;
	}

	public Report setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Report setName(String name) {
		this.name = name;
		return this;

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Report [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
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
