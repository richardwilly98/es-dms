package com.github.richardwilly98.esdms.inject;

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

import java.util.Map;

import com.github.richardwilly98.esdms.ParameterImpl;
import com.github.richardwilly98.esdms.api.Parameter;
import com.github.richardwilly98.esdms.api.Parameter.ParameterType;
import com.github.richardwilly98.esdms.exception.ServiceException;
import com.github.richardwilly98.esdms.services.BootstrapService;
import com.github.richardwilly98.esdms.services.ParameterService;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

/*
 * Read initial system parameter from this class and then inject them using named binding.
 */
public class SystemParametersModule extends AbstractModule {

    public static final String PREVIEW_LENGTH = "preview.length";
    public static final int DEFAULT_PREVIEW_LENGTH = 1024;

    @Override
    protected void configure() {

    }

    @Provides
    @Named(PREVIEW_LENGTH)
    int providePreviewLength(BootstrapService bootstrapService, ParameterService service) {
        int previewLength = DEFAULT_PREVIEW_LENGTH;
        try {
            String library = bootstrapService.loadSettings().getLibrary();
            Parameter parameter = service.get(library);
            if (parameter == null) {
                Map<String, Object> attributes = ImmutableMap.of(PREVIEW_LENGTH, (Object) DEFAULT_PREVIEW_LENGTH);
                parameter = new ParameterImpl.Builder().id(library).name("System Parameter for " + library).type(ParameterType.SYSTEM).attributes(attributes)
                        .build();
                service.create(parameter);
            }
            if (parameter.getAttributes().containsKey(PREVIEW_LENGTH)) {
                previewLength = Integer.parseInt(String.valueOf(parameter.getAttributes().get(PREVIEW_LENGTH)));
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return previewLength;
    }

}
