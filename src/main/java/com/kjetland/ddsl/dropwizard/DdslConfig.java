package com.kjetland.ddsl.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class DdslConfig {

    @JsonProperty
    @NotNull
    public String zookeeperHosts;


    // Number of milliseconds to cache service lookups
    @JsonProperty
    public long serviceLookupCacheMills = 1000*60;

    // If not specified this service will not be announced to DDSL
    @JsonProperty
    @Valid
    public DdslServiceId serviceId;

    // If not specified we resolve it
    @JsonProperty
    public String serviceUrl;

    @JsonProperty
    public double serviceQuality = 1.0;


    // If zookeeper is not working - or no service is found in ddsl,
    // in an emergency, you can enter mapping from ServiceId to url
    @JsonProperty
    public Map<String, String> failoverServiceId2UrlMapping;

}
