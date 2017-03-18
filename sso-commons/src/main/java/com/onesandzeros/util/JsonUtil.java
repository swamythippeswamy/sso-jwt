package com.onesandzeros.util;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * 
 * @author swamy
 *
 */
public class JsonUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

	private static ObjectMapper strictOm = new ObjectMapper();
	private static ObjectMapper lenientOm = new ObjectMapper();
	private static TypeFactory typeFactory = lenientOm.getTypeFactory();

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

	public static <T> List<T> deserializeListData(String str, Class<T> clazz) {

		JavaType respType = typeFactory.constructParametrizedType(List.class, List.class, clazz);
		List<T> data = null;
		try {
			data = lenientOm.readValue(str, respType);
		} catch (IOException e) {
			LOGGER.error("Error in deserializing the string : {}", str, e);
		}
		return data;
	}
}
