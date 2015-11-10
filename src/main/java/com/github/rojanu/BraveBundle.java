package com.github.rojanu;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.EmptySpanCollectorImpl;
import com.github.kristofa.brave.FixedSampleRateTraceFilter;
import com.github.kristofa.brave.ServerRequestInterceptor;
import com.github.kristofa.brave.ServerResponseInterceptor;
import com.github.kristofa.brave.ServerTracer;
import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.TraceFilter;
import com.github.kristofa.brave.http.DefaultSpanNameProvider;
import com.github.kristofa.brave.http.SpanNameProvider;
import com.github.kristofa.brave.jaxrs2.BraveContainerRequestFilter;
import com.github.kristofa.brave.jaxrs2.BraveContainerResponseFilter;
import com.github.kristofa.brave.zipkin.ZipkinSpanCollector;
import com.github.rojanu.config.BraveTraceConfiguration;
import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class BraveBundle<T> implements ConfiguredBundle<T> {
    private ServerTracer serverTracer;

    public void run(T configuration, Environment environment) throws Exception {
        BraveTraceConfiguration braveTraceConfiguration = this.getBraveTraceConfiguration(configuration);
        SpanCollector spanCollector;
        if (braveTraceConfiguration != null) {
            spanCollector = new ZipkinSpanCollector(
                    braveTraceConfiguration.server,
                    braveTraceConfiguration.port);
        } else {
            spanCollector = new EmptySpanCollectorImpl();
        }
        int sampleRate = 0;
        try {
            sampleRate = Math.round(1 / braveTraceConfiguration.sampleRate);
        } catch (Exception ignored) {
        }


        Brave.Builder braveBuilder = StringUtils.isNotBlank(braveTraceConfiguration.serviceName) ?
                new Brave.Builder(braveTraceConfiguration.serviceName) : new Brave.Builder();

        Brave brave = braveBuilder
                .spanCollector(spanCollector)
                .traceFilters(ImmutableList.of((TraceFilter) new FixedSampleRateTraceFilter(sampleRate)))
                .build();


        serverTracer = brave.serverTracer();


        ServerRequestInterceptor requestInterceptor = new ServerRequestInterceptor(serverTracer);
        SpanNameProvider spanNameProvider = new DefaultSpanNameProvider();
        BraveContainerRequestFilter containerRequestFilter = new BraveContainerRequestFilter(requestInterceptor, spanNameProvider);
        environment.jersey().register(containerRequestFilter);

        ServerResponseInterceptor responseInterceptor = new ServerResponseInterceptor(serverTracer);
        BraveContainerResponseFilter containerResponseFilter = new BraveContainerResponseFilter(responseInterceptor);
        environment.jersey().register(containerResponseFilter);
    }

    public void initialize(Bootstrap<?> bootstrap) {
    }

    public abstract BraveTraceConfiguration getBraveTraceConfiguration(T configuration);

    public ServerTracer getServerTracer() {
        return serverTracer;
    }
}
