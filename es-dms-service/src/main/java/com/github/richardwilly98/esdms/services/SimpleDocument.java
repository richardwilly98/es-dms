package com.github.richardwilly98.esdms.services;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.api.Document;



class SimpleDocument extends DocumentImpl {
	
	private static final long serialVersionUID = 1L;

	public SimpleDocument() {
	}
	
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
