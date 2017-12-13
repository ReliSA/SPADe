package com.assembla;

import java.util.ArrayList;
import java.util.List;

public class CustomReport {

	private List<Report> teamReports = new ArrayList<>();
	private List<Report> userReports = new ArrayList<>();

	public List<Report> getTeamReports() {
		return teamReports;
	}

	public CustomReport setTeamReports(List<Report> teamReports) {
		this.teamReports = teamReports;
		return this;
	}

	public List<Report> getUserReports() {
		return userReports;
	}

	public CustomReport setUserReports(List<Report> userReports) {
		this.userReports = userReports;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomReport [");
		if (teamReports != null) {
			builder.append("teamReports=");
			builder.append(teamReports);
			builder.append(", ");
		}
		if (userReports != null) {
			builder.append("userReports=");
			builder.append(userReports);
		}
		builder.append("]");
		return builder.toString();
	}

}
