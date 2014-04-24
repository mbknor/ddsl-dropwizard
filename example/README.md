Example Dropwizard service using DDSL
======================================

Make sure zookeeper is running on localhost.

Build and package the example app:

    mvn package

start the main service listening on 8080:

    java -jar target/dropwizard-example-2.0.jar server config.yml

Start one other service listening on random port:

    java -jar target/dropwizard-example-2.0.jar server config2.yml

Start as many other services as you like..

All apps has two urls:

    GET /
    GET /fromOther


The main service uses HttpClient to call one of the other-services on /fromOther - refresh multiple times - see that it changes which service is used..

Try to change ports and see that it automatically still works.

Other-services uses main-service on /fromOther