package com.github.richardwilly98.esdms.services.inject;

import org.apache.log4j.Logger;

import com.github.richardwilly98.esdms.services.ProcessService;
import com.github.richardwilly98.esdms.services.ProcessServiceProvider;
import com.google.inject.AbstractModule;

public class ProcessModule extends AbstractModule {
    private static final Logger log = Logger.getLogger(ProcessModule.class);
    @Override
    protected void configure() {
        log.info("*** configure ***");
        bind(ProcessService.class).to(ProcessServiceProvider.class).asEagerSingleton();
    }

}
