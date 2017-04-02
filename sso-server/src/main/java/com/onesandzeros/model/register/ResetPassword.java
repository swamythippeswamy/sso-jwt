package com.onesandzeros.model.register;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

public class ResetPassword implements Serializable {
	private static final long serialVersionUID = -4487358021143396410L;
	private String email;
	private String oldPassword;
	private String newPassword;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public boolean validatePwds() {
		return StringUtils.isNotBlank(oldPassword) && oldPassword.equals(newPassword);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResetPassword [email=").append(email).append(", otp=").append(oldPassword)
				.append(", newPassword=").append(newPassword).append("]");
		return builder.toString();
	}

}
