package com.kjetland.ddsl.dropwizard_example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kjetland.ddsl.dropwizard.DdslConfig;
import com.yammer.dropwizard.config.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class HelloWorldConfiguration extends Configuration {

    @JsonProperty
    @Valid
    @NotNull
    public DdslConfig ddslConfig;

}