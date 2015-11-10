dropwizard-brave
==========================

Integrating Dropwizard to Zipkin tracer using Brave

Description
-----------

This project uses Brave jersey filters to integrate into Zipkin.

This Dropwizard bundle that will add in the servlet filter for you. Jersey client filters have to be manually added to your client classes.


Integrating with existing dropwizard project
--------------------------------------------

Add the following dependency into your pom.xml

```xml
<dependency>
    <groupId>com.github.rojani</groupId>
    <artifactId>dropwizard-brave</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

Add `BraveTraceConfiguration` into your application's configuration class.

```java
public class ExampleConfiguration extends Configuration {

    private BraveTraceConfiguration braveTraceConfiguration = new BraveTraceConfiguration();

    public BraveTraceConfiguration getBraveTraceConfiguration() {
        return braveTraceConfiguration;
    }

    public void setRequestTrackerConfiguration(BraveTraceConfiguration configuration) {
        this.braveTraceConfiguration = configuration;
    }
}
```

Add the `BraveBundle` to your application

```java
bootstrap.addBundle(new BraveBundle<ExampleConfiguration>() {
    @Override
    public BraveTraceConfiguration getBraveTraceConfiguration(BundleConfiguration configuration) {
        return configuration.getBraveTraceConfiguration();
    }
});
```

and `mvn clean install`
