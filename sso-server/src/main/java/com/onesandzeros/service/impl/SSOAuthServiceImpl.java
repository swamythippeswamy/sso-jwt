package com.onesandzeros.service.impl;

import static com.onesandzeros.models.AccountType.EMAIL;
import static com.onesandzeros.models.AccountType.FACEBOOK;
import static com.onesandzeros.models.AccountType.GOOGLE;
import static com.onesandzeros.models.AccountType.PHONE;

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
import com.onesandzeros.model.register.LoginServiceResponse;
import com.onesandzeros.models.BaseResponse;
import com.onesandzeros.models.SessionData;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.service.SSOAuthService;

@Service
public class SSOAuthServiceImpl implements SSOAuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSOAuthServiceImpl.class);

	@Autowired
	private JwtTokenBuilder tokenBuilder;

	@Autowired
	private EmailLoginService emailLoginService;

	@Autowired
	private FacebookLoginService facebookLoginService;

	@Autowired
	private UserAccountDao ssoAuthDao;

	@Override
	@Transactional
	public BaseResponse<String> login(HttpServletRequest request, HttpServletResponse response,
			LoginPayload loginPayload) throws ServiceException {

		BaseResponse<String> resp = new BaseResponse<>();
		LoginServiceResponse<UserInfo> loginServiceResp = null;

		if (loginPayload.getAccountType() == null) {
			resp.setCode(HttpStatus.BAD_REQUEST.value());
			resp.setData("Account type is required in the login request");
			return resp;
		}
		// TODO: (Swamy) Use factory pattern to get which login service to use
		if (loginPayload.getAccountType() == EMAIL) {
			loginServiceResp = emailLoginService.emailLogin(loginPayload);
		} else if (loginPayload.getAccountType() == FACEBOOK) {
			loginServiceResp = facebookLoginService.login(loginPayload);

		} else if (loginPayload.getAccountType() == GOOGLE) {

		} else if (loginPayload.getAccountType() == PHONE) {

		}
		LOGGER.info("loginServiceResp : {}", loginServiceResp);

		resp.setCode(loginServiceResp.getStatus().getCode());
		resp.setData(loginServiceResp.getStatus().getMessage());

		if (null != loginServiceResp.getData()) {
			tokenBuilder.addAuthToken(response, loginServiceResp.getData());
		} else {
			LOGGER.error("Auth token not generated, since userinfo not available in loginServiceResponse");
		}
		return resp;
	}

	@Override
	public BaseResponse<String> signup(HttpServletRequest request, HttpServletResponse response,
			LoginPayload loginPayload) throws ServiceException {
		LoginServiceResponse<UserInfo> loginResp = emailLoginService.signUp(loginPayload);
		return new BaseResponse<String>(loginResp.getStatus().getCode(), loginResp.getStatus().getMessage());
	}

	@Override
	public String activateAccount(HttpServletRequest request, HttpServletResponse response, String email, String token)
			throws ServiceException {

		String message = emailLoginService.activateEmailAccount(email, token);
		return message;
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

}
