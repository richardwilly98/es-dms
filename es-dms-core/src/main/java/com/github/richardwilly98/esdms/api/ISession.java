package com.github.richardwilly98.esdms.api;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.SessionImpl;

@JsonDeserialize(as = SessionImpl.class)
public interface ISession {

	public abstract String getId();

	public abstract void setId(String id);

	public abstract String getUserId();

	public abstract void setUserId(String userId);

	public abstract boolean isActive();

	public abstract void setActive(boolean active);

	public abstract boolean isSecure();

	public abstract void setSecure(boolean secure);

	public abstract Date getCreateTime();

	public abstract void setCreateTime(Date createTime);

	public abstract Date getLastAccessTime();

	public abstract void setLastAccessTime(Date lastAccessTime);

	public abstract long getTimeout();

	public abstract void setTimeout(long timeout);

}