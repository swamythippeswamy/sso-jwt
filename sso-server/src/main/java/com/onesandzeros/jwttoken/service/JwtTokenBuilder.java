package com.onesandzeros.jwttoken.service;

import java.security.Key;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.util.JsonUtil;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenBuilder.class);

	@Autowired
	Environment env;

	@Autowired
	JwtSigningKeyService keyService;

	public void addAuthToken(HttpServletResponse response, UserInfo userInfo) {

		try {
			String tokenString = JsonUtil.serialize(userInfo);
			String token = createAuthToken(tokenString);
			LOGGER.info("Generated token for input obj : {} is {}", userInfo, token);

			String jwtTokenPrefix = env.getProperty("jwt.token.prefix", "Bearer");
			String jwtTokenHeaderName = env.getProperty("jwt.auth.header.name", "Authentication");
			response.setHeader(jwtTokenHeaderName, jwtTokenPrefix + " " + token);

		} catch (JsonProcessingException e) {
			LOGGER.error("Error in sertializing the tokenString");
		}
	}

	private String createAuthToken(String tokenString) {
		JwtBuilder builder = Jwts.builder();

		builder.setHeaderParam("kid", keyService.getKeyId()).setSubject(tokenString).signWith(SignatureAlgorithm.RS256,
				getSigningKey());
		return builder.compact();
	}

	private Key getSigningKey() {
		return keyService.getSigningKey();
	}

}