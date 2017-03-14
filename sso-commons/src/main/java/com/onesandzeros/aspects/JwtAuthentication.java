package com.onesandzeros.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for verifying the JWT token and if token is valid then stores the
 * userInfo from the JWT token in to session.
 * 
 * This can be used where ever token validation required and user basic info is
 * needed.
 * 
 * @author swamy
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JwtAuthentication {

}
