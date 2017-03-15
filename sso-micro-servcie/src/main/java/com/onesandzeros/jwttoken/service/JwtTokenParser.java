package com.onesandzeros.jwttoken.service;

import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.onesandzeros.exceptions.ServiceException;

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

	public String getAuthToken(String jwtHeader) throws ServiceException {
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
		} catch (Exception e) {
			LOGGER.error("Exception during token parsing", e);
		}

		LOGGER.info("tokenSub : {}", tokenSub);
		return tokenSub;
	}

	private String parseTokenValueFromHeader(String jwtHeader) {
		String jwtToken = jwtHeader;
		if (jwtHeader.contains("Bearer")) {
			jwtToken = jwtHeader.substring(7);
		}
		return jwtToken;
	}
}
