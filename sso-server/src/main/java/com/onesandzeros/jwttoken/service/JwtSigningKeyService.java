package com.onesandzeros.jwttoken.service;

import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.onesandzeros.models.PublicKeyData;

import io.jsonwebtoken.impl.crypto.RsaProvider;

@Component
public class JwtSigningKeyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtSigningKeyService.class);

	private KeyPair keyPair;
	private String keyId;

	@Autowired
	Environment env;

	@PostConstruct
	private void generateKeyPair() {
		keyPair = RsaProvider.generateKeyPair();
		keyId = UUID.randomUUID().toString();
		updateKeyInMicroServices(keyId, keyPair.getPublic());
	}

	public PublicKeyData getPublicKeyData() {
		String encodedPubKey = Base64Utils.encodeToString(keyPair.getPublic().getEncoded());
		return new PublicKeyData(keyId, encodedPubKey);

	}

	// TODO: (swamy) Move this to a separate thread
	@SuppressWarnings("unchecked")
	public void updateKeyInMicroServices(String keyId, Key publicKey) {
		List<String> hostsCsv = env.getProperty("microservices.hosts", List.class);
		LOGGER.info("Microservices Hosts : {}", hostsCsv);
	}

	public Key getSigningKey() {
		if (null == keyPair) {
			generateKeyPair();
		}
		return keyPair.getPrivate();
	}

	public String getKeyId() {
		return keyId;
	}
}
