package com.github.richardwilly98.esdms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.richardwilly98.esdms.api.Container;
import com.github.richardwilly98.esdms.api.SecuredItem;

public class _ContainerImpl extends SecuredItemImpl implements Container {
	
	protected _ContainerImpl(Builder<?> builder) {
		super(builder);
		// TODO Auto-generated constructor stub
	}

	@JsonIgnore
	SecuredItem parent;

	private static final long serialVersionUID = 1L;

}
