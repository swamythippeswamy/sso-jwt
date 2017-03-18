package com.onesandzeros.jwt.token.service;

import com.onesandzeros.models.JwtTokenParserResponse;

public interface JwtTokenParser {
	JwtTokenParserResponse getClaimsFromJwtToken(String jwtHeader);
}
