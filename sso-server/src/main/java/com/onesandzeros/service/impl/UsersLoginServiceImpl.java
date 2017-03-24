package com.onesandzeros.service.impl;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onesandzeros.dao.UsersLoginLogDao;
import com.onesandzeros.model.persistance.LoginStatus;
import com.onesandzeros.model.persistance.UsersLoginLogEntity;
import com.onesandzeros.service.UsersLoginLogService;

@Service
public class UsersLoginServiceImpl implements UsersLoginLogService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UsersLoginServiceImpl.class);

	@Autowired
	private UsersLoginLogDao usersLoginLogDao;

	@Override
	public void addLog(Long userId, LoginStatus loginStatus, String ipAddress) {
		UsersLoginLogEntity entity = new UsersLoginLogEntity();
		entity.setUserId(userId);
		entity.setLoginStatus(loginStatus);
		entity.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
		entity.setIpAddress(ipAddress);

		usersLoginLogDao.save(entity);
		LOGGER.info("Logged entity details: {}", entity);
	}
}
