package com.github.richardwilly98.esdms.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import com.github.richardwilly98.esdms.api.Session;
import com.github.richardwilly98.esdms.api.User;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.rest.RestAuthencationService;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.github.richardwilly98.esdms.services.UserService;
import com.google.inject.Inject;

public class EsAuthenticationFilter extends UserFilter {

	private static Logger log = Logger.getLogger(EsAuthenticationFilter.class);

	private final AuthenticationService authenticationService;
	private final UserService userService;

	@Inject
	public EsAuthenticationFilter(
			final AuthenticationService authenticationService,
			final UserService userService) {
		this.authenticationService = authenticationService;
		this.userService = userService;
		// setLoginUrl("/#/login");
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
			for (Cookie cookie : httpRequest.getCookies()) {
				if (RestAuthencationService.ES_DMS_TICKET.equals(cookie
						.getName())) {
					String token = cookie.getValue();
					log.debug(String.format("Find cookie %s: [%s]",
							RestAuthencationService.ES_DMS_TICKET, token));
					try {
						Subject subject = getSubjectFromSessionId(token);
						if (subject != null) {
						log.debug("Subject principal: "
								+ subject.getPrincipal() + " - authenticated: "
								+ subject.isAuthenticated());
						ThreadContext.bind(subject);
						return subject;
						} else {
							break;
						}
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
		Session session = authenticationService.get(token);
		if (session != null) {
			String login = session.getUserId();
			user = userService.get(login);
			log.trace(String.format("getPrincipals - Found user %s from token %s", login, token));
		} else {
			log.info(String.format("getPrincipals - Cannot find user with token %s", token));
			return null;
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, "",
				"");
		return info.getPrincipals();
	}

	private Subject getSubjectByPrincipal(String token,
			PrincipalCollection principals) {
		if (log.isTraceEnabled()) {
			log.trace(String.format("Start getSubjectByPrincipal - %s - %s",
					token, principals));
		}
		Subject currentUser = new Subject.Builder().principals(principals)/*.sessionCreationEnabled(false)*/
				.sessionId(token).authenticated(true).buildSubject();
		return currentUser;
	}

	private Subject getSubjectFromSessionId(String token)
			throws ServiceException {
		if (log.isTraceEnabled()) {
			log.trace(String
					.format("Start getSubjectFromSessionId - %s", token));
		}
		PrincipalCollection principals = getPrincipals(token);
		if (principals == null) {
			return null;
		}
		Subject subject = getSubjectByPrincipal(token, principals);
		return subject;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		Subject subject = getSubject(request, response);
		if (subject.getPrincipal() == null) {
			WebUtils.toHttp(response).sendError(
					HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			WebUtils.toHttp(response).sendError(
					HttpServletResponse.SC_UNAUTHORIZED);
		}
		return false;
	}

}
