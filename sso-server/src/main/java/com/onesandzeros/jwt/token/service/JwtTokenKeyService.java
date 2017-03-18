package com.onesandzeros.jwt.token.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.onesandzeros.models.JwtTokensKeyPair;
import com.onesandzeros.models.KeyData;
import com.onesandzeros.util.CommonUtil;
import com.onesandzeros.util.JsonUtil;
import com.onesandzeros.util.RestServiceUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SigningKeyResolver;

/**
 * Ones spring beans are loaded completely this class reads the server_key.txt
 * file and create data as {@link JwtTokensKeyPair} for keyId and public,
 * private key pairs.
 * 
 * The keys in server_key.txt is Base64 encoded.
 * 
 * These set of keys will be shared in sso-client service also.
 * 
 * Randomly one key is selected from the set of keys and particular key id is
 * added to jwt header
 * 
 * @author swamy
 *
 */
@Component
public class JwtTokenKeyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenKeyService.class);

	private static final Random random = new Random();

	/**
	 * Contains map of keyId and {@link JwtTokensKeyPair} having (keyId,
	 * privateKey and publickKey)
	 */
	private Map<String, JwtTokensKeyPair> keyPairMap = new HashMap<>();

	private List<String> keyIds = new ArrayList<>();
	private Map<String, Key> privateKeyMap = new HashMap<>();

	private SigningKeyResolver resolver;

	@Autowired
	Environment env;

	@Autowired
	RestServiceUtil restUtil;

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void readKeyPair() throws ClassNotFoundException, IOException {
		LOGGER.info("Reading Keys Info from serialized key file : start");
		InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("server_key.txt");

		String keyData = CommonUtil.readDataFromInputStream(inStream);

		List<JwtTokensKeyPair> keysList = new ArrayList<>(1);
		keysList = JsonUtil.deserializeListData(keyData, JwtTokensKeyPair.class);

		if (!CollectionUtils.isEmpty(keysList)) {
			for (JwtTokensKeyPair keyPair : keysList) {
				keyPairMap.put(keyPair.getKeyId(), keyPair);
				keyIds.add(keyPair.getKeyId());

				privateKeyMap.put(keyPair.getKeyId(), getDecodedPrivateKey(keyPair.getPrivateKey()));
			}
		}
		LOGGER.info("Reading Keys Info from serialized key file: end");
	}

	public KeyData getOnePrivateKeyRandomly() {
		KeyData keyData = null;
		if (!CollectionUtils.isEmpty(keyIds)) {
			int index = random.nextInt(keyIds.size());
			String keyId = keyIds.get(index);
			keyData = new KeyData(keyId, privateKeyMap.get(keyId));
		}
		return keyData;
	}

	public Key getPrivateKeyByKeyId(String keyId) {
		return privateKeyMap.get(keyId);
	}

	private Key getDecodedPrivateKey(String privateKeyStr) {
		byte[] decoded = Base64Utils.decodeFromString(privateKeyStr);
		Key privateKey = null;
		try {
			privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			LOGGER.error("Error in decoding privateKey", e);
		}

		return privateKey;
	}

	public class SigninKeyResolverAdapter implements SigningKeyResolver {
		@Override
		public Key resolveSigningKey(JwsHeader header, Claims claims) {
			LOGGER.info("kid in header : {}", header.getKeyId());

			String keyId = header.getKeyId();
			if (StringUtils.isEmpty(keyId)) {
				LOGGER.error("Header kid is missing in the token header with cliams: {} ", claims);
				throw new JwtException("Header kid is missing in the token header with cliams " + claims);
			}
			Key key = privateKeyMap.get(keyId);

			return key;
		}

		@Override
		public Key resolveSigningKey(JwsHeader header, String plaintext) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public SigningKeyResolver getResolver() {
		if (null == resolver) {
			resolver = new SigninKeyResolverAdapter();
		}
		return resolver;
	}
}
