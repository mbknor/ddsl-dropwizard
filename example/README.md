Example Dropwizard service using DDSL
======================================

Make sure zookeeper is running on localhost.

start the main service listening on 8080:

    java -jar target/dropwizard-example-1.0.jar server config.yml

Start one other service listening on 8081:

    java -jar target/dropwizard-example-1.0.jar server config2.yml

Start another other service listening on 8082:

    java -jar target/dropwizard-example-1.0.jar server config3.yml


All apps has two urls:

    GET /
    GET /fromOther


Main service uses one of other-servicer on /fromOther - refresh multiple times - see that it changes which service is used..

Try to change ports and see that it automatically still works.

Other-servicees uses main-service on /fromOther