package com.onesandzeros.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

public class EncryptDecryptUtil {

	private static final String encryptionKey = "0n35uN7z36O5k3y5";
	private static final Key aesKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");

	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptDecryptUtil.class);

	public static String encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String encryptedData = null;

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encrypted = cipher.doFinal(data.getBytes());
		encryptedData = Base64Utils.encodeToString(encrypted);

		return encryptedData;
	}

	public static String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String decryptedData = null;
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, aesKey);

		byte[] encrypted = cipher.doFinal(Base64Utils.decodeFromString(data));
		decryptedData = new String(encrypted);
		return decryptedData;
	}

}
