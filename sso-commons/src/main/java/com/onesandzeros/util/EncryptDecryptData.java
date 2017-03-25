package com.onesandzeros.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

/**
 * This has methods for encrypting and decrypting a string using AES encryption
 * with 128 bits key
 * 
 * @author swamy
 *
 */
@Component
public class EncryptDecryptData {

	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptDecryptData.class);

	public String encrypt(String data, Key key) {
		String encryptedData = null;
		LOGGER.info("Key Alg : {}", key.getAlgorithm());
		try {
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encrypted = cipher.doFinal(data.getBytes());
			encryptedData = Base64Utils.encodeToString(encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| InvalidKeyException e) {
			LOGGER.error("Error in encrypting the data", e);
		}

		return encryptedData;
	}

	public String decrypt(String data, Key key) {
		String decryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encrypted = cipher.doFinal(Base64Utils.decodeFromString(data));
			decryptedData = new String(encrypted);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException
				| InvalidKeyException e) {
		}
			LOGGER.error("Error in decrypting the data", e);
		}
		return decryptedData;
	}
