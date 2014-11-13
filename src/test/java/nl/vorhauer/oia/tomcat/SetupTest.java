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

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class SetupTest {

	private Setup setup;

	@Before
	public void setup() {
		setup = new Setup();
	}

	@Test
	public void genericSetupTest() {
		assertThat(setup, notNullValue());

		assertThat(setup.getBool("console.enabled"), notNullValue());
		assertThat(setup.getBool("NOT.EXISTY"), is(false));

		assertThat(setup.getInt("graphite.port"), notNullValue());
		assertThat(setup.getInt("NOT.EXISTY"), nullValue());
	}

	@Test
	public void consoleSetupTest() {
		assertThat(setup, notNullValue());
		assertThat(setup.isConsoleEnabled, notNullValue());
	}

	@Test
	public void graphiteSetupTest() {
		assertThat(setup, notNullValue());
		assertThat(setup.graphiteHost, notNullValue());
		assertThat(setup.graphiteHost.length(), greaterThan(0));
		assertThat(setup.graphitePort, notNullValue());
		assertThat(setup.graphitePort, greaterThan(1024));
		assertThat(setup.graphitePrefix, notNullValue());
		assertThat(setup.graphitePrefix.length(), greaterThan(0));
		assertThat(setup.isGraphiteEnabled, notNullValue());
	}

	@Test
	public void influxSetupTest() {
		assertThat(setup, notNullValue());
		assertThat(setup.influxDb, notNullValue());
		assertThat(setup.influxDb.length(), greaterThan(0));
		assertThat(setup.influxHost, notNullValue());
		assertThat(setup.influxHost.length(), greaterThan(0));
		assertThat(setup.influxPort, notNullValue());
		assertThat(setup.influxPort, greaterThan(1024));
		assertThat(setup.influxPassword, notNullValue());
		assertThat(setup.influxUser, notNullValue());
		assertThat(setup.influxUser.length(), greaterThan(0));
		assertThat(setup.influxPrefix, notNullValue());
		assertThat(setup.influxPrefix.length(), greaterThan(0));
	}
}
