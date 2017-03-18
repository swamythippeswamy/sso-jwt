package com.onesandzeros.aspects;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.onesandzeros.jwt.token.service.JwtTokenParser;
import com.onesandzeros.models.BaseResponse;
import com.onesandzeros.models.JwtTokenParserResponse;
import com.onesandzeros.models.SessionData;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.util.EncryptDecryptUtil;
import com.onesandzeros.util.JsonUtil;

import io.jsonwebtoken.Claims;

/**
 * Aspect implementation for annotation {@link JwtAuthentication}. This will be
 * used by both sso-client and sso-server
 * 
 * @author swamy
 *
 */
@Aspect
@Component
public class JwtAuthAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthAspect.class);

	@Autowired
	Environment env;

	@Autowired
	@Qualifier("jwtTokenParser")
	private JwtTokenParser tokenParser;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	@Around("@annotation(jwtAuth)")
	public Object jwtAuthCheck(ProceedingJoinPoint pjp, JwtAuthentication jwtAuth) throws Throwable {

		Object obj = null;

		if (!LOGGER.isDebugEnabled()) {
			logReqHeaders(request);
		}

		String jwtAuthHeaderVal = getJwtAuthHeader(request);

		if (null == jwtAuthHeaderVal) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new BaseResponse<String>(HttpStatus.UNAUTHORIZED.value(), "Auth header is missing");
		}

		JwtTokenParserResponse tokenParResp = null;
		Claims tokenClaims = null;

		try {
			tokenParResp = tokenParser.getClaimsFromJwtToken(jwtAuthHeaderVal);

			if (tokenParResp.getStatus() == JwtTokenParserResponse.FAILURE) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				return new BaseResponse<String>(HttpStatus.UNAUTHORIZED.value(), tokenParResp.getMessage());
			}

			tokenClaims = tokenParResp.getClaims();
			LOGGER.info("JWT token decoded userInfo : {}", tokenClaims.getSubject());
			if (null == tokenClaims.getSubject()) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				return new BaseResponse<String>(HttpStatus.UNAUTHORIZED.value(),
						"Invalid jwt token, User Information not present in token");
			}

		} catch (Exception e) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return new BaseResponse<String>(HttpStatus.UNAUTHORIZED.value(),
					"Invalid jwt token, Authentication failure");
		}

		// Session information will be added to ThreadLocal only of token is
		// verified
		LOGGER.info("Before initializing sessin data : {}", SessionData.getUserInfo());
		String encryptedUserData = tokenClaims.getSubject();
		String decryptedUserData = EncryptDecryptUtil.decrypt(encryptedUserData);
		UserInfo userInfo = JsonUtil.deserialize(decryptedUserData, UserInfo.class);

		SessionData.setUserInfo(userInfo);

		LOGGER.info("After initializing sessin data : {}", SessionData.getUserInfo());
		obj = pjp.proceed();

		SessionData.clear();

		LOGGER.info("After clearing sessin data : {}", SessionData.getUserInfo());
		return obj;
	}

	private String getJwtAuthHeader(HttpServletRequest request) {

		String jwtHeaderName = env.getProperty("jwt.token.header.name", "Authentication");
		String jwtAuthHeaderVal = request.getHeader(jwtHeaderName);
		LOGGER.info("Auth Header in the request : {}", jwtAuthHeaderVal);
		return jwtAuthHeaderVal;
	}

	private void logReqHeaders(HttpServletRequest request) {
		Enumeration<String> reqHeadres = request.getHeaderNames();
		while (reqHeadres.hasMoreElements()) {
			String headerKey = reqHeadres.nextElement();
			String headerVal = request.getHeader(headerKey);
			LOGGER.info("Header- Key : {}, Value : {}", headerKey, headerVal);
		}
	}
}
