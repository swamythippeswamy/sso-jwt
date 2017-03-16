package com.onesandzeros.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.onesandzeros.aspects.JwtAuthentication;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.jwttoken.service.JwtSigningKeyService;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.models.PublicKeyData;
import com.onesandzeros.models.BaseResponse;
import com.onesandzeros.service.SSOAuthService;

@RestController
public class SSOAuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSOAuthController.class);

	@Autowired
	SSOAuthService ssoService;

	@Autowired
	JwtSigningKeyService jwtSigningKeyService;

	@RequestMapping("/")
	public @ResponseBody String home() {
		return "Hello World!";
	}

	@RequestMapping("/login")
	public @ResponseBody BaseResponse<String> login(@RequestBody LoginPayload loginPayload, HttpServletRequest request,
			HttpServletResponse response) throws ServiceException {
		LOGGER.info("login api");
		ssoService.login(request, response, loginPayload);
		return new BaseResponse<String>(HttpStatus.OK.value(), "Login successful");
	}

	@RequestMapping("/register")
	public String signup() {
		LOGGER.info("signup api");
		ssoService.signup();
		return "Hello World!";
	}

	@RequestMapping("/accountInfo")
	@JwtAuthentication
	public @ResponseBody BaseResponse<UserAccountEntity> getAccountInfo() throws ServiceException {
		LOGGER.info("Get AccountInfo api");
		UserAccountEntity userAccntInfo = ssoService.getAccountInfo();
		LOGGER.info("return data : {}", userAccntInfo);
		return new BaseResponse<UserAccountEntity>(HttpStatus.OK.value(), userAccntInfo);
	}

	@RequestMapping("/getPublicKey")
	public @ResponseBody PublicKeyData getPublicKey() {
		LOGGER.info("Get Public key api");
		PublicKeyData data = jwtSigningKeyService.getPublicKeyData();
		return data;
	}
}
