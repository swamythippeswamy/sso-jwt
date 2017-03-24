package com.onesandzeros.service.impl;

import static com.onesandzeros.AppConstants.REGISTRATION_TOKEN_EXP_TIME;

import java.sql.Timestamp;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onesandzeros.dao.RegistrationVerificationDao;
import com.onesandzeros.model.persistance.RegistrationTokenEntity;
import com.onesandzeros.service.RegistrationTokenService;

@Service
public class RegistrationTokenServiceImpl implements RegistrationTokenService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationTokenServiceImpl.class);

	@Autowired
	private RegistrationVerificationDao regVerDao;

	@Autowired
	private Environment env;

	@Override
	public RegistrationTokenEntity addHashTokenForActivation(Long userId) {
		RegistrationTokenEntity tokenEnt = new RegistrationTokenEntity();
		String hashToken = generateRandomStr();
		tokenEnt.setUserId(userId);
		tokenEnt.setHash(hashToken);
		tokenEnt.setCreateTime(new Timestamp(System.currentTimeMillis()));
		regVerDao.save(tokenEnt);
		return tokenEnt;
	}

	@Override
	public boolean validateToken(String emailId, String token) {

		LOGGER.debug("Validating token : {}, for  userId : {}", token, emailId);
		boolean valid = false;
		RegistrationTokenEntity regTokEnt = regVerDao.findByEmailAndHash(emailId, token);

		Long tokenExpirationTime = Long.parseLong(env.getProperty(REGISTRATION_TOKEN_EXP_TIME, "3600000"));
		long currentTime = System.currentTimeMillis();
		if (null != regTokEnt) {
			long timeDiff = currentTime - regTokEnt.getCreateTime().getTime();
			LOGGER.info("Time diff : {}", timeDiff);
			if (timeDiff > 0 && timeDiff < tokenExpirationTime) {
				valid = true;
			}
		}
		return valid;
	}

	private String generateRandomStr() {
		return RandomStringUtils.randomAlphanumeric(30);
	}

	@Override
	@Transactional
	public void deleteByToken(String token) {
		regVerDao.deleteByHash(token);
	}

}
