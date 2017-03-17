package com.onesandzeros;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

	/*
	 * Velocity configuration.
	 */
	@Bean
	public VelocityEngine getVelocityEngine() throws VelocityException, IOException {

		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		return new VelocityEngine(props);
	}
}
