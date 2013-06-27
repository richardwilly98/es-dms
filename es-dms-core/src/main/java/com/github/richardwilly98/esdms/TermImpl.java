package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.richardwilly98.esdms.api.Term;
import com.google.common.base.Objects;

public class TermImpl implements Term {

	private final String term;
	private final int count;

	public static class Builder {

		private String term;
		private int count;

		public Builder term(String term) {
			this.term = term;
			return this;
		}

		public Builder count(int count) {
			this.count = count;
			return this;
		}

		public TermImpl build() {
			return new TermImpl(this);
		}
	}

	public TermImpl(Builder builder) {
		checkNotNull(builder);
		this.term = builder.term;
		this.count = builder.count;
	}

	@Override
	public String getTerm() {
		return term;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("term", term)
				.add("count", count).toString();
	}
}
