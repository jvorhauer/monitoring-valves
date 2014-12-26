# monitoring-valves

[![Build Status](https://travis-ci.org/jvorhauer/monitoring-valves.svg?branch=master)](https://travis-ci.org/jvorhauer/monitoring-valves)

This project consists of two Valves for Tomcat 7: one that gathers metrics on the Tomcat JVM and the requests that were handled; the other to log requests to logstash using Redis as queue.

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

Add this line to the **/Server/Service/Engine/Host** section:

### RedisAccessValve (planned)

A Valve to log all access to Redis, probably used as queue for logstash. Jedis is used as Redis client library.
The JSON formatter produces a format that satisfies my needs, but might need some attention for different logstash configurations.

### server.xml

```XML
<Valve className="nl.vorhauer.tomcat.RedisAccessValve"
       redisHost="localhost"
       redisPort=9999 />
```


## dependencies

Copy these JARs to the **lib** folder of a Tomcat instance:

- metrics-core-3.1.0.jar
- metrics-graphite-3.1.0.jar
- metrics-jvm-3.1.0.jar
- metrics-influxdb-0.4.0.jar
- slf4j-api-1.7.7.jar
- metrics-valve.jar
- jedis-2.5.2.jar
- commons-pool2.jar

### Notes on dependencies:

metrics-influxdb and metrics-graphite should be replaced with implementations in this project, thus reducing the number of dependencies. Issue #2 @ Github.

metrics-jvm contains a number of M(x)Bean Gauge sets. These sets are rather extensive. This will be replaced with an implementation that will combine all relevant settings. A facilitiy to monitor relevant filesystems will be added as well.

## Important!

This Valve has only been tested with Tomcat 7!
The Java Development Kit (JDK) version used to compile the sources is 1.8.0_25, but the Maven compiler plugin was instructed to use 1.7 source and target versions.
