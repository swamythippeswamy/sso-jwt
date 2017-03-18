package com.onesandzeros.models;

import io.jsonwebtoken.Claims;

public class JwtTokenParserResponse {

	public static final int SUCCESS = 0;
	public static final int FAILURE = 1;

	private Claims claims;
	private String message;
	private int status = SUCCESS;

	public Claims getClaims() {
		return claims;
	}

	public void setClaims(Claims claims) {
		this.claims = claims;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setStatusAndMessage(int status, String message) {
		this.status = status;
		this.message = message;
	}

	@Override
	public String toString() {
		return "JwtTokenParserResponse [claims=" + claims + ", message=" + message + ", status=" + status + "]";
	}

}
