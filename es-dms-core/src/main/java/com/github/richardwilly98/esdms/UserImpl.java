package com.github.richardwilly98.esdms;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.util.NoSuchElementException;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.richardwilly98.esdms.api.Role;
import com.github.richardwilly98.esdms.api.User;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

public class UserImpl extends PersonImpl implements User {

	private static final long serialVersionUID = 1L;

	private final Set<Role> roles = newHashSet();
	private String hash;
	private String password;
	@JsonIgnore
	private String login;

	public static class Builder extends PersonImpl.Builder<Builder> {

		private Set<Role> roles = newHashSet();
		private String hash;
		private String password;

		public Builder password(String password) {
			this.password = password;
			return getThis();
		}

		public Builder roles(Set<Role> roles) {
			this.roles = roles;
			return getThis();
		}

		public Builder hash(String hash) {
			this.hash = hash;
			return getThis();
		}

		@Override
		protected Builder getThis() {
			return this;
		}

		public UserImpl build() {
			return new UserImpl(this);
		}
	}

	UserImpl() {
		this(null);
	}

	protected UserImpl(Builder builder) {
		super(builder);
		if (builder != null) {
			this.password = builder.password;
			if (builder.roles != null) {
				this.roles.addAll(builder.roles);
			}
			this.hash = builder.hash;
			this.login = builder.email;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.User#getLogin()
	 */
	@Override
	public String getLogin() {
		return login;
	}

	@Override
	public void setEmail(String email) {
		login = email;
		super.setEmail(email);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.User#getRoles()
	 */
	@Override
	public Set<Role> getRoles() {
		return roles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.User#setRoles(java.util.Set)
	 */
	@Override
	public void setRoles(Set<Role> roles) {
		if (roles != null) {
			// if (this.roles == null) {
			// this.roles = new HashSet<Role>();
			// }
			this.roles.addAll(roles);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.User#getHash()
	 */
	@Override
	public String getHash() {
		return hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.User#setHash(java.lang.String)
	 */
	@Override
	public void setHash(String hash) {
		this.hash = hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.User#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.User#setPassword(java.lang.String)
	 */
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.api.User#addRole(com.github.richardwilly98.
	 * api.Role)
	 */
	@Override
	public void addRole(Role role) {
		if (role != null) {
			// if (this.roles == null) {
			// this.roles = new HashSet<Role>();
			// }
			roles.add(role);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.richardwilly98.api.User#removeRole(com.github.richardwilly98
	 * .api.Role)
	 */
	@Override
	public void removeRole(Role role) {
		if (role != null)
			roles.remove(role);
	}

	@Override
	@JsonIgnore
	public boolean hasRole(Role role) {
		try {
			checkNotNull(role);
			return Iterables.contains(roles, role);
		} catch (NoSuchElementException ex) {
			return false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		UserImpl obj2 = (UserImpl) obj;
		return (super.equals(obj)
				&& (hash == obj2.getHash() || (hash != null && hash.equals(obj2
						.getHash())))
				&& (login == obj2.getLogin() || (login != null && login
						.equals(obj2.getLogin()))) && (roles == obj2.getRoles() || (roles != null && roles
				.equals(obj2.getRoles()))));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		result = prime * result + ((login == null) ? 0 : login.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass()).add("id", getId())
				.add("name", getName()).add("login", login)
				.add("email", getEmail()).add("roles", getRoles()).toString();
	}
}
