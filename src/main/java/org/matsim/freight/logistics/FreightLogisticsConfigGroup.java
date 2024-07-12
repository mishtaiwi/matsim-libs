/*
 *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       :  (C) 2024 by the members listed in the COPYING,       *
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
 * ***********************************************************************
 */

package org.matsim.freight.logistics;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ReflectiveConfigGroup;

public class FreightLogisticsConfigGroup extends ReflectiveConfigGroup {

    public static final String GROUPNAME="freightLogistics" ;

    private String lspsFile;
    static final String LSPS_FILE = "lspsFile";
    private static final String CARRIERS_FILE_DESC = "Freight LogisticsServiceProvider File, according to MATSim logistics extension as part of MATSim's freight contrib.";


    public enum LogicOfVrp {serviceBased, shipmentBased}

    static final String VRP_LOGIC_OF_DISTRIBUTION_CARRIER = "vrpLogicOfDistributionCarrier";
    private LogicOfVrp vrpLogicOfDistributionCarrier = LogicOfVrp.serviceBased;
    private static final String VRP_LOGIC_OF_DISTRIBUTION_CARRIER_DESC = "Define, on which type of jobs the VRP of the **distribution** carrier will base on:" + Arrays.toString(LogicOfVrp.values());

    static final String VRP_LOGIC_OF_MAINRUN_CARRIER = "vrpLogicOfMainRunCarrier";
    private LogicOfVrp vrpLogicOfMainRunCarrier = LogicOfVrp.serviceBased;
    private static final String VRP_LOGIC_OF_MAINRUN_CARRIER_DESC = "Define, on which type of jobs the VRP of the **MainRun** carrier will base on:" + Arrays.toString(LogicOfVrp.values());

    static final String VRP_LOGIC_OF_COLLECTION_CARRIER = "vrpLogicOfCollectionCarrier";
    private LogicOfVrp vrpLogicOfCollectionCarrier = LogicOfVrp.serviceBased;
    private static final String VRP_LOGIC_OF_COLLECTION_CARRIER_DESC = "Define, on which type of jobs the VRP of the **Collection** carrier will base on:" + Arrays.toString(LogicOfVrp.values());


    public FreightLogisticsConfigGroup() {
        super(GROUPNAME);
    }

    //### CarriersFile ###
    /**
     * @return -- {@value #CARRIERS_FILE_DESC}
     */
    @StringGetter(LSPS_FILE)
    public String getLspsFile() {
        return lspsFile;
    }

    URL getLspsFileUrl(URL context) {
        return ConfigGroup.getInputFileURL(context, this.lspsFile);
    }

    /**
     * @param -- {@value #CARRIERS_FILE_DESC}
     */
    @StringSetter(LSPS_FILE)
    public void setLspsFile(String lspsFile) {
        this.lspsFile = lspsFile;
    }



    //---
    //---

    /**
     *
     * @return The internal type of jobs, on which the VRPs of the distribution carrier bases on.
     */
    @StringGetter(VRP_LOGIC_OF_DISTRIBUTION_CARRIER)
    public LogicOfVrp getVrpLogicOfDistributionCarrier() {
        return vrpLogicOfDistributionCarrier;
    }

    /**
     * @param vrpLogicOfDistributionCarrier {@value #VRP_LOGIC_OF_DISTRIBUTION_CARRIER}
     */
    @StringSetter(VRP_LOGIC_OF_DISTRIBUTION_CARRIER)
    public void setVrpLogicOfDistributionCarrier(LogicOfVrp vrpLogicOfDistributionCarrier) {
        this.vrpLogicOfDistributionCarrier = vrpLogicOfDistributionCarrier;
    }

    /**
     * @return The internal type of jobs, on which the VRPs of the main run carrier bases on.
     */
    @StringGetter(FreightLogisticsConfigGroup.VRP_LOGIC_OF_MAINRUN_CARRIER)
    public LogicOfVrp getVrpLogicOfMainRunCarrier() {
        return vrpLogicOfMainRunCarrier;
    }

    /**
     * @param vrpLogicOfMainRunCarrier {@value #VRP_LOGIC_OF_MAINRUN_CARRIER}
     */
    @StringSetter(FreightLogisticsConfigGroup.VRP_LOGIC_OF_MAINRUN_CARRIER)
    public void setVrpLogicOfMainRunCarrier(LogicOfVrp vrpLogicOfMainRunCarrier) {
        this.vrpLogicOfMainRunCarrier = vrpLogicOfMainRunCarrier;
    }

    /**
     * @return The internal type of jobs, on which the VRPs of the collection carrier bases on.
     */
    @StringGetter(FreightLogisticsConfigGroup.VRP_LOGIC_OF_COLLECTION_CARRIER)
    public LogicOfVrp getVrpLogicOfCollectionCarrier() {
        return vrpLogicOfCollectionCarrier;
    }

    /**
     * @param vrpLogicOfCollectionCarrier {@value #VRP_LOGIC_OF_COLLECTION_CARRIER}
     */
    @StringSetter(FreightLogisticsConfigGroup.VRP_LOGIC_OF_COLLECTION_CARRIER)
    public void setVrpLogicOfCollectionCarrier(LogicOfVrp vrpLogicOfCollectionCarrier) {
        this.vrpLogicOfCollectionCarrier = vrpLogicOfCollectionCarrier;
    }

    //---
    //---
    @Override
    public Map<String, String> getComments() {
        Map<String, String> map = super.getComments();
        map.put(LSPS_FILE, CARRIERS_FILE_DESC);
        map.put(VRP_LOGIC_OF_DISTRIBUTION_CARRIER, VRP_LOGIC_OF_DISTRIBUTION_CARRIER_DESC);
        map.put(VRP_LOGIC_OF_MAINRUN_CARRIER, VRP_LOGIC_OF_MAINRUN_CARRIER_DESC);
        map.put(VRP_LOGIC_OF_COLLECTION_CARRIER, VRP_LOGIC_OF_COLLECTION_CARRIER_DESC);
        return map;
    }

}
