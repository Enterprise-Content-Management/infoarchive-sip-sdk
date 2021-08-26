/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.opentext.ia.sdk.support.io.DataBuffer;
import com.opentext.ia.sdk.support.io.MemoryBuffer;
import com.opentext.ia.sdk.support.xml.XmlUtil;


public class WhenAssemblingPackagingInformation extends XmlTestCase {

  private final Assembler<PackagingInformation> assembler = new InfoArchivePackagingInformationAssembler();

  @Test
  public void shouldGenerateValidXml() throws IOException {
    String application = "a_" + randomString(62);
    String holding = "h_" + randomString(62);
    String producer = "p_" + randomString(62);
    String dssId = "i_" + randomString(30);
    String pdiSchema = "s_" + randomString(254);
    String entity = "e_" + randomString(62);
    int aiuCount = randomInt(20);
    String attr1 = randomString(5);
    String attr2 = randomString(27);
    String value1 = randomString(95);
    String value2 = randomString(31);
    PackagingInformationFactory packagingInformationFactory =
        new DefaultPackagingInformationFactory(PackagingInformation.builder()
          .dss()
              .id(dssId)
              .application(application)
              .holding(holding)
              .entity(entity)
              .producer(producer)
              .schema(pdiSchema)
          .end()
          .customAttribute(attr1, value1)
          .customAttribute(attr2, value2)
          .build());
    PackagingInformation packagingInformation = packagingInformationFactory.newInstance(aiuCount, Optional.empty());
    DataBuffer buffer = new MemoryBuffer();

    assembler.start(buffer);
    assembler.add(packagingInformation);
    assembler.end();

    try (InputStream stream = buffer.openForReading()) {
      Element packageInfo = assertValidXml(stream, "Packaging Information", "sip.xsd").getDocumentElement();
      Element dss = XmlUtil.getFirstChildElement(packageInfo, "dss");
      assertEquals(dssId, getText(dss, "id"), "ID");
      assertEquals(application, getText(dss, "application"), "Application");
      assertEquals(holding, getText(dss, "holding"), "Holding");
      assertEquals(entity, getText(dss, "entity"), "PDI entity");
      assertEquals(producer, getText(dss, "producer"), "Producer");
      assertEquals(pdiSchema, getText(dss, "pdi_schema"), "PDI schema");
      assertEquals(Integer.toString(aiuCount), getText(dss.getParentNode(), "aiu_count"), "# AIUs");
      Element customAttributes = XmlUtil.getFirstChildElement(
          XmlUtil.getFirstChildElement(packageInfo, "custom"), "attributes");
      assertEquals(value1, getAttributeValue(customAttributes, attr1), "Attr #1");
      assertEquals(value2, getAttributeValue(customAttributes, attr2), "Attr #2");
    }
  }

  private String getText(Node parent, String childName) {
    Element child = XmlUtil.getFirstChildElement((Element)parent, childName);
    return child == null ? "" : child.getTextContent();
  }

  private String getAttributeValue(Element customAttributes, String name) {
    return XmlUtil.namedElementsIn(customAttributes, "attribute")
        .filter(e -> e.getAttributeNS(null, "name").equals(name))
        .map(e -> e.getTextContent())
        .findAny()
        .orElse(null);
  }

}
