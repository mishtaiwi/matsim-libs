/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2018 by the members listed in the COPYING,        *
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

package org.matsim.contrib.drt.extension.edrt.run;

import java.net.URL;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.matsim.contrib.drt.prebooking.PrebookingParams;
import org.matsim.contrib.drt.prebooking.logic.ProbabilityBasedPrebookingLogic;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.drt.run.MultiModeDrtConfigGroup;
import org.matsim.contrib.dvrp.passenger.*;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.ev.EvConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.examples.ExamplesUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

/**
 * @author michalm
 */
public class RunEDrtScenarioIT {
	@Test
	void test() {
		URL configUrl = IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("mielec"), "mielec_edrt_config.xml");
		RunEDrtScenario.run(configUrl, false);
	}

	@Test
	void testMultiModeDrtDeterminism() {
		URL configUrl = IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("mielec"), "mielec_multiModeEdrt_config.xml");

		DvrpConfigGroup dvrpConfigGroup = new DvrpConfigGroup();
		Config config = ConfigUtils.loadConfig(configUrl, new MultiModeDrtConfigGroup(), dvrpConfigGroup,
			new OTFVisConfigGroup(), new EvConfigGroup());

		Controler controller = RunEDrtScenario.createControler(config, false);
		config.controller().setLastIteration(2);

		PassengerPickUpTracker tracker = new PassengerPickUpTracker();
		tracker.install(controller);

		controller.run();

		assertEquals(1926, tracker.passengerPickupEvents);
	}


	@Test
	void testWithPrebooking() {
		URL configUrl = IOUtils.extendUrl(ExamplesUtils.getTestScenarioURL("mielec"), "mielec_edrt_config.xml");

		DvrpConfigGroup dvrpConfigGroup = new DvrpConfigGroup();
		Config config = ConfigUtils.loadConfig(configUrl, new MultiModeDrtConfigGroup(), dvrpConfigGroup,
				new OTFVisConfigGroup(), new EvConfigGroup());

		DrtConfigGroup drtConfig = DrtConfigGroup.getSingleModeDrtConfig(config);
		PrebookingParams prebookingParams = new PrebookingParams();
		prebookingParams.setAbortRejectedPrebookings(false);
		drtConfig.addParameterSet(prebookingParams);

		Controler controller = RunEDrtScenario.createControler(config, false);
		ProbabilityBasedPrebookingLogic.install(controller, drtConfig, 0.5, 4.0 * 3600.0);

		PrebookingTracker tracker = new PrebookingTracker();
		tracker.install(controller);

		controller.run();

		assertEquals(112, tracker.immediateScheduled);
		assertEquals(182, tracker.prebookedScheduled);
		assertEquals(94, tracker.immediateRejected);
		assertEquals(23, tracker.prebookedRejected);
	}

	static private class PassengerPickUpTracker implements PassengerPickedUpEventHandler {
		int passengerPickupEvents = 0;

		void install(Controler controller) {
			PassengerPickUpTracker thisTracker = this;

			controller.addOverridingModule(new AbstractModule() {
				@Override
				public void install() {
					addEventHandlerBinding().toInstance(thisTracker);
				}
			});
		}

		@Override
		public void handleEvent(PassengerPickedUpEvent event) {
			passengerPickupEvents++;
		}

		@Override
		public void reset(int iteration) {
			passengerPickupEvents=0;
		}

	}

	static private class PrebookingTracker implements PassengerRequestRejectedEventHandler, PassengerRequestScheduledEventHandler {
		int immediateScheduled = 0;
		int prebookedScheduled = 0;
		int immediateRejected = 0;
		int prebookedRejected = 0;

		@Override
		public void handleEvent(PassengerRequestScheduledEvent event) {
			if (event.getRequestId().toString().contains("prebooked")) {
				prebookedScheduled++;
			} else {
				immediateScheduled++;
			}
		}

		@Override
		public void handleEvent(PassengerRequestRejectedEvent event) {
			if (event.getRequestId().toString().contains("prebooked")) {
				prebookedRejected++;
			} else {
				immediateRejected++;
			}
		}

		void install(Controler controller) {
			PrebookingTracker thisTracker = this;

			controller.addOverridingModule(new AbstractModule() {
				@Override
				public void install() {
					addEventHandlerBinding().toInstance(thisTracker);
				}
			});
		}
	}
}
