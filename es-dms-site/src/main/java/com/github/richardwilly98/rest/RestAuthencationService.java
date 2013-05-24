package com.github.richardwilly98.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.Session;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.google.inject.Inject;

@Path("/auth")
public class RestAuthencationService extends RestServiceBase<Session> {

	public static final String ES_DMS_TICKET = "ES_DMS_TICKET";

	@Inject
	public RestAuthencationService(AuthenticationService authenticationService) {
		super(authenticationService, authenticationService);
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Response login(Credential credential) {
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
	@Path("/logout")
	public Response logout(@CookieParam(value = ES_DMS_TICKET) String token) {
		try {
			if (log.isTraceEnabled()) {
				log.trace(String.format("logout: %s", token));
			}
//			if (SecurityUtils.getSubject() != null) {
//				SecurityUtils.getSubject().logout();
//			}
			authenticationService.logout(token);
			return Response
					.ok()
					.cookie(new NewCookie(ES_DMS_TICKET, "", "/", "", "", -1,
							false)).build();
		} catch (Throwable t) {
			log.error("login failed", t);
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
