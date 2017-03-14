package com.onesandzeros.models;

import java.io.Serializable;

public class ResponseModel<T> implements Serializable {

	private static final long serialVersionUID = -4913375761240032408L;

	private int code;

	private T data;

	public ResponseModel(int code, T data) {
		this.code = code;
		this.data = data;
	}

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
		return "ApiResponse [code=" + code + ", data=" + data + "]";
	}

}
