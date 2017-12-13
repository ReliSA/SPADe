package com.assembla;

import com.assembla.enums.IntValuedEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PublicPermission implements IntValuedEnum {
	NONE, READ, WRITE;

	@Override
	@JsonValue
	public Integer getValue() {
		return this.ordinal();
	}
}
