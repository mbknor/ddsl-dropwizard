package com.kjetland.ddsl.dropwizard_example;

import com.kjetland.ddsl.dropwizard.DdslService;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloWorldService extends Application<HelloWorldConfiguration> {

    public static void main(String[] args) throws Exception {
        new HelloWorldService().run(args);
    }

    private HelloWorldService() {

    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> helloWorldConfigurationBootstrap) {
    }

    @Override
    public void run(HelloWorldConfiguration helloWorldConfiguration, Environment environment) throws Exception {
        DdslService ddslService = new DdslService( helloWorldConfiguration.ddslConfig);
        environment.lifecycle().addServerLifecycleListener( ddslService );

        environment.jersey().register( new HelloWorldResource(helloWorldConfiguration, ddslService) );
    }


}