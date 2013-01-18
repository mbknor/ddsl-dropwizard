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
}
