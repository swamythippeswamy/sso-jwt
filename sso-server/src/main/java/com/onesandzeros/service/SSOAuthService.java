package com.onesandzeros.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.models.BaseResponse;

public interface SSOAuthService {

	UserAccountEntity getAccountInfo() throws ServiceException;

	BaseResponse<String> login(HttpServletRequest request, HttpServletResponse response, LoginPayload userDetails)
			throws ServiceException;

	BaseResponse<String> signup(HttpServletRequest request, HttpServletResponse response, LoginPayload loginPayload)
			throws ServiceException;
}
