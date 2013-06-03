package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.Permission;

public class PermissionImpl extends ItemBaseImpl implements Permission {

	private static final long serialVersionUID = 1L;
	String access;

	public PermissionImpl() {
	}
	
	public PermissionImpl(String name) {
		super(name);
		setName(name);
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.Permission#setAccess(java.lang.String)
	 */
	@Override
	public void setAccess(String access) {
		this.access = access;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.Permission#getAccess()
	 */
	@Override
	public String getAccess() {
		return access;
	}
}
