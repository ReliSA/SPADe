package com.assembla;

import com.assembla.enums.IntValuedEnum;
import com.fasterxml.jackson.annotation.JsonValue;

public enum State implements IntValuedEnum{
	CLOSED, OPEN;
	
		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
}
