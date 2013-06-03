package com.github.richardwilly98.esdms.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.SessionImpl;
import com.github.richardwilly98.esdms.api.ISession;
import com.github.richardwilly98.esdms.rest.exception.RestServiceException;
import com.github.richardwilly98.esdms.services.AuthenticationService;
import com.google.inject.Inject;

@Path(RestAuthencationService.AUTH_PATH)
public class RestAuthencationService extends RestServiceBase<SessionImpl> {

	public static final String LOGOUT_PATH = "logout";
	public static final String LOGIN_PATH = "login";
	public static final String AUTH_PATH = "auth";
	public static final String ES_DMS_TICKET = "ES_DMS_TICKET";

	@Inject
	public RestAuthencationService(AuthenticationService authenticationService) {
		super(authenticationService, authenticationService);
	}

	@POST
	@Path(LOGIN_PATH)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response login(CredentialImpl credential) {
		try {
			checkNotNull(credential);
			if (log.isTraceEnabled()) {
				log.trace(String.format("login - %s", credential.getUsername()));
			}
			String token = authenticationService.login(credential);
			if (log.isTraceEnabled()) {
				log.trace(String.format("Create cookie %s: [%s]", ES_DMS_TICKET, token));
			}
			return Response.ok().entity(new AuthenticationResponse("AUTHENTICATED", token))
					.cookie(new NewCookie(ES_DMS_TICKET, token, "/", null, 1, "", 30000, false)).build();
		} catch (Throwable t) {
			log.error("login failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}

	@POST
	@Path(LOGOUT_PATH)
	public Response logout(@CookieParam(value = ES_DMS_TICKET) String token) {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("logout: %s", token));
			}
			authenticationService.logout(token);
			return Response
					.ok()
					.cookie(new NewCookie(ES_DMS_TICKET, "", "/", "", "", -1,
							false)).build();
		} catch (Throwable t) {
			log.error("logout failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}
	
	private class AuthenticationResponse {
		private final String status;
		private final String token;

		public AuthenticationResponse(String status, String token) {
			this.status = status;
			this.token = token;
		}
		@SuppressWarnings("unused")
		public String getToken() {
			return token;
		}
		@SuppressWarnings("unused")
		public String getStatus() {
			return status;
		}
	}
}
