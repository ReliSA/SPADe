package com.assembla.client;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import com.assembla.exception.AssemblaAPIException;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class AssemblaClient  {
	/**
	 * API Key
	 */
	protected final String apiKey;
	/**
	 * API Password
	 */
	protected final String apiPassword;
	/**
	 * Client for making HTTP requests
	 */
	protected final OkHttpClient client;

	/**
	 * Json Object Mapper
	 */
	protected final ObjectMapper mapper;
	/**
	 * URL for api calls
	 */
	protected final String baseURL;

	/**
	 * MediaType for JSON media type
	 */
	protected static final MediaType JSON = MediaType.parse(AssemblaConstants.JSON_MEDIA_TYPE);
	/**
	 * Media type for file uploads
	 */
	private static final MediaType FILE_TYPE = MediaType.parse("");
	/**
	 * Create new Assembla client. Key and password will be used to authenticate
	 * requests
	 * 
	 * @param key
	 *            User's API
	 * @param password
	 *            User's API password
	 */
	public AssemblaClient(String key, String password) {
		this.apiKey = key;
		this.apiPassword = password;
		client = new OkHttpClient();
		mapper = new ObjectMapper();
		baseURL = AssemblaConstants.URL;
		configureMapper();
	}

	public String getKey() {
		return apiKey;
	}

	public String getBaseURL() {
		return baseURL;
	}

	public AssemblaResponse get(AssemblaRequest request) {
		Request.Builder builder = new Request.Builder();
		addCommon(request, builder);
		return doRequest(builder.build(), request);
	}

	public AssemblaResponse put(AssemblaRequest request) {
		Request.Builder builder = new Request.Builder();
		addCommon(request, builder);
		builder.put(createJSONBody(request));
		return doRequest(builder.build(), request);
	}

	public AssemblaResponse post(AssemblaRequest request) {
		Request.Builder builder = new Request.Builder();
		addCommon(request, builder);
		builder.post(createJSONBody(request));
		return doRequest(builder.build(), request);
	}

	public AssemblaResponse delete(AssemblaRequest request) {
		Request.Builder builder = new Request.Builder();
		addCommon(request, builder);
		builder.delete();
		return doRequest(builder.build(), request);
	}

	public AssemblaResponse post(UploadAssemblaRequest request) {
		MultipartBuilder multiPartBuilder = buildMultipartFormRequest(request);
		Request.Builder builder = new Request.Builder();
		addCommon(request, builder);
		builder.post(multiPartBuilder.build());
		return doRequest(builder.build(), request);
	}
	
	public AssemblaResponse put(UploadAssemblaRequest request) {
		MultipartBuilder multiPartBuilder = buildMultipartFormRequest(request);
		Request.Builder builder = new Request.Builder();
		addCommon(request, builder);
		builder.put(multiPartBuilder.build());
		return doRequest(builder.build(), request);
	}

	private MultipartBuilder buildMultipartFormRequest(UploadAssemblaRequest request) {
		UploadableItem<?> uploadableItem = request.getUploadableItem();

		RequestBody fileBody = RequestBody.create(FILE_TYPE, uploadableItem.fIle());
		String name = String.format(AssemblaConstants.UPLOAD_NAME_FORMAT, uploadableItem.namespace());
		MultipartBuilder multiPartBuilder = new MultipartBuilder()
		.type(MultipartBuilder.FORM)
		.addFormDataPart(name, uploadableItem.fIle().getName(), fileBody);
		uploadableItem.location().ifPresent(e -> multiPartBuilder.addFormDataPart(AssemblaConstants.UPLOAD_LOCATION, e));
		return multiPartBuilder;
	}

	protected AssemblaResponse doRequest(Request httpRequest, AssemblaRequest request) {
		Optional<Class<?>> type = request.getType();
		Reader charStream = null;
		try {
			Response response = client.newCall(httpRequest).execute();
			// Not a success - notify caller via exception
			if (!response.isSuccessful()) {
				throw new AssemblaAPIException(String.format("Error making request to [%s, %s], message [%d, %s]",
						httpRequest.method(), httpRequest.urlString(), response.code(), response.message()));
			}
			// No content or request has not requested a type, so return null
			// object
			if (response.code() == AssemblaConstants.NO_CONTENT || !type.isPresent()) {
				return new AssemblaResponse();
			}
			// Otherwise we can return the response in requested format
			charStream = response.body().charStream();
			return new AssemblaResponse(mapper.readValue(charStream, type.get()), type.get());
		} catch (IOException e) {
			throw new AssemblaAPIException("Error making request", e);
		} finally {
			try {
				if (charStream != null) {
					charStream.close();
				}
			} catch (IOException ioe) {
			}
		}
	}

	protected void addCommon(AssemblaRequest request, Request.Builder rb) {
		rb.url(this.getBaseURL() + request.getFullURI());
		rb.header(AssemblaConstants.HEADER_API_KEY, this.apiKey);
		rb.header(AssemblaConstants.HEADER_API_SECRET, this.apiPassword);
	}

	private RequestBody createJSONBody(AssemblaRequest request) {
		return request.getBody()
				.map(this::jsonRequestBody)
				.orElse(emptyRequestBody());
	}

	private RequestBody emptyRequestBody() {
		return RequestBody.create(null, new byte[0]);	
	}
	
	private RequestBody jsonRequestBody(Object request) {
		try {
			return RequestBody.create(JSON, mapper.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new AssemblaAPIException("Error creating JSON request body");
		}
	}

	private void configureMapper() {
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.findAndRegisterModules();
	}

}
