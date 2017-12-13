package com.assembla.enums;

import java.util.Arrays;
import java.util.Objects;

public interface ValuedEnum<T> {
	T getValue();

	static <E extends ValuedEnum<T>, T> E parse(T value, E[] all) {
		return Arrays.stream(all).filter(e -> Objects.equals(e.getValue(), value)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(String.format("Illegal value supplied (%s)", value)));
	}

}

