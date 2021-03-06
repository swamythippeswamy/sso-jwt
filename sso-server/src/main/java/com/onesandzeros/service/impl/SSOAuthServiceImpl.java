package com.onesandzeros.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onesandzeros.dao.UserAccountDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.jwt.token.service.JwtTokenBuilder;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.ServiceResponse;
import com.onesandzeros.model.register.Status;
import com.onesandzeros.model.register.ResetPassword;
import com.onesandzeros.models.BaseResponse;
import com.onesandzeros.models.SessionData;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.service.LoginService;
import com.onesandzeros.service.SSOAuthService;

@Service
public class SSOAuthServiceImpl implements SSOAuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSOAuthServiceImpl.class);

	@Autowired
	private JwtTokenBuilder tokenBuilder;

	@Autowired
	private EmailLoginService emailLoginService;

	@Autowired
	private UserAccountDao ssoAuthDao;

	@Autowired
	private LoginServiceFactory loginServiceFactory;

	@Override
	@Transactional
	public BaseResponse<String> login(HttpServletRequest request, HttpServletResponse response,
			LoginPayload loginPayload) throws ServiceException {

		BaseResponse<String> resp = new BaseResponse<>();
		ServiceResponse<UserInfo> loginServiceResp = null;

		if (loginPayload.getAccountType() == null) {
			resp.setCode(HttpStatus.BAD_REQUEST.value());
			resp.setData("Account type is required in the login request");
			return resp;
		}

		try {
			LoginService loginService = loginServiceFactory.getLoginService(loginPayload.getAccountType());

			loginServiceResp = loginService.login(loginPayload, request);

			LOGGER.info("loginServiceResp : {}", loginServiceResp);

			resp.setCode(loginServiceResp.getStatus().getCode());
			resp.setData(loginServiceResp.getStatus().getMessage());

			if (null != loginServiceResp.getData()) {
				tokenBuilder.addAuthToken(response, loginServiceResp.getData());
			} else {
				LOGGER.error("Auth token not generated, since userinfo not available in loginServiceResponse");
			}

		} catch (ServiceException e) {
			resp.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			resp.setData(e.getMessage());
		}
		return resp;
	}

	@Override
	public BaseResponse<String> signup(HttpServletRequest request, HttpServletResponse response,
			LoginPayload loginPayload) throws ServiceException {
		BaseResponse<String> resp = new BaseResponse<>();
		try {
			emailLoginService.validateSignUpData(loginPayload);

			if (emailLoginService.isEmailAlreadyExists(loginPayload.getEmail())) {

				resp.setCode(HttpStatus.BAD_REQUEST.value());
				resp.setData("EmailId is already registered");
			} else {
				emailLoginService.addUserAndSendVerificationMail(loginPayload);

				resp.setCode(HttpStatus.OK.value());
				resp.setData("A mail is sent to registered email id for account activation");
			}
			LOGGER.info("signUp Resp : {}", resp);
		} catch (ServiceException e) {
			resp.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			resp.setData(e.getMessage());
		}
		return resp;
	}

	@Override
	public String activateAccount(HttpServletRequest request, HttpServletResponse response, String email, String token)
			throws ServiceException {

		String message = emailLoginService.activateEmailAccount(email, token);
		return message;
	}

	// Revoke jwt token
	@Override
	public BaseResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
		return new BaseResponse<>();
	}

	@Override
	@Transactional(readOnly = true)
	public UserAccountEntity getAccountInfo() throws ServiceException {
		UserAccountEntity entity = null;
		try {
			entity = ssoAuthDao.findByEmail(SessionData.getUserInfo().getEmailId());
		} catch (DaoException e) {
			LOGGER.error("Error in getting the data from db", e);
			throw new ServiceException("Error in getting the data from db");
		}
		return entity;
	}

	@Override
	public BaseResponse<String> resetPassword(HttpServletRequest request, HttpServletResponse response,
			ResetPassword resetPwd) {
		BaseResponse<String> resp = new BaseResponse<>();
		try {
			ServiceResponse<String> loginResp = emailLoginService.resetPassword(resetPwd);

			resp.setCode(loginResp.getStatus().getCode());
			resp.setData(loginResp.getStatus().getMessage());
			LOGGER.info("Reset Password : {}", loginResp);
		} catch (ServiceException e) {
			resp.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			resp.setData(e.getMessage());
		}
		return resp;
	}

}
