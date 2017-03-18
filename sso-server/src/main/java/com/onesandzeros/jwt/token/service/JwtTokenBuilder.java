package com.onesandzeros.jwt.token.service;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onesandzeros.models.KeyData;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.util.EncryptDecryptUtil;
import com.onesandzeros.util.JsonUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * This is used for building the jwt token if login is successful, it adds the
 * generated jwt token to {@link HttpServletResponse}
 * 
 * @author swamy
 *
 */
@Component
public class JwtTokenBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenBuilder.class);

	@Autowired
	Environment env;

	@Autowired
	JwtTokenKeyService keyService;

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

		long currentTime = System.currentTimeMillis();
		long expiryTime = System.currentTimeMillis() + Long.parseLong(env.getProperty("jwt.token.validity.time"));

		Claims claims = Jwts.claims();
		claims.setSubject(EncryptDecryptUtil.encrypt(tokenString));
		claims.setIssuedAt(new Date(currentTime));
		claims.setExpiration(new Date(expiryTime));
		claims.setIssuer(env.getProperty("jwt.token.issuer"));
		KeyData keyData = keyService.getOnePrivateKeyRandomly();

		builder.setHeaderParam("kid", keyData.getKeyId()).setClaims(claims).signWith(SignatureAlgorithm.RS256,
				keyData.getKey());
		return builder.compact();
	}

}