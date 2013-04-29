package com.github.richardwilly98.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Document extends SecuredItem {

	private static final long serialVersionUID = 1L;
	public final static String CREATION_DATE = "creation";
	public final static String MODIFIED_DATE = "modified";
	public final static String AUTHOR = "author";
	public final static String STATUS = "status";
	
	@JsonProperty("file")
	File file;
	
	Map<String, Object> attributes = new HashMap<String, Object>();
	
	public Document() {
		super();
	}
	
	public Document(String id, File file) {
		this(id, file, null);
	}
	
	public Document(String id, File file, Map<String, Object> attributes) {
		if (file == null) {
			file = new File();
		}
		this.id = id;
		this.file = file;
		this.attributes = attributes;
	}

	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public void setAttribute(String name, Object attribute){
		if (this.attributes == null) this.attributes = new HashMap<String, Object>();
		this.attributes.put(name, attribute);
	}
}
