//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.02.13 at 12:10:00 AM MEZ
//

package org.matsim.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public final class Adapter1 extends XmlAdapter<String, Integer> {

  public Integer unmarshal(String value) {
    return (jakarta.xml.bind.DatatypeConverter.parseInt(value));
  }

  public String marshal(Integer value) {
    if (value == null) {
      return null;
    }
    return (jakarta.xml.bind.DatatypeConverter.printInt(value));
  }
}
