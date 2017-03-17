package com.onesandzeros.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.onesandzeros.model.persistance.RegistrationTokenEntity;

@Repository
public interface RegistrationVerificationDao extends CrudRepository<RegistrationTokenEntity, Long> {
	RegistrationTokenEntity findByUserIdAndHash(Long userId, String hash);
}
