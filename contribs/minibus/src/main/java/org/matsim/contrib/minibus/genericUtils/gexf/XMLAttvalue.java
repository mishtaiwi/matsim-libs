//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.09.19 at 03:18:45 PM MESZ
//

package org.matsim.contrib.minibus.genericUtils.gexf;

import jakarta.xml.bind.annotation.*;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.gexf.net/1.2draft}attvalue-content"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "attvalue")
public class XMLAttvalue {

  @XmlAttribute(name = "for", required = true)
  private String _for;

  @XmlAttribute(required = true)
  private String value;

  @XmlAttribute private String start;
  @XmlAttribute private String startopen;
  @XmlAttribute private String end;
  @XmlAttribute private String endopen;

  /**
   * Gets the value of the for property.
   *
   * @return possible object is {@link String }
   */
  public String getFor() {
    return _for;
  }

  /**
   * Sets the value of the for property.
   *
   * @param value allowed object is {@link String }
   */
  public void setFor(String value) {
    this._for = value;
  }

  /**
   * Gets the value of the value property.
   *
   * @return possible object is {@link String }
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   *
   * @param value allowed object is {@link String }
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Gets the value of the start property.
   *
   * @return possible object is {@link String }
   */
  public String getStart() {
    return start;
  }

  /**
   * Sets the value of the start property.
   *
   * @param value allowed object is {@link String }
   */
  public void setStart(String value) {
    this.start = value;
  }

  /**
   * Gets the value of the startopen property.
   *
   * @return possible object is {@link String }
   */
  public String getStartopen() {
    return startopen;
  }

  /**
   * Sets the value of the startopen property.
   *
   * @param value allowed object is {@link String }
   */
  public void setStartopen(String value) {
    this.startopen = value;
  }

  /**
   * Gets the value of the end property.
   *
   * @return possible object is {@link String }
   */
  public String getEnd() {
    return end;
  }

  /**
   * Sets the value of the end property.
   *
   * @param value allowed object is {@link String }
   */
  public void setEnd(String value) {
    this.end = value;
  }

  /**
   * Gets the value of the endopen property.
   *
   * @return possible object is {@link String }
   */
  public String getEndopen() {
    return endopen;
  }

  /**
   * Sets the value of the endopen property.
   *
   * @param value allowed object is {@link String }
   */
  public void setEndopen(String value) {
    this.endopen = value;
  }
}
