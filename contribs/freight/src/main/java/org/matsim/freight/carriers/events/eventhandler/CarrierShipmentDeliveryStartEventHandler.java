/*
 *   *********************************************************************** *
 *   project: org.matsim.*
 *   *********************************************************************** *
 *                                                                           *
 *   copyright       : (C)  by the members listed in the COPYING,        *
 *                     LICENSE and WARRANTY file.                            *
 *   email           : info at matsim dot org                                *
 *                                                                           *
 *   *********************************************************************** *
 *                                                                           *
 *     This program is free software; you can redistribute it and/or modify  *
 *     it under the terms of the GNU General Public License as published by  *
 *     the Free Software Foundation; either version 2 of the License, or     *
 *     (at your option) any later version.                                   *
 *     See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                           *
 *   ***********************************************************************
 *
 */

package org.matsim.freight.carriers.events.eventhandler;

import org.matsim.core.events.handler.EventHandler;
import org.matsim.freight.carriers.events.CarrierShipmentDeliveryStartEvent;

/**
 * Interface to listen to shipmentDeliveredEvents.
 *
 * @author sschroeder
 */
public interface CarrierShipmentDeliveryStartEventHandler extends EventHandler {

  void handleEvent(CarrierShipmentDeliveryStartEvent event);
}
