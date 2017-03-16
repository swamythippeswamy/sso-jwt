package com.onesandzeros.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.onesandzeros.exceptions.DaoException;
import com.onesandzeros.model.UserAccountEntity;
import com.onesandzeros.models.AccountType;

@Repository
public interface SSOAuthDao extends CrudRepository<UserAccountEntity, Long> {

	UserAccountEntity findByEmail(String email) throws DaoException;

	UserAccountEntity findByFacebookUserIdAndAccountType(String fbUserId, AccountType accountType) throws DaoException;

}
