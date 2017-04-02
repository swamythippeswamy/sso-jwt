package com.onesandzeros.service.impl;

import static com.onesandzeros.model.social.FaceBookAuthResponse.Status.FAILED;
import static com.onesandzeros.model.social.FaceBookAuthResponse.Status.SUCCESS;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.onesandzeros.dao.UserAccountDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.persistance.LoginStatus;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.ServiceResponse;
import com.onesandzeros.model.register.Status;
import com.onesandzeros.model.social.FaceBookAuthResponse;
import com.onesandzeros.models.AccountStatus;
import com.onesandzeros.models.AccountType;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.service.LoginService;
import com.onesandzeros.service.UsersLoginLogService;
import com.onesandzeros.social.auth.service.FacebookAuthentication;

@Component
public class FacebookLoginService implements LoginService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookLoginService.class);

	@Autowired
	private FacebookAuthentication facebookAuth;

	@Autowired
	private UserAccountDao userActDao;

	@Autowired
	private UsersLoginLogService loginLogService;

	@Override
	public ServiceResponse<UserInfo> login(LoginPayload loginPayload, HttpServletRequest request)
			throws ServiceException {
		FaceBookAuthResponse authResp = facebookAuth.authenticateToken(loginPayload.getToken());

		LOGGER.info("AuthResponse generated : {}", authResp);

		ServiceResponse<UserInfo> loginResp = new ServiceResponse<>();
		if (authResp.getStatus() == SUCCESS) {
			try {

				UserAccountEntity userAcctEnt = userActDao.findByEmail(authResp.getEmail());

				if (null == userAcctEnt) {
					userAcctEnt = addNewFacebookUser(authResp);
				} else if (StringUtils.isBlank(userAcctEnt.getFacebookUserId())) {
					if (AccountType.EMAIL == userAcctEnt.getAccountType()
							|| AccountType.GOOGLE == userAcctEnt.getAccountType()) {
						userAcctEnt.setName(authResp.getName());
						userAcctEnt.setFacebookUserId(authResp.getUserId());
						userAcctEnt.setStatus(AccountStatus.ACTIVE);
						userAcctEnt.setAccountType(AccountType.FACEBOOK);
						userActDao.save(userAcctEnt);
					}
				}

				UserInfo userInfo = new UserInfo();
				userInfo.setAccountType(userAcctEnt.getAccountType());
				userInfo.setUserName(userAcctEnt.getName());
				userInfo.setEmailId(userAcctEnt.getEmail());
				userInfo.setFbUserId(userAcctEnt.getFacebookUserId());

				loginResp.setData(userInfo);
				loginResp.setStatus(new Status(HttpStatus.OK.value(), "Login successful"));

				loginLogService.addLog(userAcctEnt.getUserId(), LoginStatus.SUCCESS, request.getRemoteAddr());

			} catch (DaoException e) {
				LOGGER.error("Error in fetching user account info data from db", e);
				loginResp.setStatus(
						new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error in fetching data from db"));
				throw new ServiceException(e.getMessage());
			}
		} else if (authResp.getStatus() == FAILED) {
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
		accountEntity.setStatus(AccountStatus.ACTIVE);

		userActDao.save(accountEntity);
		return accountEntity;
	}
}
