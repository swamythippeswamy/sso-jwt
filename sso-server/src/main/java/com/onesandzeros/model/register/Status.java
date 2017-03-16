package com.onesandzeros.model.register;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

public class Status implements Serializable {
	private static final long serialVersionUID = -1473859903427903096L;
	private int code = HttpStatus.OK.value();
	private String message;

	public Status() {
	}

	public Status(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Status [code=" + code + ", message=" + message + "]";
	}

}
