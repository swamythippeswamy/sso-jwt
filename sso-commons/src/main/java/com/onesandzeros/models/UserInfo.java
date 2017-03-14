package com.onesandzeros.models;

import java.io.Serializable;

/**
 * Model used for storing the user's session data
 * 
 * @author ubuntu
 *
 */
public class UserInfo implements Serializable {
	private static final long serialVersionUID = -6870910757476030312L;
	private String userName;
	private String emailId;
	private String role;

	public UserInfo() {

	}

	public UserInfo(String userName, String emailId) {
		this.userName = userName;
		this.emailId = emailId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "UserInfo [userName=" + userName + ", emailId=" + emailId + ", role=" + role + "]";
	}

}
