# metrics-valve

[![Build Status](https://travis-ci.org/jvorhauer/metrics-valve.svg?branch=master)](https://travis-ci.org/jvorhauer/metrics-valve)

Tomcat 7 Valve to gather timing metrics on requests and report the gathered timings to the console or graphite. Or both.

*Each* request that arrives at the Tomcat instance with this Valve added to server.xml, is timed.

## server.xml

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

Add this line near the AccessLogValve resource, at least in the **/Server/Service/Engine/Host** section:

```XML
<Valve className="org.apache.catalina.valves.AccessLogValve"
       directory="logs"
       prefix="localhost_access_log."
       suffix=".txt"
       pattern="%h %l %u %t &quot;%r&quot; %s %b %D" />
```

## dependencies

Copy these JARs to the **lib** folder of a Tomcat instance:

- metrics-core-3.1.0.jar
- metrics-graphite-3.1.0.jar
- slf4j-api-1.7.7.jar
- metrics-influxdb-0.4.0.jar
- metrics-jvm-3.1.0.jar
- metrics-valve.jar

### Notes on dependencies:

metrics-influxdb and metrics-graphite should be replaced with specific versions for this project. This will
reduce the number of dependencies. Issue #2 @ Github.

metrics-jvm contains a number of M(x)Bean Gauge sets. These sets are rather extensive. This could be replaced

## Important!

This Valve has only been tested with Tomcat 7!
The Java Development Kit (JDK) version used to compile the sources is 1.8.0_25, but the Maven compiler plugin was
instructed to use 1.7 source and target versions.

