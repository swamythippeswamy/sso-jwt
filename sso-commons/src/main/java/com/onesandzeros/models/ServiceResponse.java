package com.onesandzeros.models;

import java.io.Serializable;

public class ServiceResponse<T> implements Serializable {

	private static final long serialVersionUID = 8174661666931009996L;

	private int code;

	private T data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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
		builder.append("ServiceResponse [code=").append(code).append(", data=").append(data).append("]");
		return builder.toString();
	}

}
