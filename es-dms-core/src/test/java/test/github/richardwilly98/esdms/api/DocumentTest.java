package test.github.richardwilly98.esdms.api;

import com.github.richardwilly98.esdms.DocumentImpl;

public class DocumentTest extends DocumentImpl {

	private static final long serialVersionUID = 1L;

	DocumentTest() {
		this(null);
	}
	
	public DocumentTest(Builder builder) {
		super(builder);
	}
	
	@Override
	protected void setReadOnlyAttribute(String name, Object value) {
		super.setReadOnlyAttribute(name, value);
	}
}
