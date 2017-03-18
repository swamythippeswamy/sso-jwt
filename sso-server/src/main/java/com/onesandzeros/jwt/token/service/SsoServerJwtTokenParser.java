package com.onesandzeros.jwt.token.service;

import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.onesandzeros.models.JwtTokenParserResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@Component("jwtTokenParser")
public class SsoServerJwtTokenParser implements JwtTokenParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(SsoServerJwtTokenParser.class);

	@Autowired
	Environment env;

	@Autowired
	JwtTokenKeyService keyService;

	@Override
	public JwtTokenParserResponse getClaimsFromJwtToken(String jwtHeader) {
		JwtTokenParserResponse parserResp = new JwtTokenParserResponse();

		try {
			String jwtToken = parseTokenValueFromHeader(jwtHeader);
			Jws<Claims> claims = Jwts.parser().setSigningKeyResolver(keyService.getResolver()).parseClaimsJws(jwtToken);
			LOGGER.info("claims.getBody() : {}", claims.getBody());
			if (null != claims && null != claims.getBody()) {
				parserResp.setStatus(JwtTokenParserResponse.SUCCESS);
				parserResp.setClaims(claims.getBody());
			}
		} catch (SignatureException e) {
			LOGGER.error("SignatureException during token parsing");
			parserResp.setStatusAndMessage(JwtTokenParserResponse.FAILURE, "SignatureException during token parsing");
		} catch (ExpiredJwtException e) {
			LOGGER.error("Token expired");
			parserResp.setStatusAndMessage(JwtTokenParserResponse.FAILURE, "Token expired, please re-login");
		} catch (MalformedJwtException e) {
			LOGGER.error("MalformedJwt, token is tampered", e);
			parserResp.setStatusAndMessage(JwtTokenParserResponse.FAILURE, "Malformed token");
		} catch (Exception e) {
			LOGGER.error("Exception during token parsing", e);
			parserResp.setStatusAndMessage(JwtTokenParserResponse.FAILURE, "Exception during parsing jwt token");
		}

		LOGGER.info("JwtTokenParserResponse : {}", parserResp);
		return parserResp;
	}

	private String parseTokenValueFromHeader(String jwtHeader) {
		String jwtToken = jwtHeader;
		if (jwtHeader.contains("Bearer")) {
			jwtToken = jwtHeader.substring(7);
		}
		return jwtToken;
	}
}
