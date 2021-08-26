/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.test.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import com.opentext.ia.sdk.support.io.ByteArrayInputOutputStream;
import com.opentext.ia.sdk.support.io.RepeatableInputStream;
import com.opentext.ia.sdk.support.xml.XmlUtil;

public class SipFileValidator {

  private final Object owner;
  private final ZipFile zipFile;

  public SipFileValidator(Object owner, File file) throws IOException {
    this.owner = owner;
    this.zipFile = new ZipFile(file);
  }

  public SipFileValidator assertFileCount(int count) {
    assertEquals(count, zipFile.size(), "Entries in sip file.");
    return this;
  }

  public SipFileValidator assertPackagingInformation(int aiuCount) throws IOException {
    ZipEntry entry = zipFile.getEntry("eas_sip.xml");
    assertNotNull(entry, "Missing Packaging Information");

    try (ByteArrayInputOutputStream packagingInformation = new ByteArrayInputOutputStream();
        InputStream in = zipFile.getInputStream(entry)) {
      IOUtils.copy(in, packagingInformation);
      Element sipElement =
          assertValidXml(packagingInformation.getInputStream(), "PackagingInformation", "sip.xsd").getDocumentElement();
      String actualAiuCount = XmlUtil.getFirstChildElement(sipElement, "aiu_count")
        .getTextContent();
      assertEquals(aiuCount, Integer.parseInt(actualAiuCount), "# AIUs");
    }

    return this;
  }

  public SipFileValidator assertPreservationInformationIdenticalTo(String resource) throws IOException {
    ZipEntry entry = zipFile.getEntry("eas_pdi.xml");
    assertNotNull(entry, "Missing Preservation Information");

    try (InputStream expectedIn = owner.getClass()
      .getResourceAsStream(resource); InputStream actualIn = zipFile.getInputStream(entry)) {

      Diff myDiff = DiffBuilder.compare(Input.fromStream(expectedIn))
        .withTest(Input.fromStream(actualIn))
        .checkForSimilar()
        .ignoreWhitespace()
        .ignoreComments()
        .build();
      assertFalse(myDiff.hasDifferences(), "XML similar " + myDiff);
    }
    return this;

  }

  protected Document assertValidXml(InputStream stream, String humanFriendlyDocumentType, String schema)
      throws IOException {
    RepeatableInputStream repeatableStream = new RepeatableInputStream(stream);
    XmlUtil.validate(repeatableStream.get(), owner.getClass()
      .getResourceAsStream("/" + schema), humanFriendlyDocumentType);
    return XmlUtil.parse(repeatableStream.get());
  }

  public SipFileValidator assertContentFileIdenticalTo(String referenceInformation, String resource)
      throws IOException {
    ZipEntry entry = zipFile.getEntry(referenceInformation);
    assertNotNull(entry, "Missing Digital Object");
    try (InputStream expectedIn = owner.getClass()
      .getResourceAsStream(resource); InputStream actualIn = zipFile.getInputStream(entry)) {
      assertTrue(IOUtils.contentEquals(expectedIn, actualIn),
          "The DigitalObject " + referenceInformation + " is not the expected one.");
    }
    return this;
  }

}
