package com.onesandzeros.service;

import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.LoginServiceResponse;
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
	LoginServiceResponse<UserInfo> login(LoginPayload loginPayload) throws ServiceException;
}
