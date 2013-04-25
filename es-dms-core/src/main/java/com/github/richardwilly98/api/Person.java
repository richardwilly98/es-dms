package com.github.richardwilly98.api;

public class Person extends ItemBase {
	
	String city;
	String email;

	public Person() {
		
	}
	
	public Person(String id, String name, String city, String email) {
		this.id = id;
		this.name = name;
		this.city = city;
		this.email = email;
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
