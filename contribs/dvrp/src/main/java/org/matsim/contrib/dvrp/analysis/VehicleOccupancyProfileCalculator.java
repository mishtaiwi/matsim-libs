/*
 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2023 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */
package org.matsim.contrib.dvrp.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.HasPersonId;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.common.timeprofile.TimeDiscretizer;
import org.matsim.contrib.dvrp.fleet.DvrpVehicle;
import org.matsim.contrib.dvrp.fleet.DvrpVehicleSpecification;
import org.matsim.contrib.dvrp.fleet.FleetSpecification;
import org.matsim.contrib.dvrp.fleet.VehicleAddedEvent;
import org.matsim.contrib.dvrp.fleet.VehicleAddedEventHandler;
import org.matsim.contrib.dvrp.fleet.VehicleRemovedEvent;
import org.matsim.contrib.dvrp.fleet.VehicleRemovedEventHandler;
import org.matsim.contrib.dvrp.schedule.Task;
import org.matsim.contrib.dvrp.schedule.Task.TaskType;
import org.matsim.contrib.dvrp.vrpagent.TaskEndedEvent;
import org.matsim.contrib.dvrp.vrpagent.TaskEndedEventHandler;
import org.matsim.contrib.dvrp.vrpagent.TaskStartedEvent;
import org.matsim.contrib.dvrp.vrpagent.TaskStartedEventHandler;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.vehicles.Vehicle;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableSet;

/**
 * @author michalm (Michal Maciejewski)
 */
public class VehicleOccupancyProfileCalculator
		implements PersonEntersVehicleEventHandler, PersonLeavesVehicleEventHandler, TaskStartedEventHandler,
		TaskEndedEventHandler, VehicleAddedEventHandler, VehicleRemovedEventHandler {

	private static class VehicleState {
		private Task.TaskType taskType;
		private int occupancy;
		private double beginTime;
	}

	private final TimeDiscretizer timeDiscretizer;

	private Map<Task.TaskType, double[]> nonPassengerServingTaskProfiles;
	private ArrayList<double[]> vehicleOccupancyProfiles;

	private final ImmutableSet<Task.TaskType> passengerServingTaskTypes;
	private final Map<Id<DvrpVehicle>, VehicleState> vehicleStates = new IdMap<>(DvrpVehicle.class);

	private final double analysisEndTime;
	private int initialCapacity;

	private final String dvrpMode;

	private boolean wasConsolidatedInThisIteration = false;

	public VehicleOccupancyProfileCalculator(String dvrpMode, FleetSpecification fleet, int timeInterval,
			QSimConfigGroup qsimConfig, ImmutableSet<Task.TaskType> passengerServingTaskTypes) {
		this.dvrpMode = dvrpMode;
		this.passengerServingTaskTypes = passengerServingTaskTypes;

		double qsimEndTime = qsimConfig.getEndTime().orElse(0);
		double maxServiceEndTime = fleet.getVehicleSpecifications()
				.values()
				.stream()
				.mapToDouble(DvrpVehicleSpecification::getServiceEndTime)
				.max()
				.orElse(0);
		analysisEndTime = Math.max(qsimEndTime, maxServiceEndTime);
		timeDiscretizer = new TimeDiscretizer((int)Math.ceil(analysisEndTime), timeInterval);

		initialCapacity = fleet.getVehicleSpecifications()
				.values()
				.stream()
				.mapToInt(DvrpVehicleSpecification::getCapacity)
				.max()
				.orElse(0);
	}

	private void consolidate() {
		Preconditions.checkState(!wasConsolidatedInThisIteration || vehicleStates.isEmpty(),
					"The profiles has been already consolidated, but the vehicles states are not empty."
							+ " This means consolidation was done too early (before all events has been processed)."
							+ " Hint: run consolidate() after Mobsim is completed (e.g. MobsimBeforeCleanupEvent is sent).");

		if (!wasConsolidatedInThisIteration) {
			// consolidate
			for (VehicleState state : vehicleStates.values()) {
				if (state.taskType != null) {
					increment(state, analysisEndTime);
				}
			}
			vehicleStates.clear();

			nonPassengerServingTaskProfiles.values().forEach(this::normalizeProfile);
			vehicleOccupancyProfiles.forEach(this::normalizeProfile);
			wasConsolidatedInThisIteration = true;
		}
	}

	private void normalizeProfile(double[] profile) {
		for (int i = 0; i < profile.length; i++) {
			profile[i] /= timeDiscretizer.getTimeInterval();
		}
	}

	public Map<Task.TaskType, double[]> getNonPassengerServingTaskProfiles() {
		this.consolidate();
		return nonPassengerServingTaskProfiles;
	}

	public List<double[]> getVehicleOccupancyProfiles() {
		this.consolidate();
		return vehicleOccupancyProfiles;
	}

	public double[] getNumberOfVehiclesInServiceProfile() {
		double[] numberOfVehiclesInServiceProfile = new double[timeDiscretizer.getIntervalCount()];
		Map<Task.TaskType, double[]> nonPassengerServingTaskProfiles = getNonPassengerServingTaskProfiles();
		List<double[]> vehicleOccupancyProfiles = getVehicleOccupancyProfiles();
		for (int i = 0; i < timeDiscretizer.getIntervalCount(); i++) {
			double total = 0.0;
			for (double[] profile : nonPassengerServingTaskProfiles.values()) {
				total += profile[i];
			}
			for (double[] profile : vehicleOccupancyProfiles) {
				total += profile[i];
			}
			numberOfVehiclesInServiceProfile[i] = total;
		}
		return numberOfVehiclesInServiceProfile;
	}

	public TimeDiscretizer getTimeDiscretizer() {
		return timeDiscretizer;
	}

	private void increment(VehicleState state, double endTime) {
		if (state.taskType == VEHICLE_ADDED) {
			return; // Don't count tasks that did not come from this dvrpMode
		}
		
		Verify.verify(state.taskType != null);
		Verify.verify(state.occupancy >= 0);

		boolean servingPassengers = passengerServingTaskTypes.contains(state.taskType) || state.occupancy > 0;

		double[] profile = servingPassengers ?
				occupancyProfile(state.occupancy) :
				nonPassengerServingTaskProfiles.computeIfAbsent(state.taskType,
						v -> new double[timeDiscretizer.getIntervalCount()]);
		increment(profile, Math.min(state.beginTime, endTime), endTime);
	}

	private void increment(double[] values, double beginTime, double endTime) {
		if (beginTime == endTime && beginTime >= analysisEndTime) {
			return;
		}
		endTime = Math.min(endTime, analysisEndTime);

		double timeInterval = timeDiscretizer.getTimeInterval();
		int fromIdx = timeDiscretizer.getIdx(beginTime);
		int toIdx = timeDiscretizer.getIdx(endTime);

		for (int i = fromIdx; i < toIdx; i++) {
			values[i] += timeInterval;
		}

		// reduce first time bin
		values[fromIdx] -= beginTime % timeInterval;

		// handle last time bin
		values[toIdx] += endTime % timeInterval;
	}

	/* Event handling starts here */

	@Override
	public void handleEvent(VehicleAddedEvent event) {
		if (!event.getMode().equals(dvrpMode)) {
			return;
		}
		
		VehicleState state = new VehicleState();
		state.beginTime = event.getTime();
		state.taskType = VEHICLE_ADDED;
		vehicleStates.put(event.getVehicleId(), state);
	}
	
	@Override
	public void handleEvent(VehicleRemovedEvent event) {
		if (!event.getMode().equals(dvrpMode)) {
			return;
		}
		
		VehicleState state = vehicleStates.remove(event.getVehicleId());
		
		if (state.taskType != null) {
			increment(state, event.getTime());
		}
	}
	
	@Override
	public void handleEvent(TaskStartedEvent event) {
		if (!event.getDvrpMode().equals(dvrpMode)) {
			return;
		}

		VehicleState state = vehicleStates.get(event.getDvrpVehicleId());
		state.taskType = event.getTaskType();
		state.beginTime = event.getTime();
	}

	@Override
	public void handleEvent(TaskEndedEvent event) {
		if (!event.getDvrpMode().equals(dvrpMode)) {
			return;
		}

		VehicleState state = vehicleStates.get(event.getDvrpVehicleId());
		state.taskType = state.taskType == VEHICLE_ADDED ? event.getTaskType() : state.taskType;
		
		increment(state, event.getTime());
		state.taskType = null;
	}

	@Override
	public void handleEvent(PersonEntersVehicleEvent event) {
		processOccupancyChange(event, event.getVehicleId(), +1);
	}

	@Override
	public void handleEvent(PersonLeavesVehicleEvent event) {
		processOccupancyChange(event, event.getVehicleId(), -1);
	}

	private <E extends Event & HasPersonId> void processOccupancyChange(E event, Id<Vehicle> vehicleId, int change) {
		VehicleState state = vehicleStates.get(Id.create(vehicleId, DvrpVehicle.class));
		if (state != null && !isDriver(event.getPersonId(), vehicleId)) {
			increment(state, event.getTime());
			state.occupancy += change;
			state.beginTime = event.getTime();
		}
	}

	private boolean isDriver(Id<Person> personId, Id<Vehicle> vehicleId) {
		return personId.equals(vehicleId);
	}

	private double[] occupancyProfile(int occupancy) {
		while (vehicleOccupancyProfiles.size() <= occupancy) {
			vehicleOccupancyProfiles.add(new double[timeDiscretizer.getIntervalCount()]);
		}
		
		return vehicleOccupancyProfiles.get(occupancy);
	}

	@Override
	public void reset(int iteration) {
		vehicleStates.clear();

		vehicleOccupancyProfiles = new ArrayList<>(IntStream.rangeClosed(0, initialCapacity)
				.mapToObj(i -> new double[timeDiscretizer.getIntervalCount()])
				.collect(Collectors.toList()));

		nonPassengerServingTaskProfiles = new HashMap<>();
		wasConsolidatedInThisIteration = false;
	}
	
	static private final TaskType VEHICLE_ADDED = () -> "VEHICLE_ADDED";
}
