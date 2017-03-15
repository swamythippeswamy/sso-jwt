package com.onesandzeros.jwttoken.service;

import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtTokenParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenParser.class);

	@Autowired
	Environment env;

	@Autowired
	private JwtSecurityService securityService;

	public String getAuthToken(String jwtHeader) throws Exception {
		String tokenSub = null;
		try {
			String jwtTokenVal = parseTokenValueFromHeader(jwtHeader);

			Jws<Claims> claims = Jwts.parser().setSigningKeyResolver(securityService.getResolver())
					.parseClaimsJws(jwtTokenVal);
			LOGGER.info("claims.getBody() : {}", claims.getBody());
			if (null != claims && null != claims.getBody()) {
				tokenSub = claims.getBody().getSubject();
			}
		} catch (SignatureException e) {
			LOGGER.error("SignatureException during token parsing", e);
			throw new SignatureException(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("Exception during token parsing", e);
		}

		LOGGER.info("tokenSub : {}", tokenSub);
		return tokenSub;
	}

	private Key getSigningKey(String jsonToken) {
		Key publicKey = null;
		Header jwtHeader = Jwts.parser().parse(jsonToken).getHeader();
		LOGGER.info("Jwt Header is : {}", jwtHeader);
		if (jwtHeader.containsKey(JwsHeader.KEY_ID)) {
			String keyId = (String) jwtHeader.get(JwsHeader.KEY_ID);

			LOGGER.info("KeyId in header is : {}", keyId);
			publicKey = securityService.getPublicKey(keyId);
		}

		return publicKey;
	}

	private String parseTokenValueFromHeader(String jwtHeader) {
		String jwtToken = jwtHeader;
		if (jwtHeader.contains("Bearer")) {
			jwtToken = jwtHeader.substring(7);
		}
		return jwtToken;
	}
}
