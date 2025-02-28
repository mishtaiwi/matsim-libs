/*
 *  *********************************************************************** *
 *  * project: org.matsim.*
 *  * *********************************************************************** *
 *  *                                                                         *
 *  * copyright       : (C) 2022 by the members listed in the COPYING,        *
 *  *                   LICENSE and WARRANTY file.                            *
 *  * email           : info at matsim dot org                                *
 *  *                                                                         *
 *  * *********************************************************************** *
 *  *                                                                         *
 *  *   This program is free software; you can redistribute it and/or modify  *
 *  *   it under the terms of the GNU General Public License as published by  *
 *  *   the Free Software Foundation; either version 2 of the License, or     *
 *  *   (at your option) any later version.                                   *
 *  *   See also COPYING, LICENSE and WARRANTY file                           *
 *  *                                                                         *
 *  * ***********************************************************************
 */

package org.matsim.freight.logistics.shipment;

import org.matsim.api.core.v01.Id;
import org.matsim.freight.logistics.LSPConstants;
import org.matsim.freight.logistics.LSPResource;
import org.matsim.freight.logistics.LogisticChainElement;

//Consider changing this Logged<...> ShipmentPlanElement to MATSim's experienced plans.
//This would be closer to MATSim and makes clear that this is what happened in the simulation. kmt/kn jan'25
/*package-private*/ class LoggedLspShipmentLoad implements LspShipmentPlanElement {

	private final double startTime;
	private final double endTime;
	private final LogisticChainElement element;
	private final Id<LSPResource> resourceId;

	LoggedLspShipmentLoad(LspShipmentUtils.LoggedShipmentLoadBuilder builder) {
		this.startTime = builder.getStartTime();
		this.endTime = builder.getEndTime();
		this.element = builder.getElement();
		this.resourceId = builder.getResourceId();
	}

	/**
	 * @deprecated //see bloch item 23: Prefer class hierarchies to tagged classes.
	 * Mixing class tagging and class hierarchies is a bad idea.
	 * Getting the type is ok for writing it somewhere, but do NOT use it for specifying the type within a decision logic!
	 * So maybe better use something like getActivityType() -- analogous to e.g. PersonEntersVehicleEvent.class) kmt/kn jan'25
	 */
	@Override
	@Deprecated
	public String getElementType() {
		return LSPConstants.LOAD;
	}

	@Override
	public double getStartTime() {
		return startTime;
	}

	@Override
	public double getEndTime() {
		return endTime;
	}

	@Override
	public LogisticChainElement getLogisticChainElement() {
		return element;
	}

	@Override
	public Id<LSPResource> getResourceId() {
		return resourceId;
	}
}
