package com.kjetland.ddsl.dropwizard_example;

import com.google.common.base.Optional;
import com.kjetland.ddsl.dropwizard.DdslService;
import com.kjetland.ddsl.dropwizard.DdslServiceId;
import com.kjetland.ddsl.utils.NetUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final HelloWorldConfiguration config;
    private final DdslService ddslService;

    public HelloWorldResource(HelloWorldConfiguration httpConfiguration, DdslService ddslService) {
        config = httpConfiguration;
        this.ddslService = ddslService;
    }

    @GET
    public String sayHello() {
        return "Hello world from " + config.serviceName + " on " + NetUtils.resolveLocalPublicIP() + ":" + config.getHttpConfiguration().getPort();
    }

    @Path("fromOther")
    @GET
    public String sayHelloFromOther() {
        DdslServiceId otherService = config.otherService;
        if ( otherService == null) {
            return "Other service not configured";
        }

        String response = null;
        String url = null;
        try {
            url = ddslService.getBestLocationUrl(otherService);
        } catch (Exception e) {
            logger.error("Error fetching best url to other service: " + e.getMessage());
            return "Error fetching best url to other service: " + e.getMessage();
        }

        logger.info("Using other Service: " + url);

        try {
            HttpClient httpClient = new DefaultHttpClient();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            httpClient.execute( new HttpGet(url)).getEntity().writeTo(out);

            response = config.serviceName + " got response from " + url + ": " + new String(out.toByteArray(), "UTF-8");
        } catch (IOException e) {
            logger.error("Error fetching data from other server: " + url);

            response = "Error fetching data from other server: " + url;
        }


        return response;
    }
}