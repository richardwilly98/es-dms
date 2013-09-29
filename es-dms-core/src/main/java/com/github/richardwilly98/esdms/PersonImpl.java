package com.github.richardwilly98.esdms;

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

import com.github.richardwilly98.esdms.api.Person;

public class PersonImpl extends ItemBaseImpl implements Person {

    private static final long serialVersionUID = 1L;
    private String city;
    private String email;

    public static abstract class Builder<T extends Builder<T>> extends ItemBaseImpl.BuilderBase<Builder<T>> {

	String city;
	String email;

	public T city(String city) {
	    this.city = city;
	    return getThis();
	}

	public T email(String email) {
	    this.email = email;
	    return getThis();
	}

	@Override
	protected abstract T getThis();

	public PersonImpl build() {
	    return new PersonImpl(this);
	}
    }

    protected PersonImpl(Builder<?> builder) {
	super(builder);
	if (builder != null) {
	    this.city = builder.city;
	    this.email = builder.email;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.Person#getCity()
     */
    @Override
    public String getCity() {
	return city;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.Person#setCity(java.lang.String)
     */
    @Override
    public void setCity(String city) {
	this.city = city;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.Person#getEmail()
     */
    @Override
    public String getEmail() {
	return email;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.richardwilly98.api.Person#setEmail(java.lang.String)
     */
    @Override
    public void setEmail(String email) {
	this.email = email;
    }

}
