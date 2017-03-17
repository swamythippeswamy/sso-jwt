package com.onesandzeros.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author swamy
 */
@Component
public class RestServiceUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceUtil.class);

	private static final RestTemplate restTemplate = new RestTemplate();

	public <T> T getRequest(String url, Class<T> responseClass) throws RestClientException {
		T response = null;
		HttpEntity<T> requestEnt = new HttpEntity<T>(getDefaultHeaders());
		ResponseEntity<T> responseEnt = restTemplate.exchange(url, HttpMethod.GET, requestEnt, responseClass);
		if (responseEnt.getStatusCode() == HttpStatus.OK) {
			response = responseEnt.getBody();
		}
		LOGGER.info("Request made to url : {}, response status : {}, response content : {}", url,
				responseEnt.getStatusCode(), response);

		return response;
	}

	public <T> T postRequest(String url, String postData, Class<T> responseClass) throws RestClientException {
		T response = null;
		HttpEntity<String> requestEnt = new HttpEntity<String>(postData, getDefaultHeaders());
		ResponseEntity<T> responseEnt = restTemplate.exchange(url, HttpMethod.POST, requestEnt, responseClass);
		if (responseEnt.getStatusCode() == HttpStatus.OK) {
			response = responseEnt.getBody();
		}
		LOGGER.info("Request made to url : {}, response status : {}, response content : {}", url,
				responseEnt.getStatusCode(), response);

		return response;
	}

	private HttpHeaders getDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");
		return headers;
	}
}
