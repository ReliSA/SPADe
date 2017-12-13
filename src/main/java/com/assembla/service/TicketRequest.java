package com.assembla.service;

import java.util.Optional;

import com.assembla.TicketReport;
import com.assembla.client.AssemblaConstants;
import com.assembla.client.Paging;
import com.assembla.client.Sort;
import com.assembla.client.Sort.Direction;
import com.assembla.enums.ValuedEnum;
import com.assembla.service.TicketRequest.Builder;
import com.assembla.service.TicketRequest.Status;

public class TicketRequest {

	public enum Status implements ValuedEnum<String> {
		ACTIVE("active"), CLOSED("closed"), ALL("all");

		private String value;

		Status(String value) {
			this.value = value;
		}

		@Override
		public String getValue() {
			return this.value;
		}
	}

	private String report;
	private Paging paging;
	private Sort sort;
	private Status status;

	public TicketRequest(Builder builder) {
		this.paging = new Paging(builder.pageNumber, builder.pageSize);
		this.sort = new Sort(builder.sortByField, builder.sortDirection);
		this.report = builder.report;
		this.status = builder.status;
	}

	public int getPageSize() {
		return paging.size();
	}

	public int getPageNumber() {
		return paging.page();
	}

	public Optional<String> getSortBy() {
		return Optional.ofNullable(sort.getField());
	}

	public Optional<Direction> getDirection() {
		return Optional.ofNullable(sort.getDirection());
	}

	public Optional<String> getReport() {
		return Optional.ofNullable(report);
	}

	public Optional<Status> getStatus() {
		return Optional.ofNullable(this.status);
	}

	public static class Builder {

		private String report;
		private Status status;
		protected int pageSize = AssemblaConstants.DEFAULT_PAGE_SIZE;
		protected int pageNumber = 1;
		protected String sortByField;
		protected Direction sortDirection;

		public Builder desc() {
			this.sortDirection = Sort.Direction.DESC;
			return this;
		}

		public Builder asc() {
			this.sortDirection = Sort.Direction.ASC;
			return this;
		}

		public Builder report(TicketReport report) {
			this.report(String.valueOf(report.getValue()));
			return this;
		}

		public Builder report(String report) {
			this.report = report;
			return this;
		}

		public TicketRequest build() {
			return new TicketRequest(this);
		}

		public Builder status(Status status) {
			this.status = status;
			return this;
		}

		public Builder all() {
			status(Status.ALL);
			return this;
		}

		public Builder closed() {
			status(Status.CLOSED);
			return this;
		}

		public Builder open() {
			status(Status.ACTIVE);
			return this;
		}

		public Builder pageSize(int pageSize) {
			this.pageSize = pageSize;
			return this;
		}

		public Builder page(int pageNumber) {
			this.pageNumber = pageNumber;
			return this;
		}

		public Builder sortBy(String sortByField) {
			this.sortByField = sortByField;
			return this;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder2 = new StringBuilder();
		builder2.append("TicketRequest [");
		if (report != null) {
			builder2.append("report=");
			builder2.append(report);
			builder2.append(", ");
		}
		if (paging != null) {
			builder2.append("paging=");
			builder2.append(paging);
			builder2.append(", ");
		}
		if (sort != null) {
			builder2.append("sort=");
			builder2.append(sort);
		}
		builder2.append("]");
		return builder2.toString();
	}

}
