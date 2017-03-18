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
import com.onesandzeros.jwt.token.service.JwtTokenKeyService;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.models.BaseResponse;
import com.onesandzeros.models.PublicKeyData;
import com.onesandzeros.service.SSOAuthService;

@RestController
public class SSOAuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSOAuthController.class);

	@Autowired
	SSOAuthService ssoService;

	@Autowired
	JwtTokenKeyService jwtSigningKeyService;

	@RequestMapping("/")
	public @ResponseBody String home() {
		return "Hello World!";
	}

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

	@RequestMapping("/logout")
	public @ResponseBody BaseResponse<String> logout(@RequestBody LoginPayload loginPayload, HttpServletRequest request,
			HttpServletResponse response) throws ServiceException {
		LOGGER.info("signup api : {}", loginPayload);
		return ssoService.signup(request, response, loginPayload);
	}

	@RequestMapping("/verifyAccount")
	public @ResponseBody String verifyAccount(@RequestParam(name = "email", required = true) String emailId,
			@RequestParam(name = "token", required = true) String token, HttpServletRequest request,
			HttpServletResponse response) throws ServiceException {

		LOGGER.info("Activation api, params - email : {}, toekn : {}", emailId, token);

		return ssoService.activateAccount(request, response, emailId, token);
	}

	/**
	 * API used by micro services to fetch the public key
	 * 
	 * @return {@link PublicKeyData}
	 */
	@RequestMapping("/getPublicKey")
	public @ResponseBody PublicKeyData getPublicKey() {
		LOGGER.info("Get Public key api");
		PublicKeyData data = jwtSigningKeyService.getPublicKeyData();
		return data;
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
