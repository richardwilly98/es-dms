package com.github.richardwilly98.activiti.identity;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;

public class GroupManagerFactory implements SessionFactory {

	private EsDmsConfigurator configurator;

	public GroupManagerFactory(EsDmsConfigurator configurator) {
		this.configurator = configurator;
	}
	public void setConfigurator(EsDmsConfigurator configurator) {
		this.configurator = configurator;
	}
	public EsDmsConfigurator getConfiguration() {
		return this.configurator;
	}
	public Class<?> getSessionType() {
		return GroupIdentityManager.class;
	}

	public Session openSession() {
		return new GroupEntityManager(configurator);
	}

}
