package com.github.richardwilly98.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.api.services.AuthenticationService;
import com.github.richardwilly98.rest.exception.RestServiceException;
import com.google.inject.Inject;

@Path("/auth")
public class RestAuthencationService extends RestServiceBase {

	@Inject
	public RestAuthencationService(AuthenticationService authenticationService) {
		super(authenticationService);
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(Credential credential) {
		try {
			if (credential == null) {
				throw new IllegalArgumentException("credential");
			}
			if (log.isTraceEnabled()) {
				log.trace(String.format("login - %s", credential.getUsername()));
			}
			String token = authenticationService.login(credential);
//			for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms())
//				log.trace("Realm: " + realm.getName());
//			
//			UsernamePasswordToken token = new UsernamePasswordToken(
//					credential.getLogin(), credential.getPassword());
//			SecurityUtils.getSubject().login(token);
//			token.clear();
			return Response.ok().entity("AUTHENTICATED")
					.cookie(new NewCookie("ES_DMS_TICKET", token)).build();
		} catch (Throwable t) {
			log.error("login failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}

	@GET
	@Path("/logout")
	public Response logout() {
		try {
			if (log.isTraceEnabled()) {
				log.trace("logout");
			}
//			if (SecurityUtils.getSubject() != null) {
//				SecurityUtils.getSubject().logout();
//			}
//			authenticationService.logout("token");
			return Response
					.ok()
					.cookie(new NewCookie("ES_DMS_TICKET", "", "/", "", "", -1,
							false)).build();
		} catch (Throwable t) {
			log.error("login failed", t);
			throw new RestServiceException(t.getLocalizedMessage());
		}
	}
}
