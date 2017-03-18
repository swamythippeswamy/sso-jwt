package com.onesandzeros.service.impl;

import static com.onesandzeros.AppConstants.ACCOUNT_ACTIVATION_URL;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.MessageFormat;

import javax.mail.MessagingException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
import com.onesandzeros.service.RegistrationTokenService;
import com.onesandzeros.util.CommonUtil;

@Component
public class EmailLoginService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailLoginService.class);

	@Autowired
	private UserAccountDao userActDao;

	@Autowired
	private MailClient mailClient;

	@Autowired
	private VelocityEngine vEngine;

	@Autowired
	private Environment env;

	@Autowired
	private RegistrationTokenService regService;

	public LoginServiceResponse<UserInfo> emailLogin(LoginPayload loginPayload) throws ServiceException {
		UserInfo userInfo = new UserInfo();

		validate(loginPayload);
		LoginServiceResponse<UserInfo> loginResp = new LoginServiceResponse<>();
		UserAccountEntity userAccEnt;

		// TODO: Validate password
		try {
			userAccEnt = userActDao.findByEmail(loginPayload.getEmail());
		} catch (DaoException e) {
			loginResp.setStatus(
					new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching login details from db"));
			return loginResp;
		}

		if (null == userAccEnt) {
			loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Login id is not regiesterd"));
			return loginResp;
		} else {
			if (!userAccEnt.isActive()) {
				loginResp.setStatus(
						new Status(HttpStatus.BAD_REQUEST.value(), "Login id is not activated, do signup again"));
			} else {
				userInfo = new UserInfo(userAccEnt.getName(), userAccEnt.getEmail());
				loginResp.setStatus(new Status(HttpStatus.OK.value(), "Login successful"));
				loginResp.setData(userInfo);
			}
		}

		return loginResp;

	}

	@Transactional
	public LoginServiceResponse<UserInfo> signUp(LoginPayload loginPayload) throws ServiceException {
		LoginServiceResponse<UserInfo> resp = new LoginServiceResponse<>();
		validateSignUpData(loginPayload);
		try {

			if (isEmailAlreadyExists(loginPayload.getEmail())) {
				resp.setStatus(new Status(HttpStatus.BAD_REQUEST.value(), "EmailId is already registered"));
				return resp;
			}

			UserAccountEntity userAccEntity = addNewUser(loginPayload);
			if (null != userAccEntity) {
				Long userId = userAccEntity.getId();
				RegistrationTokenEntity tokenEnt = regService.addHashTokenForActivation(userId);
				String mailBody = generateMailBody(userAccEntity, tokenEnt);
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

	@Transactional
	public String activateEmailAccount(String email, String token) throws ServiceException {
		String message = null;
		try {
			UserAccountEntity userActEnt = userActDao.findByEmail(email);
			if (null == userActEnt) {
				message = "Email Id is not registered, Invalid emailId";
				return message;
			}

			if (userActEnt.isActive()) {
				message = "Email Id is already activated";
				return message;
			}

			boolean validToken = regService.validateToken(userActEnt.getId(), token);

			if (!validToken) {
				message = "Invalid token/Activation link might be expired";
				return message;
			}

			userActEnt.setActive(true);
			userActEnt.setEmailActivatedOn(new Timestamp(System.currentTimeMillis()));

			userActDao.save(userActEnt);

			message = "Account activated successfully";
			// TODO: (Delete token once email is activated)

		} catch (DaoException e) {
			LOGGER.error("Error in fetching data from db", e);
		}

		return message;
	}

	private void validate(LoginPayload loginPayload) throws ServiceException {
		if (!CommonUtil.validateEmail(loginPayload.getEmail())) {
			throw new ServiceException("Invalid EmailId");
		}
		if (!CommonUtil.validatePassword(loginPayload.getPassword())) {
			throw new ServiceException("Invalid Password");
		}
	}

	private void validateSignUpData(LoginPayload loginPayload) throws ServiceException {
		if (StringUtils.isEmpty(loginPayload.getName())) {
			throw new ServiceException("Name should not be empty");
		}
		validate(loginPayload);
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

	private String generateMailBody(UserAccountEntity userAcctEnt, RegistrationTokenEntity tokenEnt) {
		// String emailTemplateStr = mailUtil.getEmailActivationTemplate();
		Template template = vEngine.getTemplate("./templates/mail_template.html", "UTF-8");

		String url = MessageFormat.format(env.getProperty(ACCOUNT_ACTIVATION_URL), userAcctEnt.getEmail(),
				tokenEnt.getHash());

		VelocityContext vContext = new VelocityContext();
		vContext.put("name", userAcctEnt.getName());
		vContext.put("url", url);

		StringWriter stringWriter = new StringWriter();

		template.merge(vContext, stringWriter);

		String mailText = stringWriter.toString();

		LOGGER.info("Generated velocity template : {}", mailText);
		return mailText;
	}

	private boolean isEmailAlreadyExists(String email) {
		boolean emailIdRegistered = false;
		try {
			UserAccountEntity userAccEnt = userActDao.findByEmail(email);
			if (null != userAccEnt) {
				emailIdRegistered = true;
			}
		} catch (DaoException e) {
			LOGGER.error("Error in finding account by emailId, e");
		}
		return emailIdRegistered;
	}
}
