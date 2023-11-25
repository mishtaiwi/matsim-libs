/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
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

package org.matsim.contrib.parking.parkingsearch.events;

import java.util.Map;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

/**
 * @author Ricardo Ewert
 */
public class RemoveParkingActivityEvent extends Event {
  public static final String EVENT_TYPE = "planed parking activity is skipped";
  public static final String ATTRIBUTE_VEHICLE = "vehicle";
  public static final String ATTRIBUTE_Current_LINK = "link";
  private final Id<Link> currentLinkId;
  private final Id<Vehicle> vehicleId;

  public RemoveParkingActivityEvent(
      final double time, Id<Vehicle> vehicleId, Id<Link> currentLinkId) {
    super(time);
    this.currentLinkId = currentLinkId;
    this.vehicleId = vehicleId;
  }

  @Override
  public String getEventType() {
    return EVENT_TYPE;
  }

  public Id<Link> getCurrentLinkId() {
    return currentLinkId;
  }

  public Id<Vehicle> getVehicleId() {
    return vehicleId;
  }

  @Override
  public Map<String, String> getAttributes() {
    Map<String, String> attr = super.getAttributes();
    attr.put(ATTRIBUTE_VEHICLE, this.vehicleId.toString());
    attr.put(ATTRIBUTE_Current_LINK, this.currentLinkId.toString());
    return attr;
  }
}
