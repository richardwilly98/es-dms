package com.github.richardwilly98.api;

public class Version extends Container{
	
	private static final long serialVersionUID = 4697283267229534468L;
	String versionId;
	File content;
	boolean current;
	
	public Version(String versionId){
		this(versionId, false);
	}
	
	public Version(String versionId, boolean current){
		super();
		this.versionId = versionId;
		this.current = current;
	}
	
	public String getVersionId(){
		return versionId;
	}
	
	public void setContent(File content){
		this.content = content;
	}
	
	public File getContent(){
		return content;
	}
	
	public void setCurrent(boolean current){
		this.current = current;
	}
	
	public boolean isCurrent(){
		return current;
	}
}
