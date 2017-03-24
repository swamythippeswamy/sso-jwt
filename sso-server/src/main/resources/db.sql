create database sso;

DROP TABLE IF EXISTS `user_account`;

CREATE TABLE `user_account` (
  `user_id` bigint(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(40) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(30) DEFAULT NULL,
  `account_type` varchar(20) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fb_user_id` varchar(30) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `email_activated_on` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `user_registration_token`;

CREATE TABLE `user_registration_token` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `hash` varchar(35) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` bigint(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `users_login_log`;

CREATE TABLE `users_login_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `last_login_time` timestamp not null default CURRENT_TIMESTAMP,
  `user_id` bigint(11) NOT NULL,
  `login_status` varchar(20) DEFAULT NULL,
  `ip_address` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
