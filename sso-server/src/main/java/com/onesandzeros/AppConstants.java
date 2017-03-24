package com.onesandzeros;

public interface AppConstants {

	enum LoginAttempt {
		SUCCESS, FAILURE
	}

	String MAIL_SMTP_HOST = "mail.smtp.host";
	String MAIL_SMTP_PORT = "mail.smtp.port";
	String MAIL_SMTP_AUTH = "mail.smtp.auth";
	String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	String MAIL_USERNAME = "mail.username";
	String MAIL_PASSWORD = "mail.password";

	String MAIL_SUBJECT = "mail.subject";

	String MAIL_ACTIVATION_TEMPLATE = "mail.activation.template";

	String ACCOUNT_ACTIVATION_URL = "account.activation.url";

	String PWD_ENCRYPT_DECRYPT_KEY = "password.encrypt.decrypt.key";

	String JWT_TOKEN_HEADER_PREFIX = "jwt.token.header.prefix";
	String JWT_TOKEN_HEADER_NAME = "jwt.auth.header.name";

	String JWT_TOKEN_ISSUER = "jwt.token.issuer";
	String JWT_TOKEN_VALID_TIME = "jwt.token.valid.time";

	String SERVER_KEY_FILE_NAME = "server.key.file.name";

	String REGISTRATION_TOKEN_EXP_TIME = "registration.token.exp.time";

}
