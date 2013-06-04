package com.github.richardwilly98.esdms;

import java.util.HashMap;
import java.util.Map;

import com.github.richardwilly98.esdms.api.Annotation;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.SecuredItem;

public class SecuredItemImpl extends ItemBaseImpl implements SecuredItem {

	private static final long serialVersionUID = 1L;

	Map<String, Role> roles;
	Map<String, SecuredItem> attachments;
	Map<String, Annotation> annotations;
	Map<String, _CommentImpl> comments;

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
			this.roles = builder.roles;
			this.annotations = builder.annotations;
			this.attachments = builder.attachments;
			this.comments = builder.comments;
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
		this.roles = roles;
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
		if (roles == null)
			roles = new HashMap<String, Role>();
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
		this.attachments = attachments;
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
		if (attachments == null)
			attachments = new HashMap<String, SecuredItem>();
		this.attachments.put(name, attachment);
	}
}
