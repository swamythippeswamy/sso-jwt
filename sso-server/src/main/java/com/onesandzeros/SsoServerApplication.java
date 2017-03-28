package com.onesandzeros;

import java.io.IOException;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring Boot Application Starting point
 * 
 * @author swamy
 */

@SpringBootApplication(scanBasePackages = { "com.onesandzeros" })
// @ImportResource()
public class SsoServerApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SsoServerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SsoServerApplication.class, args);
	}

	@Bean
	public VelocityEngine getVelocityEngine() throws VelocityException, IOException {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		return new VelocityEngine(props);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				LOGGER.info("REgistry : {}", registry);
				registry.addMapping("/**").allowedHeaders("*").allowCredentials(true).exposedHeaders("Authentication")
						.allowedOrigins("*")
						.allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.OPTIONS.name());
			}
		};
	}
}
