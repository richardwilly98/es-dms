package com.github.richardwilly98.shiro;

import org.apache.log4j.Logger;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import com.google.inject.Inject;

public class EsWebSessionManager extends DefaultWebSessionManager {

	private static Logger log = Logger.getLogger(EsWebSessionManager.class);
	
	@Inject
	public EsWebSessionManager(SessionDAO sessionDAO) {
		super();
		log.debug("*** constructor ***");
//        this.setDeleteInvalidSessions(true);
//        this.setSessionFactory(new SimpleSessionFactory());
        this.setSessionDAO(sessionDAO);
        Cookie cookie = new SimpleCookie("ES_DMS_TICKET");
//        cookie.setHttpOnly(true);
        cookie.setHttpOnly(true);
        setSessionIdCookie(cookie);
	}
	 
}
