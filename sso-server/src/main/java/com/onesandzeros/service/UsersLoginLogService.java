package com.onesandzeros.service;

import com.onesandzeros.model.persistance.LoginStatus;

public interface UsersLoginLogService {
	void addLog(Long userId, LoginStatus loginStatus, String ipAddress);
}
