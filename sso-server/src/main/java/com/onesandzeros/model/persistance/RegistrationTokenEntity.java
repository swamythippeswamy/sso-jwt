package com.onesandzeros.model.persistance;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Model for storing and tracking the tokens added in the email activation link
 * 
 * @author swamy
 *
 */
@Entity
@Table(name = "user_registration_token")
public class RegistrationTokenEntity implements Serializable {
	private static final long serialVersionUID = 290874030709012600L;

	@Id
	@GenericGenerator(name = "gen", strategy = "identity")
	@GeneratedValue(generator = "gen", strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "hash")
	private String hash;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "email")
	private String email;

	@Column(name = "create_time")
	private Timestamp createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RegistrationTokenEntity [id=").append(id).append(", hash=").append(hash).append(", userId=")
				.append(userId).append(", email=").append(email).append(", createTime=").append(createTime).append("]");
		return builder.toString();
	}

}
