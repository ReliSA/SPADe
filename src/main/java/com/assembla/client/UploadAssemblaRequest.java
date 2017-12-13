package com.assembla.client;


public class UploadAssemblaRequest extends AssemblaRequest {

	private final UploadableItem<?> item;

	public UploadAssemblaRequest(String uri, UploadableItem<?> item) {
		super(uri);
		this.item = item;
	}

	public UploadAssemblaRequest(String url, Class<?> type, UploadableItem<?> item) {
		super(url, type);
		this.item = item;
	}

	public UploadableItem<?> getUploadableItem() {
		return item;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((item == null) ? 0 : item.hashCode());
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
		UploadAssemblaRequest other = (UploadAssemblaRequest) obj;
		if (item == null) {
			if (other.item != null) {
				return false;
			}
		} else if (!item.equals(other.item)) {
			return false;
		}
		return true;
	}

}
