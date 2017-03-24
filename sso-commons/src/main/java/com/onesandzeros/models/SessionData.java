package com.onesandzeros.models;

/**
 * 
 * Created for keeping the user session information in ThreadLocal
 * 
 * @author swamy
 *
 */
public class SessionData {
	// TODO (Why is thread local needed? I mean why single SessionData class and
	// a static threadlocal. What is the reason for not creating more session
	// data objects?)

	// The user information present in the jwt token is parsed and kept
	// in Threadlocal object for each request thread, so that this can be used
	// anywhere in the code instead and decrypting and reading from token
	// everytime
	private static ThreadLocal<UserInfo> userInfo = new ThreadLocal<UserInfo>();

	public static UserInfo getUserInfo() {
		if (null == userInfo.get()) {
			userInfo.set(new UserInfo());
		}
		return userInfo.get();
	}

	public static void setUserInfo(UserInfo info) {
		if (null == info) {
			info = new UserInfo();
		}
		userInfo.set(info);
	}

	public static void clear() {
		userInfo.remove();
	}

}
