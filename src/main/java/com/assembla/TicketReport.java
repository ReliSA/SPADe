package com.assembla;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketReport {
	ALL(0),
	ACTIVE(1), 
	ACTIVE_BY_COMPONENT(2),
	ACTIVE_BY_USER(3),
	CLOSED_BY_MILESTONE(4),
	CLOSED_BY_COMPONENT(5),
	CLOSED_BY_DATE(6),
	ALL_CURRENT_USER(7),
	ACTIVE_CURRENT_USER(8),
	CLOSED_CURRENT_USER(9),
	FOLLOWED_CURRENT_USER(10);

	
	private int value;

	TicketReport(int value) {
		this.value = value;
	}

	@JsonValue
	public int getValue() {
		return this.value;
	}

}

