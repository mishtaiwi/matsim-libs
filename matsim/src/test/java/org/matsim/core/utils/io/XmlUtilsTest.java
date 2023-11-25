/* *********************************************************************** *
 * project: org.matsim.*
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

package org.matsim.core.utils.io;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author mrieser
 */
public class XmlUtilsTest {

  @Test
  public void testEncodeAttributeValue() {
    Assert.assertEquals("hello world!", XmlUtils.encodeAttributeValue("hello world!"));
    Assert.assertEquals("you &amp; me", XmlUtils.encodeAttributeValue("you & me"));
    Assert.assertEquals("you &amp; me &amp; her", XmlUtils.encodeAttributeValue("you & me & her"));
    Assert.assertEquals("tick &quot; tack", XmlUtils.encodeAttributeValue("tick \" tack"));
    Assert.assertEquals(
        "tick &quot; tack &quot; tock", XmlUtils.encodeAttributeValue("tick \" tack \" tock"));
    Assert.assertEquals(
        "this &amp; that &quot; these &amp; those",
        XmlUtils.encodeAttributeValue("this & that \" these & those"));
    Assert.assertEquals(
        "tick &lt; tack &gt; tock", XmlUtils.encodeAttributeValue("tick < tack > tock"));
  }

  @Test
  public void testEncodedContent() {
    Assert.assertEquals("hello world!", XmlUtils.encodeContent("hello world!"));
    Assert.assertEquals("you &amp; me", XmlUtils.encodeContent("you & me"));
    Assert.assertEquals("you &amp; me &amp; her", XmlUtils.encodeContent("you & me & her"));
    Assert.assertEquals("tick \" tack", XmlUtils.encodeContent("tick \" tack"));
    Assert.assertEquals("tick \" tack \" tock", XmlUtils.encodeContent("tick \" tack \" tock"));
    Assert.assertEquals(
        "this &amp; that \" these &amp; those",
        XmlUtils.encodeContent("this & that \" these & those"));
    Assert.assertEquals("tick &lt; tack &gt; tock", XmlUtils.encodeContent("tick < tack > tock"));
  }
}
