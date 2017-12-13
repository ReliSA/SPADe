package com.assembla;

import java.time.ZonedDateTime;
import java.util.Map;

import com.assembla.enums.IntValuedEnum;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonRootName("webhook")
public class Webhook {

	public enum HttpMethod implements IntValuedEnum{
		POST, GET;

		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	public enum AuthType implements IntValuedEnum{
		BASIC, OAUTH;
		
		@Override
		@JsonValue
		public Integer getValue() {
			return this.ordinal();
		}
	}

	private Integer id;
	private String title;
	private Boolean enabled;
	private AuthType authenticationType;
	private String appApiKey;
	private String appSecret;
	private String appRequestTokenUrl;
	private String appAccessTokenUrl;
	private String appAuthorizeUrl;
	private String appAuthorizeQuery;
	private String accessToken;
	private String accessTokenSecret;
	private String externalUrl;
	private HttpMethod httpMethod;
	private String contentType;
	private String content;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
	private Map<String, Boolean> filter;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public AuthType getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(AuthType authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getAppApiKey() {
		return appApiKey;
	}

	public void setAppApiKey(String appApiKey) {
		this.appApiKey = appApiKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAppRequestTokenUrl() {
		return appRequestTokenUrl;
	}

	public void setAppRequestTokenUrl(String appRequestTokenUrl) {
		this.appRequestTokenUrl = appRequestTokenUrl;
	}

	public String getAppAccessTokenUrl() {
		return appAccessTokenUrl;
	}

	public void setAppAccessTokenUrl(String appAccessTokenUrl) {
		this.appAccessTokenUrl = appAccessTokenUrl;
	}

	public String getAppAuthorizeUrl() {
		return appAuthorizeUrl;
	}

	public void setAppAuthorizeUrl(String appAuthorizeUrl) {
		this.appAuthorizeUrl = appAuthorizeUrl;
	}

	public String getAppAuthorizeQuery() {
		return appAuthorizeQuery;
	}

	public void setAppAuthorizeQuery(String appAuthorizeQuery) {
		this.appAuthorizeQuery = appAuthorizeQuery;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

	public void setExternalUrl(String externalUrl) {
		this.externalUrl = externalUrl;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Map<String, Boolean> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, Boolean> filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Webhook [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (enabled != null) {
			builder.append("enabled=");
			builder.append(enabled);
			builder.append(", ");
		}
		if (authenticationType != null) {
			builder.append("authenticationType=");
			builder.append(authenticationType);
		}
		builder.append("]");
		return builder.toString();
	}

}
