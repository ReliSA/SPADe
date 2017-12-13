package com.assembla.client;

public class Paging {

	private final int size;
	private final int page;

	public Paging(int page, int size) {
		this.size = size;
		this.page = page;
	}

	public int size() {
		return this.size;
	}

	public int page() {
		return this.page;
	}

	public Paging next() {
		return new Paging(size, page + 1);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Paging [size=");
		builder.append(size);
		builder.append(", page=");
		builder.append(page);
		builder.append("]");
		return builder.toString();
	}
	
	

}
