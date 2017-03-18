package com.onesandzeros.service.impl;

import java.sql.Timestamp;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onesandzeros.dao.RegistrationVerificationDao;
import com.onesandzeros.model.persistance.RegistrationTokenEntity;
import com.onesandzeros.service.RegistrationTokenService;

@Service
public class RegistrationTokenServiceImpl implements RegistrationTokenService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationTokenServiceImpl.class);

	/**
	 * In milliseconds
	 */
	private static final Long tokenExpirationTime = 60 * 60 * 1000l;

	@Autowired
	private RegistrationVerificationDao regVerDao;

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
	public boolean validateToken(Long userId, String token) {

		LOGGER.debug("Validating token : {}, for  userId : {}", token, userId);
		boolean valid = false;
		RegistrationTokenEntity regTokEnt = regVerDao.findByUserIdAndHash(userId, token);

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

}
