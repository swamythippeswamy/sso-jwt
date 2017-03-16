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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onesandzeros.dao.SSOAuthDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.jwttoken.service.JwtTokenBuilder;
import com.onesandzeros.model.UserAccountEntity;
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
	private SSOAuthDao ssoAuthDao;

	@Override
	@Transactional
	public BaseResponse<String> login(HttpServletRequest request, HttpServletResponse response,
			LoginPayload loginPayload) throws ServiceException {

		BaseResponse<String> resp = new BaseResponse<>();
		LoginServiceResponse<UserInfo> loginServiceResp = null;

		// TODO: (Swamy) Use factory method to get which login service to use
		if (loginPayload.getAccountType() == EMAIL) {
			loginServiceResp = emailLoginService.emailLogin(loginPayload);
		} else if (loginPayload.getAccountType() == FACEBOOK) {
			loginServiceResp = facebookLoginService.login(loginPayload);

		} else if (loginPayload.getAccountType() == GOOGLE) {

		} else if (loginPayload.getAccountType() == PHONE) {

		}
		LOGGER.info("userDetails : {}", loginPayload);

		if (null != loginServiceResp && null != loginServiceResp.getData()) {
			tokenBuilder.addAuthToken(response, loginServiceResp.getData());
		}
		resp.setCode(loginServiceResp.getStatus().getCode());
		resp.setData(loginServiceResp.getStatus().getMessage());
		return resp;
	}

	@Override
	public void signup() {
		LOGGER.info("signup");
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
