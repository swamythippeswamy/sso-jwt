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
	private Long id;

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

	@Column(name = "active")
	private boolean active;

	@Column(name = "create_time")
	private Timestamp createTime;

	@Column(name = "last_login_time")
	private Timestamp lastLoginTime;

	@Column(name = "fb_user_id")
	private String facebookUserId;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Timestamp getEmailActivatedOn() {
		return emailActivatedOn;
	}

	public void setEmailActivatedOn(Timestamp emailActivatedOn) {
		this.emailActivatedOn = emailActivatedOn;
	}

	@Override
	public String toString() {
		return "UserAccountEntity [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone
				+ ", password=" + password + ", accountType=" + accountType + ", active=" + active + ", createTime="
				+ createTime + ", lastLoginTime=" + lastLoginTime + ", facebookUserId=" + facebookUserId
				+ ", emailActivatedOn=" + emailActivatedOn + "]";
	}

}
