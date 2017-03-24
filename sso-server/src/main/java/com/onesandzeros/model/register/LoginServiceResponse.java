package com.onesandzeros.model.register;

public class LoginServiceResponse<T> {
	private Status status;
	private T data;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoginServiceResponse [status=").append(status).append(", data=").append(data).append("]");
		return builder.toString();
	}

}
