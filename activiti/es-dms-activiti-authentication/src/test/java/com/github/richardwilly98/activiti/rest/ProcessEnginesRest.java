package com.github.richardwilly98.activiti.rest;

import org.activiti.engine.ProcessEngines;

public class ProcessEnginesRest extends ProcessEngines {
    public synchronized static void init() {
        isInitialized = true;
    }
}
