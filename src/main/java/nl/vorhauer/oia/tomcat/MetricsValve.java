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

package nl.vorhauer.oia.tomcat;

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
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Jurjen Vorhauer (yo37ba)
 * @since 2014-11-10
 */
public class MetricsValve extends ValveBase implements AccessLog {

	private static final Log logger = LogFactory.getLog(MetricsValve.class);
	private static final MetricRegistry registry = new MetricRegistry();
	private static final Map<String, Timer> uris = new ConcurrentHashMap<>();

	private final Setup setup = new Setup();

	private boolean requestAttributesEnabled = false;
	private ConsoleReporter console;
	private InfluxdbReporter influx;
	private GraphiteReporter graphite;

	public MetricsValve() throws Exception {
		super(true);

		registry.registerAll(new MemoryUsageGaugeSet());
		registry.registerAll(new GarbageCollectorMetricSet());
		registry.registerAll(new ThreadStatesGaugeSet());

		setupReporters();

		logger.info("enabled reporters started");
	}
		private synchronized void setupReporters() throws Exception {
			if (setup.isConsoleEnabled) {
				console = ConsoleReporter.forRegistry(registry).
																	convertDurationsTo(TimeUnit.MILLISECONDS).
																	convertRatesTo(TimeUnit.MILLISECONDS).
																	filter(MetricFilter.ALL).
				                          build();
				console.start(1, TimeUnit.MINUTES);
				logger.info("setup: console reporter started.");
			}
			if (setup.isGraphiteEnabled) {
				final InetSocketAddress isa = new InetSocketAddress(setup.graphiteHost, setup.graphitePort);
				graphite = GraphiteReporter.forRegistry(registry).
				                            prefixedWith(setup.graphitePrefix).
				                            convertDurationsTo(TimeUnit.MILLISECONDS).
				                            convertRatesTo(TimeUnit.MILLISECONDS).
				                            filter(MetricFilter.ALL).
				                            build(new Graphite(isa));
				graphite.start(1, TimeUnit.MINUTES);
				logger.info("setup: graphite reporter started.");
			}
			if (setup.isInfluxdbEnabled) {
				final Influxdb db = new Influxdb(setup.influxHost, setup.influxPort,
				                                 setup.influxDb, setup.influxUser, setup.influxPassword);
				influx = InfluxdbReporter.forRegistry(registry).
				                          prefixedWith(setup.influxPrefix).
				                          convertDurationsTo(TimeUnit.MILLISECONDS).
				                          convertRatesTo(TimeUnit.MILLISECONDS).
				                          filter(MetricFilter.ALL).
				                          build(db);
				influx.start(1, TimeUnit.MINUTES);
				logger.info("setup: influx reporter started.");
			}
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
			if (!uris.containsKey(uri)) {
				final Timer timer = registry.timer(uri);
				uris.put(uri, timer);
			}
			return uris.get(uri);
		}
		private String rewriteUri(final String in) {
			final String dotted = in.replaceAll("/", "\\.");
			return dotted.equals(".") ? "ROOT" : dotted.substring(1);
		}

	@Override
	public void setRequestAttributesEnabled(final boolean b) {
		requestAttributesEnabled = b;
	}

	@Override
	public boolean getRequestAttributesEnabled() {
		return requestAttributesEnabled;
	}

	@Override
	protected synchronized void stopInternal() throws LifecycleException {
		super.stopInternal();
		if (setup.isInfluxdbEnabled && influx != null) {
			influx.report();
			influx.stop();
		}
		if (setup.isGraphiteEnabled && graphite != null) {
			graphite.report();
			graphite.stop();
		}
		if (setup.isConsoleEnabled && console != null) {
			console.report();
			console.stop();
		}
		logger.info("Stopped.");
	}
}
