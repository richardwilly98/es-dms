package com.github.richardwilly98.esdms.services;

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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.elasticsearch.common.io.Streams.copyToStringFromClasspath;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.beanutils.PropertyUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.github.richardwilly98.esdms.api.Parameter;
import com.github.richardwilly98.esdms.api.Parameter.ParameterType;
import com.github.richardwilly98.esdms.api.SearchResult;
import com.github.richardwilly98.esdms.exception.ServiceException;

@Singleton
public class ParameterProvider extends ProviderBase<Parameter> implements ParameterService {

    private static final String PARAMETER_MAPPING_JSON = "/com/github/richardwilly98/esdms/services/parameter-mapping.json";
    private final static String index = "system";
    private final static String type = "parameter";

    @Inject
    ParameterProvider(Client client, BootstrapService bootstrapService) throws ServiceException {
        super(client, bootstrapService, ParameterProvider.index, ParameterProvider.type, Parameter.class);
    }

    @Override
    protected void loadInitialData() throws ServiceException {
    }

    @Override
    protected String getMapping() {
        try {
            return copyToStringFromClasspath(PARAMETER_MAPPING_JSON);
        } catch (IOException ioEx) {
            log.error("getMapping failed", ioEx);
            return null;
        }
    }

    @Override
    public SearchResult<Parameter> findByType(ParameterType type, int first, int pageSize) throws ServiceException {
        QueryBuilder query = QueryBuilders.matchQuery("type", type.getType());
        SearchResult<Parameter> searchResult = search(query, first, pageSize);
        return searchResult;
    }

    /*
     * (non-Javadoc)
     * @see com.github.richardwilly98.esdms.services.ParameterService#getParameterValue(java.lang.String)
     * Follow syntax from http://commons.apache.org/proper/commons-beanutils/apidocs/org/apache/commons/beanutils/package-summary.html#standard.background
     * For example: parameter-id.name, parameter-id.attributes, parameter-id.attributes(key) ...
     */
    @Override
    public Object getParameterValue(String name) throws ServiceException {
        checkNotNull(name);
        if (name.contains(".")) {
            String id = name.substring(0, name.indexOf("."));
            String propertyName = name.substring(name.indexOf(".") + 1);
            Parameter parameter = super.get(id);
            try {
                return PropertyUtils.getProperty(parameter, propertyName);
            } catch (Exception ex) {
                log.error(String.format("getParameterValue %s failed", name), ex);
                throw new ServiceException(ex.getLocalizedMessage());
            }
        } else
            return super.get(name);
    }

    @Override
    public Parameter setParameterValue(Parameter parameter, String name, Object value) throws ServiceException {
        checkNotNull(parameter);
        checkNotNull(name);
        try {
            PropertyUtils.setProperty(parameter, name, value);
            return update(parameter);
        } catch (Exception ex) {
            log.error(String.format("setParameterValue %s - %s failed", parameter, name), ex);
            throw new ServiceException(ex.getLocalizedMessage());
        }
    }
}
