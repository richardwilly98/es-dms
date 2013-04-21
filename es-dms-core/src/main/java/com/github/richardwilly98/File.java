package com.github.richardwilly98;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class File {

	@JsonProperty("content")
	String content;
	
	@JsonProperty("_name")
	String name;
	
	@JsonProperty("_content_type")
	String contentType;

//	@JsonIgnore
	String highlight;
	
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
	public String getHighlight() {
		return highlight;
	}
	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}
}
