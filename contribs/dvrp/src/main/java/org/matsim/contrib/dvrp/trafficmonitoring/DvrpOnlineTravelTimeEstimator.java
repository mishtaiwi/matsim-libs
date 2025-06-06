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

package org.matsim.contrib.dvrp.trafficmonitoring;

import jakarta.inject.Inject;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.core.mobsim.framework.MobsimTimer;
import org.matsim.core.mobsim.framework.events.MobsimBeforeCleanupEvent;
import org.matsim.core.mobsim.framework.events.MobsimInitializedEvent;
import org.matsim.core.mobsim.framework.listeners.MobsimBeforeCleanupListener;
import org.matsim.core.mobsim.framework.listeners.MobsimInitializedListener;
import org.matsim.core.mobsim.qsim.QSim;
import org.matsim.vehicles.Vehicle;
import org.matsim.withinday.trafficmonitoring.WithinDayTravelTime;

/**
 * Online estimation of travel times for VrpOptimizer by combining WithinDayTravelTime and
 * DvrpOfflineTravelTimeEstimator. The beta coefficient is provided in seconds and should be either 0 (no online
 * estimation) or positive (mixed online-offline estimation). If beta is sufficiently large, 'beta >> 0', only the
 * currently observed TT is used
 *
 * @author michalm
 */
public class DvrpOnlineTravelTimeEstimator
		implements DvrpTravelTimeEstimator, MobsimInitializedListener, MobsimBeforeCleanupListener {
	private final WithinDayTravelTime withinDayTT;
	private final DvrpOfflineTravelTimeEstimator offlineTTEstimator;
	private MobsimTimer mobsimTimer;
	private final double beta;

	@Inject
	public DvrpOnlineTravelTimeEstimator(WithinDayTravelTime withinDayTT,
			DvrpOfflineTravelTimeEstimator offlineTTEstimator, DvrpConfigGroup dvrpConfig) {
		this.withinDayTT = withinDayTT;
		this.offlineTTEstimator = offlineTTEstimator;

		beta = dvrpConfig.getTravelTimeEstimationBeta();
		if (beta < 0) {
			throw new IllegalArgumentException("travelTimeEstimationBeta must be zero or positive");
		}
	}

	@Override
	public double getLinkTravelTime(Link link, double time, Person person, Vehicle vehicle) {
		double offlineTT = offlineTTEstimator.getLinkTravelTime(link, time, person, vehicle);
		if (mobsimTimer == null) {//called before/after QSim ==> no online estimation
			return offlineTT;
		}

		double currentTime = mobsimTimer.getTimeOfDay();
		double correction = Math.min(1, Math.max(0, 1 - (time - currentTime) / beta));
		double currentTT = withinDayTT.getLinkTravelTime(link, currentTime, person, vehicle);
		return correction * currentTT + (1 - correction) * offlineTT;

		// XXX Alternatively:
		// double currentOfflineTT = offlineTTEstimator.getLinkTravelTime(link, currentTime, person, vehicle);
		// return correction * currentTT * offlineTT / currentOfflineTT + (1-correction) * offlineTT
	}

	@Override
	public void notifyMobsimInitialized(@SuppressWarnings("rawtypes") MobsimInitializedEvent e) {
		mobsimTimer = ((QSim)e.getQueueSimulation()).getSimTimer();
	}

	@Override
	public void notifyMobsimBeforeCleanup(MobsimBeforeCleanupEvent e) {
		mobsimTimer = null;
	}
}
