package com.onesandzeros.mail.service;

import static com.onesandzeros.AppConstants.MAIL_PASSWORD;
import static com.onesandzeros.AppConstants.MAIL_SMTP_AUTH;
import static com.onesandzeros.AppConstants.MAIL_SMTP_HOST;
import static com.onesandzeros.AppConstants.MAIL_SMTP_PORT;
import static com.onesandzeros.AppConstants.MAIL_SMTP_STARTTLS_ENABLE;
import static com.onesandzeros.AppConstants.MAIL_USERNAME;
import static com.onesandzeros.AppConstants.MAIL_ACTIVATION_TEMPLATE;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

	@Autowired
	private Environment env;

	public Session getSession() {

		Session session;
		Properties props = new Properties();
		props.put(MAIL_SMTP_HOST, env.getProperty(MAIL_SMTP_HOST));
		props.put(MAIL_SMTP_PORT, env.getProperty(MAIL_SMTP_PORT));
		props.put(MAIL_SMTP_AUTH, env.getProperty(MAIL_SMTP_AUTH));
		props.put(MAIL_SMTP_STARTTLS_ENABLE, env.getProperty(MAIL_SMTP_STARTTLS_ENABLE));

		Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(env.getProperty(MAIL_USERNAME), env.getProperty(MAIL_PASSWORD));
			}
		};

		session = Session.getDefaultInstance(props, authenticator);

		return session;
	}

}
