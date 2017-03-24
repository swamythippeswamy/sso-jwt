package com.onesandzeros.jwt.token.service;

import java.security.Key;

public interface JwtTokenKeyService {

	public Key getPublicKey(String keyId);
}
