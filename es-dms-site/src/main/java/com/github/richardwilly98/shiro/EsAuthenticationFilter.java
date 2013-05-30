package com.github.richardwilly98.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
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
	public EsAuthenticationFilter(final AuthenticationService authenticationService, final UserService userService) {
		this.authenticationService = authenticationService;
		this.userService = userService;
//		setLoginUrl("/#/login");
	}
	
	@Override
	protected Subject getSubject(ServletRequest request,
			ServletResponse response) {
		if (log.isTraceEnabled()) {
			log.trace("Start getSubject");
		}
		if (request == null) {
			log.warn("Request is null");
			return new Subject.Builder().buildSubject();
		}
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			log.debug("url: " + httpRequest.getRequestURL());
			if (httpRequest.getCookies() == null) {
				log.warn("Cookies collection is null");
				return new Subject.Builder().buildSubject();
			}
			for(Cookie cookie : httpRequest.getCookies()) {
				if (RestAuthencationService.ES_DMS_TICKET.equals(cookie.getName())) {
					String token = cookie.getValue();
					log.debug(String.format("Find cookie %s: [%s]", RestAuthencationService.ES_DMS_TICKET, token));
					try {
						Subject subject = getSubjectFromSessionId(token);
						log.debug("Subject principal: " + subject.getPrincipal() + " - authenticated: " + subject.isAuthenticated());
						ThreadContext.bind(subject);
						return subject;
					} catch (Throwable t) {
						log.error("getSubject failed", t);
					}
				}
			}
		}
		return new Subject.Builder().buildSubject();
	}

	private PrincipalCollection getPrincipals(String token)
			throws ServiceException {
		if (log.isTraceEnabled()) {
			log.trace(String.format("Start getPrincipals - %s", token));
		}
		User user = null;
		ISession session = authenticationService.get(token);
		if (session != null) {
			String login = session.getUserId();
			user = userService.get(login);
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, "", "");
		return info.getPrincipals();
	}

	private Subject getSubjectByPrincipal(String token, PrincipalCollection principals) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("Start getSubjectByPrincipal - %s - %s", token, principals));
		}
		Subject currentUser = new Subject.Builder().principals(principals).sessionId(token).authenticated(true)
				.buildSubject();
		return currentUser;
	}

	private Subject getSubjectFromSessionId(String token)
			throws ServiceException {
		if (log.isTraceEnabled()) {
			log.trace(String.format("Start getSubjectFromSessionId - %s", token));
		}
		Subject subject = getSubjectByPrincipal(token, getPrincipals(token));
		return subject;
	}

}
