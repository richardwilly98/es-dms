package com.github.richardwilly98.activiti.rest;

import org.activiti.rest.service.application.ActivitiRestServicesApplication;

public class EsDmsActivitiRestServicesApplication extends ActivitiRestServicesApplication {

    public EsDmsActivitiRestServicesApplication() {
        this.restAuthenticator = new EsDmsRestAuthenticator();
    }

}
