package com.assembla.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class AbstractListeDesrializer extends JsonDeserializer<List<String>> {

	private String delimiter;

	public AbstractListeDesrializer(String delimiter) {
		this.delimiter = delimiter;
	}

	@Override
	public List<String> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		JsonNode node = parser.getCodec().readTree(parser);
		String text = node.asText();
		if (text.isEmpty()) {
			return Collections.emptyList();
		}
		return new ArrayList<>(Arrays.asList(text.split(delimiter)));
	}
}
