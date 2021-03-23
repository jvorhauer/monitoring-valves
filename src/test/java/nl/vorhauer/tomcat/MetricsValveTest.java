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

import org.apache.catalina.LifecycleException;
import org.assertj.core.api.Assertions;
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
		Assertions.assertThat(metricsValve).isNotNull();
		Assertions.assertThat(metricsValve.getRequestAttributesEnabled()).isNotNull();

		Assertions.assertThat(metricsValve.isJvmMetricsEnabled()).isTrue();
		metricsValve.setJvmMetricsEnabled(false);
		Assertions.assertThat(metricsValve.isJvmMetricsEnabled()).isFalse();
	}

	@Test
	public void checkConsoleProperties() {
		Assertions.assertThat(metricsValve).isNotNull();
		Assertions.assertThat(metricsValve.isConsoleEnabled()).isFalse();
		metricsValve.setConsoleEnabled(Boolean.TRUE);
		Assertions.assertThat(metricsValve.isConsoleEnabled()).isTrue();
	}

	@Test
	public void checkGraphiteProperties() {
    Assertions.assertThat(metricsValve).isNotNull();
		Assertions.assertThat(metricsValve.getGraphiteHost()).isNull();
    Assertions.assertThat(metricsValve.isGraphiteEnabled()).isFalse();
		metricsValve.setGraphiteHost("localhost");
    Assertions.assertThat(metricsValve.isGraphiteEnabled()).isTrue();

		final Integer port = metricsValve.getGraphitePort();
		metricsValve.setGraphitePort(null);
    Assertions.assertThat(metricsValve.isGraphiteEnabled()).isFalse();

    Assertions.assertThat(metricsValve.getGraphitePrefix()).isEqualTo("");
		metricsValve.setGraphitePort(port);
		metricsValve.setGraphitePrefix(null);
    Assertions.assertThat(metricsValve.isGraphiteEnabled()).isFalse();
	}

	@Test
	public void checkInfluxProperties() {
    Assertions.assertThat(metricsValve).isNotNull();

    Assertions.assertThat(metricsValve.getInfluxHost()).isNull();
    Assertions.assertThat(metricsValve.getInfluxPort()).isEqualTo(8086);
    Assertions.assertThat(metricsValve.getInfluxDbName()).isNull();
    Assertions.assertThat(metricsValve.getInfluxUser()).isNull();
    Assertions.assertThat(metricsValve.getInfluxPasswd()).isNull();
    Assertions.assertThat(metricsValve.getInfluxPrefix()).isEqualTo("");

    Assertions.assertThat(metricsValve.isInfluxEnabled()).isFalse();

		metricsValve.setInfluxHost("localhost");
		metricsValve.setInfluxDbName("dbname");
		metricsValve.setInfluxUser("username");
		metricsValve.setInfluxPasswd("password");
		metricsValve.setInfluxPrefix("prefix");
    Assertions.assertThat(metricsValve.isInfluxEnabled()).isTrue();

		metricsValve.setInfluxPort(null);
    Assertions.assertThat(metricsValve.isInfluxEnabled()).isFalse();
	}
}
