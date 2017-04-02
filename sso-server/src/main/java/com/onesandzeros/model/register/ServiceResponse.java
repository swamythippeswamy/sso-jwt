package com.onesandzeros.model.register;

import java.io.Serializable;

public class ServiceResponse<T> implements Serializable {
	private static final long serialVersionUID = -3941295705330764813L;
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
