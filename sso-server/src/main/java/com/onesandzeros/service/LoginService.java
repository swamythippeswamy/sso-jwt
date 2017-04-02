package com.onesandzeros.service;

import javax.servlet.http.HttpServletRequest;

import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.ServiceResponse;
import com.onesandzeros.models.UserInfo;

/**
 *
 * {@link LoginService} implemented by other login services like Facebook,
 * Google, Email and Phone login
 * 
 * @author swamy
 *
 */
public interface LoginService {
	ServiceResponse<UserInfo> login(LoginPayload loginPayload, HttpServletRequest request) throws ServiceException;
}
