package com.github.richardwilly98.esdms;

import com.github.richardwilly98.esdms.api.Person;

public class PersonImpl extends ItemBaseImpl implements Person {

	private static final long serialVersionUID = 1L;
	private String city;
	private String email;

	public static abstract class Builder<T extends Builder<T>>
			extends ItemBaseImpl.BuilderBase<Builder<T>> {

		String city;
		String email;

		public T city(String city) {
			this.city = city;
			return getThis();
		}

		public T email(String email) {
			this.email = email;
			return getThis();
		}

		@Override
		protected abstract T getThis();
		
//		public PersonImpl build(){
//            return new PersonImpl(this);
//        }
	}

	protected PersonImpl(Builder<?> builder) {
		super(builder);
		if (builder != null) {
			this.city = builder.city;
			this.email = builder.email;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.Person#getCity()
	 */
	@Override
	public String getCity() {
		return city;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.Person#setCity(java.lang.String)
	 */
	@Override
	public void setCity(String city) {
		this.city = city;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.Person#getEmail()
	 */
	@Override
	public String getEmail() {
		return email;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.richardwilly98.api.Person#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

}
