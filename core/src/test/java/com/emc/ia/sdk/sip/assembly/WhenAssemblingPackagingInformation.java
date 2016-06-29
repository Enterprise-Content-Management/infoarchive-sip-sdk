/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 */
package com.emc.ia.sdk.sip.assembly;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.emc.ia.sdk.support.io.DataBuffer;
import com.emc.ia.sdk.support.io.MemoryBuffer;
import com.emc.ia.sdk.support.test.XmlTestCase;
import com.emc.ia.sdk.support.xml.XmlUtil;


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
    PackagingInformationFactory packagingInformationFactory = new DefaultPackagingInformationFactory(
        PackagingInformation.builder()
            .dss()
                .id(dssId)
                .application(application)
                .holding(holding)
                .entity(entity)
                .producer(producer)
                .schema(pdiSchema)
            .end()
            .build());
    PackagingInformation packagingInformation = packagingInformationFactory.newInstance(aiuCount, Optional.empty());
    DataBuffer buffer = new MemoryBuffer();

    assembler.start(buffer);
    assembler.add(packagingInformation);
    assembler.end();

    try (InputStream stream = buffer.openForReading()) {
      Element dss = assertValidPackagingInformation(stream);
      assertEquals("ID", dssId, getText(dss, "id"));
      assertEquals("Application", application, getText(dss, "application"));
      assertEquals("Holding", holding, getText(dss, "holding"));
      assertEquals("PDI entity", entity, getText(dss, "entity"));
      assertEquals("Producer", producer, getText(dss, "producer"));
      assertEquals("PDI schema", pdiSchema, getText(dss, "pdi_schema"));
      assertEquals("# AIUs", Integer.toString(aiuCount), getText(dss.getParentNode(), "aiu_count"));
    }
  }

  private Element assertValidPackagingInformation(InputStream stream) throws IOException {
    Document packagingInformation = assertValidXml(stream, "Packaging Information", "sip.xsd");
    return XmlUtil.getFirstChildElement(packagingInformation.getDocumentElement(), "dss");
  }

  private String getText(Node parent, String childName) {
    Element child = XmlUtil.getFirstChildElement((Element)parent, childName);
    return child == null ? "" : child.getTextContent();
  }

}
