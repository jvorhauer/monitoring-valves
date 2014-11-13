# metrics-valve

Tomcat 7 Valve to gather timing metrics on requests.

NB: Mainly based on the AccessLogValve class.

Each request that arrives at the Tomcat instance with this Valve added to server.xml is timed.

## server.xml

```
<Valve className="nl.vorhauer.oia.tomcat.MetricsValve" />
```

## dependencies

Copy these JARs to the **lib** folder of a Tomcat instance:

- metrics-core-3.1.0.jar
- metrics-graphite-3.1.0.jar
- slf4j-api-1.7.7.jar
- metrics-influxdb-0.4.0.jar
- metrics-jvm-3.1.0.jar
- config-1.2.1.jar
- metrics-valve.jar

## configuration

The configuration of the metrics reporters is in the **reference.conf** file in **src/main/resources**.

If you want to override the default/reference configuration, copy the settings to a new file, say **my.conf** and
add **-Dconfig.path=path/to/my.conf** to your setenv.sh.

## Important!

This Valve has only been tested with Tomcat 7!

