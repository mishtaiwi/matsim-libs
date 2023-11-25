/* *********************************************************************** *
 * project: org.matsim.*
 * XMLLaneDefinitions.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.02.13 at 12:10:00 AM MEZ
//

package org.matsim.jaxb.lanedefinitions20;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lanesToLinkAssignment" type="{http://www.matsim.org/files/dtd}lanesToLinkAssignmentType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"lanesToLinkAssignment"})
@XmlRootElement(name = "laneDefinitions")
public class XMLLaneDefinitions {

  protected List<XMLLanesToLinkAssignmentType> lanesToLinkAssignment;

  /**
   * Gets the value of the lanesToLinkAssignment property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the lanesToLinkAssignment property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getLanesToLinkAssignment().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link XMLLanesToLinkAssignmentType
   * }
   */
  public List<XMLLanesToLinkAssignmentType> getLanesToLinkAssignment() {
    if (lanesToLinkAssignment == null) {
      lanesToLinkAssignment = new ArrayList<XMLLanesToLinkAssignmentType>();
    }
    return this.lanesToLinkAssignment;
  }
}
