package com.onesandzeros.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;

public interface SSOAuthService {
	void signup();

	UserAccountEntity getAccountInfo();

	UserAccountEntity login(HttpServletRequest request, HttpServletResponse response, LoginPayload userDetails);
}
