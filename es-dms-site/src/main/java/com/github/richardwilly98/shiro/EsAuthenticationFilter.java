package com.github.richardwilly98.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;

import com.github.richardwilly98.api.ISession;
import com.github.richardwilly98.api.User;
import com.github.richardwilly98.api.exception.ServiceException;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.api.services.UserService;
import com.github.richardwilly98.rest.RestAuthencationService;
import com.google.inject.Inject;

public class EsAuthenticationFilter extends UserFilter {

	private static Logger log = Logger.getLogger(EsAuthenticationFilter.class);
	
	private final AuthenticationService authenticationService;
	private final UserService userService;
	
	@Inject
	public EsAuthenticationFilter(AuthenticationService authenticationService, UserService userService) {
		log.debug("*** Constructor ***");
		this.authenticationService = authenticationService;
		this.userService = userService;
	}
	
	@Override
	protected Subject getSubject(ServletRequest request,
			ServletResponse response) {
		log.debug("*** getSubject ***");
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			for(Cookie cookie : httpRequest.getCookies()) {
				if (RestAuthencationService.ES_DMS_TICKET.equals(cookie.getName())) {
					String token = cookie.getValue();
					try {
//						Subject subject = new Subject.Builder(securityManager).sessionId(sessionId).buildSubject();
						Subject subject = getSubjectFromSessionId(token);
						log.debug("Subject principal: " + subject.getPrincipal());
						return subject;
					} catch (ServiceException ex) {
						log.error("getSubject failed", ex);
					}
				}
				log.debug(cookie.getName() + " - " + cookie.getValue());
			}
		}
		return null;
	}

	private PrincipalCollection getPrincipals(String token)
			throws ServiceException {
		User user = null;
		ISession session = authenticationService.get(token);
		if (session != null) {
			String login = session.getUserId();
			user = userService.get(login);
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, "", "");
		return info.getPrincipals();
	}

	private Subject getSubjectByPrincipal(PrincipalCollection principals) {
		Subject currentUser = new Subject.Builder().principals(principals)
				.buildSubject();
		return currentUser;
	}

	private Subject getSubjectFromSessionId(String token)
			throws ServiceException {
		Subject subject = getSubjectByPrincipal(getPrincipals(token));
		return subject;
	}

}
