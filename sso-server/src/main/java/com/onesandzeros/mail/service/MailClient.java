package com.onesandzeros.mail.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);

	@Autowired
	private JavaMailSender javaMailSender;

	public void prepareAndSend(String toMailId, String message) {
		MimeMessage mimeMsg = javaMailSender.createMimeMessage();

		MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMsg);
		try {
			msgHelper.setTo(toMailId);
			msgHelper.setFrom("swamypes2010@gmail.com");
			msgHelper.setSubject("Test mail");
			msgHelper.setText(message);
		} catch (MessagingException e1) {
			LOGGER.error("Error in while creating message");
		}

		try {
			javaMailSender.send(mimeMsg);
		} catch (MailException e) {
			LOGGER.error("Error in sending message");
		}
	}

}
