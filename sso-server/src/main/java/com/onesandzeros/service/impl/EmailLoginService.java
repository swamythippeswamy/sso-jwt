package com.onesandzeros.service.impl;

import org.glassfish.jersey.server.spring.AutowiredInjectResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.onesandzeros.dao.SSOAuthDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.mail.service.MailClient;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.LoginServiceResponse;
import com.onesandzeros.model.register.Status;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.util.CommonUtil;

@Component
public class EmailLoginService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailLoginService.class);

	@Autowired
	private SSOAuthDao ssoAuthDao;

	@Autowired
	private MailClient mailClient;

	public LoginServiceResponse<UserInfo> emailLogin(LoginPayload loginPayload) throws ServiceException {
		UserInfo userInfo = new UserInfo();

		validate(loginPayload);
		LoginServiceResponse<UserInfo> loginResp = new LoginServiceResponse<>();
		UserAccountEntity userAccInfo;
		try {
			userAccInfo = ssoAuthDao.findByEmail(loginPayload.getEmail());
		} catch (DaoException e) {
			loginResp.setStatus(
					new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching login details from db"));
			return loginResp;
		}

		if (null == userAccInfo) {
			loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Login id is not regiesterd"));
			return loginResp;
		}
		userInfo = new UserInfo(userAccInfo.getName(), userAccInfo.getEmail());
		loginResp.setData(userInfo);

		return loginResp;

	}

	public LoginServiceResponse<UserInfo> signUp(LoginPayload loginPayload) throws ServiceException {
		validate(loginPayload);
		mailClient.prepareAndSend(loginPayload.getEmail(), "Test message");
		return new LoginServiceResponse<>();
	}

	private void validate(LoginPayload userDetails) throws ServiceException {
		if (!CommonUtil.validateEmail(userDetails.getEmail())) {
			throw new ServiceException("Invalid EmailId");
		}
		if (!CommonUtil.validatePassword(userDetails.getPassword())) {
			throw new ServiceException("Invalid Password");
		}
	}
}
