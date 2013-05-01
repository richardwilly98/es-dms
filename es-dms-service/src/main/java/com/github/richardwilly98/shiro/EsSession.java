package com.github.richardwilly98.shiro;

import java.util.Date;

import org.apache.shiro.session.mgt.SimpleSession;

import com.github.richardwilly98.api.ISession;

public class EsSession extends SimpleSession implements ISession {

	private static final long serialVersionUID = 1L;

	private final ISession session;

	public EsSession(ISession session) {
		if (session == null) {
			throw new NullPointerException("session is null");
		}
		this.session = session;
	}

	@Override
	public String getId() {
		if (session.getId() != null) {
			return session.getId().toString();
		} else {
			return null;
		}
	}

	@Override
	public void setId(String id) {
	}

	@Override
	public String getUserId() {
		return session.getUserId();
	}

	@Override
	public void setUserId(String userId) {
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void setActive(boolean active) {
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void setSecure(boolean secure) {
	}

	@Override
	public Date getCreateTime() {
		return session.getCreateTime();
	}

	@Override
	public void setCreateTime(Date createTime) {
	}

}
