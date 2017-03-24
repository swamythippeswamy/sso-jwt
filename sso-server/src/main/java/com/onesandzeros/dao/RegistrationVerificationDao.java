package com.onesandzeros.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.onesandzeros.model.persistance.RegistrationTokenEntity;

/**
 * 
 * Repository for {@link RegistrationTokenEntity}
 * 
 * @author swamy
 *
 */
@Repository
public interface RegistrationVerificationDao extends CrudRepository<RegistrationTokenEntity, Long> {
	RegistrationTokenEntity findByEmailAndHash(String email, String hash);

	Long deleteByHash(String token);
}
