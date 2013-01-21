package com.kjetland.ddsl.dropwizard_example;

import com.kjetland.ddsl.dropwizard.DdslService;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class HelloWorldService extends Service<HelloWorldConfiguration> {

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
        DdslService ddslService = new DdslService( helloWorldConfiguration.getHttpConfiguration(), helloWorldConfiguration.ddslConfig);
        environment.addResource( new HelloWorldResource(helloWorldConfiguration, ddslService) );
        environment.manage( ddslService );
    }


}