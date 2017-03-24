package com.onesandzeros.model.persistance;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Table used for tracking the users login attempts
 * 
 * @author swamy
 *
 */

@Entity
@Table(name = "users_login_log")
public class UsersLoginLogEntity implements Serializable {

	private static final long serialVersionUID = 710101482098286421L;

	@Id
	@GenericGenerator(name = "gen", strategy = "identity")
	@GeneratedValue(generator = "gen", strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "last_login_time")
	private Timestamp lastLoginTime;

	@Column(name = "ip_address")
	private String ipAddress;

	@Column(name = "login_status")
	@Enumerated(EnumType.STRING)
	private LoginStatus loginStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public LoginStatus getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(LoginStatus loginStatus) {
		this.loginStatus = loginStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UsersLoginLogEntity [id=").append(id).append(", userId=").append(userId)
				.append(", lastLoginTime=").append(lastLoginTime).append(", ipAddress=").append(ipAddress)
				.append(", loginStatus=").append(loginStatus).append("]");
		return builder.toString();
	}

}
