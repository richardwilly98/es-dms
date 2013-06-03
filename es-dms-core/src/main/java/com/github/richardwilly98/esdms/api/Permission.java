package com.github.richardwilly98.esdms.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.PermissionImpl;

@JsonDeserialize(as = PermissionImpl.class)
public interface Permission extends ItemBase {

	public abstract void setAccess(String access);

	public abstract String getAccess();

}