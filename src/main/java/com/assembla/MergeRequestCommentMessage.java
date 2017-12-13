package com.assembla;

import com.assembla.utils.ValidationUtils;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName(value = "content")
public class MergeRequestCommentMessage {

	private final String content;

	public MergeRequestCommentMessage(String message) {
		this.content = message;
		ValidationUtils.notNull(message, "message == null");
	}

	public String getContent() {
		return content;
	}

	@Override
	@JsonValue
	public String toString() {
		return content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
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
		MergeRequestCommentMessage other = (MergeRequestCommentMessage) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		return true;
	}

}
