package com.onesandzeros.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.onesandzeros.aspects.JwtAuthentication;
import com.onesandzeros.jwttoken.service.JwtSecurityService;
import com.onesandzeros.models.PublicKeyData;
import com.onesandzeros.models.ResponseModel;

@RestController
public class TestController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

	@Autowired
	private JwtSecurityService securityService;

	@RequestMapping(value = "/addPublicKey", method = RequestMethod.POST)
	public boolean addPublicKeys(@RequestBody PublicKeyData publicKeyData) {
		LOGGER.info("addPublicKey Request : {}", publicKeyData);
		return securityService.addPublicKey(publicKeyData);
	}

	@JwtAuthentication
	@RequestMapping("/verifyToken")
	public ResponseModel<String> testAnnot() {
		return new ResponseModel<String>(HttpStatus.OK.value(), "test success");
	}

}