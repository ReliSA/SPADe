package com.assembla.client;

import java.io.File;
import java.util.Optional;

import com.assembla.utils.ValidationUtils;

public abstract class UploadableItem<T> {

	protected final File file;
	protected final Optional<String> location;

	public UploadableItem(File file, String location) {
		ValidationUtils.notNull(file, "file == null");
		if (!file.exists()) {
			throw new IllegalArgumentException(String.format("No file with path [%s]", file.getPath()));
		}
		this.location = Optional.ofNullable(location);
		this.file = file;
	}

	public File fIle() {
		return file;
	}

	public Optional<String> location() {
		return location;
	}

	public abstract String namespace();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UploadableItem other = (UploadableItem) obj;
		if (file == null) {
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		return true;
	}

}
