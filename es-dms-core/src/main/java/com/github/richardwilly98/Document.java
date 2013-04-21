package com.github.richardwilly98;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Document extends ItemBase {

	public final static String CREATION_DATE = "creation";
	public final static String MODIFIED_DATE = "modified";
	public final static String AUTHOR = "author";
	public final static String STATUS = "status";
	
	@JsonProperty("file")
	File file;
	
//	Map<String, Serializable> attributes = new HashMap<String, Serializable>();
	
	public Document() {
		
	}
	
	public Document(String id, File file) {
		this.id = id;
		this.file = file;
	}

//	public Document(String id, File file) {
//		this(id, file, null);
//	}
	
//	public Document(String id, File file, Map<String, Serializable> attributes) {
//		this.id = id;
//		this.file = file;
//		this.attributes = attributes;
//	}

	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
//	public Map<String, Serializable> getAttributes() {
//		return attributes;
//	}
	
}
