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


import com.github.richardwilly98.esdms.api.Permission;
import com.google.common.base.Strings;

public class PermissionImpl extends ItemBaseImpl implements Permission {

	private static final long serialVersionUID = 1L;
	private String access;

	public static class Builder extends BuilderBase<Builder> {

		String access;

		public Builder access(String access) {
			this.access = access;
			return getThis();
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		public Permission build() {
			if (Strings.isNullOrEmpty(this.name)) {
				this.name = this.id;
			} else if (Strings.isNullOrEmpty(this.id)) {
				this.id = this.name;
			}
			return new PermissionImpl(this);
		}
	}

	PermissionImpl() {
		super(null);
	}

	protected PermissionImpl(Builder builder) {
		super(builder);
		if (builder != null) {
			this.access = builder.access;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.Permission#setAccess(java.lang.String)
	 */
	@Override
	public void setAccess(String access) {
		this.access = access;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.Permission#getAccess()
	 */
	@Override
	public String getAccess() {
		return access;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		PermissionImpl obj2 = (PermissionImpl) obj;
		return (super.equals(obj) && (access == obj2.getAccess() || (access != null && access
				.equals(obj2.getAccess()))));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((access == null) ? 0 : access.hashCode());
		return result;
	}
}
