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

import com.onesandzeros.models.AccountStatus;
import com.onesandzeros.models.AccountType;

/**
 * Entity for storing the complete user account information
 * 
 * @author swamy
 *
 */
@Entity
@Table(name = "user_account")
public class UserAccountEntity implements Serializable {

	private static final long serialVersionUID = 6036682665976681403L;

	@Id
	@GenericGenerator(name = "gen", strategy = "identity")
	@GeneratedValue(generator = "gen", strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "user_name")
	private String name;

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	@Column(name = "password")
	private String password;

	@Column(name = "account_type")
	@Enumerated(EnumType.STRING)
	private AccountType accountType;

	// TODO (Status is desirable - ACTIVE. SUSPEND. DELTE. TOBEVERIFIED). Other
	// details of Tobeverified should be in another table
	// (RegistrationTokenEntity)
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private AccountStatus status;

	@Column(name = "create_time")
	private Timestamp createTime;

	// TODO (This is not sufficient. Create db/file log entry for each
	// login)UserId, source IP, status-SUC/FAIL should be logged

	// Created a separate table for tracking the user's last logins

	@Column(name = "fb_user_id")
	private String facebookUserId;

	// Does it mean email verified on?
	@Column(name = "email_activated_on")
	private Timestamp emailActivatedOn;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(String facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	public Timestamp getEmailActivatedOn() {
		return emailActivatedOn;
	}

	public void setEmailActivatedOn(Timestamp emailActivatedOn) {
		this.emailActivatedOn = emailActivatedOn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAccountEntity [userId=").append(userId).append(", name=").append(name).append(", email=")
				.append(email).append(", phone=").append(phone).append(", password=").append(password)
				.append(", accountType=").append(accountType).append(", status=").append(status).append(", createTime=")
				.append(createTime).append(", facebookUserId=").append(facebookUserId).append(", emailActivatedOn=")
				.append(emailActivatedOn).append("]");
		return builder.toString();
	}

}
