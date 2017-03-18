package com.onesandzeros.models;

import java.io.Serializable;
import java.security.Key;

public class KeyData implements Serializable {
	private static final long serialVersionUID = -77199349582048088L;
	private String keyId;
	private Key key;

	public KeyData(String keyId, Key key) {
		this.keyId = keyId;
		this.key = key;
	}

	public KeyData() {
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return "KeyData [keyId=" + keyId + ", key=" + key + "]";
	}

}
