package com.onesandzeros.service.impl;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.onesandzeros.dao.SSOAuthDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.LoginServiceResponse;
import com.onesandzeros.model.register.Status;
import com.onesandzeros.models.AccountType;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.social.auth.service.FacebookAuthentication;
import com.onesandzeros.social.model.FaceBookAuthResponse;

@Component
public class FacebookLoginService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookLoginService.class);

	@Autowired
	private FacebookAuthentication facebookAuth;

	@Autowired
	private SSOAuthDao ssoAuthDao;

	public LoginServiceResponse<UserInfo> login(LoginPayload loginPayload) throws ServiceException {
		FaceBookAuthResponse authResp = facebookAuth.authenticateToken(loginPayload.getToken());

		LoginServiceResponse<UserInfo> loginResp = new LoginServiceResponse<>();
		if (authResp.getStatus() == FaceBookAuthResponse.SUCCESS) {
			try {
				UserAccountEntity userAcctEnt = ssoAuthDao.findByFacebookUserIdAndAccountType(authResp.getUserId(),
						AccountType.FACEBOOK);
				if (null == userAcctEnt) {
					userAcctEnt = addNewFacebookUser(authResp);
				}

				UserInfo userInfo = new UserInfo();
				userInfo.setAccountType(userAcctEnt.getAccountType());
				userInfo.setUserName(userAcctEnt.getName());
				userInfo.setEmailId(userAcctEnt.getEmail());

				loginResp.setData(userInfo);

			} catch (DaoException e) {
				LOGGER.error("Error in fetching user account info data from db", e);
				loginResp.setStatus(
						new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error in fetching data from db"));
				throw new ServiceException(e.getMessage());
			}
		} else if (authResp.getStatus() == FaceBookAuthResponse.FAILED) {
			LOGGER.error("Failure in authenticating the facebook");
			loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Facebook Authentication failed"));
			throw new ServiceException("Authentication failure exception");
		}

		return loginResp;

	}

	private UserAccountEntity addNewFacebookUser(FaceBookAuthResponse authResp) {
		UserAccountEntity accountEntity = new UserAccountEntity();
		accountEntity.setAccountType(AccountType.FACEBOOK);
		accountEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
		accountEntity.setEmail(authResp.getEmail());
		accountEntity.setFacebookUserId(authResp.getUserId());

		accountEntity.setName(authResp.getName());

		ssoAuthDao.save(accountEntity);
		return accountEntity;
	}
}
