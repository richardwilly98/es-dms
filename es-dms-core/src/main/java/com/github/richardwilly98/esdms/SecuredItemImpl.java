package com.github.richardwilly98.esdms;

import java.util.HashMap;
import java.util.Map;

import com.github.richardwilly98.esdms.api.Annotation;
import com.github.richardwilly98.esdms.api.SecuredItem;

public class SecuredItemImpl extends ItemBaseImpl implements SecuredItem {
	
	private static final long serialVersionUID = 1L;
	Map<String, RoleImpl> roles;
	
	Map<String, SecuredItem> attachments;
	Map<String, Annotation> annotations;
	Map<String, _CommentImpl> comments;
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.SecuredItem#getRoles()
	 */
	@Override
	public Map<String, RoleImpl> getRoles() {
		return roles;
	}
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.SecuredItem#setPermissions(java.util.Map)
	 */
	@Override
	public void setPermissions(Map<String, RoleImpl> roles) {
		this.roles = roles;
	}
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.SecuredItem#setRole(java.lang.String, com.github.richardwilly98.esdms.Role)
	 */
	@Override
	public void setRole(String name, RoleImpl role){
		if (roles == null) roles = new HashMap <String, RoleImpl>();
		this.roles.put(name, role);
	}
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.SecuredItem#getAttachments()
	 */
	@Override
	public Map<String, SecuredItem> getAttachments() {
		return attachments;
	}
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.SecuredItem#setAttachments(java.util.Map)
	 */
	@Override
	public void setAttachments(Map<String, SecuredItem> attachments) {
		this.attachments = attachments;
	}
	
	/* (non-Javadoc)
	 * @see com.github.richardwilly98.esdms.SecuredItem#setAttachment(java.lang.String, com.github.richardwilly98.esdms.SecuredItemImpl)
	 */
	@Override
	public void setAttachment(String name, SecuredItem attachment){
		if (attachments == null) attachments = new HashMap <String, SecuredItem>();
		this.attachments.put(name, attachment);
	}
}
