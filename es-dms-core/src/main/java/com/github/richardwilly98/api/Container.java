package com.github.richardwilly98.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Container extends SecuredItem{
	
	@JsonIgnore
	SecuredItem parent;

	private static final long serialVersionUID = 1L;

}
