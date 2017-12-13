package com.assembla.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.assembla.client.AssemblaConstants;
import com.assembla.client.Paging;
import com.assembla.client.Sort;
import com.assembla.client.Sort.Direction;
import com.assembla.service.TaskRequest.Builder;
import com.assembla.service.TicketRequest.Status;

public class TaskRequest {

	private Sort sort;
	private Paging paging;
	private List<Integer> tickets;
	private List<String> users;
	private List<String> spaces;
	private DateRange dateRange;

	public Optional<String> getSortBy() {
		return Optional.ofNullable(sort.getField());
	}

	public Optional<Direction> getDirection() {
		return Optional.ofNullable(sort.getDirection());
	}

	TaskRequest(Builder builder) {
		this.paging = new Paging(builder.pageNumber, builder.pageSize);
		this.sort = new Sort(builder.sortByField, builder.sortDirection);
		this.tickets = builder.tickets;
		this.users = builder.users;
		this.spaces = builder.spaces;
		this.dateRange = builder.dateRange;
	}

	public Sort sort() {
		return sort;
	}

	public static class Builder {
		//todo : remove redundancy with ticket request
		private DateRange dateRange = null;
		private List<String> spaces = new ArrayList<>();
		private List<String> users = new ArrayList<>();
		private List<Integer> tickets = new ArrayList<>();
		private int pageSize = AssemblaConstants.DEFAULT_PAGE_SIZE;
		private int pageNumber = 1;
		private String sortByField;
		private Direction sortDirection;

		public TaskRequest build() {
			return new TaskRequest(this);
		}

		public Builder ticketIds(List<Integer> ticketIds) {
			this.tickets = ticketIds;
			return this;
		}

		public Builder userIds(List<String> userId) {
			this.users = userId;
			return this;
		}

		public Builder spaceIds(List<String> spaces) {
			this.spaces = spaces;
			return this;
		}

		public Builder fromTo(LocalDateTime start, LocalDateTime end) {
			this.dateRange = new DateRange(start, end);
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
		
		public Builder desc() {
			this.sortDirection = Sort.Direction.DESC;
			return this;
		}

		public Builder asc() {
			this.sortDirection = Sort.Direction.ASC;
			return this;
		}
	}

	public Paging paging() {
		return this.paging;
	}

	public int getPageNumber() {
		return this.paging.page();
	}

	public int getPageSize() {
		return this.paging.size();
	}

	public List<Integer> getTickets() {
		return this.tickets;
	}

	public List<String> getUsers() {
		return this.users;
	}

	public List<String> getSpaces() {
		return this.spaces;
	}

	public Optional<DateRange> getDateRange() {
		return Optional.ofNullable(dateRange);
	}

}
