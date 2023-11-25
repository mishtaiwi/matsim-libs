//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vhudson-jaxb-ri-2.1-558
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2011.09.19 at 03:18:45 PM MESZ
//

package org.matsim.contrib.minibus.genericUtils.gexf.viz;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.matsim.contrib.minibus.genericUtils.gexf.XMLSpellsContent;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the net.gexf._1_2draft.viz package.
 *
 * <p>An ObjectFactory allows you to programatically construct new instances of the Java
 * representation for XML content. The Java representation of XML content can consist of schema
 * derived interfaces and classes representing the binding of schema type definitions, element
 * declarations and model groups. Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

  private static final QName _Spells_QNAME =
      new QName("http://www.gexf.net/1.2draft/viz", "spells");

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes
   * for package: net.gexf._1_2draft.viz
   */
  public ObjectFactory() {}

  /** Create an instance of {@link SizeContent } */
  public SizeContent createSizeContent() {
    return new SizeContent();
  }

  /** Create an instance of {@link ColorContent } */
  public ColorContent createColorContent() {
    return new ColorContent();
  }

  /** Create an instance of {@link ThicknessContent } */
  public ThicknessContent createThicknessContent() {
    return new ThicknessContent();
  }

  /** Create an instance of {@link NodeShapeContent } */
  public NodeShapeContent createNodeShapeContent() {
    return new NodeShapeContent();
  }

  /** Create an instance of {@link PositionContent } */
  public PositionContent createPositionContent() {
    return new PositionContent();
  }

  /** Create an instance of {@link EdgeShapeContent } */
  public EdgeShapeContent createEdgeShapeContent() {
    return new EdgeShapeContent();
  }

  /** Create an instance of {@link JAXBElement }{@code <}{@link XMLSpellsContent }{@code >}} */
  @XmlElementDecl(namespace = "http://www.gexf.net/1.2draft/viz", name = "spells")
  public JAXBElement<XMLSpellsContent> createSpells(XMLSpellsContent value) {
    return new JAXBElement<>(_Spells_QNAME, XMLSpellsContent.class, null, value);
  }
}
