package com.onesandzeros.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

	private static ObjectMapper strictOm = new ObjectMapper();
	private static ObjectMapper lenientOm = new ObjectMapper();

	static {
		strictOm.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		lenientOm.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static String serialize(Object obj) throws JsonProcessingException {
		String serializedObj = lenientOm.writeValueAsString(obj);
		return serializedObj;
	}

	public static <T> T deserialize(String str, Class<T> clazz) {
		T data = null;
		try {
			data = lenientOm.readValue(str, clazz);
		} catch (IOException e) {
			LOGGER.error("Error in deserializing the string : {}", str, e);
		}
		return data;
	}
}
