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

package lsp.resourceImplementations.transshipmentHub;

import lsp.*;
import lsp.shipment.ShipmentPlan;
import lsp.shipment.ShipmentPlanElement;
import lsp.shipment.ShipmentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matsim.api.core.v01.Id;

import java.util.ArrayList;

/*package-private*/ class TransshipmentHubScheduler extends LSPResourceScheduler {

	final Logger log = LogManager.getLogger(TransshipmentHubScheduler.class);
	private final double capacityNeedLinear;
	private final double capacityNeedFixed;
	private TransshipmentHubResource transshipmentHubResource;
	private TransshipmentHubTourEndEventHandler eventHandler;

	TransshipmentHubScheduler(TranshipmentHubUtils.TranshipmentHubSchedulerBuilder builder) {
		this.lspShipmentsWithTime = new ArrayList<>();
		this.capacityNeedLinear = builder.getCapacityNeedLinear();
		this.capacityNeedFixed = builder.getCapacityNeedFixed();

	}
	
	@Override protected void initializeValues( LSPResource resource ) {
		this.transshipmentHubResource = (TransshipmentHubResource) resource;
	}
	
	@Override protected void scheduleResource() {
		for( LspShipmentWithTime tupleToBeAssigned: lspShipmentsWithTime){
			updateSchedule(tupleToBeAssigned);
		}
	}

	@Override @Deprecated
	protected void updateShipments() {
		log.error("This method is not implemented. Nothing will happen here. ");
	}


	private void updateSchedule(LspShipmentWithTime tuple) {
		addShipmentHandleElement(tuple);
		addShipmentToEventHandler(tuple);
	}

	private void addShipmentHandleElement(LspShipmentWithTime tuple) {
		ShipmentUtils.ScheduledShipmentHandleBuilder builder = ShipmentUtils.ScheduledShipmentHandleBuilder.newInstance();
		builder.setStartTime(tuple.getTime());
		builder.setEndTime(tuple.getTime() + capacityNeedFixed + capacityNeedLinear * tuple.getShipment().getSize());
		builder.setResourceId(transshipmentHubResource.getId());
		for (LogisticChainElement element : transshipmentHubResource.getClientElements()) {
			if (element.getIncomingShipments().getShipments().contains(tuple)) {
				builder.setLogisticsChainElement(element);
			}
		}
		ShipmentPlanElement handle = builder.build();
		String idString = handle.getResourceId() + "" + handle.getLogisticChainElement().getId() + "" + handle.getElementType();
		Id<ShipmentPlanElement> id = Id.create(idString, ShipmentPlanElement.class);
		ShipmentUtils.getOrCreateShipmentPlan(super.lspPlan, tuple.getShipment().getId()).addPlanElement(id, handle);
	}

	private void addShipmentToEventHandler(LspShipmentWithTime tuple) {
		for (LogisticChainElement element : transshipmentHubResource.getClientElements()) {
			if (element.getIncomingShipments().getShipments().contains(tuple)) {
				ShipmentPlan shipmentPlan = ShipmentUtils.getOrCreateShipmentPlan(lspPlan, tuple.getShipment().getId());
				eventHandler.addShipment(tuple.getShipment(), element, shipmentPlan);
				break;
			}
		}
	}

	public double getCapacityNeedLinear() {
		return capacityNeedLinear;
	}


	public double getCapacityNeedFixed() {
		return capacityNeedFixed;
	}


	public void setTranshipmentHub(TransshipmentHubResource transshipmentHubResource) {
		this.transshipmentHubResource = transshipmentHubResource;
	}

	public void setEventHandler(TransshipmentHubTourEndEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

}
