package com.onesandzeros.service;

import com.onesandzeros.model.persistance.RegistrationTokenEntity;

public interface RegistrationTokenService {

	RegistrationTokenEntity addHashTokenForActivation(Long userId);

	boolean validateToken(Long userId, String token);

}
