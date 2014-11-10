package nl.ing.oia.tomcat;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
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
	private static final Graphite graphite = new Graphite("lrv13715.europe.intranet", 2003);
	private static final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry).
					                                                          prefixedWith("oia.api.tst").
																																		convertDurationsTo(TimeUnit.MILLISECONDS).
																																		convertRatesTo(TimeUnit.SECONDS).
																																		filter(MetricFilter.ALL).
									                                                  build(graphite);
	private static final Map<String, Timer> uris = new ConcurrentHashMap<String, Timer>();

	protected boolean requestAttributesEnabled = false;

	public MetricsValve() {
		super(true);
		reporter.start(1, TimeUnit.MINUTES);
		logger.info("graphite reporter started");
	}

	@Override
	public void invoke(final Request request, final Response response) throws IOException, ServletException {
		logger.info("invoke: uri: " + request.getDecodedRequestURI());

		getNext().invoke(request, response);
	}

	@Override
	public void log(final Request request, final Response response, final long l) {
		final String uri = request.getDecodedRequestURI().replaceAll("/", "\\.");
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
		logger.info("stopInternal");
		setState(LifecycleState.STOPPING);
		reporter.report();
		reporter.stop();
	}
}
