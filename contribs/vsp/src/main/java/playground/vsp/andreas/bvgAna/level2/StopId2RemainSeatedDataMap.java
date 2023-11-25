/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2010 by the members listed in the COPYING,        *
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

package playground.vsp.andreas.bvgAna.level2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.core.api.experimental.events.VehicleArrivesAtFacilityEvent;
import org.matsim.core.api.experimental.events.handler.VehicleArrivesAtFacilityEventHandler;
import playground.vsp.andreas.bvgAna.level1.VehId2OccupancyHandler;

/**
 * Calculates the number of agents which remain in the vehicles and do not leave the vehicle at the
 * stop.
 *
 * @author aneumann
 */
public class StopId2RemainSeatedDataMap
    implements VehicleArrivesAtFacilityEventHandler,
        PersonEntersVehicleEventHandler,
        PersonLeavesVehicleEventHandler {

  private final Logger log = LogManager.getLogger(StopId2RemainSeatedDataMap.class);
  //	private final Level logLevel = Level.DEBUG;

  private VehId2OccupancyHandler vehId2OccupancyHandler;
  private TreeMap<Id, StopId2RemainSeatedDataMapData> vehId2PersonsLeavingMap =
      new TreeMap<Id, StopId2RemainSeatedDataMapData>();
  private TreeMap<Id, List<StopId2RemainSeatedDataMapData>> stopId2RemainSeatedDataMap =
      new TreeMap<Id, List<StopId2RemainSeatedDataMapData>>();

  public StopId2RemainSeatedDataMap() {
    //		this.log.setLevel(this.logLevel);
    this.vehId2OccupancyHandler = new VehId2OccupancyHandler();
  }

  /**
   * @return A map containing a list of <code>StopId2RemainSeatedDataMapData</code> for each stop
   */
  public Map<Id, List<StopId2RemainSeatedDataMapData>> getStopId2RemainSeatedDataMap() {
    return this.stopId2RemainSeatedDataMap;
  }

  @Override
  public void handleEvent(VehicleArrivesAtFacilityEvent event) {
    this.vehId2PersonsLeavingMap.put(
        event.getVehicleId(),
        new StopId2RemainSeatedDataMapData(
            event,
            this.vehId2OccupancyHandler.getVehicleLoad(event.getVehicleId(), event.getTime())));

    if (this.stopId2RemainSeatedDataMap.get(event.getFacilityId()) == null) {
      this.stopId2RemainSeatedDataMap.put(
          event.getFacilityId(), new ArrayList<StopId2RemainSeatedDataMapData>());
    }
    this.stopId2RemainSeatedDataMap
        .get(event.getFacilityId())
        .add(this.vehId2PersonsLeavingMap.get(event.getVehicleId()));
  }

  @Override
  public void handleEvent(PersonEntersVehicleEvent event) {
    this.vehId2OccupancyHandler.handleEvent(event);
    this.vehId2PersonsLeavingMap.get(event.getVehicleId()).addAgentEntering();
  }

  @Override
  public void handleEvent(PersonLeavesVehicleEvent event) {
    this.vehId2OccupancyHandler.handleEvent(event);
    this.vehId2PersonsLeavingMap.get(event.getVehicleId()).addAgentLeaving();
  }

  @Override
  public void reset(int iteration) {
    this.vehId2OccupancyHandler.reset(iteration);
  }
}
