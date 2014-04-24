DDSL plugin for Dropwizard
===============================

This is a plugin for Dropwizard that makes it really easy to enable Service Discovery to your
services using [DDSL](https://github.com/mbknor/ddsl).


Note about versions
-----------------------

Use version 2.0 if you are using Dropwizard 0.7.0 and version 1.0 if using Dropwizard 0.6.1

Example
-----------------------------

Have a look at the Dropwizard [example](https://github.com/mbknor/ddsl-dropwizard/tree/master/example) application.

How to use it in your own code
=============================

Maven dependency
------------------------------

You need to add the following dependency to your pom.xml:

        <dependency>
            <groupId>com.kjetland.ddsl</groupId>
            <artifactId>ddsl-dropwizard</artifactId>
            <version>2.0</version>
        </dependency>


Since DDSL and this plugin is hosted on my own maven repo here on github, you also have to add this to your pom:

        <repositories>
            <repository>
                <id>mbknor</id>
                <name>mbknor</name>
                <url>http://mbknor.github.com/m2repo/releases</url>
            </repository>
        </repositories>



Announce your service
-------------------------------

This is how easy it is to enable Service Discovery announcing for your Dropwizard service


Add DdslConfig to your configuration class:

    public class HelloWorldConfiguration extends Configuration {

        @JsonProperty
        @Valid
        @NotNull
        public DdslConfig ddslConfig;

    }


Configure it like this in your yml-file:

    ddslConfig:
      zookeeperHosts: localhost:2181
      serviceId:
        environment: test
        name: ddsl-example
        version: 1.0


Initialize it in your Service class:

        @Override
        public void run(HelloWorldConfiguration config, Environment environment) throws Exception {
            DdslService ddslService = new DdslService( config.ddslConfig);
            environment.lifecycle().addServerLifecycleListener( ddslService );
        }


When your app starts, it will resolve the url to your app and register it in DDSL.
Now other apps can discover it using ddsl, or you can use [ddslConfigWriter](https://github.com/mbknor/ddslConfigWriter) to automatically (re)configure an nginx reverse-proxy
to start to send traffic to it. You can auto-scale.


Discover other services
------------------------

This is how easy it is to use DDSL to discover a valid url to the service you want to connect to - with automatic load-balancing.

From code:

    DdslServiceId otherService = new DdslServiceId( "test", "myService", "1.0 );
    String url = ddslService.getBestLocationUrl(otherService);


If you want to make it configurable in your yml file, add this to your config file:

    @JsonProperty
    @Valid
    public DdslServiceId otherService;



And configure it like this in yml:

    otherService:
        environment: test
        name: ddsl-example-other
        version: 1.0

