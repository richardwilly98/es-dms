package com.github.richardwilly98.activiti.identity;

import java.io.Serializable;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurator;
import org.apache.log4j.Logger;

public class EsDmsConfigurator implements ProcessEngineConfigurator, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4191243756715104057L;
	private static final Logger log = Logger.getLogger(EsDmsConfigurator.class);
	
	private String url;
	private String userId;
	private String password;
	
	private UserManagerFactory userManagerFactory;
	private GroupManagerFactory groupManagerFactory;
	public void configure(
			ProcessEngineConfigurationImpl processEngineConfiguration) {
		log.debug("*** configure ***");
		UserManagerFactory userManagerFactory = getUserManagerFactory();
		processEngineConfiguration.getSessionFactories().put(userManagerFactory.getSessionType(), userManagerFactory);
		GroupManagerFactory groupManagerFactory = getGroupManagerFactory();
		processEngineConfiguration.getSessionFactories().put(groupManagerFactory.getSessionType(), groupManagerFactory);
		
	}
	
	private UserManagerFactory getUserManagerFactory() {
		log.debug("*** getUserManagerFactory ***");
		if (userManagerFactory != null) {
			userManagerFactory.setConfigurator(this);
			return userManagerFactory;
		}
		return new UserManagerFactory(this);
	}

	private GroupManagerFactory getGroupManagerFactory() {
		log.debug("*** getGroupManagerFactory ***");
		if (groupManagerFactory != null) {
			groupManagerFactory.setConfigurator(this);
			return groupManagerFactory;
		}
		return new GroupManagerFactory(this);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
