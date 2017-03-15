package com.onesandzeros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application Starting point
 * 
 * @author swamy
 */

@SpringBootApplication(scanBasePackages = { "com.onesandzeros" })
// @ImportResource()
public class SSOAuthBootAppMain {

	public static void main(String[] args) {
		SpringApplication.run(SSOAuthBootAppMain.class, args);
	}
}
