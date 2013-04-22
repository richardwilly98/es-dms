package com.github.richardwilly98.api;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonIgnore
	String highlight;
	
	@JsonProperty("date")
	DateTime date;
	
	@JsonProperty("title")
	String title;
	
	@JsonProperty("author")
	String author;
	
	public File() {
		this("", "", "");
	}
	
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
	public DateTime getDate() {
		return date;
	}
	public void setDate(DateTime date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
}
