package com.onesandzeros.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.models.BaseResponse;

public interface SSOAuthService {
	void signup();

	UserAccountEntity getAccountInfo() throws ServiceException;

	BaseResponse<String> login(HttpServletRequest request, HttpServletResponse response, LoginPayload userDetails)
			throws ServiceException;
}
