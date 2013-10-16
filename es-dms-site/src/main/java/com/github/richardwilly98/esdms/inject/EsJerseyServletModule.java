package com.github.richardwilly98.esdms.inject;

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

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.log4j.Logger;
import org.apache.shiro.guice.web.GuiceShiroFilter;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.richardwilly98.esdms.services.inject.ProcessModule;
import com.google.inject.servlet.ServletModule;

public class EsJerseyServletModule extends ServletModule {

    private final Logger log = Logger.getLogger(this.getClass());

    public EsJerseyServletModule(String securityFilterPath) { }

    @Override
    protected void configureServlets() {
        log.debug("*** configureServlets ***");
        install();
        bindings();
        filters();
    }

    /*
     * Install modules
     */
    private void install() {
        log.debug("*** install ***");
        /* bind services */
        install(new ProviderModule());
        install(new ProcessModule());
    }

    private void filters() {
        filter("/api/*").through(GuiceShiroFilter.class);
    }

    private void bindings() {
        /* bind jackson converters for JAXB/JSON serialization */
        bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
        bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
    }

}
