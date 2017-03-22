package com.onesandzeros.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.models.AccountType;

/**
 * 
 * Repository for {@link UserAccountEntity}
 * 
 * @author swamy
 *
 */
@Repository
public interface UserAccountDao extends CrudRepository<UserAccountEntity, Long> {

	UserAccountEntity findByEmail(String email) throws DaoException;

	// TODO (I could not get this. When it is find by facebook user id then why
	// is account type needed. It is implied that account type is Facebook.
	// Correct me if I am wrong.)
	UserAccountEntity findByFacebookUserIdAndAccountType(String fbUserId, AccountType accountType) throws DaoException;

}
