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


import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import com.github.richardwilly98.esdms.api.Annotation;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SecuredItem;

public class SecuredItemImpl extends ItemBaseImpl implements SecuredItem {

	private static final long serialVersionUID = 1L;

	private final Map<String, Role> roles = newHashMap();
	private final Map<String, SecuredItem> attachments = newHashMap();
	private final Map<String, Annotation> annotations = newHashMap();
	private final Map<String, _CommentImpl> comments = newHashMap();

	public static abstract class Builder<T extends Builder<T>> extends
			ItemBaseImpl.BuilderBase<Builder<T>> {

		Map<String, Role> roles;
		Map<String, SecuredItem> attachments;
		Map<String, Annotation> annotations;
		Map<String, _CommentImpl> comments;

		public T roles(Map<String, Role> roles) {
			this.roles = roles;
			return getThis();
		}

		public T attachments(Map<String, SecuredItem> attachments) {
			this.attachments = attachments;
			return getThis();
		}

		public T annotations(Map<String, Annotation> annotations) {
			this.annotations = annotations;
			return getThis();
		}

		public T comments(Map<String, _CommentImpl> comments) {
			this.comments = comments;
			return getThis();
		}

		@Override
		protected abstract T getThis();

		// public SecuredItemImpl build() {
		// return new SecuredItemImpl(this);
		// }
	}

	protected SecuredItemImpl(Builder<?> builder) {
		super(builder);
		if (builder != null) {
			if (builder.roles != null) {
				this.roles.putAll(builder.roles);
			}
			if (builder.annotations != null) {
				this.annotations.putAll(builder.annotations);
			}
			if (builder.attachments != null) {
				this.attachments.putAll(builder.attachments);
			}
			if (builder.comments != null) {
				this.comments.putAll(builder.comments);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.esdms.SecuredItem#getRoles()
	 */
	@Override
	public Map<String, Role> getRoles() {
		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.esdms.SecuredItem#setPermissions(java.util.Map)
	 */
	@Override
	public void setPermissions(Map<String, Role> roles) {
		if (roles != null) {
			this.roles.putAll(roles);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.esdms.SecuredItem#setRole(java.lang.String,
	 * com.github.richardwilly98.esdms.Role)
	 */
	@Override
	public void setRole(String name, Role role) {
		// if (roles == null)
		// roles = new HashMap<String, Role>();
		this.roles.put(name, role);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.esdms.SecuredItem#getAttachments()
	 */
	@Override
	public Map<String, SecuredItem> getAttachments() {
		return attachments;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.esdms.SecuredItem#setAttachments(java.util.Map)
	 */
	@Override
	public void setAttachments(Map<String, SecuredItem> attachments) {
		if (attachments != null) {
			this.attachments.putAll(attachments);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.esdms.SecuredItem#setAttachment(java.lang.String
	 * , com.github.richardwilly98.esdms.SecuredItemImpl)
	 */
	@Override
	public void setAttachment(String name, SecuredItem attachment) {
//		if (attachments == null)
//			attachments = new HashMap<String, SecuredItem>();
		this.attachments.put(name, attachment);
	}
}
