package com.github.richardwilly98.services;

import com.github.richardwilly98.api.Document;


class SimpleDocument extends Document {
	
	private static final long serialVersionUID = 1L;

	SimpleDocument(final Document document) {
		super(document);
	}
	
	protected void setReadOnlyAttribute(String name, Object value) {
		super.setReadOnlyAttribute(name, value);
	}
	
	protected void removeReadOnlyAttribute(String name) {
		setReadOnlyAttribute(name, null);
	}
	
}
