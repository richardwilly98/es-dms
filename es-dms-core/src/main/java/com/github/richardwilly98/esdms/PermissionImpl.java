package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.Permission;
import com.google.common.base.Strings;

public class PermissionImpl extends ItemBaseImpl implements Permission {

	private static final long serialVersionUID = 1L;
	String access;

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

	// public PermissionImpl(String name) {
	// super(name);
	// setName(name);
	// }

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
}
