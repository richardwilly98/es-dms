package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.File;

public class _VersionImpl extends _ContainerImpl{
	
	private static final long serialVersionUID = 4697283267229534468L;
	String versionId;
	File content;
	boolean current;
	
	public _VersionImpl(String versionId){
		this(versionId, false);
	}
	
	public _VersionImpl(String versionId, boolean current){
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
