package com.kjetland.ddsl.dropwizard_example;

import com.google.common.base.Optional;
import com.kjetland.ddsl.utils.NetUtils;
import com.yammer.dropwizard.config.HttpConfiguration;
import com.yammer.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    private final int port;

    public HelloWorldResource(HttpConfiguration httpConfiguration) {
        this.port = httpConfiguration.getPort();
    }

    @GET
    public String sayHello() {
        return "Hello world from " + NetUtils.resolveLocalPublicIP() + ":" + port;
    }
}