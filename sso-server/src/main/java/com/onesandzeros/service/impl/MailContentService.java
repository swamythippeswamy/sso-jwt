package com.onesandzeros.service.impl;

import static com.onesandzeros.AppConstants.ACCOUNT_ACTIVATION_URL;
import static com.onesandzeros.AppConstants.LOGIN_PAGE_URL;
import static com.onesandzeros.AppConstants.SSO_AUTH_SERVER_BASE_URL;

import java.io.StringWriter;
import java.text.MessageFormat;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.onesandzeros.model.persistance.RegistrationTokenEntity;
import com.onesandzeros.model.persistance.UserAccountEntity;

@Component
public class MailContentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailContentService.class);

	@Autowired
	private VelocityEngine vEngine;

	@Autowired
	private Environment env;

	public String generateAcntVerficationMailBody(UserAccountEntity userAcctEnt, RegistrationTokenEntity tokenEnt) {
		Template template = vEngine.getTemplate("./templates/verfication_mail.html", "UTF-8");

		String url = MessageFormat.format(
				env.getProperty(SSO_AUTH_SERVER_BASE_URL) + env.getProperty(ACCOUNT_ACTIVATION_URL),
				userAcctEnt.getEmail(), tokenEnt.getHash());

		VelocityContext vContext = new VelocityContext();
		vContext.put("name", userAcctEnt.getName());
		vContext.put("url", url);

		StringWriter stringWriter = new StringWriter();

		template.merge(vContext, stringWriter);

		String mailText = stringWriter.toString();

		LOGGER.info("Generated velocity template for account activation: {}", mailText);
		return mailText;
	}

	public String generateAcntSuccessMailBody(UserAccountEntity userAcctEnt) {
		Template template = vEngine.getTemplate("./templates/verification_success.html", "UTF-8");

		String url = env.getProperty(LOGIN_PAGE_URL);

		VelocityContext vContext = new VelocityContext();
		vContext.put("name", userAcctEnt.getName());
		vContext.put("url", url);

		StringWriter stringWriter = new StringWriter();

		template.merge(vContext, stringWriter);

		String mailText = stringWriter.toString();

		LOGGER.info("Generated velocity template for successful account verfication: {}", mailText);
		return mailText;
	}
}
