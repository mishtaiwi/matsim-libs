//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.09.19 at 03:18:45 PM MESZ
//

package playground.vsp.gexf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class for edges-content complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="edges-content">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.gexf.net/1.2draft}edge" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="count" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "edges-content",
    propOrder = {"edge"})
public class XMLEdgesContent {

  protected List<XMLEdgeContent> edge;

  @XmlAttribute
  @XmlSchemaType(name = "nonNegativeInteger")
  protected BigInteger count;

  /**
   * Gets the value of the edge property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the edge property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getEdge().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link XMLEdgeContent }
   */
  public List<XMLEdgeContent> getEdge() {
    if (edge == null) {
      edge = new ArrayList<XMLEdgeContent>();
    }
    return this.edge;
  }

  /**
   * Gets the value of the count property.
   *
   * @return possible object is {@link BigInteger }
   */
  public BigInteger getCount() {
    return count;
  }

  /**
   * Sets the value of the count property.
   *
   * @param value allowed object is {@link BigInteger }
   */
  public void setCount(BigInteger value) {
    this.count = value;
  }
}
