package com.github.richardwilly98.esdms.api;

import java.util.Set;

public interface Facet {

	public abstract Set<Term> getTerms();
	
	public abstract long getMissingCount();
	
	public abstract long getOtherCount();
	
	public abstract long getTotalCount();
}
