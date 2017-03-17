package com.onesandzeros.service.impl;

import java.io.StringWriter;
import java.sql.Timestamp;

import javax.mail.MessagingException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.onesandzeros.dao.RegistrationVerificationDao;
import com.onesandzeros.dao.UserAccountDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.mail.service.MailClient;
import com.onesandzeros.mail.service.MailUtil;
import com.onesandzeros.model.persistance.RegistrationTokenEntity;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.LoginServiceResponse;
import com.onesandzeros.model.register.Status;
import com.onesandzeros.models.AccountType;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.util.CommonUtil;

@Component
public class EmailLoginService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailLoginService.class);

	@Autowired
	private UserAccountDao userActDao;

	@Autowired
	private RegistrationVerificationDao regVerDao;

	@Autowired
	private MailClient mailClient;

	@Autowired
	private MailUtil mailUtil;

	@Autowired
	private VelocityEngine vEngine;

	public LoginServiceResponse<UserInfo> emailLogin(LoginPayload loginPayload) throws ServiceException {
		UserInfo userInfo = new UserInfo();

		validate(loginPayload);
		LoginServiceResponse<UserInfo> loginResp = new LoginServiceResponse<>();
		UserAccountEntity userAccInfo;

		// TODO: Validate password
		try {
			userAccInfo = userActDao.findByEmail(loginPayload.getEmail());
		} catch (DaoException e) {
			loginResp.setStatus(
					new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching login details from db"));
			return loginResp;
		}

		if (null == userAccInfo) {
			loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Login id is not regiesterd"));
			return loginResp;
		}
		userInfo = new UserInfo(userAccInfo.getName(), userAccInfo.getEmail());
		loginResp.setData(userInfo);

		return loginResp;

	}

	@Transactional
	public LoginServiceResponse<UserInfo> signUp(LoginPayload loginPayload) throws ServiceException {
		LoginServiceResponse<UserInfo> resp = new LoginServiceResponse<>();
		validate(loginPayload);
		try {
			// TODO: Use Velocity template
			UserAccountEntity entity = addNewUser(loginPayload);
			if (null != entity) {
				Long userId = entity.getId();
				RegistrationTokenEntity tokenEnt = addHashTokenForActivation(userId);
				String mailBody = getMailBody(entity.getName(), tokenEnt);
				mailClient.sendMail(loginPayload.getEmail(), mailBody);
			}

		} catch (MessagingException e) {
			LOGGER.error("Error in sending mail to the entered emailId", e);
			throw new ServiceException("Error in sending mail to the entered emailId");
		}
		resp.setStatus(
				new Status(HttpStatus.OK.value(), "A mail is sent to registered email id for account activation"));

		return resp;
	}

	private void validate(LoginPayload userDetails) throws ServiceException {
		if (!CommonUtil.validateEmail(userDetails.getEmail())) {
			throw new ServiceException("Invalid EmailId");
		}
		if (!CommonUtil.validatePassword(userDetails.getPassword())) {
			throw new ServiceException("Invalid Password");
		}
	}

	private UserAccountEntity addNewUser(LoginPayload loginPayload) {
		UserAccountEntity accountEntity = new UserAccountEntity();
		accountEntity.setAccountType(AccountType.EMAIL);
		accountEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
		accountEntity.setEmail(loginPayload.getEmail());
		accountEntity.setPassword(loginPayload.getPassword());

		accountEntity.setName(loginPayload.getName());
		accountEntity.setActive(false);
		accountEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));

		userActDao.save(accountEntity);
		return accountEntity;
	}

	private String generateRandomStr() {
		return RandomStringUtils.randomAlphanumeric(30);
	}

	private RegistrationTokenEntity addHashTokenForActivation(Long userId) {
		RegistrationTokenEntity tokenEnt = new RegistrationTokenEntity();
		String hashToken = generateRandomStr();
		tokenEnt.setUserId(userId);
		tokenEnt.setHash(hashToken);
		tokenEnt.setCreateTime(new Timestamp(System.currentTimeMillis()));
		regVerDao.save(tokenEnt);
		return tokenEnt;
	}

	private String getMailBody(String name, RegistrationTokenEntity tokenEnt) {
		// String emailTemplateStr = mailUtil.getEmailActivationTemplate();
		Template template = vEngine.getTemplate("./templates/mail_template.html", "UTF-8");

		VelocityContext vContext = new VelocityContext();
		vContext.put("name", name);
		vContext.put("email", tokenEnt.getId());
		vContext.put("randomStr", tokenEnt.getHash());

		StringWriter stringWriter = new StringWriter();

		template.merge(vContext, stringWriter);

		String mailText = stringWriter.toString();

		LOGGER.info("Generated velocity template : {}", mailText);
		return mailText;
	}
}
