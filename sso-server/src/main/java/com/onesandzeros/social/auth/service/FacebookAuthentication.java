package com.onesandzeros.social.auth.service;

import static com.onesandzeros.model.social.FaceBookAuthResponse.Status.FAILED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.onesandzeros.model.social.FaceBookAuthResponse;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;

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

	@Autowired
	private Environment env;

	public FaceBookAuthResponse authenticateToken(String token) {
		FaceBookAuthResponse authResponse = new FaceBookAuthResponse();

		LOGGER.info("Authenticating for accesstoken : {}", token);

		String outhPermissions = env.getProperty("oauth.permissions");
		String appSecret = env.getProperty("oauth.appSecret");

		
		FacebookClient fbClient = new DefaultFacebookClient(token, appSecret, Version.VERSION_2_8);
		User user = null;

		try {
			user = fbClient.fetchObject("me", User.class, Parameter.with("fields", outhPermissions));

			LOGGER.debug("Rest FB userId: {}", user.getId());
			LOGGER.debug("Rest FB user Name: {}", user.getName());
			LOGGER.debug("Rest FB Email id: {}", user.getEmail());

		} catch (FacebookOAuthException e) {
			LOGGER.error("Error in authenticating user in facebook", e);
			if (e.getErrorCode() == EXPIRED_TOKEN_ERROR_CODE) {
				authResponse.setExpired(true);
			}
			if (e.getErrorCode() == INVALID_TOKEN_ERROR_CODE) {
				authResponse.setInvalid(true);
			}
		} catch (FacebookNetworkException e) {
			LOGGER.error("Network failure in connecting to facebook", e);
			authResponse.setStatus(FAILED);
			return authResponse;
		} catch (com.restfb.exception.FacebookException e) {
			LOGGER.error("Error in getting user datas using access token", e);
			authResponse.setStatus(FAILED);
			return authResponse;
		}

		if (null != user) {
			authResponse.setName(user.getName());
			authResponse.setUserId(user.getId());
			authResponse.setEmail(user.getEmail());
		} else {
			LOGGER.error("Error in getting userInfo from facebook");
			authResponse.setStatus(FAILED);
		}

		return authResponse;
	}
}
