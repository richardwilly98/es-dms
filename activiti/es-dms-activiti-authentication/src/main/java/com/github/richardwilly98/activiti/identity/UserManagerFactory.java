package com.github.richardwilly98.activiti.identity;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;

public class UserManagerFactory implements SessionFactory {

	private EsDmsConfigurator configurator;

	public UserManagerFactory(EsDmsConfigurator configurator) {
		this.configurator = configurator;
	}
	public EsDmsConfigurator getConfigurator() {
		return this.configurator;
	}
	public void setConfigurator(EsDmsConfigurator configurator) {
		this.configurator = configurator;
	}
	public Class<?> getSessionType() {
		return UserIdentityManager.class;
	}

	public Session openSession() {
		return new UserEntityManager(this.configurator);
	}

}
