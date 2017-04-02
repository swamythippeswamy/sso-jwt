package com.onesandzeros.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.ResetPassword;
import com.onesandzeros.models.BaseResponse;

public interface SSOAuthService {

	UserAccountEntity getAccountInfo() throws ServiceException;

	BaseResponse<String> login(HttpServletRequest request, HttpServletResponse response, LoginPayload userDetails)
			throws ServiceException;

	BaseResponse<String> signup(HttpServletRequest request, HttpServletResponse response, LoginPayload loginPayload)
			throws ServiceException;

	String activateAccount(HttpServletRequest request, HttpServletResponse response, String email, String token)
			throws ServiceException;

	BaseResponse<String> logout(HttpServletRequest request, HttpServletResponse response);

	BaseResponse<String> resetPassword(HttpServletRequest request, HttpServletResponse response, ResetPassword resetPwd);
}
