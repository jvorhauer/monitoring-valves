/*
 * **********************************************************************************************************************
 *
 *  Copyright (c) 2014 Jurjen Vorhauer.
 *
 * **********************************************************************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations under the License.
 *
 * **********************************************************************************************************************
 */

package nl.vorhauer.tomcat;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Setup {

	private static final String PREFIX = "metrics.";
	public static final Config config = ConfigFactory.load();

	public final Boolean isConsoleEnabled = getBool("console.enabled");
	public final Boolean isGraphiteEnabled= getBool("graphite.enabled");
	public final Boolean isInfluxdbEnabled= getBool("influx.enabled");

	public final Boolean isJvmMemEnabled = getBool("jvm.mem.enabled");
	public final Boolean isJvmGcEnabled  = getBool("jvm.gc.enabled");
	public final Boolean isJvmThreadStateEnabled = getBool("jvm.ts.enabled");

	public final String graphiteHost   = getString("graphite.host");
	public final Integer graphitePort  = getInt("graphite.port");
	public final String graphitePrefix = getString("graphite.prefix");

	public final String  influxHost     = getString("influx.host");
	public final Integer influxPort     = getInt("influx.port");
	public final String  influxPrefix   = getString("influx.prefix");
	public final String  influxDb       = getString("influx.db");
	public final String  influxUser     = getString("influx.username");
	public final String  influxPassword = getString("influx.password");


	public String  getString(final String key) {
		assert key != null;
		return config.hasPath(PREFIX + key) ? config.getString(PREFIX + key) : null;
	}
	public Boolean getBool(final String key) {
		assert key != null;
		return config.hasPath(PREFIX + key) && config.getBoolean(PREFIX + key);
	}
	public Integer getInt(final String key) {
		assert key != null;
		return config.hasPath(PREFIX + key) ? config.getInt(PREFIX + key) : null;
	}
}
