package com.onesandzeros.service.impl;

import static com.onesandzeros.model.register.AccountType.EMAIL;
import static com.onesandzeros.model.register.AccountType.FACEBOOK;
import static com.onesandzeros.model.register.AccountType.GOOGLE;
import static com.onesandzeros.model.register.AccountType.PHONE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onesandzeros.dao.SSOAuthDao;
import com.onesandzeros.jwttoken.service.JwtTokenBuilder;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.models.SessionData;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.service.SSOAuthService;

@Service
public class SSOAuthServiceImpl implements SSOAuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSOAuthServiceImpl.class);

	@Autowired
	private SSOAuthDao ssoAuthDao;

	@Autowired
	private JwtTokenBuilder tokenBuilder;

	@Override
	@Transactional(readOnly = true)
	public UserAccountEntity login(HttpServletRequest request, HttpServletResponse response, LoginPayload userDetails) {
		UserAccountEntity userAccInfo = null;
		if (userDetails.getAccountType() == EMAIL) {
			userAccInfo = ssoAuthDao.findByEmail(userDetails.getEmail());
		} else if (userDetails.getAccountType() == FACEBOOK) {

		} else if (userDetails.getAccountType() == GOOGLE) {

		} else if (userDetails.getAccountType() == PHONE) {

		}
		LOGGER.info("userDetails : {}, userAccInfo : {}", userDetails, userAccInfo);
		if (null == userAccInfo) {
			
		}

		UserInfo userInfo = new UserInfo(userAccInfo.getName(), userAccInfo.getEmail());

		tokenBuilder.addAuthToken(response, userInfo);
		return userAccInfo;
	}

	@Override
	public void signup() {
		LOGGER.info("signup");
	}

	@Override
	@Transactional(readOnly = true)
	public UserAccountEntity getAccountInfo() {
		return ssoAuthDao.findByEmail(SessionData.getUserInfo().getEmailId());
	}

}
