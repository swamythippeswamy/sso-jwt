package com.onesandzeros.models;

import java.io.Serializable;

public class JwtTokensKeyPair implements Serializable {
	private static final long serialVersionUID = 5866151861913736065L;
	private String privateKey;
	private String publicKey;
	private String keyId;

	public JwtTokensKeyPair(String privateKey, String publicKey, String keyId) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.keyId = keyId;
	}

	public JwtTokensKeyPair(String publicKey, String keyId) {
		this.publicKey = publicKey;
		this.keyId = keyId;
	}

	public JwtTokensKeyPair() {
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JwtTokensKeyPair [privateKey=").append(privateKey).append(", publicKey=").append(publicKey)
				.append(", keyId=").append(keyId).append("]");
		return builder.toString();
	}

}
