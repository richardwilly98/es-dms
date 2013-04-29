package com.github.richardwilly98.api;

import java.util.Date;

public class Session extends ItemBase {

	private static final long serialVersionUID = 1L;

	//private String sessionId; // id
	private String userId; // user
	private boolean active; // session active?
	private boolean secure; // session secure?

	private Date createTime; // session create time
	private Date lastAccessedTime; // session last use time

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isSecure() {
		return secure;
	}
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastAccessedTime() {
		return lastAccessedTime;
	}
	public void setLastAccessedTime(Date lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
