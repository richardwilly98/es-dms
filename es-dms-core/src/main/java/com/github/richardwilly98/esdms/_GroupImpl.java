package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.Group;

public class _GroupImpl extends ItemBaseImpl implements Group {

	public static class Builder extends BuilderBase<Builder> {

		@Override
		protected Builder getThis() {
			return this;
		}

		public _GroupImpl build() {
			return new _GroupImpl(this);
		}
	}

	protected _GroupImpl(Builder builder) {
		super(builder);
	}

	private static final long serialVersionUID = 1L;

}
