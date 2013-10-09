package com.github.richardwilly98.esdms.shiro;

/*
 * #%L
 * es-dms-site
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

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.github.richardwilly98.esdms.shiro.EsAuthenticationFilter;
import com.github.richardwilly98.esdms.shiro.EsRealm;
import com.github.richardwilly98.esdms.shiro.EsSessionDAO;
import com.github.richardwilly98.esdms.shiro.EsWebSessionManager;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.name.Names;

public class TestEsShiroWebModule extends ShiroWebModule {

    // private final String securityFilterPath;
    private final Logger log = Logger.getLogger(this.getClass());

    public TestEsShiroWebModule(ServletContext servletContext, String securityFilterPath) {
        super(servletContext);
        // this.securityFilterPath = securityFilterPath;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void configureShiroWeb() {
        log.debug("*** configureShiroWeb ***");
        bindConstant().annotatedWith(Names.named("shiro.loginUrl")).to("/#/login");

        bindRealm().to(EsRealm.class).asEagerSingleton();
        bind(SessionDAO.class).to(EsSessionDAO.class);
        bind(EsSessionDAO.class);

        addFilterChain("/auth/**", ANON);
        // addFilterChain("/auth/**", SSL);

        addFilterChain("/**", Key.get(EsAuthenticationFilter.class));
    }

    @Override
    protected void bindSessionManager(AnnotatedBindingBuilder<SessionManager> bind) {
        bind.to(EsWebSessionManager.class).in(Scopes.SINGLETON);
        bindConstant().annotatedWith(Names.named("shiro.globalSessionTimeout")).to(30000L);
        bind(EsWebSessionManager.class);

    }

}
