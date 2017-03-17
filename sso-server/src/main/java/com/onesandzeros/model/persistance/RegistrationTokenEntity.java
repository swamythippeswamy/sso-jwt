package com.onesandzeros.model.persistance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_registration_token")
public class RegistrationTokenEntity {
	@Id
	@GenericGenerator(name = "gen", strategy = "identity")
	@GeneratedValue(generator = "gen", strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "hash")
	private String hash;

	@Column(name = "user_id")
	private Long userId;

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

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "RegistrationTokenEntity [id=" + id + ", hash=" + hash + ", userId=" + userId + ", createTime="
				+ createTime + "]";
	}

}
