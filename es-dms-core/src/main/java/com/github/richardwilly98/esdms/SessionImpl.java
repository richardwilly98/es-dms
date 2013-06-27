package com.github.richardwilly98.esdms;

/*
 * #%L
 * es-dms-core
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.Date;

import com.github.richardwilly98.esdms.api.Session;
import com.github.richardwilly98.esdms.api.ItemBase;
import com.google.common.base.Objects;

public class SessionImpl extends ItemBaseImpl implements ItemBase, Session {

	private static final long serialVersionUID = 1L;

	private String id; // session id
	private String userId; // user
	private boolean active; // session active?
	private boolean secure; // session secure?
	private Date createTime; // session create time
	private Date lastAccessTime; // session last use time
	private long timeout; // session create time

	public static class Builder extends BuilderBase<Builder> {

		private String userId;
		private boolean active;
		private boolean secure;
		private Date createTime;
		private Date lastAccessTime;
		private long timeout;

		public Builder userId(String userId) {
			this.userId = userId;
			return getThis();
		}

		public Builder active(boolean active) {
			this.active = active;
			return getThis();
		}

		public Builder secure(boolean secure) {
			this.secure = secure;
			return getThis();
		}

		public Builder createTime(Date createTime) {
			this.createTime = createTime;
			return getThis();
		}

		public Builder lastAccessTime(Date lastAccessTime) {
			this.lastAccessTime = lastAccessTime;
			return getThis();
		}

		public Builder timeout(long timeout) {
			this.timeout = timeout;
			return getThis();
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		public SessionImpl build() {
			this.name = "session-" + this.id;
			return new SessionImpl(this);
		}
	}

	SessionImpl() {
		super(null);
	}

	protected SessionImpl(Builder builder) {
		super(builder);
		if (builder != null) {
			this.id = builder.id;
			this.active = builder.active;
			this.createTime = builder.createTime;
			this.lastAccessTime = builder.lastAccessTime;
			this.secure = builder.secure;
			this.timeout = builder.timeout;
			this.userId = builder.userId;
		}
	}

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
		return Objects.toStringHelper(this).add("id", id).add("userId", userId)
				.add("createTime", createTime).toString();
	}

}
