package com.onesandzeros.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.onesandzeros.models.AccountType;
import com.onesandzeros.service.LoginService;

@Component
public class LoginServiceFactory {

	@Autowired
	EmailLoginService emailLoginService;

	@Autowired
	FacebookLoginService facebookLoginService;

	public LoginService getLoginService(AccountType accountType) {
		LoginService loginService = null;
		switch (accountType) {
		case EMAIL:
			loginService = emailLoginService;
			break;
		case FACEBOOK:
			loginService = facebookLoginService;
			break;
		case GOOGLE:
			break;
		case PHONE:
			break;
		}

		return loginService;
	}

}
