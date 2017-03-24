package com.onesandzeros.model.social;

public class FaceBookAuthResponse {

	public static enum Status {
		SUCCESS, FAILED
	}

	String email;
	String userId;
	String name;
	Status status = Status.SUCCESS;
	boolean expired;
	boolean invalid;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	@Override
	public String toString() {
		return "FaceBookAuthResponse [email=" + email + ", userId=" + userId + ", name=" + name + ", status=" + status
				+ ", expired=" + expired + ", invalid=" + invalid + "]";
	}

}
