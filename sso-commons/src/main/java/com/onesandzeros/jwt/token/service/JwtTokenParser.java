package com.onesandzeros.jwt.token.service;

import static com.onesandzeros.util.Constants.JWT_TOKEN_HEADER_PREFIX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.onesandzeros.models.JwtTokenParserResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.SigningKeyResolver;

@Component
public class JwtTokenParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenParser.class);

	@Autowired
	@Qualifier("keyResolver")
	private SigningKeyResolver keyResolver;

	public JwtTokenParserResponse getClaimsFromJwtToken(String jwtHeader) {

		JwtTokenParserResponse parserResp = new JwtTokenParserResponse();
		try {
			String jwtTokenVal = parseTokenValueFromHeader(jwtHeader);

			Jws<Claims> claims = Jwts.parser().setSigningKeyResolver(keyResolver).parseClaimsJws(jwtTokenVal);
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
		if (jwtHeader.contains(JWT_TOKEN_HEADER_PREFIX)) {
			jwtToken = jwtHeader.substring(7);
		}
		return jwtToken;
	}
}
