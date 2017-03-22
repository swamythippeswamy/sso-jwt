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
