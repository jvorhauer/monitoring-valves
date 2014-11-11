package nl.vorhauer.oia.tomcat;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Very rudimentary test :-(
 * Try to find out if the Valve can be tested in a Container environment...
 */
public class MetricsValveTest {

	MetricsValve metricsValve;

	@Before
	public void setup() throws Exception {
		metricsValve = new MetricsValve();
	}

	@Test
	public void checkValve() {
		assertThat(metricsValve, notNullValue());
		assertThat(metricsValve.getRequestAttributesEnabled(), notNullValue());
	}
}
