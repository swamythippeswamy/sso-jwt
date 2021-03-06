package com.onesandzeros.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.util.StringUtils;

/**
 * @author swamy
 *
 */
public class CommonUtil {

	/**
	 * 
	 * @param email
	 * @return
	 */
	public static boolean validateEmail(String email) {
		boolean validEmail = true;
		String emailPattern = "^[a-zA-Z0-9_]+[\\.a-zA-Z0-9_]*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$";
		if (StringUtils.isEmpty(email) || !email.matches(emailPattern)) {
			validEmail = false;
		}
		return validEmail;
	}

	/**
	 * 
	 * Matches for Minimum any 4 characters
	 * 
	 * @param password
	 * @return
	 */
	public static boolean validatePassword(String password) {
		boolean validPwd = true;
		String passwordPattern = ".{4,}";
		if (StringUtils.isEmpty(password) || !password.matches(passwordPattern)) {
			validPwd = false;
		}
		return validPwd;
	}

	public static String readDataFromInputStream(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder jsonKeyData = new StringBuilder();

		String line = "";
		while ((line = reader.readLine()) != null) {
			jsonKeyData.append(line);
		}
		return jsonKeyData.toString();
	}
}
