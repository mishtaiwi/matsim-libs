/* *********************************************************************** *
 * project: org.matsim.*
 * AmberTimesDataFactory
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
package org.matsim.contrib.signals.data.ambertimes.v10;

import org.matsim.api.core.v01.Id;
import org.matsim.contrib.signals.model.SignalSystem;
import org.matsim.core.api.internal.MatsimFactory;

/**
 * @author dgrether
 */
public interface AmberTimesDataFactory extends MatsimFactory {

  public AmberTimeData createAmberTimeData(Id<SignalSystem> signalSystemId);
}
