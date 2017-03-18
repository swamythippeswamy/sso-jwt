package com.onesandzeros.social.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.onesandzeros.model.social.FaceBookAuthResponse;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.User;
import facebook4j.auth.AccessToken;

/**
 * 
 * Authenticates the Facebook token sent by client by contacting the facebook
 * rest api's
 * 
 * @author Swamy
 *
 */
@Component
public class FacebookAuthentication {

	private static final Logger LOGGER = LoggerFactory.getLogger(FacebookAuthentication.class);

	private static final int INVALID_TOKEN_ERROR_CODE = 190;
	private static final int EXPIRED_TOKEN_ERROR_CODE = 100;

	public FaceBookAuthResponse authenticateToken(String token) {
		FaceBookAuthResponse authResponse = new FaceBookAuthResponse();

		LOGGER.info("Authenticating for accesstoken : {}", token);

		Facebook faceBook = new FacebookFactory().getInstance(new AccessToken(token, null));
		AccessToken accessToken = null;
		User user = null;
		try {
			accessToken = faceBook.getOAuthAccessTokenInfo(token);
			if (null != accessToken) {
				user = faceBook.getMe();
			}
		} catch (FacebookException e) {
			LOGGER.error("Error in authenticating user in facebook", e);
			if (e.getErrorCode() == EXPIRED_TOKEN_ERROR_CODE) {
				authResponse.setExpired(true);
			}
			if (e.getErrorCode() == INVALID_TOKEN_ERROR_CODE) {
				authResponse.setInvalid(true);
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
