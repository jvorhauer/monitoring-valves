# monitoring-valves

[![GitHub Action](https://github.com/jvorhauer/monitoring-valves/actions/workflows/maven.yml/badge.svg)](https://github.com/jvorhauer/monitoring-valves/actions/workflows/maven.yml)

## MetricsValve

A Valve to gather timing metrics on requests and report the gathered timings to the console, influxdb and/or graphite.

*Each* request that arrives at the Tomcat instance with this Valve is timed.

### server.xml

```XML
<Valve className="nl.vorhauer.tomcat.MetricsValve"
       graphiteHost="localhost"
       graphitePrefix="prefix"
       consoleEnabled="true"
       influxHost="localhost"
       influxDbName="dbname"
       influxUser="username"
       influxPasswd="password"
       influxPrefix="prefix" />
```

Add this line to the **/Server/Service/Engine/Host** section.
The absence or presence of certain attributes determines the backend to be used, so a `graphiteHost` with a (valid!) hostname means that 
metrics are send to that Graphite backend. For InfluxDB all influx* attributes are required.

## Dependencies

Copy these JARs to the **lib** folder of a Tomcat instance:

- metrics-core-4.1.18.jar
- metrics-graphite-4.1.18.jar
- metrics-jvm-4.1.18.jar
- metrics-influxdb-0.7.0.jar
- slf4j-api-1.7.7.jar
- metrics-valve.jar

### Notes on dependencies:

metrics-jvm contains a number of M(x)Bean Gauge sets. These sets are rather extensive. This will be replaced with an implementation that will combine all relevant settings. A facilitiy to monitor relevant filesystems will be added as well.

## Important!

This Valve has only been tested with Tomcat 7!
The Java Development Kit (JDK) version used to compile the sources is 11, and the Maven compiler plugin was instructed to use 11 source and target versions.

## Build

Use the included Maven wrapper to build this project. For example `./mvnw clean verify`.

And use `./mvnw package` to create the required **metrics-valve.jar**.