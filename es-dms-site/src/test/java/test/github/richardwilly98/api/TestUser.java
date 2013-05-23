package test.github.richardwilly98.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.richardwilly98.api.User;

public class TestUser extends User {

	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "password")
	@Override
	public String getPassword() {
		return super.getPassword();
	}
}
