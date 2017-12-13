package com.assembla;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TeamPermission implements IntValuedEnum {
	READ(1), WRITE(2), ALL(3);

	private int value;

	private TeamPermission(int value) {
		this.value = value;
	}

	@JsonValue
	@Override
	public Integer getValue() {
		return this.value;
	}

	@JsonCreator
	public static TeamPermission parse(int value) {
		return ValuedEnum.parse(value, TeamPermission.values());
	}

}
