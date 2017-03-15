package com.onesandzeros.util;

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
	// TODO: User Pattern and Matcher for validation
	public static boolean validateEmail(String email) {
		String emailPattern = "^[a-zA-Z0-9_]+[\\.a-zA-Z0-9_]*@[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$";
		if (!StringUtils.isEmpty(email) || email.matches(emailPattern)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Matches for Minimum 6 characters
	 * 
	 * @param password
	 * @return
	 */
	public static boolean validatePassword(String password) {
		String passwordPattern = ".{6,}";
		if (!StringUtils.isEmpty(password) || password.matches(passwordPattern)) {
			return true;
		}
		return false;
	}

}
