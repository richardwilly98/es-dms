package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.github.richardwilly98.esdms.api.Facet;
import com.github.richardwilly98.esdms.api.Term;
import com.google.common.base.Objects;

public class FacetImpl implements Facet {

	private final Set<Term> terms = newHashSet();
	private final long missingCount;
	private final long otherCount;
	private final long totalCount;

	public static class Builder {

		private final Set<Term> terms = newHashSet();
		private long missingCount;
		private long otherCount;
		private long totalCount;

		public Builder terms(Set<Term> terms) {
			checkNotNull(terms);
			this.terms.addAll(terms);
			return this;
		}

		public Builder missingCount(long missingCount) {
			this.missingCount = missingCount;
			return this;
		}

		public Builder otherCount(long otherCount) {
			this.otherCount = otherCount;
			return this;
		}

		public Builder totalCount(long totalCount) {
			this.totalCount = totalCount;
			return this;
		}

		public FacetImpl build() {
			return new FacetImpl(this);
		}
	}

	public FacetImpl(Builder builder) {
		checkNotNull(builder);
		this.terms.addAll(builder.terms);
		this.missingCount = builder.missingCount;
		this.otherCount = builder.otherCount;
		this.totalCount = builder.totalCount;
	}

	@Override
	public Set<Term> getTerms() {
		return terms;
	}

	@Override
	public long getMissingCount() {
		return missingCount;
	}

	@Override
	public long getOtherCount() {
		return otherCount;
	}

	@Override
	public long getTotalCount() {
		return totalCount;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("terms", terms)
				.add("missingCount", missingCount)
				.add("otherCount", otherCount).add("totalCount", totalCount)
				.toString();
	}

}
