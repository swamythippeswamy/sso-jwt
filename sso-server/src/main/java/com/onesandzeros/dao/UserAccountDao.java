package com.onesandzeros.dao;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.model.persistance.UserAccountEntity;
import com.onesandzeros.models.AccountStatus;

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

	@Modifying
	@Query("update UserAccountEntity set status = ?1, emailActivatedOn = current_timestamp() where email = ?2")
	int updateStatusByEmail(AccountStatus accountStatus, String email) throws DaoException;

	// TODO (I could not get this. When it is find by facebook user id then why
	// is account type needed. It is implied that account type is Facebook.
	// Correct me if I am wrong.)

	// Agreed, added this by thinking of using same userId column for facebook
	// and google. Still we can have seperate column for google and facebook
	// userids
	UserAccountEntity findByFacebookUserId(String fbUserId) throws DaoException;

}
