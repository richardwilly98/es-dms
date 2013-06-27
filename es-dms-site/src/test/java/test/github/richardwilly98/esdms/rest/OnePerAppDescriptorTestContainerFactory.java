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


import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;

public class OnePerAppDescriptorTestContainerFactory implements
		TestContainerFactory {
	private static final ConcurrentMap<AppDescriptor, TestContainer> cache = new ConcurrentHashMap<AppDescriptor, TestContainer>();

	private final TestContainerFactory tcf;

	public OnePerAppDescriptorTestContainerFactory(TestContainerFactory tcf) {
		this.tcf = tcf;
	}

	@Override
	public <T extends AppDescriptor> Class<T> supports() {
		return tcf.supports();
	}

	@Override
	public TestContainer create(URI baseUri, AppDescriptor ad) {
		if (cache.get(ad) == null) {
			cache.putIfAbsent(ad, tcf.create(baseUri, ad));
		}
		return cache.get(ad);
	}
}
