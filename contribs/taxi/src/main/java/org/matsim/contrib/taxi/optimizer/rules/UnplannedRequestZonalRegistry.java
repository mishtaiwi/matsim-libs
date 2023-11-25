/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2015 by the members listed in the COPYING,        *
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

package org.matsim.contrib.taxi.optimizer.rules;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.IdMap;
import org.matsim.api.core.v01.network.Node;
import org.matsim.contrib.drt.passenger.DrtRequest;
import org.matsim.contrib.dvrp.optimizer.Request;
import org.matsim.contrib.zone.ZonalSystem;
import org.matsim.contrib.zone.ZonalSystems;
import org.matsim.contrib.zone.Zone;

public class UnplannedRequestZonalRegistry {
  private final ZonalSystem zonalSystem;
  private final IdMap<Zone, List<Zone>> zonesSortedByDistance;
  private final IdMap<Zone, Map<Id<Request>, DrtRequest>> requestsInZones = new IdMap<>(Zone.class);

  private int requestCount = 0;

  public UnplannedRequestZonalRegistry(ZonalSystem zonalSystem) {
    this.zonalSystem = zonalSystem;
    zonesSortedByDistance = ZonalSystems.initZonesByDistance(zonalSystem.getZones());

    for (Id<Zone> id : zonalSystem.getZones().keySet()) {
      requestsInZones.put(id, new LinkedHashMap<>()); // LinkedHashMap to preserve iteration order
    }
  }

  // after submitted
  public void addRequest(DrtRequest request) {
    Id<Zone> zoneId = getZoneId(request);

    if (requestsInZones.get(zoneId).put(request.getId(), request) != null) {
      throw new IllegalStateException(request + " is already in the registry");
    }

    requestCount++;
  }

  // after scheduled
  public void removeRequest(DrtRequest request) {
    Id<Zone> zoneId = getZoneId(request);

    if (requestsInZones.get(zoneId).remove(request.getId()) == null) {
      throw new IllegalStateException(request + " is not in the registry");
    }

    requestCount--;
  }

  public Stream<DrtRequest> findNearestRequests(Node node, int minCount) {
    return zonesSortedByDistance.get(zonalSystem.getZone(node).getId()).stream()
        .flatMap(z -> requestsInZones.get(z.getId()).values().stream())
        .limit(minCount);
  }

  private Id<Zone> getZoneId(DrtRequest request) {
    return zonalSystem.getZone(request.getFromLink().getFromNode()).getId();
  }

  public int getRequestCount() {
    return requestCount;
  }
}
