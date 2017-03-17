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
	private AccountType accountType;
	private String role;
	private String fbUserId;

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

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getFbUserId() {
		return fbUserId;
	}

	public void setFbUserId(String fbUserId) {
		this.fbUserId = fbUserId;
	}

	@Override
	public String toString() {
		return "UserInfo [userName=" + userName + ", emailId=" + emailId + ", accountType=" + accountType + ", role="
				+ role + ", fbUserId=" + fbUserId + "]";
	}

}
