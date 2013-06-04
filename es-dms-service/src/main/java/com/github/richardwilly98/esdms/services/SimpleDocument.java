package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.api.Document;



class SimpleDocument extends DocumentImpl {
	
	private static final long serialVersionUID = 1L;

	public static class Builder extends DocumentImpl.Builder {

		private Document document;

		public Builder document(Document document) {
			this.document = document;
			return this;
		}

		public SimpleDocument build() {
			this
				.file(document.getFile())
				.versionId(document.getVersionId())
				.tags(document.getTags())
//				.roles(document.get)
//				.attachments(document.get)
//				.annotations(document.geta)
//				.comments(document.get)
				.id(document.getId())
				.name(document.getName())
				.disabled(document.isDisabled())
				.description(document.getDescription())
				.attributes(document.getAttributes());
			return new SimpleDocument(this);
		}
	}

	SimpleDocument() {
		this(null);
	}
	
	SimpleDocument(final Builder builder) {
		super(builder);
	}
	
	protected void setReadOnlyAttribute(String name, Object value) {
		super.setReadOnlyAttribute(name, value);
	}
	
	protected void removeReadOnlyAttribute(String name) {
		setReadOnlyAttribute(name, null);
	}
	
}
