package com.github.richardwilly98.esdms.shiro;

/*
 * #%L
 * es-dms-service
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

import org.apache.shiro.session.mgt.SimpleSession;

import com.github.richardwilly98.esdms.api.Session;

public class EsSession extends SimpleSession implements Session {

    private static final long serialVersionUID = 1L;

    private final Session session;

    public EsSession(Session session) {
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
    public String getUserId() {
        return session.getUserId();
    }

    @Override
    public boolean isActive() {
        return session.isActive();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public Date getCreateTime() {
        return session.getCreateTime();
    }

    @Override
    public Date getLastAccessTime() {
        return session.getLastAccessTime();
    }

    @Override
    public void setLastAccessTime(Date lastAccessTime) {
        session.setLastAccessTime(lastAccessTime);
    }

    @Override
    public void setUserId(String userId) {
        session.setUserId(userId);
    }

    @Override
    public long getTimeout() {
        return session.getTimeout();
    }

}
