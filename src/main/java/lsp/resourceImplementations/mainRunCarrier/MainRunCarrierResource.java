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

package lsp.resourceImplementations.mainRunCarrier;

import lsp.*;
import lsp.resourceImplementations.ResourceImplementationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.freight.carrier.Carrier;

import java.util.Collection;

/*package-private*/ class MainRunCarrierResource extends LSPDataObject<LSPResource> implements LSPCarrierResource {

	private static final Logger log = LogManager.getLogger(MainRunCarrierResource.class);

	private final Carrier carrier;
	private final Id<Link> fromLinkId;
	private final Id<Link> toLinkId;
	private final Collection<LogisticChainElement> clientElements;
	private final MainRunCarrierScheduler mainRunScheduler;

	private final ResourceImplementationUtils.VehicleReturn vehicleReturn;
	private final Network network;


	MainRunCarrierResource(MainRunCarrierUtils.MainRunCarrierResourceBuilder builder) {
		super(builder.getId());
		this.carrier = builder.getCarrier();
		this.fromLinkId = builder.getFromLinkId();
		this.toLinkId = builder.getToLinkId();
		this.clientElements = builder.getClientElements();
		this.mainRunScheduler = builder.getMainRunScheduler();
		if (builder.getVehicleReturn() != null){
			this.vehicleReturn = builder.getVehicleReturn();
		} else {
			log.warn("Return behaviour was not specified. Using the following setting as default: " + ResourceImplementationUtils.VehicleReturn.endAtToLink);
			this.vehicleReturn = ResourceImplementationUtils.VehicleReturn.endAtToLink;
		}
		this.network = builder.getNetwork();
	}

	@Override
	public Id<Link> getStartLinkId() {
		return fromLinkId;
	}

	@Override
	public Id<Link> getEndLinkId() {
		return toLinkId;
	}

	@Override
	public Collection<LogisticChainElement> getClientElements() {
		return clientElements;
	}

	@Override
	public void schedule(int bufferTime, LSPPlan lspPlan) {
		mainRunScheduler.scheduleShipments(lspPlan, this, bufferTime);
	}

	public Carrier getCarrier() {
		return carrier;
	}

	public Network getNetwork() {
		return network;
	}

	public ResourceImplementationUtils.VehicleReturn getVehicleReturn() {
		return vehicleReturn;
	}
}
