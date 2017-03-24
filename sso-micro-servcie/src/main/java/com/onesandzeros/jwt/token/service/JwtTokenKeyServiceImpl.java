package com.onesandzeros.jwt.token.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import com.onesandzeros.models.JwtTokensKeyPair;
import com.onesandzeros.util.CommonUtil;
import com.onesandzeros.util.JsonUtil;
import com.onesandzeros.util.RestClient;

@Service("tokenKeyService")
public class JwtTokenKeyServiceImpl implements JwtTokenKeyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenKeyServiceImpl.class);

	private static final String KEY_ALGORITHM = "RSA";
	/**
	 * Contains map of keyId and {@link JwtTokensKeyPair} having (keyId and
	 * publickKey)
	 */
	private Map<String, JwtTokensKeyPair> keyPairMap = new HashMap<>();
	private Map<String, Key> publicKeys = new HashMap<String, Key>();

	@Autowired
	Environment env;

	@Autowired
	RestClient restClient;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void readKeyData() throws ClassNotFoundException, IOException {

		LOGGER.info("Reading publicKeys from serialized key file: start");

		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("client_key.txt");

		String keyData = CommonUtil.readDataFromInputStream(inStream);

		List<JwtTokensKeyPair> keysList = new ArrayList<>(1);
		keysList = JsonUtil.deserializeListData(keyData, JwtTokensKeyPair.class);

		if (!CollectionUtils.isEmpty(keysList)) {
			for (JwtTokensKeyPair keyPair : keysList) {
				keyPairMap.put(keyPair.getKeyId(), keyPair);
				publicKeys.put(keyPair.getKeyId(), getDecodedPublicKey(keyPair.getPublicKey()));
			}
		}
		LOGGER.info("Reading publicKeys from serialized key file: end");
	}

	public Key getPublicKey(String keyId) {
		return publicKeys.get(keyId);
	}

	private Key getDecodedPublicKey(String publicKeyStr) {
		byte[] decoded = Base64Utils.decodeFromString(publicKeyStr);
		Key publicKey = null;
		try {
			publicKey = KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			LOGGER.error("Error in decoding public key", e);
		}

		return publicKey;
	}
}