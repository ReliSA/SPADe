package com.assembla.serialization;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ListToStringSerializer extends JsonSerializer<List<String>>{

	@Override
	public void serialize(List<String> value, JsonGenerator gen, SerializerProvider serializers) throws IOException,
			JsonProcessingException {
		String output = value.stream().collect(Collectors.joining(","));
		gen.writeString(output);
	}

}
