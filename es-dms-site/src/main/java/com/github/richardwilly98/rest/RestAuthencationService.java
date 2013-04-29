package com.github.richardwilly98.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;

import com.github.richardwilly98.api.Credential;
import com.github.richardwilly98.rest.exception.RestServiceException;

@Path("/auth")
public class RestAuthencationService extends RestServiceBase {

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(Credential credential) {
		try {
			if (credential == null) {
				throw new IllegalArgumentException("credential");
			}
			if (log.isTraceEnabled()) {
				log.trace(String.format("login - %s", credential.getLogin()));
			}
			for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms())
				log.trace("Realm: " + realm.getName());
			
			UsernamePasswordToken token = new UsernamePasswordToken(
					credential.getLogin(), credential.getPassword());
			SecurityUtils.getSubject().login(token);
			token.clear();
			return Response.ok().entity("AUTHENTICATED")
					.cookie(new NewCookie("ES_DMS_TICKET", "XXXX")).build();
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
			if (SecurityUtils.getSubject() != null) {
				SecurityUtils.getSubject().logout();
			}
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
