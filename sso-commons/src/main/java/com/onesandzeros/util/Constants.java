package com.onesandzeros.util;

public interface Constants {
	String JWT_TOKEN_KEY_ID = "kid";
	String JWT_TOKEN_HEADER_PREFIX = "Bearer";

	// TODO: (Discuss whether this needs to be kept in props??)
	// Base64 encoded signing key secret
	String JWT_TOKEN_SIGNING_SECRET = "MG4zNXVON3ozNk81azN5NQ==";
}
