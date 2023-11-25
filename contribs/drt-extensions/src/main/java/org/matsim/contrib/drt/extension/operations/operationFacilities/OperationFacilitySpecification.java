package org.matsim.contrib.drt.extension.operations.operationFacilities;

import java.util.List;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Identifiable;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.ev.infrastructure.Charger;

/**
 * @author nkuehnel / MOIA
 */
public interface OperationFacilitySpecification extends Identifiable<OperationFacility> {

  int getCapacity();

  List<Id<Charger>> getChargers();

  OperationFacilityType getType();

  Id<Link> getLinkId();

  Coord getCoord();
}
