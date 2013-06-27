package test.github.richardwilly98.esdms.rest;

/*
 * #%L
 * es-dms-site
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import javax.ws.rs.core.Cookie;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.CredentialImpl;
import com.github.richardwilly98.esdms.api.Credential;

public class TestRestAuthenticationService extends TestRestUserService {

	public TestRestAuthenticationService() throws Exception {
		super();
	}

	@Test()
	public void testLoginLogout() throws Throwable {
		log.debug("*** testLoginLogout ***");
		try {
			String password = "secret1";
			String login = "user-" + System.currentTimeMillis();
			createUser(login, password);
			boolean rememberMe = true;
			Credential credential = new CredentialImpl.Builder().username(login).password(password).rememberMe(rememberMe).build();
			Cookie cookie = login(credential);
			Assert.assertNotNull(cookie);
			logout(cookie);
		} catch (Throwable t) {
			log.error("testLoginLogout fail", t);
			Assert.fail();
		}
	}

}
