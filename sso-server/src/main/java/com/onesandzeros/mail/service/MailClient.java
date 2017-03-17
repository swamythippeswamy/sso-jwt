package com.onesandzeros.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.onesandzeros.exceptions.ServiceException;

@Service
public class MailClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private Environment env;

	public void prepareAndSend(String toMailId, String message) throws ServiceException {

		SimpleMailMessage simpleMailMsg = new SimpleMailMessage();
		simpleMailMsg.setText(message);
		simpleMailMsg.setFrom(env.getProperty("spring.mail.username"));
		simpleMailMsg.setTo(toMailId);

		try {
			mailSender.send(new SimpleMailMessage(simpleMailMsg));

		} catch (Exception e) {
			LOGGER.error("Error in sending mail to mailid : {}", toMailId);
			throw new ServiceException("Error in sending mail to mailid " + toMailId);
		}

	}

}
