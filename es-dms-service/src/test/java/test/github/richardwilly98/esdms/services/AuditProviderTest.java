package test.github.richardwilly98.esdms.services;

/*
 * #%L
 * es-dms-service
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

import static com.google.common.collect.Lists.newArrayList;

import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.github.richardwilly98.esdms.AuditEntryImpl;
import com.github.richardwilly98.esdms.api.AuditEntry;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.services.UserService;

public class AuditProviderTest extends ProviderTestBase {

    private AuditEntry testCreateAudit(AuditEntry.Event event, String user, String itemId, Date date) throws Throwable {
	// String id = String.valueOf(System.currentTimeMillis());
	AuditEntry audit = new AuditEntryImpl.Builder()/* .id(id) */
	/* .name(event.toString() + "-" + user + "-" + id) */.event(event).user(user).itemId(itemId).date(date).build();

	audit = auditService.create(audit);
	// Assert.assertEquals(id, audit.getId());

	AuditEntry newAudit = auditService.get(audit.getId());
	Assert.assertNotNull(newAudit);
	Assert.assertEquals(audit.getName(), newAudit.getName());
	Assert.assertEquals(audit.getDescription(), newAudit.getDescription());
	Assert.assertEquals(audit.isDisabled(), newAudit.isDisabled());
	Assert.assertEquals(audit.getEvent(), newAudit.getEvent());
	Assert.assertEquals(audit.getUser(), newAudit.getUser());
	Assert.assertEquals(audit.getDate(), newAudit.getDate());
	Assert.assertEquals(audit.getItemId(), newAudit.getItemId());
	return newAudit;
    }

    @Test
    public void testCreateAudit() throws Throwable {
	log.info("Start testCreateAudit");

	// Make sure to be login with user having sufficient permission
	loginAdminUser();

	// content permissions
	AuditEntry audit = testCreateAudit(AuditEntry.Event.UPLOAD, UserService.DEFAULT_ADMIN_LOGIN,
	        "document-" + System.currentTimeMillis(), new Date());
	log.debug(audit);
    }

    @Test
    public void testFindAudit() throws Throwable {
	log.info("Start testFindAudit");

	Date date = new Date(0);
	String itemId = "document-" + System.currentTimeMillis();
	testCreateAudit(AuditEntry.Event.UPLOAD, UserService.DEFAULT_ADMIN_LOGIN, itemId, date);

	SearchResult<AuditEntry> result = auditService.search("item_id:" + itemId, 0, 1);

	Assert.assertNotNull(result);
	Assert.assertTrue(result.getTotalHits() >= 1);
	log.debug(String.format("TotalHits: %s", result.getTotalHits()));
	for (AuditEntry audit : result.getItems()) {
	    log.debug("audit found: " + audit);
	}

	testCreateAudit(AuditEntry.Event.CHECKOUT, UserService.DEFAULT_ADMIN_LOGIN, "document-" + System.currentTimeMillis(), new Date());
	testCreateAudit(AuditEntry.Event.CHECKIN, UserService.DEFAULT_ADMIN_LOGIN, "document-" + System.currentTimeMillis(), new Date());

	result = auditService.search("event:" + AuditEntry.Event.CHECKOUT.toString(), 0, 1);

	Assert.assertNotNull(result);
	Assert.assertTrue(result.getTotalHits() >= 1);

	Assert.assertNotNull(result.getItems().iterator().next());
    }

    @Test
    public void testDeleteAudit() throws Throwable {
	log.info("Start testDeleteAudit");
	AuditEntry audit = testCreateAudit(AuditEntry.Event.UPLOAD, UserService.DEFAULT_ADMIN_LOGIN,
	        "document-" + System.currentTimeMillis(), new Date());
	String id = audit.getId();
	audit = auditService.get(id);
	auditService.delete(audit);
	audit = auditService.get(id);
	Assert.assertNull(audit);
    }

    @Test
    public void testClearAuditByIds() throws Throwable {
	log.info("Start testClearAuditByIds");
	List<String> ids = newArrayList();
	AuditEntry audit = testCreateAudit(AuditEntry.Event.UPLOAD, UserService.DEFAULT_ADMIN_LOGIN,
	        "document-" + System.currentTimeMillis(), new Date());
	ids.add(audit.getId());
	audit = testCreateAudit(AuditEntry.Event.CHECKOUT, UserService.DEFAULT_ADMIN_LOGIN, "document-" + System.currentTimeMillis(),
	        new Date());
	ids.add(audit.getId());
	auditService.clear(ids);
	for (String id : ids) {
	    audit = auditService.get(id);
	    Assert.assertNull(audit);
	}
    }

    @Test
    public void testClearAuditByDateRange() throws Throwable {
	log.info("Start testClearAuditByDateRange");
	List<String> ids = newArrayList();
	Date from = new Date(8000);
	Date to = new Date(9000);
	AuditEntry audit = testCreateAudit(AuditEntry.Event.UPLOAD, UserService.DEFAULT_ADMIN_LOGIN,
	        "document-" + System.currentTimeMillis(), from);
	ids.add(audit.getId());
	audit = testCreateAudit(AuditEntry.Event.CHECKOUT, UserService.DEFAULT_ADMIN_LOGIN, "document-" + System.currentTimeMillis(), to);
	ids.add(audit.getId());
	auditService.clear(from, to);
	for (String id : ids) {
	    audit = auditService.get(id);
	    Assert.assertNull(audit);
	}
    }
}
