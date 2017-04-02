package com.onesandzeros.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.onesandzeros.aspects.JwtAuthentication;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.jwt.token.service.JwtTokenKeyServiceImpl;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.ResetPassword;
import com.onesandzeros.models.BaseResponse;
import com.onesandzeros.service.SSOAuthService;

/**
 * 
 * @author swamy
 *
 */
@RestController
public class SSOAuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSOAuthController.class);

	@Autowired
	private SSOAuthService ssoService;

	@RequestMapping("/login")
	public @ResponseBody BaseResponse<String> login(@RequestBody LoginPayload loginPayload, HttpServletRequest request,
			HttpServletResponse response) throws ServiceException {
		LOGGER.info("login api : {}", loginPayload);
		BaseResponse<String> baseResp = ssoService.login(request, response, loginPayload);
		return baseResp;
	}

	@RequestMapping("/signup")
	public @ResponseBody BaseResponse<String> signup(@RequestBody LoginPayload loginPayload, HttpServletRequest request,
			HttpServletResponse response) throws ServiceException {
		LOGGER.info("signup api : {}", loginPayload);
		return ssoService.signup(request, response, loginPayload);
	}

	// TODO: (Implement logout functionality )
	// For JWT: Clear the token at client side, even if not cleared token will
	// be invalidated after sometime
	@RequestMapping("/logout")
	public @ResponseBody BaseResponse<String> logout(@RequestBody LoginPayload loginPayload, HttpServletRequest request,
			HttpServletResponse response) throws ServiceException {
		LOGGER.info("signup api : {}", loginPayload);
		return ssoService.logout(request, response);
	}

	@RequestMapping("/verifyAccount")
	public @ResponseBody String verifyAccount(@RequestParam(name = "email", required = true) String emailId,
			@RequestParam(name = "token", required = true) String token, HttpServletRequest request,
			HttpServletResponse response) throws ServiceException {

		LOGGER.info("Activation api, params - email : {}, toekn : {}", emailId, token);

		return ssoService.activateAccount(request, response, emailId, token);
	}

	// API for reseting the existing password
	@RequestMapping("/resetPassword")
	@JwtAuthentication
	public @ResponseBody BaseResponse<String> verifyAccount(@RequestBody ResetPassword resetPwd,
			HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		LOGGER.info("Activation api, params - email : {}, toekn : {}", resetPwd);
		return ssoService.resetPassword(request, response, resetPwd);
	}

	// Redirect url configured in Facebook app settings
	@RequestMapping("/facebook/redirect")
	public @ResponseBody BaseResponse<String> verifyAccount(@RequestParam(name = "code", required = false) String code,
			HttpServletRequest request, HttpServletResponse response) throws ServiceException {

		LOGGER.info("Facebook Redirection {}", code);

		return new BaseResponse<>();
	}

	// Testing api
	@RequestMapping("/accountInfo")
	@JwtAuthentication
	public @ResponseBody BaseResponse<UserAccountEntity> getAccountInfo() throws ServiceException {
		LOGGER.info("Get AccountInfo api");
		UserAccountEntity userAccntInfo = ssoService.getAccountInfo();
		LOGGER.info("return data : {}", userAccntInfo);
		return new BaseResponse<UserAccountEntity>(HttpStatus.OK.value(), userAccntInfo);
	}

}
