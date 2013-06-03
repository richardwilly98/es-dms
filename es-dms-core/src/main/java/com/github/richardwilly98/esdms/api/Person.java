package com.github.richardwilly98.esdms.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.richardwilly98.esdms.PersonImpl;

@JsonDeserialize(as = PersonImpl.class)
public interface Person extends ItemBase {

	public abstract String getCity();

	public abstract void setCity(String city);

	public abstract String getEmail();

	public abstract void setEmail(String email);

}