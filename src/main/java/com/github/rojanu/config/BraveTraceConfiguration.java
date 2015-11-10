package com.github.rojanu.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BraveTraceConfiguration {
    public String server;
    public Integer port;

    @JsonProperty("sample-rate")
    public Float sampleRate;

    @JsonProperty("service-name")
    public String serviceName;
}
