package com.assembla.client;

import java.util.Optional;

public class AssemblaResponse {

	private static final AssemblaResponse EMPTY = new AssemblaResponse();
	private final Object value;
	private final Class<?> type;

	public AssemblaResponse(Object value, Class<?> type) {
		this.value = value;
		this.type = type;
	}

	public AssemblaResponse(Object value) {
		this(value, null);
	}

	public AssemblaResponse(Class<?> type) {
		this(null, type);
	}

	public AssemblaResponse() {
		this.value = null;
		this.type = null;
	}

	public Optional<Class<?>> getType() {
		return Optional.ofNullable(type);
	}

	@SuppressWarnings("unchecked")
	public <E> Optional<E> getValue() {
		return (Optional<E>) Optional.ofNullable(value);
	}

	public static AssemblaResponse empty() {
		return EMPTY;
	}

}
