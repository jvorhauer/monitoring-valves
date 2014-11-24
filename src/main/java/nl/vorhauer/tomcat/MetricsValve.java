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

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import metrics_influxdb.Influxdb;
import metrics_influxdb.InfluxdbReporter;
import org.apache.catalina.AccessLog;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author Jurjen Vorhauer (yo37ba)
 * @since 2014-11-10
 */
public class MetricsValve extends ValveBase implements AccessLog {

	protected static final String info  = "nl.vorhauer.tomcat.MetricsValve/1.1";
	private   static final Log    logger= LogFactory.getLog(MetricsValve.class);

	private final MetricRegistry registry = new MetricRegistry();

	private boolean requestAttributesEnabled = false;
	private boolean jvmMetricsEnabled        = true;

	private String  graphiteHost   = null;
	private Integer graphitePort   = 2003;
	private String  graphitePrefix = "";
	private Boolean consoleEnabled = Boolean.FALSE;
	private String  influxHost     = null;
	private Integer influxPort     = 8086;
	private String  influxDbName   = null;
	private String  influxUser     = null;
	private String  influxPasswd   = null;
	private String  influxPrefix   = "";

	private ConsoleReporter  console = null;
	private InfluxdbReporter influx  = null;
	private GraphiteReporter graphite= null;


	public MetricsValve() throws Exception {
		super(true);    // supports async: running in Tomcat 7 or higher
	}

	@Override
	protected synchronized void startInternal() throws LifecycleException {

		super.startInternal();

		if (jvmMetricsEnabled) {
			registry.registerAll(new MemoryUsageGaugeSet());
			registry.registerAll(new GarbageCollectorMetricSet());
			registry.registerAll(new ThreadStatesGaugeSet());
		}

		if (isConsoleEnabled()) {
			console = ConsoleReporter.forRegistry(registry).
																convertDurationsTo(TimeUnit.MILLISECONDS).
																convertRatesTo(TimeUnit.MILLISECONDS).
																filter(MetricFilter.ALL).
			                          build();
			console.start(1, TimeUnit.MINUTES);
			logger.info("setup: console reporter started.");
		}
		if (isGraphiteEnabled()) {
			final InetSocketAddress isa = new InetSocketAddress(graphiteHost, graphitePort);
			graphite = GraphiteReporter.forRegistry(registry).
			                            prefixedWith(graphitePrefix).
			                            convertDurationsTo(TimeUnit.MILLISECONDS).
			                            convertRatesTo(TimeUnit.MILLISECONDS).
			                            filter(MetricFilter.ALL).
			                            build(new Graphite(isa));
			graphite.start(1, TimeUnit.MINUTES);
			logger.info("setup: graphite reporter started.");
		}
		if (isInfluxEnabled()) {
			try {
				final Influxdb db = new Influxdb(influxHost, influxPort,
				                                 influxDbName, influxUser, influxPasswd);
				influx = InfluxdbReporter.forRegistry(registry).
				                          prefixedWith(influxPrefix).
				                          convertDurationsTo(TimeUnit.MILLISECONDS).
				                          convertRatesTo(TimeUnit.MILLISECONDS).
				                          filter(MetricFilter.ALL).
				                          build(db);
				influx.start(1, TimeUnit.MINUTES);
				logger.info("setup: influx reporter started.");
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	public void setJvmMetricsEnabled(final Boolean b) { jvmMetricsEnabled = b; }
	public Boolean isJvmMetricsEnabled() { return jvmMetricsEnabled; }

	public void setConsoleEnabled(final Boolean b) { consoleEnabled = b; }
	public Boolean isConsoleEnabled() { return consoleEnabled; }

	public void setGraphiteHost(final String s) { graphiteHost = s; }
	public String getGraphiteHost() { return graphiteHost; }

	public void setGraphitePort(final Integer i) { graphitePort = i; }
	public Integer getGraphitePort() { return graphitePort; }

	public void setGraphitePrefix(final String s) { graphitePrefix = s; }
	public String getGraphitePrefix() { return graphitePrefix; }

	public Boolean isGraphiteEnabled() {
		return notBlank(graphiteHost) &&
		       graphitePort != null && graphitePort > 1024 &&
		       graphitePrefix != null;
	}

	public void setInfluxHost(final String s) { influxHost = s; }
	public String getInfluxHost() { return influxHost; }

	public void setInfluxPort(final Integer i) { influxPort = i; }
	public Integer getInfluxPort() { return influxPort; }

	public void setInfluxDbName(final String s) { influxDbName = s; }
	public String getInfluxDbName() { return influxDbName; }

	public void setInfluxUser(final String s) { influxUser = s; }
	public String getInfluxUser() { return influxUser; }

	public void setInfluxPasswd(final String s) { influxPasswd = s; }
	public String getInfluxPasswd() { return influxPasswd; }

	public void setInfluxPrefix(final String s) { influxPrefix = s; }
	public String getInfluxPrefix() { return influxPrefix; }

	public Boolean isInfluxEnabled() {
		return notBlank(influxHost) &&
		       influxPort != null && influxPort > 1024 &&
		       notBlank(influxDbName) && notBlank(influxUser) && notBlank(influxPasswd) &&
		       influxPrefix != null;
	}

	@Override
	public void invoke(final Request request, final Response response) throws IOException, ServletException {
		getNext().invoke(request, response);
	}

	@Override
	public void log(final Request request, final Response response, final long l) {
		final String uri = rewriteUri(request.getDecodedRequestURI());
		logger.info("log: " + uri + " took: " + l);
		final Timer timer = timerForUri(uri);
		final Timer.Context ctx = timer.time();
		timer.update(l, TimeUnit.MILLISECONDS);
		ctx.stop();
	}
		private Timer timerForUri(final String uri) {
			if (!registry.getTimers().containsKey(uri)) {
				registry.timer(uri);
			}
			return registry.getTimers().get(uri);
		}
		private String rewriteUri(final String in) {
			final String dotted = in.replaceAll("/", "\\.");
			return dotted.equals(".") ? "ROOT" : dotted.substring(1);
		}

	@Override
	public void setRequestAttributesEnabled(final boolean b) { requestAttributesEnabled = b; }

	@Override
	public boolean getRequestAttributesEnabled() { return requestAttributesEnabled; }


	@Override
	protected synchronized void stopInternal() throws LifecycleException {

		super.stopInternal();

		if (isInfluxEnabled() && influx != null) {
			influx.report();
			influx.stop();
			logger.info("stopInternal: influx reporter stopped.");
		}

		if (isGraphiteEnabled() && graphite != null) {
			graphite.report();
			graphite.stop();
			logger.info("stopInternal: graphite reporter stopped.");
		}

		if (isConsoleEnabled() && console != null) {
			console.report();
			console.stop();
			logger.info("stopInternal: console reporter stopped.");
		}
	}


	private Boolean notBlank(final String s) {
		return s != null && !s.trim().isEmpty();
	}
}
