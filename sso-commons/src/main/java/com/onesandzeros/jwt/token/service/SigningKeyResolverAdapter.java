package com.onesandzeros.jwt.token.service;

import static com.onesandzeros.util.Constants.JWT_TOKEN_KEY_ID;

import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SigningKeyResolver;

@Component("keyResolver")
public class SigningKeyResolverAdapter implements SigningKeyResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(SigningKeyResolverAdapter.class);

	@Autowired
	JwtTokenKeyService jwtTokenKeyService;

	@Override
	public Key resolveSigningKey(JwsHeader header, Claims claims) {
		LOGGER.info("kid in header : {}", header.getKeyId());

		String keyId = (String) claims.get(JWT_TOKEN_KEY_ID);
		if (StringUtils.isEmpty(keyId)) {
			LOGGER.error("Header kid is missing in the token header with cliams: {} ", claims);
			throw new JwtException("Header kid is missing in the token header with cliams " + claims);
		}
		Key key = jwtTokenKeyService.getPublicKey(keyId);

		return key;
	}

	@Override
	public Key resolveSigningKey(JwsHeader header, String plaintext) {
		return null;
	}

}
