package com.onesandzeros.service.impl;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.onesandzeros.dao.UserAccountDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.LoginServiceResponse;
import com.onesandzeros.model.register.Status;
import com.onesandzeros.model.social.FaceBookAuthResponse;
import com.onesandzeros.models.AccountType;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.social.auth.service.FacebookAuthentication;

@Component
public class FacebookLoginService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookLoginService.class);

	@Autowired
	private FacebookAuthentication facebookAuth;

	@Autowired
	private UserAccountDao userActDao;

	public LoginServiceResponse<UserInfo> login(LoginPayload loginPayload) throws ServiceException {
		FaceBookAuthResponse authResp = facebookAuth.authenticateToken(loginPayload.getToken());

		LOGGER.info("AuthResponse generated : {}", authResp);

		LoginServiceResponse<UserInfo> loginResp = new LoginServiceResponse<>();
		if (authResp.getStatus() == FaceBookAuthResponse.SUCCESS) {
			try {
				UserAccountEntity userAcctEnt = userActDao.findByFacebookUserIdAndAccountType(authResp.getUserId(),
						AccountType.FACEBOOK);
				if (null == userAcctEnt) {
					userAcctEnt = addNewFacebookUser(authResp);
				}

				UserInfo userInfo = new UserInfo();
				userInfo.setAccountType(userAcctEnt.getAccountType());
				userInfo.setUserName(userAcctEnt.getName());
				userInfo.setEmailId(userAcctEnt.getEmail());
				userInfo.setFbUserId(userAcctEnt.getFacebookUserId());

				loginResp.setData(userInfo);
				loginResp.setStatus(new Status(HttpStatus.OK.value(), "Login successful"));

			} catch (DaoException e) {
				LOGGER.error("Error in fetching user account info data from db", e);
				loginResp.setStatus(
						new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error in fetching data from db"));
				throw new ServiceException(e.getMessage());
			}
		} else if (authResp.getStatus() == FaceBookAuthResponse.FAILED) {
			LOGGER.error("Failure in authenticating user token with facebook");
			if (authResp.isExpired()) {
				loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Access token is expired"));
			} else if (authResp.isInvalid()) {
				loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Access token is invalid"));
			}
		}

		LOGGER.info("LoginServiceResponse : {}", loginResp);
		return loginResp;

	}

	private UserAccountEntity addNewFacebookUser(FaceBookAuthResponse authResp) {
		UserAccountEntity accountEntity = new UserAccountEntity();
		accountEntity.setAccountType(AccountType.FACEBOOK);
		accountEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
		accountEntity.setEmail(authResp.getEmail());
		accountEntity.setFacebookUserId(authResp.getUserId());

		accountEntity.setName(authResp.getName());

		userActDao.save(accountEntity);
		return accountEntity;
	}
}
