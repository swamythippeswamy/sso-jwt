package com.onesandzeros.service;

import com.onesandzeros.model.persistance.RegistrationTokenEntity;

public interface RegistrationTokenService {

	RegistrationTokenEntity addHashTokenForActivation(Long userId, String email);

	boolean validateToken(String emailId, String token);

	void deleteByToken(String token);

}
