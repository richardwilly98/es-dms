package com.github.richardwilly98.api;

public class Permission extends ItemBase{
	
	private static final long serialVersionUID = 1L;
	String access;
	Object property;	//property access permission is applied to
	
	public Permission(){
		
	}
	
	public Permission(String access, Object property){
		
	}
	
	public void setAccess(String access){
		this.access = access;
	}
	
	public void setProperty(Object property){
		this.property = property;
	}
	
	public void set(String access, Object property){
		this.access = access;
		this.property = property;
	}
	
	public String getAccess(){
		return access;
	}
	
	public Object getProperty(){
		return this.property;
	}
}
