package com.github.richardwilly98.inject;

import com.github.richardwilly98.api.User;
import com.google.inject.Provider;

public class UserProvider implements Provider<User> {

	@Override
	public User get() {
		User user = new User();
		return user;
	}

}
