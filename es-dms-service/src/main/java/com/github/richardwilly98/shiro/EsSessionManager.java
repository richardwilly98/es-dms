package com.github.richardwilly98.shiro;

import org.apache.log4j.Logger;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.SessionDAO;

import com.google.inject.Inject;

public class EsSessionManager extends DefaultSessionManager {

	private static Logger log = Logger.getLogger(EsSessionManager.class);
//	private final EsSessionDAO sessionDAO;
	@Inject 
	public EsSessionManager(SessionDAO sessionDAO) {
		log.debug("*** constructor ***");
//		super();
        this.setDeleteInvalidSessions(true);
        this.setSessionFactory(new SimpleSessionFactory());
        this.setSessionDAO(sessionDAO);
//		this.sessionDAO = sessionDAO;
	}

//	@Override
//	public SessionDAO getSessionDAO() {
//		return this.sessionDAO;
//	}
}
