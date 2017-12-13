package com.assembla.client;

public  final class PagedAssemblaRequest extends AssemblaRequest {

	private final int page;
	private final int pageSize;

	public PagedAssemblaRequest(String uri, Class<?> type) {
		this(uri, type, AssemblaConstants.DEFAULT_PAGE, AssemblaConstants.DEFAULT_PAGE_SIZE);
	}

	public PagedAssemblaRequest(String uri, Class<?> type, int page, int pageSize) {
		super(uri, type);
		if (!type.isArray()) {
			throw new IllegalArgumentException("Cannot create PagedAssemblaRequest for non array type");
		}
		this.page = page;
		this.pageSize = pageSize;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}
	
	@Override
	protected String buildParameters() {
		StringBuilder sb = new StringBuilder();
		String parameters = super.buildParameters();
		if(parameters.isEmpty()) {
			sb.append("?");
		}else {
			sb.append(parameters)
			.append("&");
		}
		
		sb.append(AssemblaConstants.PAGE_PARAMETER)
		.append("=")
		.append(page)
		.append("&")
		.append(AssemblaConstants.PAGE_SIZE_PARAMETER)
		.append("=")
		.append(pageSize);
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + page;
		result = prime * result + pageSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PagedAssemblaRequest other = (PagedAssemblaRequest) obj;
		if (page != other.page) {
			return false;
		}
		if (pageSize != other.pageSize) {
			return false;
		}
		return true;
	}

}
