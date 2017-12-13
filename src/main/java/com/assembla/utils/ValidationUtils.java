package com.assembla.utils;

public final class ValidationUtils {

	/**
	 * Check if an object is null and throw an IllegalArgumentException with
	 * supplied message 
	 * 
	 * @param object
	 * @param message
	 */
	public static <T> T notNull(T object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
		return object;
	}

	private ValidationUtils() {
	}
}
