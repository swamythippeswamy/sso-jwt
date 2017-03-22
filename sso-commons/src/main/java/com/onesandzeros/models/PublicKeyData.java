package com.onesandzeros.models;

import java.io.Serializable;

//TODO this class is not used!
public class PublicKeyData implements Serializable {

	private static final long serialVersionUID = -4983952003568512603L;

	private String keyId;
	private String encodedPublicKey;

	public PublicKeyData() {
	}

	public PublicKeyData(String keyId, String encodedPublicKey) {
		this.keyId = keyId;
		this.encodedPublicKey = encodedPublicKey;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getEncodedPublicKey() {
		return encodedPublicKey;
	}

	public void setEncodedPublicKey(String encodedPublicKey) {
		this.encodedPublicKey = encodedPublicKey;
	}

	@Override
	public String toString() {
		return "PublicKeyData [keyId=" + keyId + ", encodedPublicKey=" + encodedPublicKey + "]";
	}

}
