package com.onesandzeros.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onesandzeros.aspects.JwtAuthentication;
import com.onesandzeros.models.BaseResponse;
import com.onesandzeros.models.SessionData;
import com.onesandzeros.models.UserInfo;

@RestController
public class TestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	@JwtAuthentication
	@RequestMapping("/verifyToken")
	public BaseResponse<UserInfo> testAnnot() {
		return new BaseResponse<UserInfo>(HttpStatus.OK.value(), SessionData.getUserInfo());
	}

}
