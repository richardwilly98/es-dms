package com.github.richardwilly98.api;

public class Person extends ItemBase {

	String name;
	String city;

	public Person() {
		
	}
	
	public Person(String id, String name, String city) {
		this.id = id;
		this.name = name;
		this.city = city;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
}
