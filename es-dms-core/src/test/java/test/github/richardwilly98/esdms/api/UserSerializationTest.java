package test.github.richardwilly98.esdms.api;

/*
 * #%L
 * es-dms-core
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


import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.richardwilly98.esdms.FacetImpl;
import com.github.richardwilly98.esdms.PermissionImpl;
import com.github.richardwilly98.esdms.RoleImpl;
import com.github.richardwilly98.esdms.SearchResultImpl;
import com.github.richardwilly98.esdms.TermImpl;
import com.github.richardwilly98.esdms.UserImpl;
import com.github.richardwilly98.esdms.api.Facet;
import com.github.richardwilly98.esdms.api.Permission;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.api.Term;
import com.github.richardwilly98.esdms.api.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class UserSerializationTest {

	private static Logger log = Logger.getLogger(UserSerializationTest.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@BeforeClass
	public void initialize() {
		log.info("*** initialize ***");
	}

	@Test
	public void testSerializeDeserializeUser() throws Throwable {
		log.debug("*** testSerializeDeserializeUser ***");
		String id = "user-" + System.currentTimeMillis();
		String name = id;
		String email = id + "@gmail.com";
		String password = "secret";
		User user = new UserImpl.Builder().password(password).id(id).name(name).email(email).build();
		log.debug(user);
		String json = mapper.writeValueAsString(user);
		log.debug(json);
		Assert.assertNotNull(json);
		User user2 = mapper.readValue(json, User.class);
		log.debug(user2);
		Assert.assertEquals(user, user2);
	}
	
//	@Test
//	public void testSerializeDeserializeSession() throws Throwable {
//		log.debug("*** testSerializeDeserializeSession ***");
//		String userId = "user-" + System.currentTimeMillis();
//		
//		boolean active = true;
//		boolean secure = true ;
//		Date createTime = Date.parse(System.currentTimeMillis());
//		Date lastAccessTime;
//		long timeout;
//		
//		Session item = new SessionImpl.Builder().password(password).id(id).name(name).email(email).build();
//		log.debug(item);
//		String json = mapper.writeValueAsString(item);
//		log.debug(json);
//		Assert.assertNotNull(json);
//		User item2 = mapper.readValue(json, User.class);
//		log.debug(item2);
//		Assert.assertEquals(item, item2);
//	}
	
//	@Test
//	public void testSerializeDeserializePerson() throws Throwable {
//		log.debug("*** testSerializeDeserializePerson ***");
//		String id = "person-" + System.currentTimeMillis();
//		String name = id;
//		String city = "some place";
//		String email = id + "@gmail.com";
//		String password = "secret";
//		Person person = new PersonImpl.Builder().city(city).email(email).build();
//		log.debug(person);
//		String json = mapper.writeValueAsString(person);
//		log.debug(json);
//		Assert.assertNotNull(json);
//		Person person2 = mapper.readValue(json, Person.class);
//		log.debug(person2);
//		Assert.assertEquals(person, person2);
//	}
	
	@Test
	public void testSerializeDeserializeTerm() throws Throwable {
		log.debug("*** testSerializeDeserializeTerm ***");
		int count = 1;
		String term = "term";
		Term item = new TermImpl.Builder().term(term).count(count).build();
		log.debug(item);
		String json = mapper.writeValueAsString(item);
		log.debug(json);
		Assert.assertNotNull(json);
		Term item2 = mapper.readValue(json, Term.class);
		log.debug(item2);
		Assert.assertEquals(item, item2);
	}
	
	@Test
	public void testSerializeDeserializeSearchResult() throws Throwable {
		log.debug("*** testSerializeDeserializeSearchResult ***");
		Set<User> users = newHashSet();
		users.add(new UserImpl.Builder().password("test").id("test").name("test").email("test@test").build());
		long elapsedTime = 1;
		String term = "term";
		Set<Term> terms = newHashSet();
		terms.add(new TermImpl.Builder().term(term).count(1).build());
		Facet facet = new FacetImpl.Builder().missingCount(0).otherCount(0).totalCount(0).terms(terms).build();
		Map<String, Facet> facets = newHashMap(ImmutableMap.of("facet1", facet));
		SearchResult<User> items = new SearchResultImpl.Builder<User>().items(users).elapsedTime(elapsedTime).firstIndex(0).pageSize(10).facets(facets).build();
		log.debug(items);
		String json = mapper.writeValueAsString(items);
		log.debug(json);
		Assert.assertNotNull(json);
		SearchResult<User> items2 = mapper.readValue(json, new TypeReference<SearchResultImpl<User>>() {});
		log.debug(items2);
		Assert.assertEquals(items, items2);
	}
	
	@Test
	public void testSerializeDeserializeFacet() throws Throwable {
		log.debug("*** testSerializeDeserializeFacet ***");
		long missingCount = 1;
		long otherCount = 0;
		long totalCount = 10;
		Set<Term> terms = newHashSet();
		terms.add(new TermImpl.Builder().term("term").count(1).build());
		Facet item = new FacetImpl.Builder().missingCount(missingCount).otherCount(otherCount).totalCount(totalCount).terms(terms).build();
		log.debug(item);
		String json = mapper.writeValueAsString(item);
		log.debug(json);
		Assert.assertNotNull(json);
		Facet item2 = mapper.readValue(json, Facet.class);
		log.debug(item2);
		Assert.assertEquals(item, item2);
	}
	
	@Test
	public void testUserHasRole() throws Throwable {
		log.debug("*** testUserHasRole ***");
		String id = "user-" + System.currentTimeMillis();
		String name = id;
		String email = id + "@gmail.com";
		String password = "secret";
		Role role = new RoleImpl.Builder().id("my-role").name("My role").build();
		Set<Role> roles = newHashSet(ImmutableSet.of(role));
		User user = new UserImpl.Builder().password(password).id(id).name(name).email(email).roles(roles).build();
		log.debug("user: " + user);
		Assert.assertTrue(user.hasRole(role));
		String json = mapper.writeValueAsString(user);
		log.debug(json);
		Assert.assertNotNull(json);
		User user2 = mapper.readValue(json, User.class);
		log.debug("user2: " + user2);
		Assert.assertEquals(user, user2);
		Assert.assertTrue(user2.hasRole(role));
	}

	@Test
	public void testSerializeDeserializePermission() throws Throwable {
		log.debug("*** testSerializeDeserializePermission ***");
		String id = "permission-" + System.currentTimeMillis();
		String name = id;
		Permission permission = new PermissionImpl.Builder().id(id).name(name).access("access1").build();
		log.debug(permission);
		String json = mapper.writeValueAsString(permission);
		log.debug(json);
		Assert.assertNotNull(json);
		Permission permission2 = mapper.readValue(json, Permission.class);
		log.debug(permission2);
		Assert.assertEquals(permission, permission2);
		Permission permission3 = new PermissionImpl.Builder().id(id).name(name).access("access1").build();
		Assert.assertNotSame(permission3, permission2);
	}
}
