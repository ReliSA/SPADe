package com.assembla;

public class SSHKey {

	private String fingerPrint;
	private String title;
	private Boolean readOnly;
	private Integer id;
	private String key;

	public String getFingerPrint() {
		return fingerPrint;
	}

	public SSHKey setFingerPrint(String fingerPrint) {
		this.fingerPrint = fingerPrint;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public SSHKey setTitle(String title) {
		this.title = title;
		return this;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public SSHKey setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public SSHKey setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getKey() {
		return key;
	}

	public SSHKey setKey(String key) {
		this.key = key;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SSHKey [");
		if (fingerPrint != null) {
			builder.append("fingerPrint=");
			builder.append(fingerPrint);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (readOnly != null) {
			builder.append("readOnly=");
			builder.append(readOnly);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (key != null) {
			builder.append("key=");
			builder.append(key);
		}
		builder.append("]");
		return builder.toString();
	}

}
