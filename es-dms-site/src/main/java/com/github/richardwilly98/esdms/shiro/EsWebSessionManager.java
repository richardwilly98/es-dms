package com.github.richardwilly98.esdms.shiro;

import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import com.github.richardwilly98.esdms.rest.RestAuthencationService;
import com.google.inject.Inject;

public class EsWebSessionManager extends DefaultWebSessionManager {

	@Inject
	public EsWebSessionManager(SessionDAO sessionDAO) {
		super();
		// this.setDeleteInvalidSessions(true);
		// this.setSessionFactory(new SimpleSessionFactory());
		this.setSessionDAO(sessionDAO);
		Cookie cookie = new SimpleCookie(RestAuthencationService.ES_DMS_TICKET);
		cookie.setHttpOnly(true);
		setSessionIdCookie(cookie);
		// Cookie session is disabled. Cookie is managed in AuthenticationService login / logout
		setSessionIdCookieEnabled(false);
	}

}
