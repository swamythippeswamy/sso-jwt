package com.onesandzeros.model.register;

import java.io.Serializable;

import com.onesandzeros.models.AccountType;

/**
 * 
 * Model used as Request Payload for login and register
 * 
 * @author swamy
 *
 */
public class LoginPayload implements Serializable {

	private static final long serialVersionUID = -38580924358230501L;
	private String name;
	private String email;
	private String phone;
	private String password;
	private AccountType accountType;
	private String token;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "LoginPayload [name=" + name + ", email=" + email + ", phone=" + phone + ", password=" + password
				+ ", accountType=" + accountType + ", token=" + token + "]";
	}
}
