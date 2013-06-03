package test.github.richardwilly98.esdms.api;

import com.github.richardwilly98.esdms.DocumentImpl;
import com.github.richardwilly98.esdms.api.Document;

public class DocumentTest extends DocumentImpl {

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
