package com.onesandzeros.service.impl;

import java.sql.Timestamp;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onesandzeros.dao.RegistrationVerificationDao;
import com.onesandzeros.model.persistance.RegistrationTokenEntity;
import com.onesandzeros.service.RegistrationTokenService;

@Service
public class RegistrationTokenServiceImpl implements RegistrationTokenService {
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
		boolean valid = false;
		RegistrationTokenEntity regTokEnt = regVerDao.findByUserIdAndHash(userId, token);

		long currentTime = System.currentTimeMillis();
		if (null != regTokEnt) {
			if (regTokEnt.getCreateTime().getTime() - currentTime > 0) {
				valid = true;
			}
		}
		return valid;
	}

	private String generateRandomStr() {
		return RandomStringUtils.randomAlphanumeric(30);
	}

}
