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

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.apache.catalina.LifecycleException;
import org.junit.After;
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

	@After
	public void cleanup() throws LifecycleException {
		metricsValve.stop();
		metricsValve = null;
	}


	@Test
	public void checkGenericProperties() {
		assertThat(metricsValve, notNullValue());
		assertThat(metricsValve.getRequestAttributesEnabled(), notNullValue());

		assertThat(metricsValve.isJvmMetricsEnabled(), is(true));
		metricsValve.setJvmMetricsEnabled(false);
		assertThat(metricsValve.isJvmMetricsEnabled(), is(false));
	}

	@Test
	public void checkConsoleProperties() {
		assertThat(metricsValve, notNullValue());

		assertThat(metricsValve.isConsoleEnabled(), is(false));
		metricsValve.setConsoleEnabled(Boolean.TRUE);
		assertThat(metricsValve.isConsoleEnabled(), is(true));
	}

	@Test
	public void checkGraphiteProperties() {
		assertThat(metricsValve.getGraphiteHost(), nullValue());
		assertThat(metricsValve.isGraphiteEnabled(), is(false));

		metricsValve.setGraphiteHost("localhost");
		assertThat(metricsValve.isGraphiteEnabled(), is(true));

		final Integer port = metricsValve.getGraphitePort();
		metricsValve.setGraphitePort(null);
		assertThat(metricsValve.isGraphiteEnabled(), is(false));

		metricsValve.setGraphitePort(port);
		metricsValve.setGraphitePrefix(null);
		assertThat(metricsValve.isGraphiteEnabled(), is(false));
	}

	@Test
	public void checkInfluxProperties() {
		assertThat(metricsValve, notNullValue());

		assertThat(metricsValve.getInfluxHost(), nullValue());
		assertThat(metricsValve.getInfluxPort(), is(8086));
		assertThat(metricsValve.getInfluxDbName(), nullValue());
		assertThat(metricsValve.getInfluxUser(), nullValue());
		assertThat(metricsValve.getInfluxPasswd(), nullValue());
		assertThat(metricsValve.getInfluxPrefix(), is(""));

		assertThat(metricsValve.isInfluxEnabled(), is(false));

		metricsValve.setInfluxHost("localhost");
		metricsValve.setInfluxDbName("dbname");
		metricsValve.setInfluxUser("username");
		metricsValve.setInfluxPasswd("password");
		metricsValve.setInfluxPrefix("prefix");
		assertThat(metricsValve.isInfluxEnabled(), is(true));

		metricsValve.setInfluxPort(null);
		assertThat(metricsValve.isInfluxEnabled(), is(false));
	}
}
