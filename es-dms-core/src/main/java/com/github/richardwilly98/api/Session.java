package com.github.richardwilly98.api;

import java.util.Date;

public class Session implements ISession {

	private static final long serialVersionUID = 1L;

	private String id; // session id
	private String userId; // user
	private boolean active; // session active?
	private boolean secure; // session secure?

	private Date createTime; // session create time
	private Date lastAccessTime; // session last use time
	private long timeout; // session create time

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String getUserId() {
		return userId;
	}
	@Override
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public boolean isActive() {
		return active;
	}
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	@Override
	public boolean isSecure() {
		return secure;
	}
	@Override
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	@Override
	public Date getCreateTime() {
		return createTime;
	}
	@Override
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	@Override
	public Date getLastAccessTime() {
		return lastAccessTime;
	}
	@Override
	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	@Override
	public long getTimeout() {
		return timeout;
	}
	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	@Override
	public String toString() {
		return getId() + " -  " + getUserId();
	}

}
