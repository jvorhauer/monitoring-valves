package nl.ing.oia.tomcat;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Setup {

	private static final String PREFIX = "nl.ing.oia.metrics.";
	public static final Config config = ConfigFactory.load();

	public Boolean isConsoleEnabled = getBool("console.enabled");
	public Boolean isGraphiteEnabled= getBool("graphite.enabled");
	public Boolean isInfluxdbEnabled= getBool("influx.enabled");

	public String graphiteHost   = getString("graphite.host");
	public Integer graphitePort  = getInt("graphite.port");
	public String graphitePrefix = getString("graphite.prefix");

	public String  influxHost     = getString("influx.host");
	public Integer influxPort     = getInt("influx.port");
	public String  influxPrefix   = getString("influx.prefix");
	public String  influxDb       = getString("influx.db");
	public String  influxUser     = getString("influx.username");
	public String  influxPassword = getString("influx.password");


	public String  getString(final String key) {
		assert key != null;
		return config.hasPath(PREFIX + key) ? config.getString(PREFIX + key) : null;
	}
	public Boolean getBool(final String key) {
		assert key != null;
		return config.hasPath(PREFIX + key) ? config.getBoolean(PREFIX + key) : null;
	}
	public Integer getInt(final String key) {
		assert key != null;
		return config.hasPath(PREFIX + key) ? config.getInt(PREFIX + key) : null;
	}
}
