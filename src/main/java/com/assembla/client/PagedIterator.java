package com.assembla.client;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import static java.util.stream.Collectors.*;
import java.util.stream.StreamSupport;

public final class PagedIterator<T> implements Iterator<Collection<T>>, Iterable<Collection<T>> {

	//Next request to execute
	private PagedAssemblaRequest request;
	//Last request executed
	private PagedAssemblaRequest lastRequest;
	// Client
	private final AssemblaClient client;

	public PagedIterator(PagedAssemblaRequest pagedRequest, AssemblaClient client) {
		if (pagedRequest == null || client == null) {
			throw new IllegalArgumentException("pagedRequest == null || client == null");
		}
		this.request = pagedRequest;
		this.client = client;
	}

	@Override
	public Iterator<Collection<T>> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return !request.equals(lastRequest);
	}

	@Override
	public Collection<T> next() {
		
		if(request.equals(lastRequest)) {
			throw new NoSuchElementException();
		}
		
		List<T> items =  client.get(request).<T[]>getValue()
			.map(Arrays::asList)
			.orElseGet(Collections::emptyList);
		
		lastRequest = request;
		if(!items.isEmpty()) {
			request = new PagedAssemblaRequest(
				request.getUri(), 
				request.getType().get(), 
				request.getPage() + 1,
				request.getPageSize()
			);
			request.addAllParameters(lastRequest.getParameters());
		}
		
		return items;
	}
	
	public AssemblaClient getClient() {
		return this.client;
	}

	public PagedAssemblaRequest getRequest() {
		return request;
	}

	public List<T> asList() {
		return StreamSupport.stream(this.spliterator(), false).flatMap(e -> e.stream()).collect(toList());
	}

}
