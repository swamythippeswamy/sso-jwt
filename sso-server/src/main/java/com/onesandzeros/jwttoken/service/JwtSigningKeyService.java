package com.onesandzeros.jwttoken.service;

import java.security.Key;
import java.security.KeyPair;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import com.onesandzeros.models.PublicKeyData;
import com.onesandzeros.util.JsonUtil;
import com.onesandzeros.util.RestServiceUtil;

import io.jsonwebtoken.impl.crypto.RsaProvider;

@Component
public class JwtSigningKeyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtSigningKeyService.class);

	private KeyPair keyPair;
	private String keyId;

	@Autowired
	Environment env;

	@Autowired
	RestServiceUtil restUtil;

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

		PublicKeyData pubKeyData = getPublicKeyData();
		LOGGER.info("Microservices Hosts : {}", hostsCsv);
		if (CollectionUtils.isEmpty(hostsCsv)) {
			LOGGER.info("No microservices url configured in the properties");
		}

		try {
			for (String url : hostsCsv) {
				Boolean addStatus = restUtil.postRequest(url, JsonUtil.serialize(pubKeyData), Boolean.class);
				LOGGER.info("Updated the public key in micro sevice : {}", addStatus);
			}
		} catch (Exception e) {
			LOGGER.error("Error in updating microservices with the new key", e);
		}

	}

	public Key getPrivateKey() {
		if (null == keyPair) {
			generateKeyPair();
		}
		return keyPair.getPrivate();
	}

	public Key getPublicKey() {
		if (null == keyPair) {
			generateKeyPair();
		}
		return keyPair.getPublic();
	}

	public String getKeyId() {
		return keyId;
	}
}
