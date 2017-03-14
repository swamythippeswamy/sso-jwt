package com.onesandzeros.jwttoken.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtTokenParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenParser.class);

	@Autowired
	Environment env;

	public String getAuthToken(String jwtHeader) throws Exception {
		String tokenSub = null;
		try {
			String jwtToken = jwtHeader;
			if (jwtHeader.contains("Bearer")) {
				jwtToken = jwtHeader.substring(7);
			}
			Jws<Claims> claims = Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(jwtToken);
			LOGGER.info("claims.getBody() : {}", claims.getBody());
			if (null != claims && null != claims.getBody()) {
				tokenSub = claims.getBody().getSubject();
			}
		} catch (SignatureException e) {
			LOGGER.info("SignatureException during token parsing");
			throw new SignatureException(e.getMessage());
		} catch (Exception e) {
			LOGGER.info("Exception during token parsing");
		}

		LOGGER.info("tokenSub : {}", tokenSub);
		return tokenSub;
	}

	private String getSigningKey() {
		return env.getProperty("jwt.signin.key", "test");
	}
}
