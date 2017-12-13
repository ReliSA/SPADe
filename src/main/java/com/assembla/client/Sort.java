package com.assembla.client;

public class Sort {

	private final String field;
	private final Direction direction;

	public String getField() {
		return field;
	}

	public Direction getDirection() {
		return direction;
	}

	public Sort(String field, Direction desc) {
		this.field = field;
		this.direction = desc;
	}

	public enum Direction {
		ASC("asc"), 
		DESC("desc");

		private String value;

		Direction(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sort [");
		if (field != null) {
			builder.append("field=");
			builder.append(field);
			builder.append(", ");
		}
		if (direction != null) {
			builder.append("direction=");
			builder.append(direction);
		}
		builder.append("]");
		return builder.toString();
	}

}
