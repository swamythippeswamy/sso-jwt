package com.onesandzeros.jwttoken.service;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import com.onesandzeros.models.PublicKeyData;
import com.onesandzeros.util.RestServiceUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.lang.Strings;

@Service
public class JwtSecurityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtSecurityService.class);

	private Map<String, Key> publicKeys = new HashMap<String, Key>();

	private SigningKeyResolver resolver;

	@Autowired
	Environment env;

	@Autowired
	RestServiceUtil restUtil;

	public boolean addPublicKey(PublicKeyData publicKeyData) {

		boolean addStatus = true;
		String encodedPublicKey = publicKeyData.getEncodedPublicKey();
		PublicKey publicKey = null;
		try {
			publicKey = getDecodedPublicKey(encodedPublicKey);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			LOGGER.error("Error in decoding the publicKey", e);
		}

		if (null != publicKey) {
			publicKeys.put(publicKeyData.getKeyId(), publicKey);
			LOGGER.info("Successfully added the public key");
		}

		return addStatus;
	}

	@PostConstruct
	public void refreshPublicKey() {

		String ssoBaseServiceUrl = env.getProperty("sso.server.get.public.key.url");
		if (StringUtils.isEmpty(ssoBaseServiceUrl)) {
			LOGGER.info("Public key is not updated, since sso.server.get.public.key.url not found in props");
			return;
		}

		PublicKeyData publicKeyData = null;
		try {
			publicKeyData = restUtil.getRequest(ssoBaseServiceUrl, PublicKeyData.class);
			addPublicKey(publicKeyData);
		} catch (Exception e) {
			LOGGER.error("Error in getting and updating the public key from sso base server", e);
		}
	}

	public Key getPublicKey(String keyId) {
		return publicKeys.get(keyId);
	}

	private PublicKey getDecodedPublicKey(String encodedPublicKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		PublicKey key = null;
		byte[] decoded = Base64Utils.decodeFromString(encodedPublicKey);

		key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));

		return key;
	}

	public class SinginKeyResolverAdapter implements SigningKeyResolver {
		@Override
		public Key resolveSigningKey(JwsHeader header, Claims claims) {
			String keyId = header.getKeyId();
			if (!Strings.hasText(keyId)) {
				throw new JwtException("Missing required 'kid' header param in JWT with claims: " + claims);
			}
			Key key = publicKeys.get(keyId);
			if (key == null) {
				throw new JwtException("No public key registered for kid: " + keyId + ". JWT claims: " + claims);
			}
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
			resolver = new SigningKeyResolverAdapter();
		}
		return resolver;
	}

}