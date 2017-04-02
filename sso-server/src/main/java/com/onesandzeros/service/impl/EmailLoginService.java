package com.onesandzeros.service.impl;

import static com.onesandzeros.AppConstants.ACCOUNT_VERIFICATION_SUCCESS_RESP_MSG;
import static com.onesandzeros.AppConstants.MAIL_SUBJECT;
import static com.onesandzeros.AppConstants.PWD_ENCRYPT_DECRYPT_KEY;
import static com.onesandzeros.models.AccountStatus.ACTIVE;
import static com.onesandzeros.models.AccountStatus.TOBEVERIFIED;

import java.security.Key;
import java.sql.Timestamp;

import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.onesandzeros.dao.UserAccountDao;
import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.exceptions.ServiceException;
import com.onesandzeros.mail.service.MailClient;
import com.onesandzeros.model.persistance.LoginStatus;
import com.onesandzeros.model.persistance.RegistrationTokenEntity;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.model.register.ResetPassword;
import com.onesandzeros.model.register.ServiceResponse;
import com.onesandzeros.model.register.Status;
import com.onesandzeros.models.AccountType;
import com.onesandzeros.models.UserInfo;
import com.onesandzeros.service.LoginService;
import com.onesandzeros.service.RegistrationTokenService;
import com.onesandzeros.service.UsersLoginLogService;
import com.onesandzeros.util.CommonUtil;
import com.onesandzeros.util.EncryptDecryptData;

@Component
public class EmailLoginService implements LoginService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailLoginService.class);

	@Autowired
	private UserAccountDao userActDao;

	@Autowired
	private MailClient mailClient;

	@Autowired
	EncryptDecryptData encDecData;

	@Autowired
	private Environment env;

	@Autowired
	private RegistrationTokenService regService;

	@Autowired
	private UsersLoginLogService loginLogService;

	@Autowired
	private MailContentService mailService;

	@Override
	public ServiceResponse<UserInfo> login(LoginPayload loginPayload, HttpServletRequest request) throws ServiceException {
		UserInfo userInfo = new UserInfo();

		validate(loginPayload);
		ServiceResponse<UserInfo> loginResp = new ServiceResponse<>();
		UserAccountEntity userAccEnt;

		try {
			userAccEnt = userActDao.findByEmailAndAccountType(loginPayload.getEmail(), AccountType.EMAIL);
		} catch (DaoException e) {
			loginResp.setStatus(
					new Status(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error fetching login details from db"));
			return loginResp;
		}

		if (null == userAccEnt) {
			loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(),
					"This email is not registered, please register to login"));
			return loginResp;
		}
		if (!(userAccEnt.getStatus() == ACTIVE)) {
			loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Login id is not activated"));
			loginLogService.addLog(userAccEnt.getUserId(), LoginStatus.FAILURE, null);
		} else {

			if (!verifyPassword(loginPayload.getPassword(), userAccEnt)) {
				loginResp.setStatus(new Status(HttpStatus.UNAUTHORIZED.value(), "Login id/Password is incorrect"));
				loginLogService.addLog(userAccEnt.getUserId(), LoginStatus.FAILURE, null);
				return loginResp;
			}
			userInfo = new UserInfo(userAccEnt.getName(), userAccEnt.getEmail());
			loginResp.setStatus(new Status(HttpStatus.OK.value(), "Login successful"));
			loginResp.setData(userInfo);
			loginLogService.addLog(userAccEnt.getUserId(), LoginStatus.SUCCESS, request.getRemoteAddr());
		}

		return loginResp;

	}

	@Transactional(rollbackFor = ServiceException.class)
	@Async("mailTaskExecuter")
	public void addUserAndSendVerificationMail(LoginPayload loginPayload) throws ServiceException {
		try {

			UserAccountEntity userAccEntity = addNewUser(loginPayload);
			if (null != userAccEntity) {
				Long userId = userAccEntity.getUserId();
				RegistrationTokenEntity tokenEnt = regService.addHashTokenForActivation(userId,
						userAccEntity.getEmail());
				String mailBody = mailService.generateAcntVerficationMailBody(userAccEntity, tokenEnt);
				String subject = env.getProperty(MAIL_SUBJECT);
				mailClient.sendMail(loginPayload.getEmail(), mailBody, subject);
			}

		} catch (MessagingException e) {
			LOGGER.error("Error in sending mail to the entered emailId", e);
			throw new ServiceException("Error in sending mail to the entered emailId");
		}
	}

	@Transactional
	public String activateEmailAccount(String email, String token) throws ServiceException {
		String message = null;
		try {
			boolean validToken = regService.validateToken(email, token);

			if (!validToken) {
				message = "Invalid token/Activation link might be expired";
				return message;
			}

			// If registration token is valid, then update the status in
			// UserAccountEntity table
			int updateCount = userActDao.updateStatusByEmail(ACTIVE, email);
			LOGGER.info("Updated Count : {}", updateCount);

			message = env.getProperty(ACCOUNT_VERIFICATION_SUCCESS_RESP_MSG, "Account activated successfully");

			UserAccountEntity userActEnt = userActDao.findByEmail(email);
			String verfSuccessMailBody = mailService.generateAcntSuccessMailBody(userActEnt);
			String subject = env.getProperty(MAIL_SUBJECT);
			mailClient.sendMail(email, verfSuccessMailBody, subject);
			// Deleting the token once the account is activated successfully
			regService.deleteByToken(token);

		} catch (DaoException e) {
			LOGGER.error("Error occured in querying db", e);
		} catch (MessagingException e) {
			LOGGER.error("Error in sending account activation success mail");
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

	public void validateSignUpData(LoginPayload loginPayload) throws ServiceException {
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

		String encryptedPwd = encDecData.encrypt(loginPayload.getPassword(), getKeyForPwd());
		accountEntity.setPassword(encryptedPwd);

		accountEntity.setName(loginPayload.getName());
		accountEntity.setStatus(TOBEVERIFIED);
		accountEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));

		userActDao.save(accountEntity);
		return accountEntity;
	}

	public boolean isEmailAlreadyExists(String email) {
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

	private boolean verifyPassword(String userEntPwd, UserAccountEntity userAccEnt) {

		boolean valid = false;
		String encryptUsrEntPwd = encDecData.encrypt(userEntPwd, getKeyForPwd());

		if (encryptUsrEntPwd.equals(userAccEnt.getPassword())) {
			valid = true;
		}
		return valid;
	}

	private Key getKeyForPwd() {
		String encryptionKey = env.getProperty(PWD_ENCRYPT_DECRYPT_KEY);
		Key key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
		return key;
	}

	@Transactional
	public ServiceResponse<String> resetPassword(ResetPassword resetPwd) throws ServiceException {
		ServiceResponse<String> resp = new ServiceResponse<>();
		String email = resetPwd.getEmail();
		if (!CommonUtil.validateEmail(email)) {
			throw new ServiceException("Invalid EmailId");
		}
		String oldPwd = resetPwd.getOldPassword();
		if (!CommonUtil.validatePassword(oldPwd)) {
			throw new ServiceException("Invalid Otp");
		}

		String newPwd = resetPwd.getNewPassword();
		if (!CommonUtil.validatePassword(newPwd)) {
			throw new ServiceException("Invalid New Password");
		}

		if (!resetPwd.validatePwds()) {
			throw new ServiceException("Passwords are not matching");
		}
		try {
			UserAccountEntity userAccEnt = userActDao.findByEmail(email);
			if (null == userAccEnt) {
				resp.setStatus(new Status(HttpStatus.BAD_REQUEST.value(), "EmailId not exist"));
				return resp;
			}

			boolean valid = verifyPassword(oldPwd, userAccEnt);
			if (!valid) {
				throw new ServiceException("Invalid Password");
			}
			String encNewPwd = encDecData.encrypt(newPwd, getKeyForPwd());
			int updateCount = userActDao.updatePwdByEmail(encNewPwd, email);
			LOGGER.debug("Updated row counts : {}", updateCount);

			resp.setData("Password updated Successfully");
		} catch (DaoException e) {
			LOGGER.error("Error in getting account infor from db");
		}

		return resp;
	}
}
