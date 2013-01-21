package com.kjetland.ddsl.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class DdslServiceId {

    @JsonProperty
    @NotNull
    public String environment;

    @JsonProperty
    public String serviceType = "http"; //This is the default for dropwizard

    @JsonProperty
    @NotNull
    public String name;

    @JsonProperty
    @NotNull
    public String version;

    public DdslServiceId() {
    }

    public DdslServiceId(String environment, String name, String version) {
        this.environment = environment;
        this.name = name;
        this.version = version;
    }

    public DdslServiceId(String environment, String name, String version, String serviceType) {
        this.environment = environment;
        this.name = name;
        this.version = version;
        this.serviceType = serviceType;
    }
}
