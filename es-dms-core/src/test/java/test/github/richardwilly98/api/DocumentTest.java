package test.github.richardwilly98.api;

import com.github.richardwilly98.api.Document;

public class DocumentTest extends Document {

	private static final long serialVersionUID = 1L;

	public DocumentTest() {
		
	}
	
	public DocumentTest(Document document) {
		super(document);
	}
	
	@Override
	protected void setReadOnlyAttribute(String name, Object value) {
		super.setReadOnlyAttribute(name, value);
	}
}
