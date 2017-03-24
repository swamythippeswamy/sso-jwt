package com.onesandzeros.jwt.token.service;

import static com.onesandzeros.AppConstants.JWT_TOKEN_HEADER_NAME;
import static com.onesandzeros.AppConstants.JWT_TOKEN_HEADER_PREFIX;
import static com.onesandzeros.AppConstants.JWT_TOKEN_ISSUER;
import static com.onesandzeros.AppConstants.JWT_TOKEN_VALID_TIME;
import static com.onesandzeros.util.Constants.JWT_TOKEN_KEY_ID;

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
import com.onesandzeros.util.EncryptDecryptData;
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
	private Environment env;

	@Autowired
	private JwtTokenKeyServiceImpl keyService;

	@Autowired
	private EncryptDecryptData encDecData;

	public void addAuthToken(HttpServletResponse response, UserInfo userInfo) {

		try {
			String tokenString = JsonUtil.serialize(userInfo);
			String token = createAuthToken(tokenString);
			LOGGER.info("Generated token for input obj : {} is {}", userInfo, token);

			String jwtTokenPrefix = env.getProperty(JWT_TOKEN_HEADER_PREFIX, "Bearer");
			String jwtTokenHeaderName = env.getProperty(JWT_TOKEN_HEADER_NAME, "Authentication");
			response.setHeader(jwtTokenHeaderName, jwtTokenPrefix + " " + token);

		} catch (JsonProcessingException e) {
			LOGGER.error("Error in sertializing the tokenString");
		}
	}

	/**
	 * Returns the jwt token Same Key is used for encrypting the userInfo in jwt
	 * claims and for signing the jwt token. The keyId is added to claim with
	 * key name kid
	 * 
	 * @param tokenString
	 * @return
	 */
	private String createAuthToken(String tokenString) {
		JwtBuilder builder = Jwts.builder();

		long currentTime = System.currentTimeMillis();
		long expiryTime = System.currentTimeMillis() + Long.parseLong(env.getProperty(JWT_TOKEN_VALID_TIME));
		KeyData keyData = keyService.getOnePrivateKeyRandomly();

		Claims claims = Jwts.claims();
		claims.setIssuedAt(new Date(currentTime));
		claims.setExpiration(new Date(expiryTime));
		claims.setIssuer(env.getProperty(JWT_TOKEN_ISSUER));
		claims.put(JWT_TOKEN_KEY_ID, keyData.getKeyId());
		claims.setSubject(encDecData.encrypt(tokenString, keyData.getKey()));

		builder.setClaims(claims).signWith(SignatureAlgorithm.RS256, keyData.getKey());
		return builder.compact();
	}

}