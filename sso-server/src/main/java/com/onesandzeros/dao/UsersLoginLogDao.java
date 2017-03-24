package com.onesandzeros.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.onesandzeros.model.persistance.UsersLoginLogEntity;

/**
 * 
 * Repository for {@link UsersLoginLogEntity}
 * 
 * @author swamy
 *
 */
@Repository
public interface UsersLoginLogDao extends CrudRepository<UsersLoginLogEntity, Long> {

}
