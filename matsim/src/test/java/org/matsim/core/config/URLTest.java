/* *********************************************************************** *
 * project: org.matsim.*
 * URLTest.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.core.config;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.scenario.ScenarioUtils;

public class URLTest {

  @Test
  public void testLoadWithURL() throws MalformedURLException {
    Config config = ConfigUtils.loadConfig(new URL("file:../examples/scenarios/equil/config.xml"));
    Scenario scenario = ScenarioUtils.loadScenario(config);
  }
}
