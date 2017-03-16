package com.onesandzeros.social.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.service.impl.SSOAuthServiceImpl;
import com.onesandzeros.social.model.FaceBookAuthResponse;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.User;
import facebook4j.auth.AccessToken;
import facebook4j.conf.ConfigurationBuilder;

/**
 * 
 * Authenticates the Facebook token sent by client
 * 
 * @author Swamy
 *
 */
@Component
public class FacebookAuthentication {

	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookAuthentication.class);

	private static final int EXPIRED_TOKEN_ERROR_CODE = 100;

	@Autowired
	private Environment env;

	public FaceBookAuthResponse authenticateToken(String token) {
		FaceBookAuthResponse authResponse = new FaceBookAuthResponse();

		LOGGER.info("Authenticating for accesstoken : {}", token);

		ConfigurationBuilder cb = new ConfigurationBuilder().setOAuthAppId(env.getProperty("oauth.appId"))
				.setOAuthAppSecret(env.getProperty("oauth.appSecret"));
		Facebook faceBook = new FacebookFactory(cb.build()).getInstance();

		AccessToken accessToken = null;
		User user = null;
		try {
			accessToken = faceBook.getOAuthAccessTokenInfo(token);
			if (null != accessToken) {
				user = faceBook.getMe();
			}
		} catch (FacebookException e) {
			LOGGER.error("Error in authenticating user in facebook", e);
			if (e.getErrorCode() == 109) {
				authResponse.setExpired(true);
			}
			authResponse.setStatus(FaceBookAuthResponse.FAILED);
			return authResponse;
		}

		if (null != user) {
			authResponse.setName(user.getName());
			authResponse.setUserId(user.getId());
			authResponse.setEmail(user.getEmail());
		} else {
			LOGGER.error("Error in getting userInfo from facebook");
			authResponse.setStatus(FaceBookAuthResponse.FAILED);
		}

		return authResponse;
	}
}
