package com.assembla;

public class Settings {

	public String state;
	public String vcsUrl;

	public String getState() {
		return state;
	}

	public Settings setState(String state) {
		this.state = state;
		return this;
	}

	public String getVcsUrl() {
		return vcsUrl;
	}

	public Settings setVcsUrl(String vcsUrl) {
		this.vcsUrl = vcsUrl;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Settings [");
		if (state != null) {
			builder.append("state=");
			builder.append(state);
			builder.append(", ");
		}
		if (vcsUrl != null) {
			builder.append("vcsUrl=");
			builder.append(vcsUrl);
		}
		builder.append("]");
		return builder.toString();
	}
}
