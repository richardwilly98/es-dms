package com.github.richardwilly98.esdms;

/*
 * #%L
 * es-dms-core
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import com.github.richardwilly98.esdms.api.Facet;
import com.github.richardwilly98.esdms.api.Term;
import com.google.common.base.Objects;
import com.google.common.primitives.Longs;

public class FacetImpl implements Facet {

	private final Set<Term> terms = newHashSet();
	private long missingCount;
	private long otherCount;
	private long totalCount;

	public static class Builder {

		private final Set<Term> terms = newHashSet();
		private long missingCount;
		private long otherCount;
		private long totalCount;

		public Builder terms(Set<Term> terms) {
			if (terms != null) {
				this.terms.addAll(terms);
			}
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

	FacetImpl() {
		this(null);
	}

	protected FacetImpl(Builder builder) {
		if (builder != null) {
			this.terms.addAll(builder.terms);
			this.missingCount = builder.missingCount;
			this.otherCount = builder.otherCount;
			this.totalCount = builder.totalCount;
		}
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

	void setMissingCount(long missingCount) {
		this.missingCount = missingCount;
	}

	void setOtherCount(long otherCount) {
		this.otherCount = otherCount;
	}

	void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		FacetImpl obj2 = (FacetImpl) obj;
		return ((missingCount == obj2.getMissingCount())
				&& (otherCount == obj2.getOtherCount())
				&& (totalCount == obj2.getTotalCount()) && (terms == obj2
				.getTerms() || (terms != null && terms.equals(obj2.getTerms()))));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// INFO: http://stackoverflow.com/questions/4045063/how-should-i-map-long-to-int-in-hashcode
		result = prime * result + Longs.hashCode(missingCount);
		result = prime * result + Longs.hashCode(otherCount);
		result = prime * result + Longs.hashCode(totalCount);
		result = prime * result + ((terms == null) ? 0 : terms.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("terms", terms)
				.add("missingCount", missingCount)
				.add("otherCount", otherCount).add("totalCount", totalCount)
				.toString();
	}

}
