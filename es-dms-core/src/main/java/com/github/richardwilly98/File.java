package com.github.richardwilly98;

import com.fasterxml.jackson.annotation.JsonProperty;

public class File {

	@JsonProperty("content")
	String content;
	
	@JsonProperty("_name")
	String name;
	
	@JsonProperty("_content_type")
	String contentType;

	public File() {}
	
	public File(String content, String name, String contentType) {
		this.content = content;
		this.name = name;
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
