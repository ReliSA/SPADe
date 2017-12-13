package com.assembla.client;

import java.io.File;

import com.assembla.Document;

public final class UploadableDocument extends UploadableItem<Document> {

	public UploadableDocument(File file, String uploadPath) {
		super(file, uploadPath);
	}

	@Override
	public String namespace() {
		return "document";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UploadableDocument [");
		if (file != null) {
			builder.append("file=");
			builder.append(file);
		}
		builder.append("]");
		return builder.toString();
	}

}
