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
