package com.assembla;

import com.assembla.enums.IntValuedEnum;
import com.assembla.enums.ValuedEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SourceSymbolType implements IntValuedEnum {
	BRANCH(1), TAG(2), REVISION(3);

	private int value;

	SourceSymbolType(int value) {
		this.value = value;
	}

	@JsonValue
	public Integer getValue() {
		return this.value;
	}

	@JsonCreator
	public static SourceSymbolType parse(Integer value) {
		return ValuedEnum.parse(value, SourceSymbolType.values());
	}

}