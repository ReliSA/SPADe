package com.assembla.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Encapsulate request parameters for Assembla api
 * 
 * @author matthewsladen
 *
 */
public class AssemblaRequest {

	private final Class<?> type;
	private final String uri;
	private final Map<String, Object> parameters = new LinkedHashMap<>();
	private Object body;

	public AssemblaRequest(String uri, Class<?> type) {
		this.uri = uri;
		this.type = type;
	}

	public AssemblaRequest(String uri) {
		this(uri, null);
	}

	public AssemblaRequest withBody(Object body) {
		this.body = body;
		return this;
	}

	/**
	 * Return type that this request will return
	 * 
	 * @return Optional wrapped type whcih this request will return
	 */
	public Optional<Class<?>> getType() {
		return Optional.ofNullable(type);
	}

	public Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}

	public String getUri() {
		return uri;
	}

	public AssemblaRequest addParam(String key, Object... value) {
		if(value.length == 1) {
			this.parameters.put(key, value[0]);
		}else {
			this.parameters.put(key, value);
		}

		return this;
	}

	protected String buildParameters() {
		if (this.parameters.isEmpty()) {
			return "";
		}

		Function<Entry<String, Object>, String> queryStringWriter = 
		e -> e.getValue().getClass().isArray() ? new ArrayParam(e).toString()
		: e.getKey() + "=" + e.getValue();
		
		return "?" + this.parameters.entrySet().stream()
		.map(queryStringWriter)
		.collect(Collectors.joining("&"));
	}

	public final String getFullURI() {
		return this.uri + buildParameters();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		AssemblaRequest other = (AssemblaRequest) obj;
		if (body == null) {
			if (other.body != null) {
				return false;
			}
		} else if (!body.equals(other.body)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (getFullURI() == null) {
			if (other.getFullURI() != null) {
				return false;
			}
		} else if (!getFullURI().equals(other.getFullURI())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssemblaRequest [type=");
		builder.append(type);
		builder.append(", getFullURI()=");
		builder.append(getFullURI());
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append(", body=");
		builder.append(body);
		builder.append("]");
		return builder.toString();
	}

	public Optional<Object> getBody() {
		return Optional.ofNullable(body);
	}

	public AssemblaRequest addAllParameters(Map<String, Object> params) {
		this.parameters.putAll(params);
		return this;
	}

	private static final class ArrayParam {

		private String key;
		private Object[] value;

		 ArrayParam(Entry<String, Object> entry) {
			this.key = entry.getKey();
			this.value = (Object[]) entry.getValue();
		}
		
		@Override
		public String toString() {
			return Arrays.stream(value)
			.map(e -> this.key + "=" + e)
			.collect(Collectors.joining("&"));
		}

	}
}
