package com.github.richardwilly98.api;

public class Person extends ItemBase {
	
	String city;

	public Person() {
		
	}
	
	public Person(String id, String name, String city) {
		this.id = id;
		this.name = name;
		this.city = city;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
}
