package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.esdms.api.File;

@JsonInclude(Include.NON_NULL)
public class FileImpl implements Serializable, File {

	private static final long serialVersionUID = 1L;
	@JsonProperty("content")
	byte[] content;

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

	public FileImpl() {
		this(new byte[0], "", "");
	}

	public FileImpl(byte[] content, String name, String contentType) {
		checkNotNull(content);
		this.content = content;
		this.name = name;
		this.contentType = contentType;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#getContent()
	 */
	@Override
	public byte[] getContent() {
		return content;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#setContent(byte[])
	 */
	@Override
	public void setContent(byte[] content) {
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#getContentType()
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#getHighlight()
	 */
	@Override
	public String getHighlight() {
		return highlight;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#setHighlight(java.lang.String)
	 */
	@Override
	public void setHighlight(String highlight) {
		this.highlight = highlight;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#getDate()
	 */
	@Override
	public DateTime getDate() {
		return date;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#setDate(org.joda.time.DateTime)
	 */
	@Override
	public void setDate(DateTime date) {
		this.date = date;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#getTitle()
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return author;
	}

	/* (non-Javadoc)
	 * @see com.github.richardwilly98.api.File#setAuthor(java.lang.String)
	 */
	@Override
	public void setAuthor(String author) {
		this.author = author;
	}

}
