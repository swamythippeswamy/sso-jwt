create database sso;

DROP TABLE IF EXISTS `user_account`;

CREATE TABLE `user_account` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(40) DEFAULT NULL,
  `email` varchar(30) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(30) DEFAULT NULL,
  `account_type` varchar(20) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

alter table user_account add column fb_user_id varchar(30) default null;
alter table user_account add column last_login_time timestamp not null default current_timestamp;
alter table user_account add column active int(1) default 0;
alter table user_account add column email_activated_on timestamp default null;




DROP TABLE IF EXISTS `user_registration_token`;

CREATE TABLE `user_registration_token` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `hash` varchar(35) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` bigint(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

