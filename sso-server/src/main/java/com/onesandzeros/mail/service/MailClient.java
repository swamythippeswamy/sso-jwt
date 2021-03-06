package com.onesandzeros.mail.service;

import static com.onesandzeros.AppConstants.MAIL_SUBJECT;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.onesandzeros.AppConstants;

/**
 * Module for sending a simple mail
 * 
 * @author swamy
 *
 */
@Service
public class MailClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);

	@Autowired
	private MailUtil sessionUtil;

	@Autowired
	private Environment env;

	/**
	 * Sends email to the specified emailId asynchronously
	 * 
	 * @param toMailId
	 * @param body
	 * @throws MessagingException
	 */
	public void sendMail(String toMailId, String body, String subject) throws MessagingException {
		LOGGER.info("Sending mail to emailId : {}", toMailId);
		MimeMessage mimeMsg = new MimeMessage(sessionUtil.getSession());
		setDefaultProps(mimeMsg);

		mimeMsg.setSubject(subject);
		mimeMsg.setText(body, "UTF-8");
		mimeMsg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toMailId, false));

		Transport.send(mimeMsg);
		LOGGER.info("Mail sent successfully to emailId : {}", toMailId);
	}

	private void setDefaultProps(MimeMessage msg) throws MessagingException {
		msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		msg.addHeader("format", "flowed");

		msg.setFrom(new InternetAddress(env.getProperty(AppConstants.MAIL_USERNAME)));
	}

}
